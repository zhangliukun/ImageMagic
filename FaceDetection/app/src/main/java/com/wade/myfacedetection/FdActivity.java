package com.wade.myfacedetection;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class FdActivity extends Activity  implements CvCameraViewListener2{
    private Activity mainAct;
    private static final String    TAG                     = "FaceDetect";
    private static final Scalar    FACE_RECT_COLOR         = new Scalar(0, 255, 0, 255);
    private static final Scalar    EYE_RECT_COLOR          = new Scalar(255, 0, 0, 255);
    public static final int        JAVA_DETECTOR           = 0;
    public static final int        NATIVE_DETECTOR         = 1;

    private Mat                    mRgba;
    private Mat                    mGray;
    private File                   mCascadeFile,mEyeFile;
    private CascadeClassifier      mFaceDetector, mEyeDetector;
    private DetectionBasedTracker  mNativeDetector;

    private int                    mDetectorType           = JAVA_DETECTOR;
    private String[]               mDetectorName;

    private float                  mRelativeFaceSize       = 0.2f;
    private int                    mAbsoluteFaceSize       = 0;


    private CameraBridgeViewBase   mOpenCvCameraView;

    static {
        OpenCVLoader.initDebug();
        System.loadLibrary("opencv_java3");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status){
            MainActivity caller = new MainActivity();

            int[] rawResources = new int[]{
                R.raw.haarcascade_frontalface_default,
                R.raw.haarcascade_profileface,
                R.raw.haarcascade_frontalface_alt_tree,
                R.raw.haarcascade_frontalface_alt2,
                R.raw.haarcascade_frontalface_alt,
                R.raw.haarcascade_frontalcatface_extended,
                R.raw.haarcascade_frontalcatface,
                R.raw.haarcascade_upperbody,
                R.raw.haarcascade_lowerbody,
                R.raw.haarcascade_eye,
                R.raw.haarcascade_eye_tree_eyeglasses,
                R.raw.haarcascade_lefteye_2splits,
                R.raw.haarcascade_righteye_2splits,
                R.raw.haarcascade_russian_plate_number,
                R.raw.haarcascade_smile,
                R.raw.haarcascade_licence_plate_rus_16stages
            };

            String[] haarXML = new String[]{
                "haarcascade_frontalface_default.xml",
                "haarcascade_profileface.xml",
                "haarcascade_frontalface_alt_tree.xml",
                "haarcascade_frontalface_alt2.xml",
                "haarcascade_frontalface_alt.xml",
                "haarcascade_frontalcatface_extended.xml",
                "haarcascade_frontalcatface.xml",
                "haarcascade_upperbody.xml",
                "haarcascade_lowerbody.xml",
                "haarcascade_eye.xml",
                "haarcascade_eye_tree_eyeglasses.xml",
                "haarcascade_lefteye_2splits.xml",
                "haarcascade_righteye_2splits.xml",
                "haarcascade_russian_plate_number.xml",
                "haarcascade_smile.xml",
                "haarcascade_licence_plate_rus_16stages.xml"
            };
            switch(status){
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV Loaded Successfully");
                    System.loadLibrary("detection_based_tracker");
                    setMinFaceSize(0.2f);

                    try{
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        InputStream is;
                        int mode = caller.getStartMode();
                        is = getResources().openRawResource(rawResources[mode]);
                        mCascadeFile = new File(cascadeDir, haarXML[mode]);
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mFaceDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mFaceDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mFaceDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

                        cascadeDir.delete();
                    }catch(IOException e){
                        e.printStackTrace();
                        Log.i(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.enableView();

                } break;
                default:
                {
                    super.onManagerConnected(status);
                }break;
            }//switch
        }//onManagerConnected
    };//BaseLoaderCallback

    public FdActivity(){
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "JAVA";
        mDetectorName[NATIVE_DETECTOR] = "NATIVE (tracking)";
    }

    /** Called when the activity is first created. **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mainAct = MainActivity.mMainActivity;
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy(){
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height){
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped(){
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect();
        MatOfRect eyes  = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) {
            if (mFaceDetector != null)
                mFaceDetector.detectMultiScale(mGray, faces, 1.3, 5, 3,
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }
        else if (mDetectorType == NATIVE_DETECTOR) {
            if (mNativeDetector != null)
                mNativeDetector.detect(mGray, faces);
        }
        else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(mGray, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
        }
        return mGray;
    }

    private void setMinFaceSize(float faceSize){
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }

    private void setDetectorType(int type) {
        if (mDetectorType != type) {
            mDetectorType = type;

            if (type == NATIVE_DETECTOR) {
                Log.i(TAG, "Detection Based Tracker enabled");
                mNativeDetector.start();
            } else {
                Log.i(TAG, "Cascade detector enabled");
                mNativeDetector.stop();
            }
        }
    }
}
