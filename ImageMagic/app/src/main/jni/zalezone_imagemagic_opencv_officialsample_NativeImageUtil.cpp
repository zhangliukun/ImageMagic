#include <zalezone_imagemagic_opencv_officialsample_NativeImageUtil.h>
#include "opencv2/core/core.hpp"
#include "opencv2/features2d/features2d.hpp"
#include <opencv2/imgproc/imgproc.hpp>
#include <string>
#include <vector>

using namespace std;
using namespace cv;


JNIEXPORT void JNICALL Java_zalezone_imagemagic_opencv_officialsample_NativeImageUtil_FindFeatures
(JNIEnv *env, jclass jclass, jlong addrGray, jlong addrRgba){
  Mat& mGr  = *(Mat*)addrGray;
  Mat& mRgb = *(Mat*)addrRgba;
  vector<KeyPoint> v;

  Ptr<FeatureDetector> detector = FastFeatureDetector::create(50);
  detector->detect(mGr, v);
  for( unsigned int i = 0; i < v.size(); i++ )
  {
    const KeyPoint& kp = v[i];
    circle(mRgb, Point(kp.pt.x, kp.pt.y), 10, Scalar(255,0,0,255));
  }

}
