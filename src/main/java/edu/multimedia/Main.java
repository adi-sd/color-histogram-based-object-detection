package edu.multimedia;

import edu.multimedia.histograms.HistogramProcessing;
import edu.multimedia.image.ImageDisplay;
import edu.multimedia.pojos.ComputedDataPojo;
import edu.multimedia.pojos.HistogramDataPojo;
import edu.multimedia.pojos.ResultObjectPojo;
import edu.multimedia.pojos.UserInputPojo;
import edu.multimedia.pojos.UserInputPojo.HISTOGRAM;
import edu.multimedia.utils.BinaryMaskUtils;
import edu.multimedia.utils.ComparisonUtils;
import edu.multimedia.utils.SlidingWindowApproach;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Main {

    static HISTOGRAM defaultHType = HISTOGRAM.H;
    final static double GENERAL_COMPARISON_THRESHOLD = 0.14d;

    public static void main(String[] args) {
        System.out.println("Object Detection Using Color Histogram");

        try {
            UserInputPojo currentUserInput = new UserInputPojo(args);
            currentUserInput.printUserInput();

            ComputedDataPojo data = ComputedDataPojo.getInstance(currentUserInput);

            System.out.println("\nInput Pixel Sum = " +
                    HistogramProcessing.getTotalPixelsFromHistogram(
                            data.getInputHistogram(HISTOGRAM.H)));
            for (int i = 0; i < data.getNumberOfObjectImages(); i++) {
                System.out.println("Object Pixel Sum = " +
                        HistogramProcessing.getTotalPixelsFromHistogram(
                                data.getObjectHistogram(i, HISTOGRAM.H)));
            }

            BufferedImage inputImage = data.getInputBufferedImage();
            HashMap<Double, HistogramDataPojo> inputHistogram = data.getInputHistogram(
                    defaultHType);
            String inputDomHue = HistogramProcessing.mapHueToColor(inputHistogram);
            ArrayList<ResultObjectPojo> resultObjectList = new ArrayList<>();

            if (inputDomHue.equals("red") || inputDomHue.equals("magenta")) {
                defaultHType = HISTOGRAM.Cb;
            }

            ArrayList<double[]> generalComparisonResult = ComparisonUtils.generalComparison(data);
            generalComparisonResult.sort((o1, o2) -> Double.compare(o2[0], o1[0]));

            generalComparisonResult.removeIf(
                    result -> result[0] < GENERAL_COMPARISON_THRESHOLD);

            // Print the reduced ArrayList
            for (double[] arr : generalComparisonResult) {
                System.out.println(Arrays.toString(arr));
            }

            System.out.println("Started Comparison -- ");
            for (double[] doubles : generalComparisonResult) {

                int currentObjectFileIdx = (int) doubles[1];
                String objectImageFileName = data.getObjectImageName(currentObjectFileIdx);

                System.out.println(
                        "For Object File - " + objectImageFileName
                );

                ResultObjectPojo currentResult = null;
                currentResult = SlidingWindowApproach.slidingWindowDetection(
                        inputImage, data.getObjectBufferedImage(currentObjectFileIdx)
                );

                currentResult.setObjectImageIndex(currentObjectFileIdx);
                currentResult.setObjectImageName(objectImageFileName);

                //int[] coordinates = ImageDisplay.averageAndScaleTheBox(resultCoordinates);
                System.out.println("Result Object = \n" + currentResult);
                resultObjectList.add(currentResult);
            }

            chooseTheFinalResult(resultObjectList);

            if (resultObjectList.size() > 1) {
                System.out.println("Remaining Objects - \n");
                ArrayList<BufferedImage> binaryMaskList = new ArrayList<>();
                for (ResultObjectPojo res : resultObjectList) {
                    System.out.println(res.getObjectImageIndex());
                    System.out.println(res.getObjectImageName());
                    System.out.println(res.getNumberOfMatchedCoordinates());
                    System.out.println(res.getMaximumSimlarity());
                    System.out.println(res);
                    binaryMaskList.add(BinaryMaskUtils.getBinaryMaskedInput(inputImage,
                            data.getObjectHistogram(res.getObjectImageIndex(), HISTOGRAM.H)));
                }

                if (BinaryMaskUtils.checkMaskEauality(binaryMaskList)) {
                    System.out.println("@@@Equal Masks!");
                    int[] box = BinaryMaskUtils.getBoundingBoxForBinaryMask(binaryMaskList.get(0),
                            inputImage);
                    assert box != null;
                    int maxIndex = 0;
                    double maxResult = 0.0d;
                    for (int i = 0; i < resultObjectList.size(); i++) {
                        double currentResult = ComparisonUtils.compareHistogramsCorrelation(
                                data.getInputHistogram(HISTOGRAM.Cb), data.getObjectHistogram(
                                        resultObjectList.get(i).getObjectImageIndex(),
                                        HISTOGRAM.Cb));
                        if (currentResult > maxResult) {
                            maxResult = currentResult;
                            maxIndex = i;
                        }
                    }
                    System.out.println("Matched With - " + resultObjectList.get(maxIndex)
                            .getObjectImageName());
                    ImageDisplay.addBoundingBoxImage(inputImage, resultObjectList.get(maxIndex));
                    ImageDisplay.showImage("Final Image", inputImage);
                } else {
                    System.out.println("No!!!");
                    ImageDisplay.renderMultiple(inputImage, resultObjectList);
                    ImageDisplay.showImage("Final Image", inputImage);
                }
                //ImageDisplay.showImage("Original Image", inputImage);
            } else if (resultObjectList.size() == 1) {
                System.out.println("Only One Match Found Creating Box for it!");
                ImageDisplay.addBoundingBoxImage(inputImage, resultObjectList.get(0));
                ImageDisplay.showImage("Final Image", inputImage);
            } else {
                System.out.println("Nothing found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void chooseTheFinalResult(ArrayList<ResultObjectPojo> resultObjectList) {

        int[] emptyArray = {0, 0, 0, 0};
        resultObjectList.removeIf(
                resultObject -> (resultObject.getMaximumSimilarity() == 0.0d || compareArrays(
                        resultObject.getMaxSimilarityBox(), emptyArray)));
        resultObjectList.removeIf(
                resultObject -> resultObject.getNumberOfMatchedCoordinates() <= 9);
    }

    public static boolean compareArrays(int[] array1, int[] array2) {
        // Check if both arrays have exactly 4 elements
        if (array1.length != 4 || array2.length != 4) {
            return false; // Arrays must have exactly 4 elements
        }
        for (int i = 0; i < 4; i++) {
            if (array1[i] != array2[i]) {
                return false; // Elements at index i are not equal
            }
        }
        return true;
    }

}
