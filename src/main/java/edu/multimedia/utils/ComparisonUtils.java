package edu.multimedia.utils;

import edu.multimedia.histograms.HistogramCreation;
import edu.multimedia.histograms.HistogramProcessing;
import edu.multimedia.pojos.ComputedDataPojo;
import edu.multimedia.pojos.HistogramDataPojo;
import edu.multimedia.pojos.UserInputPojo.HISTOGRAM;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class ComparisonUtils {

    public static double compareHistogramsIntersection(ArrayList<Double> inputHist,
            ArrayList<Double> objectHist) {
        double intersection = 0.0d;
        for (int i = 0; i < objectHist.size(); i++) {
            double minValue = Math.min(inputHist.get(i), objectHist.get(i));
            intersection += minValue;
        }
        return intersection;
    }

    public static double compareHistogramsBCD(ArrayList<Double> inputHist,
            ArrayList<Double> objectHist) {
        double term1 = 1 / (Math.sqrt(
                getAverage(inputHist) * getAverage(objectHist) * Math.pow(objectHist.size(), 2)));
        double term2 = 0.0d;
        for (int i = 0; i < objectHist.size(); i++) {
            term2 += Math.sqrt(inputHist.get(i) * objectHist.get(i));
        }
        return Math.sqrt(1 - term1 * term2);
    }

    public static double compareHistogramsChiSqr(ArrayList<Double> inputHist,
            ArrayList<Double> objectHist) {
        double distance = 0.0d;
        for (int i = 0; i < inputHist.size(); i++) {
            if (inputHist.get(i) != 0.0d) {
                distance += Math.pow((inputHist.get(i) - objectHist.get(i)), 2) / inputHist.get(i);
            }
        }
        return distance;
    }

    public static double compareHistogramsCorrelation(HashMap<Double, HistogramDataPojo> hist1,
            HashMap<Double, HistogramDataPojo> hist2) {
        ArrayList<Double> hist1Pixels = HistogramCreation.normalizeHistogram(hist1);
        ArrayList<Double> hist2Pixels = HistogramCreation.normalizeHistogram(hist2);
        double term1 = 0.0d;
        double term2 = 0.0d;
        double term3 = 0.0d;
        double inputAvg = getAverage(hist1Pixels);
        double objectAvg = getAverage(hist2Pixels);
        for (int i = 0; i < hist2Pixels.size(); i++) {
            term1 += (hist1Pixels.get(i) - inputAvg) * (hist2Pixels.get(i) - objectAvg);
            term2 += Math.pow((hist1Pixels.get(i) - inputAvg), 2);
            term3 += Math.pow((hist2Pixels.get(i) - objectAvg), 2);
        }
        return (term1 / Math.sqrt(term2 * term3));
    }

    public static double getAverage(ArrayList<Double> list) {
        return (getSum(list) / list.size());
    }

    public static double getSum(ArrayList<Double> list) {
        double sum = 0.0d;
        for (Double element : list) {
            sum += element;
        }
        return sum;
    }

    public static double calculateHistogramSimilarity(HashMap<Double, HistogramDataPojo> hist1,
            HashMap<Double, HistogramDataPojo> hist2) {
        double similarity = 0.0;
        ArrayList<Double> hist1Pixels = HistogramCreation.normalizeHistogram(hist1);
        ArrayList<Double> hist2Pixels = HistogramCreation.normalizeHistogram(hist2);
        for (int i = 0; i < hist2.size(); i++) {
            similarity += Math.sqrt(hist1Pixels.get(i) * hist2Pixels.get(i));
        }
        return similarity;
    }

    public static double cosineSimilarity(HashMap<Double, HistogramDataPojo> hist1,
            HashMap<Double, HistogramDataPojo> hist2) {
        ArrayList<Double> hist1Pixels = HistogramCreation.normalizeHistogram(hist1);
        ArrayList<Double> hist2Pixels = HistogramCreation.normalizeHistogram(hist2);
        double[] vector1 = HistogramProcessing.convertArrayListToDoubleArray(hist1Pixels);
        double[] vector2 = HistogramProcessing.convertArrayListToDoubleArray(hist2Pixels);
        RealVector v1 = new ArrayRealVector(vector1);
        RealVector v2 = new ArrayRealVector(vector2);
        double dotProduct = v1.dotProduct(v2);
        double norm1 = v1.getNorm();
        double norm2 = v2.getNorm();
        return dotProduct / (norm1 * norm2);
    }

    public static ArrayList<double[]> generalComparison(ComputedDataPojo data) {
        ArrayList<double[]> result = new ArrayList<>();
        int objectNumbers = data.getNumberOfObjectImages();
        System.out.println("\nComparing input and " + objectNumbers + " object files...");
        for (int i = 0; i < objectNumbers; i++) {
            double currentSimilarityH = cosineSimilarity(data.getInputHistogram(HISTOGRAM.H),
                    data.getObjectHistogram(i, HISTOGRAM.H));
            result.add(new double[]{(currentSimilarityH), i});
        }
        return result;
    }

    public static int compareInputAndObjects2(ComputedDataPojo data) {
        int objectNumbers = data.getNumberOfObjectImages();

        System.out.println("\nComparing input and " + objectNumbers);

        double maxSimilarity = Double.MIN_VALUE;
        int maxSimilarityIndex = -1;
        Double[] cosineResults = new Double[objectNumbers];
        HISTOGRAM type = HISTOGRAM.H;

        HashMap<Double, HistogramDataPojo> inputHistogram = data.getInputHistogram(HISTOGRAM.H);
        String color = HistogramProcessing.mapHueToColor(inputHistogram);

        if (color.equals("red") || color.equals("magenta")) {
            type = HISTOGRAM.Cb;
        } else if (color.equals("blue") || color.equals("cyan")) {
            type = HISTOGRAM.Cr;
        }

        for (int i = 0; i < objectNumbers; i++) {
            System.out.println(
                    "Dominant Color in input - " + color + "; Histogram Type - " + type);
            double currentSimilarity = calculateHistogramSimilarity(data.getInputHistogram(type),
                    data.getObjectHistogram(i, type));
            if (currentSimilarity > maxSimilarity) {
                maxSimilarity = currentSimilarity;
                maxSimilarityIndex = i;
            }
            cosineResults[i] = currentSimilarity;
        }
        System.out.println("Cosine - " + Arrays.toString(cosineResults));
        System.out.println("Max Similarity - " + maxSimilarity + " @ For Object Image - "
                + maxSimilarityIndex);
        return maxSimilarityIndex;
    }

}
