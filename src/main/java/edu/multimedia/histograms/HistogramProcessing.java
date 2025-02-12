package edu.multimedia.histograms;

import edu.multimedia.pojos.HistogramDataPojo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HistogramProcessing {

    private static final int THRESHOLD_CLUSTERS = 0;

    public static void printMinMaxKey(Map<Double, HistogramDataPojo> histogram) {
        double maxKey = Collections.max(histogram.keySet());
        double minKey = Collections.min(histogram.keySet());
        System.out.println("Maximum Key: " + maxKey);
        System.out.println("Minimum Key: " + minKey);
    }

    public static double getBackgroundQuantifier(Map<Double, HistogramDataPojo> objectHistogram) {
        HistogramDataPojo maxElement = null;
        int maxPixelCount = Integer.MIN_VALUE;
        Collection<HistogramDataPojo> dataPoints = objectHistogram.values();
        for (HistogramDataPojo dataPoint : dataPoints) {
            int pixelCount = dataPoint.getPixelCount();
            if (pixelCount > maxPixelCount) {
                maxPixelCount = pixelCount;
                maxElement = dataPoint;
            }
        }
        assert maxElement != null;
        return maxElement.getQuantifier();
    }

    public static int getTotalPixelsFromHistogram(Map<Double, HistogramDataPojo> histogram) {
        int totalPixels = 0;
        for (Map.Entry<Double, HistogramDataPojo> entry : histogram.entrySet()) {
            HistogramDataPojo value = entry.getValue();
            totalPixels += value.getPixelCount();
        }
        return totalPixels;
    }

    public static double getQuantifierSum(Map<Double, HistogramDataPojo> histogram) {
        double sum = 0.0d;
        for (Map.Entry<Double, HistogramDataPojo> entry : histogram.entrySet()) {
            HistogramDataPojo value = entry.getValue();
            sum += value.getQuantifier();
        }
        return sum;
    }

    public static ArrayList<Integer> getPixelCountHistogram(
            Map<Double, HistogramDataPojo> histogram) {
        ArrayList<Integer> pixelCountHistogram = new ArrayList<>();
        for (Map.Entry<Double, HistogramDataPojo> entry : histogram.entrySet()) {
            HistogramDataPojo value = entry.getValue();
            pixelCountHistogram.add(value.getPixelCount());
        }
        return pixelCountHistogram;
    }

    public static double getMinHue(Map<Double, HistogramDataPojo> histogram) {
        ArrayList<Integer> pixelCountHistogram = getPixelCountHistogram(histogram);
        //System.out.println(Arrays.toString(pixelCountHistogram.toArray()));
        //System.out.println(pixelCountHistogram.size());
        int minValue = Integer.MAX_VALUE; // Initialize with a high value
        int minIndex = -1;
        for (int i = 0; i < pixelCountHistogram.size(); i++) {
            int value = pixelCountHistogram.get(i);
            if (value > THRESHOLD_CLUSTERS && value < minValue) {
                minValue = value;
                minIndex = i;
            }
        }
        //System.out.println("Min Value - " + minValue + " @ " + minIndex);
        if (minIndex != -1) {
            //System.out.println("returning - " + minIndex);
            return minIndex;
        } else {
            System.out.println("No minimum value (ignoring 0s) found in the ArrayList.");
            return 0.0d;
        }
    }

    public static double getMaxHue(Map<Double, HistogramDataPojo> histogram) {
        ArrayList<Integer> pixelCountHistogram = getPixelCountHistogram(histogram);
        //System.out.println(Arrays.toString(pixelCountHistogram.toArray()));
        //System.out.println(pixelCountHistogram.size());
        int maxValue = Integer.MIN_VALUE; // Initialize with a high value
        int maxIndex = -1;
        for (int i = 0; i < pixelCountHistogram.size(); i++) {
            int value = pixelCountHistogram.get(i);
            if (value > THRESHOLD_CLUSTERS && value > maxValue) {
                maxValue = value;
                maxIndex = i;
            }
        }
        //System.out.println("Max Value - " + maxValue + " @ " + maxIndex);
        if (maxIndex != -1) {
            //System.out.println("returning - " + maxIndex);
            return maxIndex;
        } else {
            System.out.println("No Maximum value (ignoring 0s) found in the ArrayList.");
            return 0.0d;
        }
    }

    public static double getSecondMaxHue(Map<Double, HistogramDataPojo> histogram) {
        ArrayList<Integer> pixelCountHistogram = getPixelCountHistogram(histogram);
        //System.out.println(Arrays.toString(pixelCountHistogram.toArray()));
        //System.out.println(pixelCountHistogram.size());
        int maxValue = Integer.MIN_VALUE; // Initialize with a high value
        int secondMaxValue = Integer.MIN_VALUE;
        int maxIndex = -1;
        int secondMaxIndex = -1;
        for (int i = 0; i < pixelCountHistogram.size(); i++) {
            int value = pixelCountHistogram.get(i);
            if (value > THRESHOLD_CLUSTERS && value > maxValue) {
                secondMaxValue = maxValue;
                secondMaxIndex = maxIndex;
                maxValue = value;
                maxIndex = i;
            } else if (value > secondMaxValue) {
                secondMaxValue = value;
                secondMaxIndex = i;
            }
        }
        System.out.println("Second Max Value - " + secondMaxIndex + " @ " + secondMaxIndex);
        if (secondMaxIndex != -1) {
            System.out.println("returning - " + secondMaxIndex);
            return secondMaxIndex;
        } else {
            System.out.println("No Maximum value (ignoring 0s) found in the ArrayList.");
            return 0.0d;
        }
    }

    private static HashMap<Double, HistogramDataPojo> getCombinedMap(
            Map<Double, HistogramDataPojo> histogram) {
        HashMap<Double, HistogramDataPojo> combinedMap = new HashMap<>();
        ArrayList<Double> normalizedHistogram = HistogramCreation.normalizeHistogram2(histogram);
        int index = 0;
        for (Map.Entry<Double, HistogramDataPojo> entry : histogram.entrySet()) {
            if (entry.getKey() != 0.0d) {
                combinedMap.put(normalizedHistogram.get(index++) * 100, entry.getValue());
            }
        }
        return combinedMap;
    }

    private static TreeMap<Double, HistogramDataPojo> cleanCombinedMap(
            HashMap<Double, HistogramDataPojo> combinedMap) {
        TreeMap<Double, HistogramDataPojo> orderedMap = new TreeMap<>(
                Collections.reverseOrder());
        orderedMap.putAll(combinedMap);
        return orderedMap;
    }

    private static HashMap<Double, HistogramDataPojo> removeUnnecessaryElements(
            TreeMap<Double, HistogramDataPojo> combinedMap, double targetSum) {
        HashMap<Double, HistogramDataPojo> reducedMap = new HashMap<>();
        double currentSum = 0.0d;
        for (Map.Entry<Double, HistogramDataPojo> entry : combinedMap.entrySet()) {
            Double key = entry.getKey();
            if (currentSum + key >= targetSum) {
                break;
            } else {
                currentSum += key;
                reducedMap.put(entry.getKey(), entry.getValue());
            }
        }
        return reducedMap;
    }

    public static HashMap<Double, HistogramDataPojo> cleanObjectHistogramToTarget(
            HashMap<Double, HistogramDataPojo> objHistogram, double targetSum) {
        HashMap<Double, HistogramDataPojo> combinedMap = HistogramProcessing.getCombinedMap(
                objHistogram);
        TreeMap<Double, HistogramDataPojo> cleanCombinedMap = HistogramProcessing.cleanCombinedMap(
                combinedMap);
        return HistogramProcessing.removeUnnecessaryElements(cleanCombinedMap, targetSum);
    }

    public static String mapHueToColor(Map<Double, HistogramDataPojo> histogram) {
        StringBuilder dominantHue = new StringBuilder();
        double currentHue = getMaxHue(histogram);
        if (currentHue >= 0 && currentHue < 30) {
            dominantHue.append("Red");
        } else if (currentHue >= 30 && currentHue < 90) {
            dominantHue.append("Yellow");
        } else if (currentHue >= 90 && currentHue < 150) {
            dominantHue.append("Green");
        } else if (currentHue >= 150 && currentHue < 210) {
            dominantHue.append("Cyan");
        } else if (currentHue >= 210 && currentHue < 270) {
            dominantHue.append("Blue");
        } else if (currentHue >= 270 && currentHue < 330) {
            dominantHue.append("Magenta");
        } else {
            dominantHue.append("Red"); // Handle the wrap-around case
        }
        return dominantHue.toString().toLowerCase();
    }

    public static String mapHueToColor2(Map<Double, HistogramDataPojo> histogram) {

        StringBuilder dominantHue = new StringBuilder();
        double maxHue = getMaxHue(histogram);
        double secondMaxHue = getSecondMaxHue(histogram);
        double[] hues = {
                maxHue, secondMaxHue
        };

        for (int i = 0; i < 2; i++) {
            double currentHue = hues[i];
            if (currentHue >= 0 && maxHue < 30) {
                dominantHue.append("Red");
            } else if (currentHue >= 30 && currentHue < 90) {
                dominantHue.append("Yellow");
            } else if (currentHue >= 90 && currentHue < 150) {
                dominantHue.append("Green");
            } else if (currentHue >= 150 && maxHue < 210) {
                dominantHue.append("Cyan");
            } else if (currentHue >= 210 && currentHue < 270) {
                dominantHue.append("Blue");
            } else if (currentHue >= 270 && currentHue < 330) {
                dominantHue.append("Magenta");
            } else {
                dominantHue.append("Red"); // Handle the wrap-around case
            }
            if (!dominantHue.isEmpty()) {
                dominantHue.append(", ");
            }
        }
        return dominantHue.toString();
    }

    public static HashMap<Double, HistogramDataPojo> removeZeroObjects(
            HashMap<Double, HistogramDataPojo> histogram) {
        HashMap<Double, HistogramDataPojo> newHistogram = new HashMap<>();
        for (Map.Entry<Double, HistogramDataPojo> entry : histogram.entrySet()) {
            Double key = entry.getKey();
            HistogramDataPojo value = entry.getValue();
            if (value.getPixelCount() == 0) {
                // Nothing
            } else {
                newHistogram.put(key, value);
            }
        }
        return newHistogram;
    }

    public static double[] convertArrayListToDoubleArray(ArrayList<Double> arrayList) {
        double[] doubleArray = new double[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            doubleArray[i] = arrayList.get(i);
        }
        return doubleArray;
    }

}
