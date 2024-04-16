package com.chart.primefaceschart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
//import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.animation.Animation;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.optionconfig.title.Title;

@ManagedBean(name = "temperatureMonitorBean")
@ViewScoped
public class TemperatureMonitor implements Serializable {

    private BarChartModel barModel;
    private LineChartModel lineModel;
    private String cityName;
    private double latitude;
    private double longitude;
    private final String API_KEY = "9b5255153f1e0d4b0de53c1bae133728";

    public LineChartModel getLineModel() {
        return lineModel;
    }

    public void setLineModel(LineChartModel lineModel) {
        this.lineModel = lineModel;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
            double[] latLong = getLatLongFromJson(weatherJson);
            setLatitude(latLong[0]);
            System.out.println("Latitude set: " + latitude);
            setLongitude(latLong[1]);
            System.out.println("Longitude set: " + longitude);

            // Fetch forecast data
            String forecastJson = getForecastData(latitude, longitude);

            // Process forecast data to get temperatures for the next 5 days
            double[] temperatures = getTemperaturesFromForecast(forecastJson, 5);
            double[] winds = getWindSpeedFromForecast(forecastJson, 5);

            // Create bar chart model with temperature data
            createBarModel(temperatures);
            createLineModel(winds);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error: Failed to fetch data
        }
    }

    public void createBarModel(double[] temperatures) {
        barModel = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Temperature in " + '\u00B0' + "C");

        // Set background and border colors for the bars
        List<String> bgColor = new ArrayList<>();

        // Set border colors for the bars
        List<String> borderColor = new ArrayList<>();

        List<Number> values = new ArrayList<>();
        for (double temperature : temperatures) {

            if (temperature >= 36.0) {
                // RED color if temperature greater than or equal to 36 degree Celsius
                bgColor.add("rgba(255, 99, 132, 0.3)");
                borderColor.add("rgb(255, 99, 132)");
            } else if (temperature >= 30.0 && temperature < 36.0) {
                // Orange color if temperature is between 30 and 35.9 degree Celsius
                bgColor.add("rgba(255, 165, 0, 0.3)");
                borderColor.add("rgb(255, 165, 0)");
            } else if (temperature >= 25.0 && temperature < 30.0) {
                // Purple color if temperature is between 25 and 29.9 degree Celsius
                bgColor.add("rgba(153,102,255, 0.3)");
                borderColor.add("rgb(153,102,255)");
            } else if (temperature >= 4.0 && temperature < 25.0) {
                // Green color if temperature is between 4 and 24.9 degree Celsius
                bgColor.add("rgba(75, 192, 192, 0.3)");
                borderColor.add("rgb(75, 192, 192)");
            } else {
                // Blue color if temperature is less than 4 degree Celsius
                bgColor.add("rgba(54, 162, 235, 0.3)");
                borderColor.add("rgb(54, 162, 235)");
            }

            values.add(temperature);
        }
        barDataSet.setData(values);

        barDataSet.setBackgroundColor(bgColor);

        barDataSet.setBorderColor(borderColor);

        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        // Set labels for the bars
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < temperatures.length; i++) {
            String formattedDate = dateFormat.format(calendar.getTime());
            labels.add(formattedDate);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
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

    public void createLineModel(double[] windSpeeds) {
        lineModel = new LineChartModel();
        ChartData data = new ChartData();

        LineChartDataSet lineDataSet = new LineChartDataSet();
        lineDataSet.setLabel("Wind Speed in m/s");

        List<Object> values = new ArrayList<>();
        for (double windSpeed : windSpeeds) {
            values.add(windSpeed);
        }
        lineDataSet.setData(values);
        lineDataSet.setFill(false);
        lineDataSet.setBorderColor("rgb(102,187,85)");
        lineDataSet.setTension(0.1);
        data.addChartDataSet(lineDataSet);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        // Set labels for the lines
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < windSpeeds.length; i++) {
            String formattedDate = dateFormat.format(calendar.getTime());
            labels.add(formattedDate);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        data.setLabels(labels);
//        lineModel.setData(data);

        // Configure chart options
        LineChartOptions options = new LineChartOptions();
        options.setMaintainAspectRatio(false);
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Wind Speed Forecast");
        options.setTitle(title);
//
//        Title subtitle = new Title();
//        subtitle.setDisplay(true);
//        subtitle.setText("Line Chart Subtitle");
//        options.setSubtitle(subtitle);

        lineModel.setOptions(options);
        lineModel.setData(data);
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

    private double[] getLatLongFromJson(String json) {
        JSONObject obj = new JSONObject(json);
        JSONObject coord = obj.getJSONObject("coord");
        double latitude = coord.getDouble("lat");
        double longitude = coord.getDouble("lon");
        return new double[]{latitude, longitude};
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

    private double[] getWindSpeedFromForecast(String forecastJson, int days) {
        JSONObject obj = new JSONObject(forecastJson);
        JSONArray list = obj.getJSONArray("list");
        double[] windSpeeds = new double[days];
        for (int i = 0; i < days; i++) {
            int index = i * 8; // Assuming each day has 8 entries
            if (index < list.length()) {
                JSONObject dayData = list.getJSONObject(index);
                JSONObject wind = dayData.getJSONObject("wind");
                double windSpeed = wind.getDouble("speed");
                windSpeeds[i] = windSpeed; // Wind speed in m/s
            } else {
                windSpeeds[i] = 0; // Default wind speed or any other value
            }
        }
        return windSpeeds;
    }

}
