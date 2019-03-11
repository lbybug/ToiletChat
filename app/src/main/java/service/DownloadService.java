package service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.lee.toiletchat.R;
import com.example.lee.toiletchat.activity.MainActivity;

import java.io.File;

import listener.DownloadListener;
import utils.DownloadUtils;

public class DownloadService extends Service {

    public DownloadUtils downloadUtils;

    public DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1,getNotification("Downloading ...",progress));
        }

        @Override
        public void onSuccess() {
            downloadUtils = null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download Success",-1));
        }

        @Override
        public void onFailed() {
            downloadUtils = null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download Failed",-1));
        }

        @Override
        public void onPaused() {
            downloadUtils = null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Paused",-1));
        }

        @Override
        public void onCanceled() {
            downloadUtils = null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Cancel",-1));
        }
    };

    public String downloadUrl;

    public DownloadService() {
    }

    public DownloadBinder downloadBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return downloadBinder;
    }

    public class DownloadBinder extends Binder{

        public void startDownload(String url){
            if (downloadUtils == null){
                downloadUrl = url;
                downloadUtils = new DownloadUtils(listener);
                downloadUtils.execute(url);
                startForeground(1,getNotification("Download...",0));
            }
        }

        public void pausedDownload(){
            if (downloadUtils != null){
                downloadUtils.setPaused(true);
            }
        }

        public void cancelDownload(){
            if (downloadUtils != null) {
                downloadUtils.setCanceled(true);
            }else {
                if (downloadUrl != null){
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()){
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                }
            }
        }

    }

    private NotificationManager getNotificationManager(){
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
//        builder.setContentIntent(pi);
        if (progress > 0){
            builder.setContentText(progress + "%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();
    }

}
