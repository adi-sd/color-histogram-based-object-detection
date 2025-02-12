package edu.multimedia.image;

import edu.multimedia.pojos.UserInputPojo;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ImageConversion {


    public static final double[] GREEN_HSV_255 = getHSVArray(
            UserInputPojo.BACKGROUND_COLOR_255.getRGB());
    public static final double[] GREEN_HSV_200 = getHSVArray(
            UserInputPojo.BACKGROUND_COLOR_200.getRGB());
    public static final double[] GREEN_YCrCb_255 = getYCrCbArray(
            UserInputPojo.BACKGROUND_COLOR_255.getRGB());
    public static final double[] GREEN_YCrCb_200 = getYCrCbArray(
            UserInputPojo.BACKGROUND_COLOR_255.getRGB());
    public static final double[] NO_PIXEL_HSV = getHSVArray(
            UserInputPojo.REPLACEMENT_COLOR.getRGB());
    public static final double[] NO_PIXEL_YCrCb = getYCrCbArray(
            UserInputPojo.REPLACEMENT_COLOR.getRGB());

    // RGB Pixel Util
    public static int[] getRGBArray(int rgbValue) {
        int r = (rgbValue >> 16) & 0xFF;
        int g = (rgbValue >> 8) & 0xFF;
        int b = rgbValue & 0xFF;
        return new int[]{r, g, b};
    }

    private static double[] getNormalizedRGBArray(int[] rgbArray) {
        return new double[]{rgbArray[0] / 255.0d, rgbArray[1] / 255.0d, rgbArray[2] / 255.0d};
    }

    // RGB to HSV conversion function
    public static double[] getHSVArray(int rgbValue) {
        int[] rgbArray = getRGBArray(rgbValue);
        // Normalize RGB values to the range [0, 1]
        double[] normalRGBArray = getNormalizedRGBArray(rgbArray);
        return rgbToHsv(normalRGBArray[0], normalRGBArray[1], normalRGBArray[2]);
    }

    public static float[] getHSVArray2(int rgbValue) {
        int[] rgbArray = getRGBArray(rgbValue);
        return Color.RGBtoHSB(rgbArray[0], rgbArray[1], rgbArray[0], null);
    }

    private static double[] rgbToHsv(double red, double green, double blue) {
        double max = Math.max(Math.max(red, green), blue);
        double min = Math.min(Math.min(red, green), blue);
        double delta = max - min;

        double hue, saturation, value;

        // Value calculation
        value = max;

        // Saturation calculation
        if (max == 0.0d) {
            saturation = 0.0d;
        } else {
            saturation = delta / max;
        }

        // Hue calculation
        if (saturation == 0.0d) {
            hue = 0.0d;
        } else {
            if (max == red) {
                hue = (green - blue) / delta;
                hue %= 6.0d;
            } else if (max == green) {
                hue = 2.0d + (blue - red) / delta;
            } else {
                hue = 4.0d + (red - green) / delta;
            }
            hue *= 60.0d;
        }

        if (hue < 0.0d) {
            hue += 360.0d;
        }

        return new double[]{hue, saturation, value};
    }

    // RGB to YUV conversion function
    public static double[] getYUVArray(int rgbValue) {
        int[] rgbArray = getRGBArray(rgbValue);
        return rgbToYuv(rgbArray[0], rgbArray[1], rgbArray[2]);
    }

    private static double[] rgbToYuv(double red, double green, double blue) {
        double y = 0.299d * red + 0.587d * green + 0.114d * blue;
        double u = -0.147d * red - 0.289d * green + 0.436d * blue;
        double v = 0.615d * red - 0.515d * green - 0.312d * blue;
        y = Math.max(0.0d, Math.min(255d, y + 128d));
        u = Math.max(0.0d, Math.min(255d, u + 128d));
        v = Math.max(0.0d, Math.min(255d, v + 128d));
        return new double[]{y, u, v};
    }


    // RGB to YCrCb conversion function
    public static double[] getYCrCbArray(int rgbValue) {
        int[] rgbArray = getRGBArray(rgbValue);
        return rgbToYCrCb(rgbArray[0], rgbArray[1], rgbArray[2]);
    }

    private static double[] rgbToYCrCb(double red, double green, double blue) {
        double y = 0.299d * red + 0.587d * green + 0.114d * blue;
        double cr = 0.713d * (red - y) + 128d;
        double cb = 0.564d * (blue - y) + 128d;
        return new double[]{y, cr, cb};
    }

    public static void printConvertedArrays(Color color) {
        System.out.println(
                "HSV for RGB color - " + Arrays.toString(getRGBArray(color.getRGB())) + "\n"
                        + Arrays.toString(getHSVArray(color.getRGB())));
        System.out.println(
                "YCrCb for RGB color - " + Arrays.toString(getRGBArray(color.getRGB())) + "\n"
                        + Arrays.toString(getYCrCbArray(color.getRGB())));
    }

    public static BufferedImage convertToHSV(BufferedImage inputImage) {
        // Get the width and height of the image
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();

        // Create a new BufferedImage to store the hue values
        BufferedImage hueImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Loop through the pixels in the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get the RGB color of the pixel
                int rgb = inputImage.getRGB(x, y);

                // Extract the hue value
                float[] hsv = new float[3];
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                Color.RGBtoHSB(red, green, blue, hsv);
                float hue = hsv[0];

                // Create a new RGB color with the hue value
                int hueRGB = Color.HSBtoRGB(hue, 1.0f, 1.0f);

                // Set the pixel in the hueImage with the new hue value
                hueImage.setRGB(x, y, hueRGB);
            }
        }
        return hueImage;
    }
}
