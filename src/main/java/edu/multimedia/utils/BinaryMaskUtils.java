package edu.multimedia.utils;

import edu.multimedia.histograms.HistogramProcessing;
import edu.multimedia.image.ImageConversion;
import edu.multimedia.pojos.HistogramDataPojo;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BinaryMaskUtils {

    public static BufferedImage getBinaryMaskedInput(BufferedImage inputImage,
            Map<Double, HistogramDataPojo> objectHistogram) {
        double minHueReturn = HistogramProcessing.getMinHue(objectHistogram);
        double maxHueReturn = HistogramProcessing.getMaxHue(objectHistogram);
        //ImageDisplay.showImage("Binary Mask Image", binaryMask);
        return createBinaryMask(inputImage,
                Math.min(minHueReturn, maxHueReturn),
                Math.max(minHueReturn, maxHueReturn));
    }

    private static BufferedImage createBinaryMask(BufferedImage inputImage,
            double hueLowerThreshold,
            double hueUpperThreshold) {

//        System.out.println("Hue lower - " + hueLowerThreshold);
//        System.out.println("Hue upper - " + hueUpperThreshold);

        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage binaryMask = new BufferedImage(width, height,
                java.awt.image.BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double[] hsvArray = ImageConversion.getHSVArray(inputImage.getRGB(x, y));
                if (hsvArray[0] >= hueLowerThreshold
                        && hsvArray[0] <= hueUpperThreshold) {
                    binaryMask.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    binaryMask.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        return binaryMask;
    }

    public static BufferedImage collectAllThePixels(BufferedImage inputImage,
            HashMap<Double, HistogramDataPojo> inputHistogram,
            HashMap<Double, HistogramDataPojo> objectHistogram) {

        int width = inputImage.getWidth();
        int height = inputImage.getHeight();

        objectHistogram = HistogramProcessing.cleanObjectHistogramToTarget(objectHistogram, 100);
        BufferedImage binaryMask = new BufferedImage(width, height,
                java.awt.image.BufferedImage.TYPE_INT_RGB);

        for (Map.Entry<Double, HistogramDataPojo> entry : objectHistogram.entrySet()) {
            Double percentageHue = entry.getKey();
            HistogramDataPojo value = entry.getValue();
            Double hueValue = value.getQuantifier();
            System.out.println("Processing HueValue - " + hueValue);
            ArrayList<int[]> pixelList = inputHistogram.get(hueValue).getPixelArray();
            for (int[] pixel : pixelList) {
                int x = pixel[0];
                int y = pixel[1];
                int pixelColor = inputImage.getRGB(x, y);
                binaryMask.setRGB(x, y, pixelColor);
            }
        }
        return binaryMask;
    }

    public static boolean checkMaskEauality(ArrayList<BufferedImage> binaryMaskList) {
        if (binaryMaskList.size() == 1) {
            return true;
        }
        BufferedImage referenceMask = binaryMaskList.get(0);

        // Iterate through the other masks and compare each pixel
        for (int i = 1; i < binaryMaskList.size(); i++) {
            BufferedImage currentMask = binaryMaskList.get(i);
            if (!areMasksSimilar(referenceMask, currentMask, 10.0d)) {
                return false;
            }
        }
        return true;
    }

    private static boolean areMasksSimilar(BufferedImage mask1, BufferedImage mask2,
            double allowedErrorPercent) {
        if (mask1.getWidth() != mask2.getWidth() || mask1.getHeight() != mask2.getHeight()) {
            return false; // Masks have different dimensions
        }
        int totolPixels = mask1.getHeight() * mask1.getWidth();
        int errorPixel = 0;
        for (int y = 0; y < mask1.getHeight(); y++) {
            for (int x = 0; x < mask1.getWidth(); x++) {
                if (mask1.getRGB(x, y) != mask2.getRGB(x, y)) {
                    errorPixel += 1;
                }
            }
        }
        return (double) (100 * errorPixel) / totolPixels <= allowedErrorPercent;
    }

    public static int[] getBoundingBoxForBinaryMask(BufferedImage inputImage,
            BufferedImage binaryMask) {
        int width = binaryMask.getWidth();
        int height = binaryMask.getHeight();
        int minX = width;
        int minY = height;
        int maxX = -1;
        int maxY = -1;
        // Iterate through the binary mask pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = binaryMask.getRGB(x, y);
                // Check if the pixel is black (foreground)
                if (pixel == 0xFF000000) { // Assuming black pixels represent the foreground
                    // Update bounding box coordinates
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        // Check if there was no foreground in the binary mask
        if (minX > maxX || minY > maxY) {
            return null; // No bounding box found
        }
        // Return the bounding box coordinates as an array
        return new int[]{minX, minY, maxX, maxY};
    }
}
