package com.products.backend.dto.product;

import com.products.backend.classes.metrics.Metric;

import java.util.ArrayList;
import java.util.List;

public class MetricsResponse {
    private List<Metric> metrics;

    public  MetricsResponse(){
        this.metrics = new ArrayList<Metric>();
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public void addMetric(Metric metric){
        this.metrics.add(metric);
    }
}
