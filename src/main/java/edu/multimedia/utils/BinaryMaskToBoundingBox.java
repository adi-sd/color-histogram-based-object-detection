package edu.multimedia.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BinaryMaskToBoundingBox {

    public static void getBoundingBoxesfromBinaryMask(BufferedImage originalImage,
            BufferedImage binaryMask) throws IOException {

        // Perform connected component labeling
//        int label = 1;
//        for (int y = 0; y < binaryMask.getHeight(); y++) {
//            for (int x = 0; x < binaryMask.getWidth(); x++) {
//                if (binaryMask.getRGB(x, y) == Color.BLACK.getRGB()) {
//                    continue;  // Skip background pixels
//                }
//                // If this pixel is part of an object, perform DFS to label the connected component
//                labelConnectedComponent(binaryMask, x, y, label);
//                label++;
//            }
//        }

        // Create bounding boxes and draw them on the original image
        Graphics2D g = originalImage.createGraphics();
        g.setColor(Color.RED);  // You can choose any color for the bounding boxes
        for (int i = 1; i < 2; i++) {
            Rectangle boundingBox = calculateBoundingBox(binaryMask, i);
            g.drawRect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        }
        g.dispose();
    }

    // Perform DFS to label the connected component
    private static void labelConnectedComponent(BufferedImage binaryMask, int x, int y, int label) {
        if (x < 0 || y < 0 || x >= binaryMask.getWidth() || y >= binaryMask.getHeight() ||
                binaryMask.getRGB(x, y) != Color.BLACK.getRGB()) {
            return;
        }
        binaryMask.setRGB(x, y, label);
        labelConnectedComponent(binaryMask, x + 1, y, label);
        labelConnectedComponent(binaryMask, x - 1, y, label);
        labelConnectedComponent(binaryMask, x, y + 1, label);
        labelConnectedComponent(binaryMask, x, y - 1, label);
    }

    // Calculate the bounding box for a connected component
    private static Rectangle calculateBoundingBox(BufferedImage binaryMask, int label) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (int y = 0; y < binaryMask.getHeight(); y++) {
            for (int x = 0; x < binaryMask.getWidth(); x++) {
                if (binaryMask.getRGB(x, y) == label) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }
}
