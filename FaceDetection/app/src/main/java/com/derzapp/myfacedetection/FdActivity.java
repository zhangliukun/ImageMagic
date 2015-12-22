package com.derzapp.myfacedetection;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
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

    private static final String    TAG                     = "FaceDetect";
    private static final Scalar    FACE_RECT_COLOR         = new Scalar(0, 255, 0, 255);
    private static final Scalar    EYE_RECT_COLOR          = new Scalar(255, 0, 0, 255);
    public static final int        JAVA_DETECTOR           = 0;
    public static final int        NATIVE_DETECTOR         = 1;

    private MenuItem               mItemFace50;
    private MenuItem               mItemFace40;
    private MenuItem               mItemFace30;
    private MenuItem               mItemFace20;
    private MenuItem               mItemType;

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

    //Some UI items
    public TextView                textView                = null;

    static {
        if (!OpenCVLoader.initDebug()) {
        }
        System.loadLibrary("opencv_java3");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status){
            switch(status){
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV Loaded Successfully");
                    System.loadLibrary("detection_based_tracker");
                    setMinFaceSize(0.2f);

                    try{
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_default);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_default.xml");
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

                        InputStream isEye = getResources().openRawResource(R.raw.haarcascade_eye_tree_eyeglasses);
                        File cascadeEyeDir = getDir("cascadeeye", Context.MODE_PRIVATE);
                        mEyeFile = new File(cascadeEyeDir, "haarcascade_eye_tree_eyeglasses.xml");
                        FileOutputStream osEye = new FileOutputStream(mEyeFile);
                        byte[] bufferEye = new byte[4096];
                        int bytesReadEye;
                        while ((bytesReadEye = isEye.read(bufferEye)) != -1) {
                            osEye.write(bufferEye, 0, bytesReadEye);
                        }
                        isEye.close();
                        osEye.close();
                        mEyeDetector = new CascadeClassifier(mEyeFile.getAbsolutePath());
                        if (mEyeDetector.empty()) {
                            mEyeDetector = null;
                        }
                        if (mEyeDetector== null) {
                            Log.d(TAG, "No mEyeDetector");
                        }
                        else {
                            Log.d(TAG, "YES! mEyeDetector");
                        }

                        cascadeDir.delete();
                        cascadeEyeDir.delete();
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
        setContentView(R.layout.face_detect_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
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
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
            if (mEyeDetector != null) {
                Rect roi = new Rect((int) facesArray[i].tl().x,
                    (int) facesArray[i].tl().y,
                    (int) facesArray[i].width,
                    (int) facesArray[i].height);
                Mat cropped = new Mat();
                cropped = mGray.submat(roi);
                mEyeDetector.detectMultiScale(cropped, eyes, 1.1, 2, 2,
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
                Rect[] eyesArray;
                eyesArray = eyes.toArray();
                Point x1 = new Point();
                Log.d(TAG, "eye detect " + eyesArray.length + " points");
                for (int j=0; j<eyesArray.length; j++) {
                    x1.x = facesArray[i].x + eyesArray[j].x + eyesArray[j].width*0.5;
                    x1.y = facesArray[i].y + eyesArray[j].y + eyesArray[j].height*0.5;
                    int Radius = (int)((eyesArray[j].width+eyesArray[j].height)*0.25);
                    Imgproc.circle(mRgba, x1, Radius, EYE_RECT_COLOR, 2);
                }
            }
        }
        return mRgba;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mItemFace50 = menu.add("Face size 50%");
        mItemFace40 = menu.add("Face size 40%");
        mItemFace30 = menu.add("Face size 30%");
        mItemFace20 = menu.add("Face size 20%");
        mItemType = menu.add(mDetectorName[mDetectorType]);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == mItemFace50)
            setMinFaceSize(0.5f);
        else if (item == mItemFace40)
            setMinFaceSize(0.4f);
        else if (item == mItemFace30)
            setMinFaceSize(0.3f);
        else if (item == mItemFace20)
            setMinFaceSize(0.2f);
        else if (item == mItemType) {
            int tmpDetectorType = (mDetectorType + 1) % mDetectorName.length;
            textView = (TextView) findViewById(R.id.my_textview);
            textView.setText(mDetectorName[tmpDetectorType]);
            setDetectorType(tmpDetectorType);
        }
        return true;
    }

}
