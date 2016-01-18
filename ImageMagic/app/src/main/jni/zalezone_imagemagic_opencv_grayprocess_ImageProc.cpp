#include <zalezone_imagemagic_opencv_grayprocess_ImageProc.h>
#include "opencv2/core/core.hpp"
#include "opencv2/features2d/features2d.hpp"
#include <opencv2/imgproc/imgproc.hpp>
#include <string>
#include <vector>

using namespace std;
using namespace cv;

JNIEXPORT jintArray JNICALL Java_zalezone_imagemagic_opencv_grayprocess_ImageProc_grayProc
        (JNIEnv *env, jclass jclass, jintArray buf, jint w, jint h){

  jint *cbuf;
  cbuf = env->GetIntArrayElements(buf, false);
  if(cbuf == NULL){
    return 0;
  }

  Mat imgData(h,w,CV_8UC4,(unsigned char*)cbuf);

  uchar* ptr = imgData.ptr(0);

  for (int i = 0; i < w*h; i++) {

    //计算公式:Y(亮度) = 0.299*R+0.587*G+0.114*B
    //对于一个int四字节，彩色的存储方式为BGRA

    int grayScale = (int)((ptr[4*i+2])*0.299+ptr[4*i+1]*0.587+ptr[4*i+0]*0.114);
    ptr[4*i+1] = grayScale;
    ptr[4*i+2] = grayScale;
    ptr[4*i+0] = grayScale;
  }

  int size = w*h;

  jintArray result = env->NewIntArray(size);
  env->SetIntArrayRegion(result,0,size,cbuf);
  env->ReleaseIntArrayElements(buf,cbuf,0);
  return result;
}

//JNIEXPORT void JNICALL Java_zalezone_imagemagic_opencv_grayprocess_ImageProc_FindFeatures
//(JNIEnv *env, jclass jclass, jlong addrGray, jlong addrRgba){
//  Mat& mGr  = *(Mat*)addrGray;
//  Mat& mRgb = *(Mat*)addrRgba;
//  vector<KeyPoint> v;
//
//  Ptr<FeatureDetector> detector = FastFeatureDetector::create(50);
//  detector->detect(mGr, v);
//  for( unsigned int i = 0; i < v.size(); i++ )
//  {
//    const KeyPoint& kp = v[i];
//    circle(mRgb, Point(kp.pt.x, kp.pt.y), 10, Scalar(255,0,0,255));
//  }
//}