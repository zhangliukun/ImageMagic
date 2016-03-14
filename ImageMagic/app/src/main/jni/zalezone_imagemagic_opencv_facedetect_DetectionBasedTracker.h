/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class zalezone_imagemagic_opencv_facedetect_DetectionBasedTracker */

#ifndef _Included_zalezone_imagemagic_opencv_facedetect_DetectionBasedTracker
#define _Included_zalezone_imagemagic_opencv_facedetect_DetectionBasedTracker
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     zalezone_imagemagic_opencv_facedetect_DetectionBasedTracker
 * Method:    nativeCreateObject
 * Signature: (Ljava/lang/String;I)J
 */
JNIEXPORT jlong JNICALL Java_zalezone_imagemagic_opencv_facedetect_DetectionBasedTracker_nativeCreateObject
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     zalezone_imagemagic_opencv_facedetect_DetectionBasedTracker
 * Method:    nativeDestoryObject
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_zalezone_imagemagic_opencv_facedetect_DetectionBasedTracker_nativeDestoryObject
  (JNIEnv *, jclass, jlong);

/*
 * Class:     zalezone_imagemagic_opencv_facedetect_DetectionBasedTracker
 * Method:    nativeStart
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_zalezone_imagemagic_opencv_facedetect_DetectionBasedTracker_nativeStart
  (JNIEnv *, jclass, jlong);

/*
 * Class:     zalezone_imagemagic_opencv_facedetect_DetectionBasedTracker
 * Method:    nativeStop
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_zalezone_imagemagic_opencv_facedetect_DetectionBasedTracker_nativeStop
  (JNIEnv *, jclass, jlong);

/*
 * Class:     zalezone_imagemagic_opencv_facedetect_DetectionBasedTracker
 * Method:    nativeSetFaceSize
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_zalezone_imagemagic_opencv_facedetect_DetectionBasedTracker_nativeSetFaceSize
  (JNIEnv *, jclass, jlong, jint);

/*
 * Class:     zalezone_imagemagic_opencv_facedetect_DetectionBasedTracker
 * Method:    nativeDetect
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL Java_zalezone_imagemagic_opencv_facedetect_DetectionBasedTracker_nativeDetect
  (JNIEnv *, jclass, jlong, jlong, jlong);

#ifdef __cplusplus
}
#endif
#endif