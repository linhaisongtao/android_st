package com.netease.bobo.pathdemo.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.netease.bobo.pathdemo.R;

/**
 * Created by gzdaisongsong@corp.netease.com on 2017/7/28.
 */

public class LoadingView extends FrameLayout {
    private ProgressBar mProgressBar;

    public LoadingView(@NonNull Context context) {
        super(context);
        init();
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_loading, this, true);
        mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);
    }

    public void showLoading() {
        setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        setVisibility(View.GONE);
    }

}
