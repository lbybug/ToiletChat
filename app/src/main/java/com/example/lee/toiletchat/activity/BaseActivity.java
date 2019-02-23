package com.example.lee.toiletchat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.example.lee.toiletchat.R;

import utils.ActivityCollectorUtils;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_base);
        initData();
        ActivityCollectorUtils.addActivity(this);
    }

    public void initData(){
        //初始化数据操作
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollectorUtils.removeActivity(this);
    }
}
