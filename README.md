# Slide
侧滑退出（支持Fragment/Activity）

原理（Activity/Fragment）

内部构建一个FrameLayout，attach的时候主要将Activity的RootView从Activity中取出并加入FrameLayout最终将FrameLayout作为RootView放到Activity中。返回的时候利用反射拿到WindowManagerGlobal的mRoots（保存了所有Activity的RootView），取出上一个Activity的RootView加入FrameLayout后面只要做一些位移即可完成侧滑动画。Fragment原理相似操作更为简单。
