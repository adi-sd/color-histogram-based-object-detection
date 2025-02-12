package edu.multimedia.pojos;

import edu.multimedia.histograms.HistogramDisplay;
import edu.multimedia.pojos.UserInputPojo.HISTOGRAM;
import edu.multimedia.histograms.HistogramCreation;
import edu.multimedia.image.ImageDisplay;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComputedDataPojo {

    private static ComputedDataPojo instance;
    private static final String INPUT_IMAGE = "Input Image";
    private static final String OBJECT_IMAGE = "Object Image";

    // Buffered Images
    private ComputedImageAndHistograms forInputImage = null;
    // Histograms
    private List<ComputedImageAndHistograms> forObjectImages = new ArrayList<>();

    private ComputedDataPojo(UserInputPojo currentUserInput) {
        BufferedRGBImagePojo rgbImageObject = new BufferedRGBImagePojo(currentUserInput);
        forInputImage = new ComputedImageAndHistograms(rgbImageObject.getInputBufferedImageRGB(),
                rgbImageObject.getInputImageName(),
                Boolean.FALSE);
        forObjectImages = ComputedImageAndHistograms.computeForList(
                rgbImageObject.getObjectBufferedImagesRGB(), rgbImageObject.getObjectImageNames());
    }

    public static ComputedDataPojo getInstance(UserInputPojo currentUserInput) {
        if (instance == null) {
            instance = new ComputedDataPojo(currentUserInput);
        }
        return instance;
    }

    public HashMap<Double, HistogramDataPojo> getInputHistogram(
            HISTOGRAM histogramType) {
        return switch (histogramType) {
            case H -> this.forInputImage.getHistogramH();
            case Cr -> this.forInputImage.getHistogramCr();
            case Cb -> this.forInputImage.getHistogramCb();
        };
    }

    public HashMap<Double, HistogramDataPojo> getObjectHistogram(int index,
            HISTOGRAM histogramType) {
        return switch (histogramType) {
            case H -> this.forObjectImages.get(index).getHistogramH();
            case Cr -> this.forObjectImages.get(index).getHistogramCr();
            case Cb -> this.forObjectImages.get(index).getHistogramCb();
        };
    }

    public ArrayList<HashMap<Double, HistogramDataPojo>> getObjectHistogramList(
            HISTOGRAM histogramType) {
        ArrayList<HashMap<Double, HistogramDataPojo>> resultList = new ArrayList<>();
        for (ComputedImageAndHistograms object : this.forObjectImages) {
            switch (histogramType) {
                case H -> resultList.add(object.getHistogramH());
                case Cr -> resultList.add(object.getHistogramCr());
                case Cb -> resultList.add(object.getHistogramCb());
            }
        }
        return resultList;
    }

    public ArrayList<Double> getNormalInputHistogram(HISTOGRAM histogramType) {
        return switch (histogramType) {
            case H -> this.forInputImage.getNormalizedH();
            case Cr -> this.forInputImage.getNormalizedCr();
            case Cb -> this.forInputImage.getNormalizedCb();
        };
    }

    public ArrayList<Double> getNormalObjectHistogram(int index,
            HISTOGRAM histogramType) {
        return switch (histogramType) {
            case H -> this.forObjectImages.get(index).getNormalizedH();
            case Cr -> this.forObjectImages.get(index).getNormalizedCr();
            case Cb -> this.forObjectImages.get(index).getNormalizedCb();
        };
    }

    public ArrayList<ArrayList<Double>> getNormalObjectHistogramList(
            HISTOGRAM histogramType) {
        ArrayList<ArrayList<Double>> resultList = new ArrayList<>();
        for (ComputedImageAndHistograms object : this.forObjectImages) {
            switch (histogramType) {
                case H -> resultList.add(object.getNormalizedH());
                case Cr -> resultList.add(object.getNormalizedCr());
                case Cb -> resultList.add(object.getNormalizedCb());
            }
        }
        return resultList;
    }

    // Get Number of Object images
    public int getNumberOfObjectImages() {
        return this.forObjectImages.size();
    }

    // Draw Histogram Functions
    public void drawInputHistogram(HISTOGRAM histogramType) {
        HistogramDisplay.drawHistogram(this.getInputHistogram(histogramType),
                "Input : " + this.getInputImageName(),
                histogramType);
    }

    public void drawObjectHistogram(int index, HISTOGRAM histogramType) {
        HistogramDisplay.drawHistogram(this.getObjectHistogram(index, histogramType),
                "Object : " + this.getObjectImageName(index),
                histogramType);
    }

    // Get Buffered Images
    public BufferedImage getInputBufferedImage() {
        return forInputImage.getImage();
    }

    public BufferedImage getObjectBufferedImage(int index) {
        return forObjectImages.get(index).getImage();
    }

    public String getInputImageName() {
        return this.forInputImage.getImageName();
    }

    public String getObjectImageName(int index) {
        return this.forObjectImages.get(index).getImageName();
    }

    // Show Image Functions

    public void displayInputImage() {
        ImageDisplay.showImage(INPUT_IMAGE + " (RGB)", this.forInputImage.getImage());
    }

    public void displayObjectImage(int index) {
        ImageDisplay.showImage(OBJECT_IMAGE + " (RGB)",
                this.forObjectImages.get(index).getImage());
    }

    // Class to store Hue, Cr and CB histograms for an image
    private static class ComputedImageAndHistograms {

        final BufferedImage image;
        final String imageName;
        final HashMap<Double, HistogramDataPojo> histogramH;
        final HashMap<Double, HistogramDataPojo> histogramCr;
        final HashMap<Double, HistogramDataPojo> histogramCb;
        final ArrayList<Double> normalizedH;
        final ArrayList<Double> normalizedCr;
        final ArrayList<Double> normalizedCb;

        public ComputedImageAndHistograms(BufferedImage image, String imageName,
                boolean ignoreBackground) {
            this.image = image;
            this.imageName = imageName;
            ArrayList<HashMap<Double, HistogramDataPojo>> datasets =
                    (ArrayList<HashMap<Double, HistogramDataPojo>>)
                            HistogramCreation.createHistogramMap(image, ignoreBackground);
            this.histogramH = datasets.get(0);
            this.histogramCr = datasets.get(1);
            this.histogramCb = datasets.get(2);
            this.normalizedH = HistogramCreation.normalizeHistogram2(this.histogramH);
            this.normalizedCr = HistogramCreation.normalizeHistogram2(this.histogramCr);
            this.normalizedCb = HistogramCreation.normalizeHistogram2(this.histogramCb);
        }

        private static List<ComputedImageAndHistograms> computeForList(
                List<BufferedImage> images, List<String> imageNameList) {
            List<ComputedImageAndHistograms> imageAndHistogramsList = new ArrayList<>();
            int index = 0;
            for (BufferedImage image : images) {
                imageAndHistogramsList.add(new ComputedImageAndHistograms(image,
                        imageNameList.get(index), Boolean.TRUE));
                index++;
            }
            return imageAndHistogramsList;
        }

        private BufferedImage getImage() {
            return image;
        }

        private String getImageName() {
            return imageName;
        }

        private HashMap<Double, HistogramDataPojo> getHistogramH() {
            return histogramH;
        }

        private HashMap<Double, HistogramDataPojo> getHistogramCr() {
            return histogramCr;
        }

        private HashMap<Double, HistogramDataPojo> getHistogramCb() {
            return histogramCb;
        }

        public ArrayList<Double> getNormalizedH() {
            return normalizedH;
        }

        public ArrayList<Double> getNormalizedCr() {
            return normalizedCr;
        }

        public ArrayList<Double> getNormalizedCb() {
            return normalizedCb;
        }
    }

}
