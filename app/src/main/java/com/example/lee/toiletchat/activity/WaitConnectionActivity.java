package com.example.lee.toiletchat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.lee.toiletchat.R;

import utils.LoggerUtils;

public class WaitConnectionActivity extends BaseActivity {

    public int platform = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_connection);
    }

    @Override
    public void initData() {
        super.initData();
        platform = getIntent().getIntExtra("platform",0);
        LoggerUtils.d(String.valueOf(platform));
    }

    public static void actionStart(Context context, int type){ //外部启动
        Intent intent = new Intent(context,WaitConnectionActivity.class);
        intent.putExtra("platform",type);
        context.startActivity(intent);
    }

}
