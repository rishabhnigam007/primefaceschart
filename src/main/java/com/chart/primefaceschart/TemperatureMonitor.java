package com.chart.primefaceschart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.optionconfig.animation.Animation;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.optionconfig.title.Title;

@ManagedBean(name = "temperatureMonitorBean")
@RequestScoped
public class TemperatureMonitor implements Serializable {

    private BarChartModel barModel;
    private String cityName;
    private final String API_KEY = "9b5255153f1e0d4b0de53c1bae133728";

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @PostConstruct
    public void init() {
        fetchTemperatureData();
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    public void fetchTemperatureData() {
        if (cityName == null || cityName.isEmpty()) {
            // Handle error: City name is required
            return;
        }

        try {
            // Fetch latitude and longitude
            String weatherJson = getWeatherData(cityName);
            String[] latLong = getLatLongFromJson(weatherJson);
            double latitude = Double.parseDouble(latLong[0]);
            double longitude = Double.parseDouble(latLong[1]);

            // Fetch forecast data
            String forecastJson = getForecastData(latitude, longitude);

            // Process forecast data to get temperatures for the next 5 days
            double[] temperatures = getTemperaturesFromForecast(forecastJson, 5);

            // Create bar chart model with temperature data
            createBarModel(temperatures);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error: Failed to fetch data
        }
    }

    public void createBarModel(double[] temperatures) {
    barModel = new BarChartModel();
    ChartData data = new ChartData();

    BarChartDataSet barDataSet = new BarChartDataSet();
    barDataSet.setLabel("Temperature");

    List<Number> values = new ArrayList<>();
    for (double temperature : temperatures) {
        values.add(temperature);
    }
    barDataSet.setData(values);

    // Set background and border colors for the bars
    List<String> bgColor = new ArrayList<>();
    bgColor.add("rgba(255, 99, 132, 0.2)");
    bgColor.add("rgba(255, 159, 64, 0.2)");
    bgColor.add("rgba(255, 205, 86, 0.2)");
    bgColor.add("rgba(75, 192, 192, 0.2)");
    bgColor.add("rgba(54, 162, 235, 0.2)");
    barDataSet.setBackgroundColor(bgColor);

    List<String> borderColor = new ArrayList<>();
    borderColor.add("rgb(255, 99, 132)");
    borderColor.add("rgb(255, 159, 64)");
    borderColor.add("rgb(255, 205, 86)");
    borderColor.add("rgb(75, 192, 192)");
    borderColor.add("rgb(54, 162, 235)");
    barDataSet.setBorderColor(borderColor);
    barDataSet.setBorderWidth(1);

    data.addChartDataSet(barDataSet);

    // Set labels for the bars
    List<String> labels = new ArrayList<>();
    for (int i = 0; i < temperatures.length; i++) {
        labels.add("Day " + (i + 1));
    }
    data.setLabels(labels);
    barModel.setData(data);

    // Configure chart options
    BarChartOptions options = new BarChartOptions();
    options.setMaintainAspectRatio(false);
    CartesianScales cScales = new CartesianScales();
    CartesianLinearAxes linearAxes = new CartesianLinearAxes();
    linearAxes.setOffset(true);
    linearAxes.setBeginAtZero(true);
    CartesianLinearTicks ticks = new CartesianLinearTicks();
    linearAxes.setTicks(ticks);
    cScales.addYAxesData(linearAxes);
    options.setScales(cScales);

    Title title = new Title();
    title.setDisplay(true);
    title.setText("Temperature Forecast");
    options.setTitle(title);

    Legend legend = new Legend();
    legend.setDisplay(true);
    legend.setPosition("top");
    LegendLabel legendLabels = new LegendLabel();
    legendLabels.setFontStyle("italic");
    legendLabels.setFontColor("#2980B9");
    legendLabels.setFontSize(24);
    legend.setLabels(legendLabels);
    options.setLegend(legend);

    // Disable animation
    Animation animation = new Animation();
    animation.setDuration(0);
    options.setAnimation(animation);

    barModel.setOptions(options);
}

    private String getWeatherData(String cityName) throws IOException {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + API_KEY;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            throw new IOException("Failed to fetch data from API: " + responseCode);
        }
    }

    private String[] getLatLongFromJson(String json) {
        JSONObject obj = new JSONObject(json);
        JSONObject coord = obj.getJSONObject("coord");
        double latitude = coord.getDouble("lat");
        double longitude = coord.getDouble("lon");
        return new String[]{String.valueOf(latitude), String.valueOf(longitude)};
    }

    private String getForecastData(double latitude, double longitude) throws IOException {
        String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            throw new IOException("Failed to fetch forecast data from API: " + responseCode);
        }
    }

    private double[] getTemperaturesFromForecast(String forecastJson, int days) {
        JSONObject obj = new JSONObject(forecastJson);
        JSONArray list = obj.getJSONArray("list");
        double[] temperatures = new double[days];
        for (int i = 0; i < days; i++) {
            int index = i * 8; // Assuming each day has 8 entries
            if (index < list.length()) {
                JSONObject dayData = list.getJSONObject(index);
                JSONObject main = dayData.getJSONObject("main");
                double temperature = main.getDouble("temp");
                temperatures[i] = temperature - 273.15; // Convert Kelvin to Celsius
            } else {
                temperatures[i] = 0; // Default temperature or any other value
            }
        }
        return temperatures;
    }
}
