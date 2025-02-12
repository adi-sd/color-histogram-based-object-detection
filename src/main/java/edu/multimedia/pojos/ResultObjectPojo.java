package edu.multimedia.pojos;

import java.util.ArrayList;
import java.util.Arrays;

public class ResultObjectPojo {

    private double maximumSimilarity = 0.0d;
    private int[] maxSimilarityBox = new int[4];
    private double similarityThreshold = 0.0d;
    private final ArrayList<int[]> matchedCoordinates = new ArrayList<>();
    private final ArrayList<int[]> matchedCenterCoordinates = new ArrayList<>();
    private int objectImageIndex = -1;
    private String objectImageName = "";

    public ResultObjectPojo() {
    }

    public double getMaximumSimlarity() {
        return maximumSimilarity;
    }

    public void setMaximumSimlarity(double maximumSimlarity) {
        this.maximumSimilarity = maximumSimlarity;
    }

    public int[] getMaxSimilarityBox() {
        return maxSimilarityBox;
    }

    public void setMaxSimilarityBox(int[] maxSimilarityBox) {
        this.maxSimilarityBox = maxSimilarityBox;
    }

    public double getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(double similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }

    public void addMatchedCoordinates(int[] matchedCoordinate) {
        this.matchedCoordinates.add(matchedCoordinate);
    }

    public ArrayList<int[]> getMatchedCoordinates() {
        return matchedCoordinates;
    }

    public void addMatchedCenterCoordinates(int[] matchedCenterCoordinate) {
        this.matchedCenterCoordinates.add(matchedCenterCoordinate);
    }

    public ArrayList<int[]> getMatchedCenterCoordinates() {
        return matchedCenterCoordinates;
    }

    public double getMaximumSimilarity() {
        return maximumSimilarity;
    }

    public void setMaximumSimilarity(double maximumSimilarity) {
        this.maximumSimilarity = maximumSimilarity;
    }

    public int getNumberOfMatchedCoordinates() {
        return this.matchedCoordinates.size();
    }

    public int getObjectImageIndex() {
        return objectImageIndex;
    }

    public void setObjectImageIndex(int objectImageIndex) {
        this.objectImageIndex = objectImageIndex;
    }

    public String getObjectImageName() {
        return objectImageName;
    }

    public void setObjectImageName(String objectImageName) {
        this.objectImageName = objectImageName;
    }

    @Override
    public String toString() {
        return "ResultObjectPojo { " +
                "maximumSimilarity=" + maximumSimilarity +
                ", maxSimilarityBox=" + Arrays.toString(maxSimilarityBox) +
                ", similarityThreshold=" + similarityThreshold +
                ", matchedCoordinates=" + printCoordinateArray(matchedCoordinates) +
                ", numberOfMatchedCoordinates=" + matchedCoordinates.size() +
                ", matchedCenterCoordinates=" + printCoordinateArray(matchedCenterCoordinates) +
                ", objectImageIndex=" + objectImageIndex +
                ", objectImageName='" + objectImageName + '\'' +
                " }";
    }

    private String printCoordinateArray(ArrayList<int[]> matchedCoordinates) {
        StringBuilder arrayStr = new StringBuilder("[ ");
        for (int[] coordinates : matchedCoordinates) {
            arrayStr.append(Arrays.toString(coordinates));
            arrayStr.append(", ");
        }
        if (arrayStr.length() > 2) {
            arrayStr.delete(arrayStr.length() - 2, arrayStr.length());
        } else {
            arrayStr.deleteCharAt(arrayStr.length() - 1);
        }
        arrayStr.append(" ]");
        return arrayStr.toString();
    }

}
