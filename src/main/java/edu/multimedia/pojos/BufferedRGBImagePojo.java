package edu.multimedia.pojos;

import edu.multimedia.image.ImageDisplay;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BufferedRGBImagePojo {

    // RGB Buffered Images : Red, Green, Blue Intensities
    private final BufferedImage inputBufferedImageRGB;
    private final String inputImageName;
    private final List<BufferedImage> objectBufferedImagesRGB = new ArrayList<>();
    private final List<String> objectImageNames = new ArrayList<>();

    public BufferedRGBImagePojo(UserInputPojo currentUserInput) {
        // RGB Buffered Images
        System.out.println("Creating RGB Buffered Images for current input...");

        File inputImageFile = currentUserInput.getInputImageFile();
        this.inputBufferedImageRGB = ImageDisplay.readRGBImage(inputImageFile);
        this.inputImageName = inputImageFile.getName();

        ArrayList<File> objectImageFileList = currentUserInput.getObjectImageFileList();
        for (File objectFile : objectImageFileList) {
            BufferedImage objectImage = ImageDisplay.readRGBImage(objectFile);
            assert objectImage != null;
//            this.objectBufferedImagesRGB.add(
//                    ImageDisplay.removeBackground(objectImage, UserInputPojo.BACKGROUND_COLOR_255,
//                            UserInputPojo.REPLACEMENT_COLOR));
            this.objectBufferedImagesRGB.add(objectImage);
            this.objectImageNames.add(objectFile.getName());
        }
    }

    // RGB Buffered Images
    public BufferedImage getInputBufferedImageRGB() {
        return inputBufferedImageRGB;
    }

    public List<BufferedImage> getObjectBufferedImagesRGB() {
        return objectBufferedImagesRGB;
    }

    public String getInputImageName() {
        return inputImageName;
    }

    public List<String> getObjectImageNames() {
        return objectImageNames;
    }

    //    public void showInputImageRGB() {
//        ImageDisplayUtils.showImage("Input Image (RGB)", this.getInputBufferedImageRGB());
//    }
//
//    public void showObjectImageRGB(int idx) {
//        ImageDisplayUtils.showImage("Object Image (RGB) - " + idx,
//                this.getObjectBufferedImagesRGB().get(idx));
//    }

}
