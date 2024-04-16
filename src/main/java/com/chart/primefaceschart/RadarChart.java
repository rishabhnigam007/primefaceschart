/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.chart.primefaceschart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.optionconfig.elements.Elements;
import org.primefaces.model.charts.optionconfig.elements.ElementsLine;
import org.primefaces.model.charts.radar.RadarChartDataSet;
import org.primefaces.model.charts.radar.RadarChartModel;
import org.primefaces.model.charts.radar.RadarChartOptions;

/**
 *
 * @author Rishabh
 */
@ManagedBean(name = "radarChart")
@RequestScoped
public class RadarChart implements Serializable {

    private RadarChartModel radarModel;

    public RadarChartModel getRadarModel() {
        return radarModel;
    }

    public void setRadarModel(RadarChartModel radarModel) {
        this.radarModel = radarModel;
    }

    @PostConstruct
    public void init() {
        createRadarModel();
    }

    public void createRadarModel() {
        radarModel = new RadarChartModel();
        ChartData data = new ChartData();

        RadarChartDataSet radarDataSet = new RadarChartDataSet();
        radarDataSet.setLabel("My First Dataset");
        radarDataSet.setFill(true);
        radarDataSet.setBackgroundColor("rgba(255, 99, 132, 0.2)");
        radarDataSet.setBorderColor("rgb(255, 99, 132)");
        radarDataSet.setPointBackgroundColor("rgb(255, 99, 132)");
        radarDataSet.setPointBorderColor("#fff");
        radarDataSet.setPointHoverBackgroundColor("#fff");
        radarDataSet.setPointHoverBorderColor("rgb(255, 99, 132)");
        List<Number> dataVal = new ArrayList<>();
        dataVal.add(65);
        dataVal.add(59);
        dataVal.add(90);
        dataVal.add(81);
        dataVal.add(56);
        dataVal.add(55);
        dataVal.add(40);
        radarDataSet.setData(dataVal);

        RadarChartDataSet radarDataSet2 = new RadarChartDataSet();
        radarDataSet2.setLabel("My Second Dataset");
        radarDataSet2.setFill(true);
        radarDataSet2.setBackgroundColor("rgba(54, 162, 235, 0.2)");
        radarDataSet2.setBorderColor("rgb(54, 162, 235)");
        radarDataSet2.setPointBackgroundColor("rgb(54, 162, 235)");
        radarDataSet2.setPointBorderColor("#fff");
        radarDataSet2.setPointHoverBackgroundColor("#fff");
        radarDataSet2.setPointHoverBorderColor("rgb(54, 162, 235)");
        List<Number> dataVal2 = new ArrayList<>();
        dataVal2.add(28);
        dataVal2.add(48);
        dataVal2.add(40);
        dataVal2.add(19);
        dataVal2.add(96);
        dataVal2.add(27);
        dataVal2.add(100);
        radarDataSet2.setData(dataVal2);

        data.addChartDataSet(radarDataSet);
        data.addChartDataSet(radarDataSet2);

        List<String> labels = new ArrayList<>();
        labels.add("Eating");
        labels.add("Drinking");
        labels.add("Sleeping");
        labels.add("Designing");
        labels.add("Coding");
        labels.add("Cycling");
        labels.add("Running");
        data.setLabels(labels);

        /* Options */
        RadarChartOptions options = new RadarChartOptions();
        Elements elements = new Elements();
        ElementsLine elementsLine = new ElementsLine();
        elementsLine.setTension(0);
        elementsLine.setBorderWidth(3);
        elements.setLine(elementsLine);
        options.setElements(elements);

        radarModel.setOptions(options);
        radarModel.setData(data);
    }

}
