package com.netease.bobo.pathdemo;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.netease.bobo.pathdemo.stock.detail.StockDetailActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private View mDotView;
    private PathView mPathView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDotView = findViewById(R.id.mDotView);
        mPathView = (PathView) findViewById(R.id.mPathView);
        mDotView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimator();
            }
        });

        Observable.create(new ObservableOnSubscribe<Path>() {
            @Override
            public void subscribe(ObservableEmitter<Path> e) throws Exception {
                Path path = new Path();
                path.moveTo(0, 0);
                for (int i = 0; i < 100; i++) {
                    path.lineTo(10 * i, 500 + 300 * (float) Math.sin(i));
                }
                e.onNext(path);
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Path>() {
                    @Override
                    public void accept(Path path) throws Exception {
                        mPathView.setPath(path);
                    }
                });

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startAnimator() {

        Path path = makePath();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mDotView, "x", "y", path);
        animator.setDuration(3000);
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

    public void onDetailViewClicked(View view) {
        StockDetailActivity.start(view.getContext());
    }
}
