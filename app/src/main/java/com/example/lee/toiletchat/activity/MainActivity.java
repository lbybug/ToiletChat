package com.example.lee.toiletchat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.lee.toiletchat.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.chooseServer)
    Button chooseServer;
    @BindView(R.id.chooseClient)
    Button chooseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        super.initData();
    }


    @OnClick({R.id.chooseServer, R.id.chooseClient})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.chooseServer:
                WaitConnectionActivity.actionStart(this,0);
                break;
            case R.id.chooseClient:
                WaitConnectionActivity.actionStart(this,1);
                break;
        }
    }
}
