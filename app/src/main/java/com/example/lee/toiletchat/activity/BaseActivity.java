package com.example.lee.toiletchat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.example.lee.toiletchat.R;

import butterknife.ButterKnife;
import utils.ActivityCollectorUtils;
import utils.LoggerUtils;

public abstract class BaseActivity extends Activity {

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initData();
        ActivityCollectorUtils.addActivity(this);
    }


    public void initData() {
        //初始化数据操作
        LoggerUtils.d("initData: father class initData");
    }

    public abstract int getLayoutId();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollectorUtils.removeActivity(this);
    }
}
