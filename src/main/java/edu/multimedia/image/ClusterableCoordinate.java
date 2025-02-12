package edu.multimedia.image;

import org.apache.commons.math3.ml.clustering.Clusterable;

public class ClusterableCoordinate implements Clusterable {

    private final int[] coordinates;

    public ClusterableCoordinate(int[] coordinates) {
        this.coordinates = coordinates;
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    @Override
    public double[] getPoint() {
        double[] point = new double[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            point[i] = coordinates[i];
        }
        return point;
    }
}
