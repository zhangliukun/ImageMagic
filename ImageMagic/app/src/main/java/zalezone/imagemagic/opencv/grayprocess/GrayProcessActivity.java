package zalezone.imagemagic.opencv.grayprocess;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import zalezone.imagemagic.R;
import zalezone.imagemagic.base.BaseActivity;
import zalezone.imagemagic.util.FileUtils;

public class GrayProcessActivity extends BaseActivity implements View.OnClickListener{


    public static void  startActivity(Context context){
        Intent intent = new Intent(context,GrayProcessActivity.class);
        context.startActivity(intent);
    }

    private ImageView imageView;
    private Button button;

    private Bitmap bmp;

    static {
        System.loadLibrary("ProcLib");
        System.loadLibrary("opencv_java3");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_grayprocess);
        initView();
        initData();
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.image);
        button = (Button) findViewById(R.id.process);
        button.setOnClickListener(this);
    }

    private void initData() {
        bmp = BitmapFactory.decodeResource(getResources(),R.drawable.toux);
        imageView.setImageBitmap(bmp);
//        if (FileUtils.writeImageToCache(this,bmp,"zaleFace","zale", Bitmap.CompressFormat.JPEG,100)){
//            showToast("图像保存成功");
//        }else {
//            showToast("文件保存失败");
//        }
    }

    private void processImage(){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        int[] resultInt = ImageProc.grayProc(pixels,w,h);
        Bitmap resultImg = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        resultImg.setPixels(pixels,0,w,0,0,w,h);
        imageView.setImageBitmap(resultImg);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.process:
                processImage();
                break;
        }
    }
}
