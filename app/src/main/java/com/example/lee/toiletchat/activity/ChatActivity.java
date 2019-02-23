package com.example.lee.toiletchat.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

import com.example.lee.toiletchat.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends BaseActivity {

    @BindView(R.id.chatList)
    RecyclerView chatList;
    @BindView(R.id.editMsg)
    EditText editMsg;
    @BindView(R.id.sendMsg)
    Button sendMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        super.initData();
    }

    @OnClick(R.id.sendMsg)
    public void onViewClicked() {
    }
}
