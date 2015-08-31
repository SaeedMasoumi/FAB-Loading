package io.saeid.fabloadingtest;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import io.saeid.fabloading.LoadingView;

public class MainActivity extends AppCompatActivity {

  private LoadingView mLoadingView;
  private LoadingView mLoadViewLong;
  private LoadingView mLoadViewNoRepeat;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mLoadingView = (LoadingView) findViewById(R.id.loading_view_repeat);
    boolean isLollipop = Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP;
    int marvel_1 = isLollipop?R.drawable.marvel_1_lollipop:R.drawable.marvel_1;
    int marvel_2 = isLollipop?R.drawable.marvel_2_lollipop:R.drawable.marvel_2;
    int marvel_3 = isLollipop?R.drawable.marvel_3_lollipop:R.drawable.marvel_3;
    int marvel_4 = isLollipop?R.drawable.marvel_4_lollipop:R.drawable.marvel_4;
    mLoadingView.addAnimation(Color.parseColor("#FFD200"),marvel_1,
        LoadingView.FROM_LEFT);
    mLoadingView.addAnimation(Color.parseColor("#2F5DA9"),marvel_2,
        LoadingView.FROM_TOP);
    mLoadingView.addAnimation(Color.parseColor("#FF4218"),marvel_3,
        LoadingView.FROM_RIGHT);
    mLoadingView.addAnimation(Color.parseColor("#C7E7FB"), marvel_4,
        LoadingView.FROM_BOTTOM);

    mLoadingView.addListener(new LoadingView.LoadingListener() {
      @Override public void onAnimationStart(int currentItemPosition) {

      }

      @Override public void onAnimationRepeat(int nextItemPosition) {

      }

      @Override public void onAnimationEnd(int nextItemPosition) {

      }
    });

     mLoadViewNoRepeat = (LoadingView) findViewById(R.id.loading_view);
    mLoadViewNoRepeat.addAnimation(Color.parseColor("#2F5DA9"), marvel_2, LoadingView.FROM_LEFT);
    mLoadViewNoRepeat.addAnimation(Color.parseColor("#FF4218"), marvel_3, LoadingView.FROM_LEFT);
    mLoadViewNoRepeat.addAnimation(Color.parseColor("#FFD200"), marvel_1, LoadingView.FROM_RIGHT);
    mLoadViewNoRepeat.addAnimation(Color.parseColor("#C7E7FB"), marvel_4, LoadingView.FROM_RIGHT);

     mLoadViewLong = (LoadingView) findViewById(R.id.loading_view_long);
    mLoadViewLong.addAnimation(Color.parseColor("#FF4218"), marvel_3, LoadingView.FROM_TOP);
    mLoadViewLong.addAnimation(Color.parseColor("#C7E7FB"), marvel_4, LoadingView.FROM_BOTTOM);
    mLoadViewLong.addAnimation(Color.parseColor("#FF4218"), marvel_3, LoadingView.FROM_TOP);
    mLoadViewLong.addAnimation(Color.parseColor("#C7E7FB"), marvel_4, LoadingView.FROM_BOTTOM);

  }

  @Override protected void onResume() {
    super.onResume();
  }

  public void pause(View v) {
    mLoadingView.pauseAnimation();
    mLoadViewLong.pauseAnimation();
    mLoadViewNoRepeat.pauseAnimation();
  }

  public void start(View v) {
    mLoadingView.startAnimation();
    mLoadViewLong.startAnimation();
    mLoadViewNoRepeat.startAnimation();
  }

  public void resume(View v) {
    mLoadingView.resumeAnimation();
    mLoadViewLong.resumeAnimation();
    mLoadViewNoRepeat.resumeAnimation();
  }
}
