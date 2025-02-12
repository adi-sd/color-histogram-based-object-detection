package edu.multimedia.pojos;

import java.util.ArrayList;
import java.util.Arrays;

public class HistogramDataPojo {

    private double quantifier;
    private final ArrayList<int[]> pixelArray = new ArrayList<>();

    public HistogramDataPojo(double quantifier, int[] coordinateValue) {
        this.quantifier = quantifier;
        addCoordinate(coordinateValue);
    }

    public HistogramDataPojo(double quantifier) {
        this.quantifier = quantifier;
    }

    public double getQuantifier() {
        return quantifier;
    }

    public void setQuantifier(double quantifier) {
        this.quantifier = quantifier;
    }

    public int getPixelCount() {
        return this.pixelArray.size();
    }

    public ArrayList<int[]> getPixelArray() {
        return pixelArray;
    }

    public void addCoordinate(int[] coordinateValue) {
        this.pixelArray.add(coordinateValue);
    }

    public void printHistogramData() {
        System.out.println(
                "\nHistogramDataPojo {\n" + "  quantifier=" + quantifier + '\n' + "  pixelCount="
                        + getPixelCount() + '\n' + "  pixelArray=" + printPixelArrays(pixelArray)
                        + '\n' + "}\n");
    }

    @Override
    public String toString() {
        return "\nHistogramDataPojo {\n" + "  quantifier=" + quantifier + '\n' + "  pixelCount="
                + getPixelCount() + '\n' + "  pixelArray=" + printPixelArrays(pixelArray) + '\n'
                + "}\n";
    }

    private String printPixelArrays(ArrayList<int[]> pixelArray) {
        StringBuilder arrayStr = new StringBuilder("[ ");
        for (int[] coordinates : pixelArray) {
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
