package zalezone.imagemagic.opencv.facedetect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import zalezone.imagemagic.R;
import zalezone.imagemagic.base.BaseActivity;
import zalezone.imagemagic.util.CommonUtil;

/**
 * Created by zale on 16/3/14.
 */
public class FaceDetectActivity extends BaseActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    public static void  startActivity(Context context){
        Intent intent = new Intent(context,FaceDetectActivity.class);
        context.startActivity(intent);
    }

    CameraBridgeViewBase cameraBridgeViewBase;
    private Mat                    mRgba;
    private Mat                    mGray;

    public static final Scalar FACE_RECT_COLOR = new Scalar(0,255,0,255);

    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;

    private File mCascadeFile;
    private DetectionBasedTracker mNativeDetector;

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("faceDetect");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera_prew);
        initView();
        initData();
    }

    private void initData() {

        CommonUtil.initApp(this);

        try {
            InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
            File cascadeDir = getDir("cascade",Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir,"haarcascade_frontalface_alt.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while((bytesRead = is.read(buffer))!=-1){
                os.write(buffer,0,bytesRead);
            }
            is.close();
            os.close();

            mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(),0);
            cascadeDir.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
        cameraBridgeViewBase.enableView();
    }

    private void initView() {
        cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        cameraBridgeViewBase.setCvCameraViewListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
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

        if (mNativeDetector!=null){
            mNativeDetector.detect(mGray,faces);
        }

        Rect[] facesArray = faces.toArray();
        for (int i=0;i<facesArray.length;i++){
            Imgproc.rectangle(mRgba,facesArray[i].tl(),facesArray[i].br(),FACE_RECT_COLOR,3);
        }

        return mRgba;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraBridgeViewBase!=null && !cameraBridgeViewBase.isEnabled()){
            cameraBridgeViewBase.enableView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }
}
