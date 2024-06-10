package jp.jaxa.iss.kibo.rpc.sampleapk;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import org.opencv.core.Mat;

import android.graphics.BitmapFactory;

import org.opencv.android.Utils;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.DetectorParameters;
import org.opencv.aruco.Dictionary;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class meant to handle commands from the Ground Data System and execute them
 * in Astrobee.
 */

public class YourService extends KiboRpcService {
    @Override
    protected void runPlan1() {
        // The mission starts.
        api.startMission();

        double root3 = calculateDistance(new Point(0, 0, 0), new Point(1, 1, 1));
        System.out.println(root3);

        // Move to a point.
        Point point = new Point(10.9d, -9.92284d, 5.195d);
        Quaternion quaternion = new Quaternion(0f, 0f, -0.707f, 0.707f);
        api.moveTo(point, quaternion, true);

        api.flashlightControlFront(0.02f);

        // Get a camera image.
        Mat navCamImage = api.getMatNavCam();
        Mat dockCamImage = api.getMatDockCam();

        api.saveMatImage(navCamImage, "navcam");
        api.saveMatImage(dockCamImage, "dockcam");

        detectArUcoMarkers(navCamImage, "navCamImage");

        // Perform test on known ArUco tage
        Mat testImage = new Mat();

        int resourceId = getResources().getIdentifier("artag", "drawable", getPackageName());
        InputStream stream = getResources().openRawResource(resourceId);

        Utils.bitmapToMat(BitmapFactory.decodeStream(stream), testImage);

        detectArUcoMarkers(testImage, "testImage");

        /* *********************************************************************** */
        /* Write your code to recognize type and number of items in the each area! */
        /* *********************************************************************** */

        // When you recognize items, let’s set the type and number.
        // TODO: RENABLE THIS LINE!!! api.setAreaInfo(1, "item_name", 1);

        /* **************************************************** */
        /* Let's move to the each area and recognize the items. */
        /* **************************************************** */

        // When you move to the front of the astronaut, report the rounding completion.
        // TODO: RENABLE THIS LINE!!! api.reportRoundingCompletion();

        /* ********************************************************** */
        /* Write your code to recognize which item the astronaut has. */
        /* ********************************************************** */

        // Let's notify the astronaut when you recognize it.
        // TODO: RENABLE THIS LINE!!! api.notifyRecognitionItem();

        /*
         * *****************************************************************************
         * **************************
         */
        /*
         * Write your code to move Astrobee to the location of the target item (what the
         * astronaut is looking for)
         */
        /*
         * *****************************************************************************
         * **************************
         */

        // Take a snapshot of the target item.
        api.takeTargetItemSnapshot();
    }

    @Override
    protected void runPlan2() {
        // write your plan 2 here.
    }

    @Override
    protected void runPlan3() {
        // write your plan 3 here.
    }

    // You can add your method.

    private double calculateDistance(Point vec1, Point vec2) {
        double coordY = vec2.getX() - vec1.getX();
        double coordX = vec2.getY() - vec1.getY();
        double coordZ = vec2.getZ() - vec1.getZ();

        return Math.sqrt((double) ((coordY * coordY) + (coordX * coordX) + (coordZ * coordZ)));
    }

    private void detectArUcoMarkers(Mat inputImage, String imageNameBase) {
        System.out.println("---------- ArUco MARKER DETECTION ----------\n");

        // Define the marker detection settings
        Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
        DetectorParameters parameters = DetectorParameters.create();

        Mat markerIds = new Mat();
        List<Mat> markerCorners = new ArrayList<Mat>();
        List<Mat> rejectedCandidates = new ArrayList<Mat>();

        // Store results in variables
        Aruco.detectMarkers(inputImage, dictionary, markerCorners, markerIds, parameters, rejectedCandidates);

        System.out.println("----- MARKER ID INFO -----\n");
        System.out.println(markerIds);
        System.out.println(markerIds.getClass());
        System.out.println(markerIds.dump());
        System.out.println(markerIds.dump().getClass());

        System.out.println("----- MARKER ID INFO -----\n");
        Mat outputImage = inputImage.clone();
        Aruco.drawDetectedMarkers(outputImage, markerCorners, markerIds);

        api.saveMatImage(outputImage, imageNameBase + "Markers");

        System.out.println("----- INDIVIDUAL MARKER IMAGES -----\n");
        // NOTE: This may be trying to save a set of points and not an actual image
        int imageCount = 0;
        for (Mat imageMat : markerCorners) {
            api.saveMatImage(imageMat, imageNameBase + Integer.toString(imageCount));
        }

        System.out.println("---------- END OF FUNCTION ----------\n");
    }
}
