# OmnipotentSilentPhotography
一个适用任何手机静默拍照demo
====
使用姿势
  先把TakePicture类拷贝到自己的app中，然后new TakePicture();就能是实现拍照并且保存图片，非常的简单；
===
适配任何手机camera核心代码

A demo for silent photos of any mobile phone
====
Using posture
First copy the takepicture class to your own app, and then new takepicture(); you can take photos and save pictures, which is very simple;
===
Adapt to any mobile camera core code
   int PreviewWidth = 0;
            int PreviewHeight = 0;
            List<Camera.Size> sizeList = mParameters.getSupportedPreviewSizes();
            if (sizeList.size() > 1) {
                Iterator<Camera.Size> itor = sizeList.iterator();
                while (itor.hasNext()) {
                    Camera.Size cur = itor.next();
                    if (cur.width >= PreviewWidth
                            && cur.height >= PreviewHeight) {
                        PreviewWidth = cur.width;
                        PreviewHeight = cur.height;
                        break;
                    }
                }
            }else if (sizeList.size()==1){
                Camera.Size size = sizeList.get(0);
                PreviewWidth = size.width;
                PreviewHeight = size.height;
            }
           mParameters.setPreviewSize(PreviewWidth, PreviewHeight); //获得摄像区域的大小
           mParameters.setPictureSize(PreviewWidth, PreviewHeight);//设置拍出来的屏幕大小


            try {
                myCamera.setParameters(mParameters);
            }catch (Exception e) {
                Camera.Parameters parameters = myCamera.getParameters();// 得到摄像头的参数
                myCamera.setParameters(parameters);

            }



