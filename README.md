# android-photo-toolbox

This project is based on [yaoyi30/ERFNet_ncnn_Android](https://github.com/yaoyi30/ERFNet_ncnn_Android)

This is an Android toolkit that enables you to upload a photo to **generate a photo ID**, **change the background color**, **change to anime style** , and **clear up blurry pictures**. The app use the ERF-Net!

# how to build and run

## step1

https://github.com/Tencent/ncnn/releases

1.Download ncnn-YYYYMMDD-android-vulkan.zip or build ncnn for android yourself

2.Extract ncnn-YYYYMMDD-android-vulkan.zip into **app/src/main/jni** and change the **ncnn_DIR** path to yours in **app/src/main/jni/CMakeLists.txt**

## step2
Open this project with Android Studio, build it and enjoy!

# screenshot
1.human segmentation and change photo background color
![1](https://user-images.githubusercontent.com/56180347/174824531-da9838fa-11c5-4da5-8192-ce753909862e.png)

2.the result can be save,click:

![xiazai](https://user-images.githubusercontent.com/56180347/174908175-1f0cabc2-9e78-4e63-91b2-2ad280b82059.png)

3.you can also take a photo and separate the human area

# Reference

https://github.com/runrunrun1994/ncnn-android-deeplabv3plus

https://github.com/yaoyi30/ERFNet_ncnn_Android

