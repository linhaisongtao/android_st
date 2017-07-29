package com.netease.bobo.pathdemo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by daisongsong on 2017/7/29.
 */

public class LauncherActivity extends Activity {
    private View mDotView;
    private PathView mPathView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        mDotView = findViewById(R.id.mDotView);
        mPathView = (PathView) findViewById(R.id.mPathView);
        startAnimator();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startAnimator() {

        Path path = makePath();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mDotView, "x", "y", path);
        animator.setDuration(3000);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                MainActivity.start(LauncherActivity.this);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
        mPathView.setPath(path);
    }

    private Path makePath() {
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(100, 400);
        path.quadTo(150, 500, 200, 200);
        path.cubicTo(220, 100, 260, 600, 300, 300);
        path.arcTo(new RectF(0, 0, 600, 600), 180, -90);
        return path;
    }
}
