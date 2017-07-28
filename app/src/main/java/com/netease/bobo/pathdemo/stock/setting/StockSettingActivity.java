package com.netease.bobo.pathdemo.stock.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.netease.bobo.pathdemo.R;
import com.netease.bobo.pathdemo.stock.StockConfig;

/**
 * Created by daisongsong on 2017/7/28.
 */

public class StockSettingActivity extends AppCompatActivity {
    private EditText mRoeRatioEditText;
    private EditText mBenefitYearEditText;
    private EditText mRoeShowYearEditText;
    private EditText mRoeAverageYearEditText;
    private EditText mPbPositionSellEditText;
    private EditText mPbYearCountEditText;

    public static void start(Context context) {
        context.startActivity(new Intent(context, StockSettingActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_setting);
        getSupportActionBar().setTitle("SETTING");
        mRoeRatioEditText = (EditText) findViewById(R.id.mRoeRatioEditText);
        mBenefitYearEditText = (EditText) findViewById(R.id.mBenefitYearEditText);
        mRoeShowYearEditText = (EditText) findViewById(R.id.mRoeShowYearEditText);
        mRoeAverageYearEditText = (EditText) findViewById(R.id.mRoeAverageYearEditText);
        mPbPositionSellEditText = (EditText) findViewById(R.id.mPbPositionSellEditText);
        mPbYearCountEditText = (EditText) findViewById(R.id.mPbYearCountEditText);

        mRoeRatioEditText.setText(String.valueOf(StockConfig.getStockConfig().FUTURE_ROE_RATIO));
        mBenefitYearEditText.setText(String.valueOf(StockConfig.getStockConfig().BENEFIT_YEAR_COUNT));
        mRoeShowYearEditText.setText(String.valueOf(StockConfig.getStockConfig().ROE_SHOW_YEAR_COUNT));
        mRoeAverageYearEditText.setText(String.valueOf(StockConfig.getStockConfig().AVERAGE_ROE_COUNT));
        mPbPositionSellEditText.setText(String.valueOf(StockConfig.getStockConfig().SELL_PB_POSITION));
        mPbYearCountEditText.setText(String.valueOf(StockConfig.getStockConfig().PB_YEAR_COUNT));
    }

    public void onSaveClicked(View view) {
        StockConfig config = StockConfig.getStockConfig();
        config.FUTURE_ROE_RATIO = Float.parseFloat(mRoeRatioEditText.getText().toString());
        config.BENEFIT_YEAR_COUNT = Integer.parseInt(mBenefitYearEditText.getText().toString());
        config.ROE_SHOW_YEAR_COUNT = Integer.parseInt(mRoeShowYearEditText.getText().toString());
        config.AVERAGE_ROE_COUNT = Integer.parseInt(mRoeAverageYearEditText.getText().toString());
        config.SELL_PB_POSITION = Float.parseFloat(mPbPositionSellEditText.getText().toString());
        config.PB_YEAR_COUNT = Integer.parseInt(mPbYearCountEditText.getText().toString());

        StockConfig.saveConfig(config);
        finish();
    }
}
