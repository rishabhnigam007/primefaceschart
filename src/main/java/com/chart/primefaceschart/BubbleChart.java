/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.chart.primefaceschart;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bubble.BubbleChartDataSet;
import org.primefaces.model.charts.bubble.BubbleChartModel;
import org.primefaces.model.charts.data.BubblePoint;

/**
 *
 * @author Rishabh
 */
@ManagedBean(name = "bubbleChart")
@RequestScoped
public class BubbleChart {

    private BubbleChartModel bubbleChartModel;

    public BubbleChartModel getBubbleChartModel() {
        return bubbleChartModel;
    }

    public void setBubbleChartModel(BubbleChartModel bubbleChartModel) {
        this.bubbleChartModel = bubbleChartModel;
    }

    @PostConstruct
    public void init() {
        createBubbleModel();
    }

    public void createBubbleModel() {
        bubbleChartModel = new BubbleChartModel();
        ChartData data = new ChartData();

        BubbleChartDataSet dataSet = new BubbleChartDataSet();
        List<BubblePoint> values = new ArrayList<>();
        values.add(new BubblePoint(20, 30, 15));
        values.add(new BubblePoint(40, 10, 10));
        values.add(new BubblePoint(27, 23, 15));
        dataSet.setData(values);
        dataSet.setBackgroundColor("rgb(255, 99, 132)");
        dataSet.setLabel("First Dataset");
        data.addChartDataSet(dataSet);
        bubbleChartModel.setData(data);
    }

}
