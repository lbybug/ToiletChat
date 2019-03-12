package com.example.lee.toiletchat.activity;

import android.view.View;
import android.widget.Button;

import com.example.lee.toiletchat.R;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.chooseServer)
    Button chooseServer;
    @BindView(R.id.chooseClient)
    Button chooseClient;


    @Override
    public void initData() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }


    @OnClick({R.id.chooseServer, R.id.chooseClient})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.chooseServer:
                WaitConnectionActivity.actionStart(this,0);
                finish();
                break;
            case R.id.chooseClient:
                WaitConnectionActivity.actionStart(this,1);
                finish();
                break;
        }
    }
}
