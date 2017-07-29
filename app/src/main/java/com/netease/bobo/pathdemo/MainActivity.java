package com.netease.bobo.pathdemo;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.netease.bobo.pathdemo.stock.detail.StockDetailActivity;
import com.netease.bobo.pathdemo.stock.list.SListActivity;
import com.netease.bobo.pathdemo.stock.setting.StockSettingActivity;
import com.netease.bobo.pathdemo.util.FileUtil;

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

    public void onClearCacheClicked(View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("TIPS")
                .setMessage("clear cache ?")
                .setPositiveButton("clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Observable.create(new ObservableOnSubscribe<Object>() {
                            @Override
                            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                                FileUtil.clearAll();
                                e.onNext("clear cache success");
                            }
                        }).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Object>() {
                                    @Override
                                    public void accept(Object o) throws Exception {
                                        Toast.makeText(MainActivity.this, String.valueOf(o), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    public void onToListClicked(View view) {
        SListActivity.start(view.getContext());
    }

    public void onStockSettingClicked(View view) {
        StockSettingActivity.start(view.getContext());
    }

    public void onHS300ListClicked(View view) {
        SListActivity.start(view.getContext(), "hs300.txt");
    }
}
