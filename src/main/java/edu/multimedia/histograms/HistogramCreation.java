package edu.multimedia.histograms;

import edu.multimedia.image.ImageConversion;
import edu.multimedia.pojos.HistogramDataPojo;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistogramCreation {

    private static final int HUE_IDX = 0;
    private static final int Cr_IDX = 1;
    private static final int Cb_IDX = 0;
    // H range - 0 to 360
    private static final double HUE_VALUE_MIN = 0.0d;
    private static final double HUE_VALUE_MAX = 360.0d;
    // Cr range - 0 to 255 ; Cb range - 0 to 255
    private static final double CrCb_VALUE_MIN = 0.0d;
    private static final double CrCb_VALUE_MAX = 255.0d;

    public static List<HashMap<Double, HistogramDataPojo>> createHistogramMap(
            BufferedImage rgbImage, boolean ignoreBackground) {

        int height = rgbImage.getHeight();
        int width = rgbImage.getWidth();

        HashMap<Double, HistogramDataPojo> histogramDatasetH = initializeHistogramMap(
                HUE_VALUE_MIN, HUE_VALUE_MAX);
        HashMap<Double, HistogramDataPojo> histogramDatasetCr = initializeHistogramMap(
                CrCb_VALUE_MIN, CrCb_VALUE_MAX);
        HashMap<Double, HistogramDataPojo> histogramDatasetCb = initializeHistogramMap(
                CrCb_VALUE_MIN, CrCb_VALUE_MAX);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Current Pixel Coordinates
                int[] currentPixel = new int[]{x, y};

                // RGB to HSV Conversion
                double[] hsvArray = ImageConversion.getHSVArray(rgbImage.getRGB(x, y));
                double currentH = hsvArray[HUE_IDX];

                if (ignoreBackground) {
                    if (currentH != ImageConversion.GREEN_HSV_255[HUE_IDX]
                    ) {
                        createHistogramDatasetEntry(currentH, currentPixel, histogramDatasetH);
                    }
                } else {
                    createHistogramDatasetEntry(currentH, currentPixel, histogramDatasetH);
                }

                // RGB to YCrCb Conversion
                double[] yCrCbArray = ImageConversion.getYCrCbArray(rgbImage.getRGB(x, y));
                double currentCr = yCrCbArray[Cr_IDX];
                if (ignoreBackground) {
                    if (currentCr != ImageConversion.GREEN_YCrCb_255[Cr_IDX]
                    ) {
                        createHistogramDatasetEntry(currentCr, currentPixel, histogramDatasetCr);
                    }
                } else {
                    createHistogramDatasetEntry(currentCr, currentPixel, histogramDatasetCr);
                }

                double currentCb = yCrCbArray[Cb_IDX];
                if (ignoreBackground) {
                    if (currentCb != ImageConversion.GREEN_YCrCb_255[Cb_IDX]
                    ) {
                        createHistogramDatasetEntry(currentCb, currentPixel, histogramDatasetCb);
                    }
                } else {
                    createHistogramDatasetEntry(currentCb, currentPixel, histogramDatasetCb);
                }

            }
        }

        ArrayList<HashMap<Double, HistogramDataPojo>> result = new ArrayList<>();
        result.add(histogramDatasetH);
        result.add(histogramDatasetCr);
        result.add(histogramDatasetCb);

        return result;
    }

    private static HashMap<Double, HistogramDataPojo> initializeHistogramMap(double minVal,
            double maxVal) {
        HashMap<Double, HistogramDataPojo> histogram = new HashMap<>();
        for (double key = minVal; key <= maxVal; key++) {
            histogram.put(key, new HistogramDataPojo(key));
        }
        return histogram;
    }

    private static void createHistogramDatasetEntry(double currentQuantifier, int[] currentPixel,
            HashMap<Double, HistogramDataPojo> histogram) {
        double bucket = Math.floor(currentQuantifier);
        HistogramDataPojo currentPojo = histogram.get(bucket);
        if (currentPojo != null) {
            currentPojo.setQuantifier(bucket);
            currentPojo.addCoordinate(currentPixel);
        }
    }

    public static void printDataSet(Map<Double, HistogramDataPojo> histogramDataset) {
        for (Map.Entry<Double, HistogramDataPojo> entry : histogramDataset.entrySet()) {
            Double key = entry.getKey();
            HistogramDataPojo value = entry.getValue();
            System.out.println("key: " + key + ", Value: " + value);
        }
    }


    public static ArrayList<Double> normalizeHistogram(Map<Double, HistogramDataPojo> histogram) {
        int pixelSum = HistogramProcessing.getTotalPixelsFromHistogram(histogram);
        ArrayList<Double> normalizedHistogram = new ArrayList<>();
        for (Map.Entry<Double, HistogramDataPojo> entry : histogram.entrySet()) {
            HistogramDataPojo value = entry.getValue();
            normalizedHistogram.add(((double) value.getPixelCount()) / pixelSum);
        }
        return normalizedHistogram;
    }

    public static ArrayList<Double> normalizeHistogram2(Map<Double, HistogramDataPojo> histogram) {
        ArrayList<Double> resultList = new ArrayList<>();
        ArrayList<Integer> pixelCountHistogram = HistogramProcessing.getPixelCountHistogram(
                histogram);
        int sum = 0;
        for (Integer integer : pixelCountHistogram) {
            sum += integer;
        }
        //System.out.println("Normalize Sum - " + sum);
        for (Integer integer : pixelCountHistogram) {
            resultList.add((double) integer / sum);
        }
        return resultList;
    }

    public static void printNormalizedHistogram(ArrayList<Double> normalizedHistogram) {
        long index = 0L;
        for (Double element : normalizedHistogram) {
            System.out.println(index++ + " - " + element);
        }
    }


}
