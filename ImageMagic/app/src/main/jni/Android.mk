LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#opencv
OPENCVROOT:= /Users/zale/MyFileRoot/software_source/OpenCV-android-sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES := zalezone_imagemagic_opencv_grayprocess_ImageProc.cpp

LOCAL_LDLIBS += -llog
LOCAL_MODULE := ProcLib

include $(BUILD_SHARED_LIBRARY)



include $(CLEAR_VARS)

#opencv
OPENCVROOT:= /Users/zale/MyFileRoot/software_source/OpenCV-android-sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES := zalezone_imagemagic_opencv_officialsample_NativeImageUtil.cpp

LOCAL_LDLIBS += -llog
LOCAL_MODULE := ImageUtil

include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)

#opencv
OPENCVROOT:= /Users/zale/MyFileRoot/software_source/OpenCV-android-sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES := zalezone_imagemagic_opencv_facedetect_DetectionBasedTracker.cpp

LOCAL_LDLIBS += -llog
LOCAL_MODULE := faceDetect

include $(BUILD_SHARED_LIBRARY)