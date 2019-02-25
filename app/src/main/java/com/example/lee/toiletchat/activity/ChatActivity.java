package com.example.lee.toiletchat.activity;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.example.lee.toiletchat.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import adapter.ChatAdapter;
import bean.MsgBean;
import butterknife.BindView;
import butterknife.OnClick;
import listener.ChatListener;
import service.BluetoothService;
import utils.ChatUtils;
import utils.LoggerUtils;

public class ChatActivity extends BaseActivity {

    public static final int NOTIFY_MSG = 0x07;

    public ChatHandler chatHandler;

    @BindView(R.id.chatList)
    RecyclerView chatList;
    @BindView(R.id.editMsg)
    EditText editMsg;
    @BindView(R.id.sendMsg)
    Button sendMsg;

    int platform = 0;

    public BluetoothService bluetoothService;
    public BluetoothSocket bluetoothSocket;
    public ServerThread thread;

    public ChatAdapter chatAdapter;

    public List<MsgBean> msgBeanList;


    @Override
    public void initData() {
        super.initData();
        chatHandler = new ChatHandler(this);
        msgBeanList = new ArrayList<>();
        chatAdapter = new ChatAdapter(msgBeanList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(chatAdapter);
        platform = getIntent().getIntExtra("platform",0);
        if (platform == 0){
            bluetoothSocket = ChatUtils.getBluetoothSocket();
            startReadWriteThread();
        }else {
            bluetoothService = ChatUtils.getBluetoothService();
            bluetoothService.setListener(new ChatListener() {
                @Override
                public void onReceived(String content) {
                    MsgBean msg = new MsgBean();
                    msg.setContent(content);
                    msg.setType(MsgBean.RECEIVED);
                    msgBeanList.add(msg);
                    chatHandler.obtainMessage(NOTIFY_MSG).sendToTarget();
                }
            });
        }
    }

    private void startReadWriteThread() {
        LoggerUtils.d("开启读写线程");
        thread = new ServerThread(bluetoothSocket, new ChatListener() {
            @Override
            public void onReceived(String content) {
                MsgBean msg = new MsgBean();
                msg.setContent(content);
                msg.setType(MsgBean.RECEIVED);
                msgBeanList.add(msg);
                chatHandler.obtainMessage(NOTIFY_MSG).sendToTarget();
            }
        });
        thread.start();
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
            if (platform == 0){
                thread.write(content.getBytes());
            }else {
                bluetoothService.write(content.getBytes());
            }
            MsgBean msgBean = new MsgBean();
            msgBean.setContent(content);
            msgBean.setType(MsgBean.SEND);
            msgBeanList.add(msgBean);
            editMsg.setText("");
            chatHandler.obtainMessage(NOTIFY_MSG).sendToTarget();
        }
    }

    public static void actionStart(Context context,int platform){
        Intent intent = new Intent(context,ChatActivity.class);
        intent.putExtra("platform",platform);
        context.startActivity(intent);
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
                switch (msg.what){
                    case NOTIFY_MSG:
                        activity.chatAdapter.notifyDataSetChanged();
                        activity.chatList.scrollToPosition(activity.msgBeanList.size() - 1);
                        break;
                }
            }
        }
    }

    public class ServerThread extends Thread{

        public BluetoothSocket socket;
        public InputStream inputStream;
        public OutputStream outputStream;
        public ChatListener listener;

        public ServerThread(BluetoothSocket socket,ChatListener listener) {
            try {
                this.socket = socket;
                this.listener = listener;
                this.inputStream = socket.getInputStream();
                this.outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    LoggerUtils.d("循环着");
                    bytes = inputStream.read(buffer);
                    String result = new String(buffer, 0, bytes);
                    LoggerUtils.d(result);
                    if (listener != null){
                        listener.onReceived(result);
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);
            } catch (IOException e) {

            }
        }
    }

}
