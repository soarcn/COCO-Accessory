package com.cocosw.accessory.views;

import android.os.SystemClock;
import android.view.MotionEvent;

/**
 * Internal API class to analyse the recorded gestures.
 *
 * @author championswimmer
 * @version 0.2
 * @since 0.1 12/04/14
 */
public class GestureAnalyser {

    private static final String TAG = "GestureAnalyser";
    public static final boolean DEBUG = true;

    public static final int SWIPE_1_UP = 11;
    public static final int SWIPE_1_DOWN = 12;
    public static final int SWIPE_1_LEFT = 13;
    public static final int SWIPE_1_RIGHT = 14;
    public static final int SWIPE_2_UP = 21;
    public static final int SWIPE_2_DOWN = 22;
    public static final int SWIPE_2_LEFT = 23;
    public static final int SWIPE_2_RIGHT = 24;
    public static final int PINCH = 25;
    public static final int UNPINCH = 26;

    private double[] initialX = new double[2];
    private double[] initialY = new double[2];
    private double[] finalX = new double[2];
    private double[] finalY = new double[2];
    private double[] delX = new double[2];
    private double[] delY = new double[3];

    private int numFingers = 0;

    private long initialT, finalT;

    public GestureAnalyser() {
    }

    public void trackGesture(MotionEvent ev) {
        int n = ev.getPointerCount();
        for (int i = 0; i < n; i++) {
            initialX[i] = ev.getX(i);
            initialY[i] = ev.getY(i);
        }
        numFingers = n;
        initialT = SystemClock.uptimeMillis();
    }

    public void untrackGesture() {
        numFingers = 0;
    }

    public int getGesture(MotionEvent ev) {
        for (int i = 0; i < numFingers; i++) {
            finalX[i] = ev.getX(i);
            finalY[i] = ev.getY(i);
            delX[i] = finalX[i] - initialX[i];
            delY[i] = finalY[i] - initialY[i];
        }
        finalT = SystemClock.uptimeMillis();
        return calcGesture();
    }

    private int calcGesture() {
        if (numFingers == 1) {
            if ((-(delY[0])) > (2 * (Math.abs(delX[0])))) {
                return SWIPE_1_UP;
            }

            if (((delY[0])) > (2 * (Math.abs(delX[0])))) {
                return SWIPE_1_DOWN;
            }

            if ((-(delX[0])) > (2 * (Math.abs(delY[0])))) {
                return SWIPE_1_LEFT;
            }

            if (((delX[0])) > (2 * (Math.abs(delY[0])))) {
                return SWIPE_1_RIGHT;
            }
        }
        if (numFingers == 2) {
            if (((-delY[0]) > (2 * Math.abs(delX[0]))) && ((-delY[1]) > (2 * Math.abs(delX[1])))) {
                return SWIPE_2_UP;
            }
            if (((delY[0]) > (2 * Math.abs(delX[0]))) && ((delY[1]) > (2 * Math.abs(delX[1])))) {
                return SWIPE_2_DOWN;
            }
            if (((-delX[0]) > (2 * Math.abs(delY[0]))) && ((-delX[1]) > (2 * Math.abs(delY[1])))) {
                return SWIPE_2_LEFT;
            }
            if (((delX[0]) > (2 * Math.abs(delY[0]))) && ((delX[1]) > (2 * Math.abs(delY[1])))) {
                return SWIPE_2_RIGHT;
            }
            if (finalFingDist(0, 1) > 2 * (initialFingDist(0, 1))) {
                return UNPINCH;
            }
            if (finalFingDist(0, 1) < 0.5 * (initialFingDist(0, 1))) {
                return PINCH;
            }
        }
        return 0;
    }

    private double initialFingDist(int fingNum1, int fingNum2) {

        return Math.sqrt(Math.pow((initialX[fingNum1] - initialX[fingNum2]), 2)
                + Math.pow((initialY[fingNum1] - initialY[fingNum2]), 2));
    }

    private double finalFingDist(int fingNum1, int fingNum2) {

        return Math.sqrt(Math.pow((finalX[fingNum1] - finalX[fingNum2]), 2)
                + Math.pow((finalY[fingNum1] - finalY[fingNum2]), 2));
    }


}