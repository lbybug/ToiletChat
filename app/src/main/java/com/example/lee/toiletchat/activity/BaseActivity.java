package com.example.lee.toiletchat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import butterknife.ButterKnife;
import utils.ActivityCollectorUtils;

public abstract class BaseActivity extends Activity {

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        ActivityCollectorUtils.addActivity(this);
    }


    public abstract void initData();

    public abstract int getLayoutId();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollectorUtils.removeActivity(this);
    }
}
