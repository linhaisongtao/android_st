package com.netease.bobo.pathdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.netease.bobo.pathdemo.stock.StockConfig;
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

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                                FileUtil.clearAll(StockConfig.getStockConfig().WHITE_LIST);
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

    public void onZZ500ListClicked(View view) {
        SListActivity.start(view.getContext(), "zz500.txt");
    }

    public void onNoneBankClicked(View view) {
        SListActivity.start(view.getContext(), "country.txt");
    }

    public void onBankClicked(View view) {
        SListActivity.start(view.getContext(), "consume.txt");
    }
}
