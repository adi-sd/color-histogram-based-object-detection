package edu.multimedia.pojos;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class UserInputPojo {

    public static final int IMAGE_HEIGHT = 480;
    public static final int IMAGE_WIDTH = 640;
    public static final Color BACKGROUND_COLOR_255 = new Color(0, 255, 0);
    public static final Color BACKGROUND_COLOR_200 = new Color(0, 200, 0);
    public static final Color REPLACEMENT_COLOR = new Color(0, 0, 0, 0);

    public enum HISTOGRAM {
        H(0),
        Cr(1),
        Cb(2);

        private final int typeIndex;

        HISTOGRAM(int typeIndex) {
            this.typeIndex = typeIndex;
        }

        public int getTypeIndex() {
            return typeIndex;
        }

        public static HISTOGRAM fromValue(int value) {
            for (HISTOGRAM myEnum : HISTOGRAM.values()) {
                if (myEnum.typeIndex == value) {
                    return myEnum;
                }
            }
            throw new IllegalArgumentException("No enum constant associated with value " + value);
        }
    }

    private String inputImagePath;
    private ArrayList<String> objectImagePathList = new ArrayList<>();

    public UserInputPojo(String[] args) {

        System.out.println("\nGetting User Input...\n");

        // Get Image Path
        if (args.length >= 1) {
            System.out.println("Got Input RGB Image Path (1/2)");
            this.inputImagePath = args[0];
        } else {
            System.out.println("Couldn't get RGB Image Path!");
        }

        if (args.length > 1) {
            System.out.println("Got Input RGB Image Path (2/2)");
            String[] objectImagePaths = new String[args.length - 1];
            System.arraycopy(args, 1, objectImagePaths, 0, args.length - 1);
            this.objectImagePathList.addAll(Arrays.asList(objectImagePaths));
        } else {
            System.out.println("Couldn't get Object Image Paths!");
        }

    }

    public ArrayList<String> getObjectImagePathList() {
        return objectImagePathList;
    }

    public String getInputImagePath() {
        return inputImagePath;
    }

    public File getInputImageFile() {
        try {
            return new File(getInputImagePath());
        } catch (Exception e) {
            System.out.println("Exception while creating input file object - " + e);
            return null;
        }
    }

    public ArrayList<File> getObjectImageFileList() {
        try {
            ArrayList<File> files = new ArrayList<>();
            for (String objectPath : getObjectImagePathList()) {
                files.add(new File(objectPath));
            }
            return files;
        } catch (Exception e) {
            System.out.println("Exception while creating one of the object files - " + e);
            return null;
        }
    }

    public int getNumObjectImages() {
        return this.objectImagePathList.size();
    }

    public void printUserInput() {
        System.out.println("\nUserInputUtils {\n" +
                "  inputImagePath='" + inputImagePath + '\n' +
                "  objectImagePaths=" + Arrays.toString(objectImagePathList.toArray()) + '\n' +
                "}\n");
    }

}
