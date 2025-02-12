package edu.multimedia.utils;

import edu.multimedia.histograms.HistogramCreation;
import edu.multimedia.pojos.HistogramDataPojo;
import edu.multimedia.pojos.ResultObjectPojo;
import edu.multimedia.pojos.UserInputPojo.HISTOGRAM;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

public class SlidingWindowApproach {

    private static final double SIMILARITY_THRESHOLD = 0.45d;
    private static final int WINDOW_WIDTH = 80;
    private static final int WINDOW_HEIGHT = 60;
    private static final int STEP_SIZE = 20;

    public static ResultObjectPojo slidingWindowDetection(BufferedImage inputImage,
            BufferedImage objectImage) {

        ResultObjectPojo result = new ResultObjectPojo();

        List<HashMap<Double, HistogramDataPojo>> objectHistogramMap = HistogramCreation.createHistogramMap(
                objectImage, Boolean.TRUE);
        HashMap<Double, HistogramDataPojo> objectHistogramH = objectHistogramMap.get(
                HISTOGRAM.H.getTypeIndex());
        HashMap<Double, HistogramDataPojo> objectHistogramCr = objectHistogramMap.get(
                HISTOGRAM.Cr.getTypeIndex());
        HashMap<Double, HistogramDataPojo> objectHistogramCb = objectHistogramMap.get(
                HISTOGRAM.Cb.getTypeIndex());

        int inputWidth = inputImage.getWidth();
        int inputHeight = inputImage.getHeight();

        int windowWidth = WINDOW_WIDTH;
        int windowHeight = WINDOW_HEIGHT;
        int stepSize = STEP_SIZE;
        double maxSimilarity = 0.0d;
        int[] maxSimilarityBox = new int[4];

        for (int y = 0; y < inputHeight - windowHeight + 1; y += stepSize) {
            for (int x = 0; x < inputWidth - windowWidth + 1; x += stepSize) {
                if (x + stepSize <= inputWidth && y + stepSize <= inputHeight) {
                    BufferedImage windowRegion = extractWindow(inputImage, x, y, windowWidth,
                            windowHeight);

                    List<HashMap<Double, HistogramDataPojo>> inputHistogramMap = HistogramCreation.createHistogramMap(
                            windowRegion, Boolean.FALSE
                    );

                    HashMap<Double, HistogramDataPojo> inputHistogramH = inputHistogramMap.get(
                            HISTOGRAM.H.getTypeIndex());
                    HashMap<Double, HistogramDataPojo> inputHistogramCr = inputHistogramMap.get(
                            HISTOGRAM.Cr.getTypeIndex());
                    HashMap<Double, HistogramDataPojo> inputHistogramCb = inputHistogramMap.get(
                            HISTOGRAM.Cb.getTypeIndex());
                    //String inputDominantHue = HistogramProcessing.mapHueToColor(inputHistogramH);

                    double histSimilarityH = ComparisonUtils.cosineSimilarity(inputHistogramH,
                            objectHistogramH);
                    double histSimilarityCr = ComparisonUtils.cosineSimilarity(inputHistogramCr,
                            objectHistogramCr);
                    double histSimilarityCb = ComparisonUtils.cosineSimilarity(inputHistogramCb,
                            objectHistogramCb);

                    double histSimilarity =
                            (histSimilarityH + histSimilarityCr + histSimilarityCb) / 3;

                    //System.out.println("Current Similarity = " + histSimilarity);
                    result.setSimilarityThreshold(SIMILARITY_THRESHOLD);
                    if (histSimilarity > SIMILARITY_THRESHOLD) {
                        if (maxSimilarity < histSimilarity) {
                            maxSimilarity = histSimilarity;
                            maxSimilarityBox = new int[]{x, y, x + windowWidth, y + windowHeight};
                        }
                        //System.out.println("@@@ Object Detected at - " + x + ", " + y);
                        result.addMatchedCoordinates(
                                new int[]{x, y, x + windowWidth, y + windowHeight});
                        result.addMatchedCenterCoordinates(
                                new int[]{(x + x + windowWidth) / 2, (y + y + windowHeight) / 2});
                    }
                }
            }
        }

//        System.out.println("Max Similarity - " + maxSimilarity);
//        System.out.println("Max Similarity Box - " + Arrays.toString(maxSimilarityBox));

        result.setMaximumSimlarity(maxSimilarity);
        result.setMaxSimilarityBox(maxSimilarityBox);

        return result;
    }

    private static BufferedImage extractWindow(BufferedImage image, int x, int y, int width,
            int height) {
        return image.getSubimage(x, y, width, height);
    }

    public static int[] calculateDimensions(int totalPixels, int inpWidth, int inpHeight) {
        // Calculate the width by taking the square root of (totalPixels * aspectRatio)
        double aspectRatio = (double) inpWidth / inpHeight;
        int width = (int) Math.sqrt(totalPixels * aspectRatio);
        // Calculate the height by dividing totalPixels by the width
        int height = totalPixels / width;
        return new int[]{width, height};
    }

    public static int getWindowSize(int objectPixelSum) {
        if (objectPixelSum >= 30000) {
            return 200;
        } else if (objectPixelSum >= 25000) {
            return 180;
        } else if (objectPixelSum >= 20000) {
            return 160;
        } else if (objectPixelSum >= 15000) {
            return 140;
        } else if (objectPixelSum >= 10000) {
            return 120;
        } else {
            return 100;
        }
    }

}
