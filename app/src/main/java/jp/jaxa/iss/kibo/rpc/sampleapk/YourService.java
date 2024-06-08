package jp.jaxa.iss.kibo.rpc.sampleapk;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;

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
        Point point = new Point(11.1d, -10.2d, 5.195d);
        Quaternion quaternion = new Quaternion(0f, 0f, -0.707f, 0.707f);
        api.moveTo(point, quaternion, true);

        api.flashlightControlFront(0.02f);

        // Get a camera image.
        Mat navCamImage = api.getMatNavCam();
        Mat dockCamImage = api.getMatDockCam();

        api.saveMatImage(navCamImage, "navcam");
        api.saveMatImage(dockCamImage, "dockcam");

        for (int i = 0; i <= 5 - 1; i++) {
            testArUcoMarkerType(navCamImage, i);
        }

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

    private void testArUcoMarkerType(Mat navCamImage, int round) {
        Dictionary markerCorners;

        if (round == 0) {
            markerCorners = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_50);
        } else if (round == 1) {
            markerCorners = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_100);
        } else if (round == 2) {
            markerCorners = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
        } else if (round == 3) {
            markerCorners = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_1000);
        } else {
            markerCorners = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_50);
            navCamImage = Imgcodecs.imread("artag.png");
        }

        List<Mat> markerIds = new ArrayList<Mat>();
        Mat rejectedCandidates = new Mat();
        Aruco.detectMarkers(navCamImage, markerCorners, markerIds, rejectedCandidates);

        api.saveMatImage(rejectedCandidates, String.format("candidate%d", round));

        System.out.printf("NAV IMAGE MARKER DETECTION: ROUND %d", round);

        System.out.println(markerCorners);
        System.out.println(markerIds);
        System.out.println(rejectedCandidates.dump());
    }
}
