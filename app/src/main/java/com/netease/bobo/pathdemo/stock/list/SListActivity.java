package com.netease.bobo.pathdemo.stock.list;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.bobo.pathdemo.R;
import com.netease.bobo.pathdemo.stock.add.SAddActivity;
import com.netease.bobo.pathdemo.stock.detail.StockDetailActivity;
import com.netease.bobo.pathdemo.stock.model.SBasicInfo;
import com.netease.bobo.pathdemo.stock.model.StockManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by gzdaisongsong@corp.netease.com on 2017/7/28.
 */

public class SListActivity extends AppCompatActivity {
    private ListView mListView;
    private SListAdapter mAdapter = new SListAdapter();

    public static void start(Context context) {
        context.startActivity(new Intent(context, SListActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_list);
        getSupportActionBar().setTitle("LIST");

        mListView = (ListView) findViewById(R.id.mListView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SBasicInfo info = (SBasicInfo) parent.getItemAtPosition(position);
                StockDetailActivity.start(view.getContext(), info);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final SBasicInfo info = (SBasicInfo) parent.getItemAtPosition(position);
                new AlertDialog.Builder(view.getContext())
                        .setTitle("TIPS")
                        .setMessage("about " + info.name + "[" + info.code + "]")
                        .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Observable.create(new ObservableOnSubscribe<Object>() {
                                    @Override
                                    public void subscribe(ObservableEmitter<Object> e) throws Exception {
                                        StockManager.getStockManager().deleteSBasicInfo(info.code);
                                        e.onNext("delete success");
                                    }
                                }).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<Object>() {
                                            @Override
                                            public void accept(Object o) throws Exception {
                                                Toast.makeText(SListActivity.this, String.valueOf(o), Toast.LENGTH_SHORT).show();
                                                refreshData();
                                            }
                                        });
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("to top", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Observable.create(new ObservableOnSubscribe<Object>() {
                                    @Override
                                    public void subscribe(ObservableEmitter<Object> e) throws Exception {
                                        StockManager.getStockManager().deleteSBasicInfo(info.code);
                                        StockManager.getStockManager().addSBasicInfo(info);
                                        e.onNext("to top success");
                                    }
                                }).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<Object>() {
                                            @Override
                                            public void accept(Object o) throws Exception {
                                                refreshData();
                                            }
                                        });
                            }
                        })
                        .show();
                return true;
            }
        });

        refreshData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("ADD");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ("ADD".equals(item.getTitle())) {
            SAddActivity.startForResult(this, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            refreshData();
        }
    }

    private void refreshData() {
        Observable.create(new ObservableOnSubscribe<List<SBasicInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SBasicInfo>> e) throws Exception {
                List<SBasicInfo> selectedSList = StockManager.getStockManager().getSelectedSList();
                e.onNext(selectedSList);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SBasicInfo>>() {
                    @Override
                    public void accept(List<SBasicInfo> sBasicInfos) throws Exception {
                        mAdapter.setInfos(sBasicInfos);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    private static final class SListAdapter extends BaseAdapter {
        private List<SBasicInfo> mInfos = new ArrayList<>();

        public void setInfos(List<SBasicInfo> infos) {
            mInfos = infos;
        }

        @Override
        public int getCount() {
            return mInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return mInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_s_baisc, parent, false);
            }
            TextView nameTextView = (TextView) convertView.findViewById(R.id.mNameTextView);
            TextView codeTextView = (TextView) convertView.findViewById(R.id.mCodeTextView);
            SBasicInfo info = mInfos.get(position);
            nameTextView.setText(String.valueOf(info.name));
            codeTextView.setText(String.valueOf(info.code));
            return convertView;
        }
    }
}
