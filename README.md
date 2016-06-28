FAB-Loading [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-FAB--Loading-green.svg?style=flat)](https://android-arsenal.com/details/1/2418)
==========================
A loading animation based on Floating Action Button.

 ![Marvel Sample Screenshots][1]

 ![FAB-Loading Sample Screenshots][2]


Usage
=====
 1. Include the `LoadingView` widget in your view:
  ```
  <io.saeid.fabloading.LoadingView
      android:id="@+id/loading_view"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:scaleX="1.5" //(optional)
      android:scaleY="1.5" // (optional)
      app:mfl_onclickLoading="true" //(optional)
      app:mfl_duration="200" //(optional)
      app:mfl_repeat="4" //(optional)
      />
```
  2.Add your loading items.
  
     *Note that there are four types of loading animation, `LoadingView.FROM_LEFT`, `LoadingView.FROM_TOP`,          `LoadingView.FROM_RIGHT`, `LoadingView.FROM_BOTTOM`.*
  ```
    mLoadingView = (LoadingView) findViewById(R.id.loading_view);
    mLoadingView.addAnimation(yourColor,yourDrawable,yourLoadingType);

    //also you can add listener for getting callback (optional)
    mLoadingView.addListener(new LoadingView.LoadingListener() {
           @Override public void onAnimationStart(int currentItemPosition) {
           }

           @Override public void onAnimationRepeat(int nextItemPosition) {
           }

           @Override public void onAnimationEnd(int nextItemPosition) {
           }
          });
  ```

  3.Call  `mLoadingView.startAnimation();` whenever you want to start animation.

XML Attributes
-------
| XML Attribute | Related Method | Description |
|:---|:---|:---|
| mfl_onclickLoading | ... | Start animation by clicking FAB. (default is false) |
| mfl_duration | setDuration(int duration) | Set duration for each loading item. (default is 500 millis) |
| mfl_repeat | setRepeat(int repeat) | For values greater than 1, it calls next animations automatically for 'repeat-1' times. (default is 1) |

Installation
-------
```
compile 'io.saeid:fab-loading:1.0.0'
```

Credits
=====
Inspired by : http://www.materialup.com/posts/marvel-avengers-loading-animation

License
=====
```
Copyright 2015 Saeed Masoumi.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
[1]: https://raw.githubusercontent.com/smasoumi/FAB-Loading/master/images/marvel_loader.gif
[2]: https://raw.githubusercontent.com/smasoumi/FAB-Loading/master/images/preview.gif
