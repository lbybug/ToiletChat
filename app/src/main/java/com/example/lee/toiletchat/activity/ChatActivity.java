package com.example.lee.toiletchat.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.example.lee.toiletchat.R;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends BaseActivity {

    public ChatHandler chatHandler;

    @BindView(R.id.chatList)
    RecyclerView chatList;
    @BindView(R.id.editMsg)
    EditText editMsg;
    @BindView(R.id.sendMsg)
    Button sendMsg;


    @Override
    public void initData() {
        super.initData();
        chatHandler = new ChatHandler(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_chat;
    }

    @OnClick(R.id.sendMsg)
    public void onViewClicked() {
        sendMsg();
    }

    private void sendMsg() {
        String content = editMsg.getText().toString().trim();
        if (!TextUtils.isEmpty(content)){
            //此处发送数据
        }
    }

    static class ChatHandler extends Handler {

        public WeakReference<ChatActivity> weakReference;

        public ChatHandler(ChatActivity chatActivity) {
            weakReference = new WeakReference<>(chatActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            ChatActivity activity = weakReference.get();
            if (activity != null) {

            }
        }
    }
}
