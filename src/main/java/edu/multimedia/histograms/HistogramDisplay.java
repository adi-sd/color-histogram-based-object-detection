package edu.multimedia.histograms;

import edu.multimedia.pojos.HistogramDataPojo;
import edu.multimedia.pojos.UserInputPojo.HISTOGRAM;
import edu.multimedia.image.ImageDisplay;
import java.util.Map;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class HistogramDisplay {

    public static void drawHistogram(Map<Double, HistogramDataPojo> histogramDataset,
            String frameTitle, HISTOGRAM histogramType) {
        // X - Axis : All distinct possible value of the Quantifier (Hue, Cr, Cb) in the image
        // Y - Axis : Number of pixels

        String quantifier = switch (histogramType) {
            case H -> "Hue from HSV Color Space";
            case Cr -> "Cr from YCbCr Color Space";
            case Cb -> "Cb from YCbCr Color Space";
        };
        String chartTitle = quantifier + " Vs Pixels Line Chart";

        // Create a dataset for the bar chart
        XYSeriesCollection dataset = getXYSeriesCollection(histogramDataset, quantifier);
        JFreeChart chart = ChartFactory.createXYLineChart(
                chartTitle,
                quantifier + " Value",
                "Pixel Count",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        displayChartOnFrame(chart, frameTitle, chartTitle);
    }

    private static void displayChartOnFrame(JFreeChart chart, String frameTitle,
            String chartTitle) {
        // Display the chart in a ChartPanel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1920, 1080));
        JFrame graphFrame = ImageDisplay.createDisplayFrame(
                frameTitle + " (" + chartTitle + ")");
        graphFrame.getContentPane().add(chartPanel);
        graphFrame.pack();
        graphFrame.setVisible(true);
    }

    private static XYSeriesCollection getXYSeriesCollection(
            Map<Double, HistogramDataPojo> histogramDataset, String quantifier) {
        XYSeries series = new XYSeries(quantifier);
        for (Map.Entry<Double, HistogramDataPojo> entry : histogramDataset.entrySet()) {
            Double key = entry.getKey();
            HistogramDataPojo value = entry.getValue();
            series.add(Math.floor(key), value.getPixelCount());
        }
        return new XYSeriesCollection(series);
    }

}
