package zalezone.imagemagic.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zale on 16/3/14.
 */
public class CommonUtil {

    public static final String HAARCASCADE_FACE_FILENAME = "haarcascade_frontalface_alt.xml";
    public static final String HAARCASCADE_EYEGLASS_FILENAME = "haarcascade_eye_tree_eyeglasses.xml";


    public static String HAARCASCADE_FACE_FILEPATH;
    public static String HAARCASCADE_EYEGLASS_FILEPATH;

    public static File APPFOLDER;
    public static File IMAGEFOLDER;

    public static void initApp(Context context){

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                APPFOLDER = new File(Environment.getExternalStorageDirectory().getCanonicalPath()+"/imageMagic");
                if (!APPFOLDER.exists()){
                    APPFOLDER.mkdir();
                    copyAssets(context,"data",APPFOLDER.getAbsolutePath()+File.separator);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        HAARCASCADE_FACE_FILEPATH = APPFOLDER.getAbsolutePath()+File.separator+HAARCASCADE_FACE_FILENAME;
        HAARCASCADE_EYEGLASS_FILEPATH = APPFOLDER.getAbsolutePath()+File.separator+HAARCASCADE_EYEGLASS_FILENAME;
    }

    private static void copyAssets(Context context,String assetdir,String sddir){
        AssetManager assetManager = context.getResources().getAssets();
        String[] files;
        try {
            files = assetManager.list(assetdir);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        File mWorkingPath = new File(sddir);
        if (!mWorkingPath.exists()){
            mWorkingPath.mkdirs();
        }
        for (int i=0;i<files.length;i++){

            try{
                String fileName = files[i];
                if (!fileName.contains(".")){
                    if (assetdir.length() == 0){
                        copyAssets(context,fileName,sddir+fileName+File.separator);
                    }else {
                        copyAssets(context,assetdir+File.separator+fileName,sddir+fileName+File.separator);
                    }
                    continue;
                }
                File outFile = new File(mWorkingPath,fileName);
                if (outFile.exists()){
                    outFile.delete();
                }
                InputStream in = null;
                if (0!=assetdir.length()){
                    in = assetManager.open(assetdir + File.separator + fileName);
                }else {
                    in = assetManager.open(fileName);
                }
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len =in.read(buf)) >0){
                    out.write(buf,0,len);
                }
                in.close();
                out.close();
            }catch (Exception e){
            }

        }
    }

}
