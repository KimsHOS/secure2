package com.example.faceauthentication.utils;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextureAnalysis {

    public TextureAnalysis() {
        // Initialize OpenCV
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Initialization Failed");
        } else {
            Log.d("OpenCV", "OpenCV Initialization Succeeded");
        }
    }

    public boolean analyzeTexture(Mat srcImage) {
        Mat complexImage = performDFT(srcImage);
        Mat mag = calculateMagnitude(complexImage);
        return checkHighFrequencyFeatures(mag);
    }

    private Mat performDFT(Mat srcImage) {
        // Convert to grayscale if not already
        if (srcImage.channels() > 1) {
            Imgproc.cvtColor(srcImage, srcImage, Imgproc.COLOR_BGR2GRAY);
        }
        srcImage.convertTo(srcImage, CvType.CV_32F); // Convert to 32F for DFT

        // Expand image to optimal DFT size
        Mat padded = new Mat();
        int m = Core.getOptimalDFTSize(srcImage.rows());
        int n = Core.getOptimalDFTSize(srcImage.cols());
        Core.copyMakeBorder(srcImage, padded, 0, m - srcImage.rows(), 0, n - srcImage.cols(), Core.BORDER_CONSTANT, Scalar.all(0));

        // Prepare planes for DFT
        List<Mat> planes = new ArrayList<>();
        planes.add(padded);
        planes.add(Mat.zeros(padded.size(), CvType.CV_32F));

        Mat complexImage = new Mat();
        Core.merge(planes, complexImage);
        Core.dft(complexImage, complexImage);
        return complexImage;
    }

    private Mat calculateMagnitude(Mat complexImage) {
        List<Mat> planes = new ArrayList<>();
        Core.split(complexImage, planes);

        Mat mag = new Mat();
        Core.magnitude(planes.get(0), planes.get(1), mag);

        // Switch to log scale for better visualization
        Core.add(mag, Scalar.all(1), mag);
        Core.log(mag, mag);

        // Normalize for visualization purposes (optional)
        Core.normalize(mag, mag, 0, 255, Core.NORM_MINMAX, CvType.CV_8U);
        return mag;
    }

    private boolean checkHighFrequencyFeatures(Mat mag) {
        // Define a threshold for density based on testing real vs. fake images
        double highFrequencyDensityThreshold = 70.0;

        // Sum high-frequency regions (outer areas)
        double highFrequencySum = 0;
        int rowStart = mag.rows() / 4;
        int rowEnd = 3 * mag.rows() / 4;
        int colStart = mag.cols() / 4;
        int colEnd = 3 * mag.cols() / 4;

        for (int i = 0; i < mag.rows(); i++) {
            for (int j = 0; j < mag.cols(); j++) {
                if (i < rowStart || i > rowEnd || j < colStart || j > colEnd) {
                    highFrequencySum += mag.get(i, j)[0];
                }
            }
        }

        // Calculate density
        double density = highFrequencySum / (mag.rows() * mag.cols());
        Log.d("TAG", "High Frequency Density (focused area): " + density);

        // Return true if high-frequency density exceeds threshold
        return density > highFrequencyDensityThreshold;
    }
}
