package edu.multimedia.image;

import edu.multimedia.pojos.ResultObjectPojo;
import edu.multimedia.pojos.UserInputPojo;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

public class ImageDisplay {

    public static BufferedImage createBufferedImageObject() {
        return new BufferedImage(UserInputPojo.IMAGE_WIDTH, UserInputPojo.IMAGE_HEIGHT,
                BufferedImage.TYPE_INT_RGB);
    }

    public static JFrame createDisplayFrame(String title) {
        JFrame displayFrame = new JFrame(title);
        displayFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        displayFrame.setResizable(true);
        displayFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Custom logic for closing the frame
                displayFrame.dispose(); // Dispose the frame
            }
        });
        return displayFrame;
    }

    public static BufferedImage readRGBImage(File imageFile) {
        try {
            System.out.println("Reading RGB image - " + imageFile.getName());
            BufferedImage bufferedImageObject = ImageDisplay.createBufferedImageObject();

            int width = bufferedImageObject.getWidth();
            int height = bufferedImageObject.getHeight();

            int frameLength = width * height * 3;

            RandomAccessFile randomAccessFile = new RandomAccessFile(imageFile, "r");
            randomAccessFile.seek(0);

            byte[] bytes = new byte[(int) frameLength];

            randomAccessFile.read(bytes);

            // Combining Alpha, R, G, B values for each pixel in hex format
            int ind = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // byte a = 0;
                    byte r = bytes[ind];
                    byte g = bytes[ind + height * width];
                    byte b = bytes[ind + height * width * 2];

                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    // int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                    bufferedImageObject.setRGB(x, y, pix);
                    ind++;
                }
            }

            randomAccessFile.close();
            return bufferedImageObject;
        } catch (Exception e) {
            System.out.println("Exception while reading the RGB image - " + e);
            return null;
        }
    }

    public static void showImage(String frameTitle, BufferedImage currentBufferedImage) {

        JFrame displayFrame = createDisplayFrame(frameTitle);

        GridBagLayout gLayout = new GridBagLayout();
        displayFrame.getContentPane().setLayout(gLayout);

        JLabel lbIm1 = new JLabel(new ImageIcon(currentBufferedImage));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        displayFrame.getContentPane().add(lbIm1, c);
        displayFrame.pack();
        displayFrame.setVisible(true);
    }

    public static BufferedImage removeBackground(BufferedImage image, Color targetColor,
            Color replacementColor) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color pixelColor = new Color(image.getRGB(x, y));

                // Check if the pixel color is similar to the target color
                if (isSimilarColor(pixelColor, targetColor, 1)) {
                    // Replace with a transparent pixel
                    image.setRGB(x, y, replacementColor.getRGB());
                }
            }
        }
        return image;
    }

    public static boolean isSimilarColor(Color c1, Color c2, int threshold) {
        int rDiff = Math.abs(c1.getRed() - c2.getRed());
        int gDiff = Math.abs(c1.getGreen() - c2.getGreen());
        int bDiff = Math.abs(c1.getBlue() - c2.getBlue());
        return rDiff + gDiff + bDiff <= threshold;
    }

    public static void renderMultiple(BufferedImage inputImage,
            ArrayList<ResultObjectPojo> resultList) {
        for (ResultObjectPojo resultObjectPojo : resultList) {
            ArrayList<int[]> clustered = clusteringTheMidpoints(resultList.size(),
                    resultObjectPojo.getMatchedCenterCoordinates());
            clustered.forEach(
                    box -> addSimpleBoxAndName(inputImage, resultObjectPojo.getObjectImageName(),
                            box));
        }
    }


    public static void addSimpleBoxAndName(BufferedImage inputImage, String objectName,
            int[] coordinates) {
        int topLeftX = coordinates[0];  // Replace with your top-left X coordinate
        int topLeftY = coordinates[1];  // Replace with your top-left Y coordinate
        int bottomRightX = coordinates[2];  // Replace with your bottom-right X coordinate
        int bottomRightY = coordinates[3];

        Graphics2D g2d = inputImage.createGraphics();

        g2d.setColor(Color.RED);  // You can change the color
        g2d.setStroke(new BasicStroke(2));  // You can change the line thickness

        int width = bottomRightX - topLeftX;
        int height = bottomRightY - topLeftY;

        g2d.drawRect(topLeftX, topLeftY, width, height);

        Font font = new Font("Arial", Font.BOLD, 14);
        g2d.setFont(font);
        g2d.setColor(Color.BLUE);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(objectName);
        int textHeight = fm.getHeight();
        int textX = topLeftX + (width - textWidth) / 2;
        int textY = bottomRightY - 10;

        g2d.drawString(objectName, textX, textY);

        g2d.dispose();
    }


    public static void addBoundingBoxImage(BufferedImage inputImage,
            ResultObjectPojo finalResult) {

        int[] coordinates = new int[4];
        //int[] coordinates = averageAndScaleTheBox(finalResult.getMatchedCoordinates());
        int numCoords = finalResult.getNumberOfMatchedCoordinates();
        coordinates = averageAndScaleTheBox(finalResult.getMatchedCoordinates(),
                finalResult.getNumberOfMatchedCoordinates(), 1.4);

        int topLeftX = coordinates[0];  // Replace with your top-left X coordinate
        int topLeftY = coordinates[1];  // Replace with your top-left Y coordinate
        int bottomRightX = coordinates[2];  // Replace with your bottom-right X coordinate
        int bottomRightY = coordinates[3]; // Replace with your bottom-right Y coordinate

        Graphics2D g2d = inputImage.createGraphics();

        g2d.setColor(Color.RED);  // You can change the color
        g2d.setStroke(new BasicStroke(2));  // You can change the line thickness

        int width = bottomRightX - topLeftX;
        int height = bottomRightY - topLeftY;

        g2d.drawRect(topLeftX, topLeftY, width, height);

        Font font = new Font("Arial", Font.BOLD, 24);
        g2d.setFont(font);
        g2d.setColor(Color.BLUE);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(finalResult.getObjectImageName());
        int textHeight = fm.getHeight();
        int textX = topLeftX + (width - textWidth) / 2;
        int textY = bottomRightY - 10;

        g2d.drawString(finalResult.getObjectImageName(), textX, textY);

        g2d.dispose();
    }

    public static int[] averageAndScaleTheBox(ArrayList<int[]> resultCoordinates,
            int numberOfMatchedBoxes, double scalingFactor) {
        int totalX1 = 0;
        int totalY1 = 0;
        int totalX2 = 0;
        int totalY2 = 0;

        // Calculate the sum of coordinates
        for (int[] coordinates : resultCoordinates) {
            totalX1 += coordinates[0];
            totalY1 += coordinates[1];
            totalX2 += coordinates[2];
            totalY2 += coordinates[3];
        }

        // Calculate the average coordinates
        int avgX1 = totalX1 / resultCoordinates.size();
        int avgY1 = totalY1 / resultCoordinates.size();
        int avgX2 = totalX2 / resultCoordinates.size();
        int avgY2 = totalY2 / resultCoordinates.size();

        int scaledX1 = avgX1 - (int) (scalingFactor * (avgX2 - avgX1));
        int scaledY1 = avgY1 - (int) (scalingFactor * (avgY2 - avgY1));
        int scaledX2 = avgX2 + (int) (scalingFactor * (avgX2 - avgX1));
        int scaledY2 = avgY2 + (int) (scalingFactor * (avgY2 - avgY1));

        return new int[]{scaledX1, scaledY1, scaledX2, scaledY2};
    }

    public static int[] aggregateBox(ArrayList<int[]> resultCoordinates) {
        int sumX = 0;
        int sumY = 0;
        for (int[] point : resultCoordinates) {
            sumX += point[0];
            sumY += point[1];
        }
        int centerX = sumX / resultCoordinates.size();
        int centerY = sumY / resultCoordinates.size();

        // Step 2: Calculate the half-width and half-height of the bounding box
        int halfWidth = 0;
        int halfHeight = 0;
        for (int[] point : resultCoordinates) {
            int deltaX = Math.abs(centerX - point[0]);
            int deltaY = Math.abs(centerY - point[1]);
            halfWidth = Math.max(halfWidth, deltaX);
            halfHeight = Math.max(halfHeight, deltaY);
        }

        // Calculate top-left and bottom-right coordinates
        int topLeftX = centerX - halfWidth;
        int topLeftY = centerY - halfHeight;
        int bottomRightX = centerX + halfWidth;
        int bottomRightY = centerY + halfHeight;

        return new int[]{topLeftX, topLeftY, bottomRightX, bottomRightY};
    }

    public static int[] scaleTheBox(int[] coordinates, int numberOfMatchedBoxes) {

        int topLeftX = coordinates[0];
        int topLeftY = coordinates[1];
        int bottomRightX = coordinates[2];
        int bottomRightY = coordinates[3];

        int height = bottomRightY - topLeftY;
        int width = bottomRightX - topLeftX;

        double scaleFactor = (double) numberOfMatchedBoxes / 100;

        height = (int) (height * scaleFactor);
        width = (int) (width * scaleFactor);

        int scaledX1 = topLeftX - (width / 2);
        int scaledY1 = topLeftY - (height / 2);
        int scaledX2 = bottomRightX + (width / 2);
        int scaledY2 = bottomRightY + (height / 2);

        return new int[]{scaledX1, scaledY1, scaledX2, scaledY2};
    }

    public static ArrayList<int[]> clusteringTheMidpoints(int k, ArrayList<int[]> midCoordinates) {

        ArrayList<int[]> result = new ArrayList<>();

        ArrayList<ClusterableCoordinate> coordinates = new ArrayList<>();
        for (int[] point : midCoordinates) {
            coordinates.add(new ClusterableCoordinate(point));
        }

        Clusterer<ClusterableCoordinate> clusterer = new KMeansPlusPlusClusterer<>(k, -1,
                new EuclideanDistance());
        List<Cluster<ClusterableCoordinate>> clusters = (List<Cluster<ClusterableCoordinate>>) clusterer.cluster(
                coordinates);

        // Create bounding boxes for each cluster
        for (Cluster<ClusterableCoordinate> cluster : clusters) {
            // Calculate the bounding box
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;

            for (ClusterableCoordinate coordinate : cluster.getPoints()) {
                int[] point = coordinate.getCoordinates();

                int x = point[0];
                int y = point[1];

                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }

            result.add(new int[]{minX, minY, maxX, maxY});

        }

        return result;
    }

}
