package com.netease.bobo.pathdemo.stock.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.netease.bobo.pathdemo.R;
import com.netease.bobo.pathdemo.stock.model.SBasicInfo;
import com.netease.bobo.pathdemo.stock.model.StockManager;

/**
 * Created by gzdaisongsong@corp.netease.com on 2017/7/28.
 */

public class SAddActivity extends AppCompatActivity {
    private EditText mCodeEditText;
    private EditText mNameEditText;

    public static void startForResult(Activity activity, int requestCode) {
        activity.startActivityForResult(new Intent(activity, SAddActivity.class), requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_add);
        getSupportActionBar().setTitle("ADD");

        mCodeEditText = (EditText) findViewById(R.id.mCodeEditText);
        mNameEditText = (EditText) findViewById(R.id.mNameEditText);
    }

    public void onAddClicked(View view) {
        String code = mCodeEditText.getText().toString();
        String name = mNameEditText.getText().toString();

        if (TextUtils.isEmpty(code) || TextUtils.isEmpty(name)) {
            Toast.makeText(this, "code or name can not be null", Toast.LENGTH_SHORT).show();
        } else {
            StockManager.getStockManager().addSBasicInfo(new SBasicInfo(code, name));
            setResult(RESULT_OK);
            finish();
        }
    }
}
