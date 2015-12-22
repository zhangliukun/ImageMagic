Contact: wade.fs@gmail.com
本範例一律採用 Android Studio 最新版編譯，目前是 2.0 preview 4 build 143.2489090
系統採用 Ubuntu 15.04, 其他請見 http://source.android.com/source/initializing.html
OpenCV 有可能因為引用的範例來自 opencv 2.x, 我盡可能採用的是 OpenCV 3.0.0

https://github.com/tesseract-ocr/tesseract
https://www.youtube.com/watch?v=nmDiZGx5mqU
https://github.com/openalpr/openalpr

color2gray http://www.cs.northwestern.edu/~ago820/color2gray/color2gray.pdf

怎樣在 android studio 中使用 opencv-3.0?
   http://stackoverflow.com/questions/17767557/how-to-use-opencv-in-android-studio-using-gradle-build-tool/22427267#22427267

1. 請直接下載 OpenCV-android-sdk
   http://sourceforge.net/projects/opencvlibrary/files/opencv-android/
   http://opencv.org/platforms/android.html
  底下稱其解壓縮根目錄為 opencv

2. 將 cp -a opencv/sdk/java $PROJ/libraries/opencv

3. vi $PROJ/libraries/opencv/build.gradle
=================== CUT HERE ====================================================================
buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath 'com.android.tools.build:gradle:2.0.0-alpha2'
    }
}

apply plugin: 'com.android.library'

android {
    compileSdkVersion 16
    buildToolsVersion "23.0.2"

    defaultConfig {
        minSdkVersion 16
        targetSdkVersion 22
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.txt'
        }
    }
}
=================== CUT HERE ====================================================================

4. add following into $PROJ/settings.gradle
include ':libraries:opencv'

5. 參考 following into $PROJ/app/build.gradle
=================== CUT HERE ====================================================================
import org.apache.tools.ant.taskdefs.condition.Os
import com.android.build.gradle.tasks.NdkCompile

apply plugin: 'com.android.application'

android {
    compileSdkVersion 22
    buildToolsVersion "22.0.1"

    defaultConfig {
        applicationId "com.derzapp.myfacedetection"
        minSdkVersion 10
        targetSdkVersion 22
        versionCode 1
        versionName "1.0"
        ndk {moduleName "NativeCode"}
    }

    sourceSets.main {
        jniLibs.srcDir 'src/main/libs' //set .so files location to libs
        jni.srcDirs = [] //Disable automatic ndk-build call
    }

    tasks.withType(NdkCompile) {
        compileTask -> compileTask.enabled = false
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }

    /*
    task ndkBuild(type: Exec) {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            def ndkDir = System.getenv("ANDROID_NDK_ROOT")
            commandLine 'cmd', '/C', "$ndkDir/ndk-build",
                    'NDK_PROJECT_PATH=build',
                    'APP_BUILD_SCRIPT=sc/main/jni/Android.mk',
                    'NDK_APPLICATION_MK=src/main/jni/Application.mk',
                    'NDK_APP_LIBS_OUT = src/main/jnilibs'

        } else {
            //commandLine "ndk-build", '-C', file('src/main').absolutePath
            def ndkDir = System.getenv("NDKROOT")
            commandLine "$ndkDir/ndk-build",
                    'NDK_PROJECT_PATH=build',
                    'APP_BUILD_SCRIPT=src/main/jni/Android.mk',
                    'NDK_APPLICATION_MK=src/main/jni/Application.mk',
                    'NDK_APP_LIBS_OUT = src/main/jnilibs'
        }
    }
    tasks.withType(JavaCompile) {
        compileTask -> compileTask.dependsOn ndkBuild
    }
    */
}
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:appcompat-v7:22.0.0'
    compile project(':libraries:opencv')
}

6. 將 OpenCV sdk/native/libs/armeabi-v7a 複製到 $PROJ/app/src/main/jniLibs/

怎樣略過 OpenCV Manager APK?
1) http://superzoro.logdown.com/posts/2015/08/24/opencv-30-for-android-in-android-studio
2) http://www.cnblogs.com/tail/p/4618790.html

1. 註銷掉OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback); 在語句上邊直接設為SUCCESS
public void onResume()
    {
        super.onResume();
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

2. 在Activity類中添加靜態的方法

static{
        if(!OpenCVLoader.initDebug()){
          OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        }
        else {
          // System.loadLibrary("my_jni_lib1");
          // System.loadLibrary("my_jni_lib2");
          mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        System.loadLibrary("opencv_java3"); // System.loadLibrary("opencv_java");
    }


