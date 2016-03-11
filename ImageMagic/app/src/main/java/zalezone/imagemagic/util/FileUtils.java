package zalezone.imagemagic.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;

/**
 * Created by zale on 16/3/9.
 */
public class FileUtils {

    private static final String DIR_IMAGE = "local_image";
    private static final String DIR_DATA_ROOT = "/data/data";
    private static final String DIR_INTERNAL_CACHE = "cache";

    /**
     * 获取内部缓存
     * @param context
     * @return
     */
    public static String getInternalCache(Context context){
        String path = DIR_DATA_ROOT + context.getPackageName();
        if (!TextUtils.isEmpty(path)){
            String dir = path + File.separator + DIR_INTERNAL_CACHE;
            File file = new File(dir);
            if (!file.exists()){
                file.mkdir();
            }
            return file.getAbsolutePath();
        }
        return "";
    }

    /**
     * 获取外部存储的图片缓存路径
     * @param context
     * @return
     */
    public static String getImageCachePath(Context context){
        String path = getCachePath(context);
        if (!TextUtils.isEmpty(path)){
            String root = path + File.separator + DIR_IMAGE;
            File file = new File(root);
            if (!file.exists()){
                file.mkdir();
            }
            return file.getAbsolutePath();
        }
        return "";
    }


    /**
     * 获取外部存储的cache dir
     * @param context
     * @return
     */
    public static String getCachePath(Context context){
        String state = android.os.Environment.getExternalStorageState();
        String path = "";
        if (state != null && state.equals(Environment.MEDIA_MOUNTED)){
            if (Build.VERSION.SDK_INT >= 8){
                File file = context.getExternalCacheDir();
                if (file !=null){
                    path = file.getAbsolutePath();
                }
                if (TextUtils.isEmpty(path)){
                    path = Environment.getExternalStorageDirectory().getAbsolutePath();
                }
            }else {
                path = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
        }else if (context.getCacheDir()!=null){
            path = context.getCacheDir().getAbsolutePath();
        }
        return path;
    }

    /**
     * 将数据写入缓存中
     * @param context
     * @param obj
     * @param fileName
     */
    public static void writeObjectToCache(Context context,Object obj,String fileName){
        String path = getCachePath(context) + File.separator + fileName;
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        try {
            fout = new FileOutputStream(path);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(obj);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (fout!=null){
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos!=null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Object readObjectFromCache(Context context,String fileName){
        String path = getCachePath(context) + File.separator + fileName;
        FileInputStream fileInput = null;
        ObjectInputStream input = null;
        try {
            fileInput = new FileInputStream(new File(path));
            input = new ObjectInputStream(fileInput);
            return input.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fileInput!=null){
                try {
                    fileInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 递归删除文件
     * @param path
     */
    public static void deleteFile(String path){
        try {
            File f = new File(path);
            if (f.isDirectory()){
                File[] file = f.listFiles();
                if (file!=null){
                    for (File file1: file){
                        deleteFile(file1.toString());
                        file1.delete();
                    }
                }
            }else {
                f.delete();
            }
        }catch (Exception e){
        }
    }

    /**
     * 获取文件的字节流
     * @param path
     * @return
     */
    public static byte[] getFileBytes(String path){
        File file = new File(path);
        FileInputStream fin = null;
        byte[] readBytes = null;
        if (file!=null){
            try {
                fin = new FileInputStream(file);
                int len = fin.available();
                readBytes = new byte[len];
                fin.read(readBytes);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (fin!=null){
                    try {
                        fin.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return readBytes;
    }

    /**
     * 写入文件
     * @param bfile
     * @param filePath
     * @param fileName
     */
    public static void writeFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void writeFile(byte[] bfile, File file) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取指定文件大小
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
        }
        return size;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @return
     */
    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        DecimalFormat df = new DecimalFormat("#.00");
        double formatSize = 0d;
        if (size >= gb) {
            formatSize = Double.valueOf(df.format(size * 1.0 / gb));
            return formatSize + "GB";
        } else if (size >= mb) {
            formatSize = Double.valueOf(df.format(size * 1.0 / mb));
            return formatSize + "MB";
        } else {
            formatSize = Double.valueOf(df.format(size * 1.0 / kb));
            if (size == 0)
                return "0KB";
            return formatSize + "KB";
        }
    }

    /**
    * 获取本地路径的文件名称
    * @param filePath
    * @return
    */
    public static String getFileName(String filePath) {
        String result = "";
        if (!TextUtils.isEmpty(filePath)) {
            String[] strs = filePath.split("/");
            if (strs.length > 0) {
                return strs[strs.length - 1];
            }
        }
        return result;
    }

    /**
     * 移动文件
     *
     * @param srcFileName 源文件完整路径
     * @param destDirName 目的目录完整路径
     * @return 文件移动成功返回true，否则返回false
     */
    public static boolean moveFile(String srcFileName, String destDirName) {

        File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile())
            return false;

        File destDir = new File(destDirName);
        if (!destDir.exists())
            destDir.mkdirs();

        return srcFile.renameTo(new File(destDirName + File.separator + srcFile.getName()));
    }

    /**
     * 移动目录
     *
     * @param srcDirName  源目录完整路径
     * @param destDirName 目的目录完整路径
     * @return 目录移动成功返回true，否则返回false
     */
    public static boolean moveDirectory(String srcDirName, String destDirName) {

        File srcDir = new File(srcDirName);
        if (!srcDir.exists() || !srcDir.isDirectory())
            return false;

        File destDir = new File(destDirName);
        if (!destDir.exists())
            destDir.mkdirs();

        /**
         * 如果是文件则移动，否则递归移动文件夹。删除最终的空源文件夹
         * 注意移动文件夹时保持文件夹的树状结构
         */
        File[] sourceFiles = srcDir.listFiles();
        if(sourceFiles != null && sourceFiles.length > 0){
            for (File sourceFile : sourceFiles) {
                if (sourceFile.isFile()) {
                    moveFile(sourceFile.getAbsolutePath(), destDir.getAbsolutePath());
                }else if (sourceFile.isDirectory()) {
                    moveDirectory(sourceFile.getAbsolutePath(),
                            destDir.getAbsolutePath() + File.separator + sourceFile.getName());
                }
            }
        }
        deleteFile(srcDirName);
        return true;
    }

    /**
     * 检测内存卡是否可用
     */
    public static boolean isSDcardUsable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 文件是否存在
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取内存卡路径
     */
    public static File getSDcardDir() {
        if (isSDcardUsable()) {
            return Environment.getExternalStorageDirectory();
        } else {
            return null;
        }
    }

}
