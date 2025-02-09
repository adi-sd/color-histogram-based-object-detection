# Object Detection Using Color Histograms

## Overview
This project is an implementation of an **object detection system** using **color histograms** for identifying and localizing objects in images. It was developed as part of **CS 576 - Assignment 2** and leverages **OpenCV, Java, and Maven** to analyze images, extract color histograms, and compare them for object recognition.

## Problem Statement
The objective is to detect and localize objects within an input image by comparing their color histograms to a set of reference object images. The project assumes:
- Objects are placed against a green chroma background.
- The input image contains one or more objects, possibly from the reference set.
- Object detection is achieved using histogram matching in different color spaces (e.g., **RGB, YUV, HSV**).

## Features
- **Histogram-Based Object Detection**: Uses color histograms to identify objects.
- **Multiple Color Space Support**: Supports **RGB, YUV, and HSV** for color-based detection.
- **Bounding Box Visualization**: Highlights detected objects using bounding boxes.
- **Efficient Processing**: Implements histogram matching for fast comparisons.
- **Command Line Execution**: Accepts input image and object library as arguments.

## Technologies Used
- **Programming Language**: Java
- **Image Processing Library**: OpenCV
- **Build Tool**: Maven

## Setup and Installation
### Prerequisites
Ensure you have the following installed:
- **Java JDK (8 or later)**
- **Maven**
- **OpenCV for Java**

### Build and Run
1. Clone the repository:
   ```sh
   git clone <repo-url>
   cd <repo-folder>
   ```
2. Build the project using Maven:
   ```sh
   mvn clean install
   ```
3. Run the program with the required arguments:
   ```sh
   java -jar target/ObjectDetection.jar InputImage.rgb object1.rgb object2.rgb ... objectN.rgb
   ```

## Input and Output
### Input
- **Input Image (`InputImage.rgb`)**: An image containing objects to detect.
- **Reference Objects (`objectX.rgb`)**: A set of known objects against a green background.

### Output
- The program outputs the processed image, where detected objects are outlined with bounding boxes.

## Implementation Details
### Steps:
1. **Preprocess Images**: Convert images to the chosen color space.
2. **Compute Histograms**: Extract histograms for the input and reference images.
3. **Match Histograms**: Compare input image histograms with reference histograms.
4. **Localize Objects**: Identify object regions and highlight them.

### Color Space Choices:
- **RGB**: Standard color comparison.
- **YUV**: Ignores luminance and focuses on chromaticity.
- **HSV**: Uses hue for color-based matching.

## Example Usage
```sh
java -jar target/ObjectDetection.jar input.rgb obj1.rgb obj2.rgb obj3.rgb
```

## Future Improvements
- Support for **scale and rotation invariance**.
- Integration with **deep learning** for improved accuracy.
- Optimization for **real-time processing**.

## Author
Developed as part of **CS 576 - Fall 2023** under **Professor Parag Havaldar**.

## License
This project is for educational purposes only and should not be used for commercial applications.

