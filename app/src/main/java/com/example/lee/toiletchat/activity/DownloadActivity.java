package com.example.lee.toiletchat.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lee.toiletchat.R;

import butterknife.BindView;
import butterknife.OnClick;
import service.DownloadService;
import utils.OkHttpUtils;

public class DownloadActivity extends BaseActivity {

    private static final String TAG = "DownloadActivity";

    @BindView(R.id.startDownload)
    Button startDownload;
    @BindView(R.id.pauseDownload)
    Button pauseDownload;
    @BindView(R.id.cancelDownload)
    Button cancelDownload;

    public static final int REQUEST_WRITE = 0x01;
    public static final String DOWNLOAD_URL = "http://192.168.70.167/Gprinter/Gprinter.apk";
    
    public boolean isCanDownload = false;

    public DownloadService.DownloadBinder downloadBinder;

    public ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            downloadBinder = (DownloadService.DownloadBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_download;
    }

    @Override
    public void initData() {
        Intent intent = new Intent(this,DownloadService.class);
        startService(intent);
        bindService(intent,conn,BIND_AUTO_CREATE);
        Log.d(TAG, "initData: start service");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE);
        }else {
            isCanDownload = true;
        }
    }

    @OnClick({R.id.startDownload, R.id.pauseDownload, R.id.cancelDownload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.startDownload:
                if (isCanDownload){
                    downloadBinder.startDownload(DOWNLOAD_URL);
                }
                break;
            case R.id.pauseDownload:
                if (isCanDownload){
                    downloadBinder.pausedDownload();
                }
                break;
            case R.id.cancelDownload:
                if (isCanDownload){
                    downloadBinder.cancelDownload();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_WRITE:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "无读写权限", Toast.LENGTH_SHORT).show();
                }else {
                    isCanDownload = true;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this,DownloadService.class);
        stopService(intent);
        unbindService(conn);
        OkHttpUtils.clean();
        Log.d(TAG, "onDestroy: 解除服务");
    }
}
