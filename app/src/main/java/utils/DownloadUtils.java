package utils;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import listener.DownloadListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lee on 2019/3/11.
 */


public class DownloadUtils extends AsyncTask<String,Integer,Integer>{

    public static final int TYPE_SUCCESS = 0x01;
    public static final int TYPE_FAILED = 0x02;
    public static final int TYPE_PAUSED = 0x03;
    public static final int TYPE_CANCELED = 0x04;

    public DownloadListener downloadListener;

    private boolean isCanceled = false;

    private boolean isPaused = false;

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public DownloadUtils(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
        isCanceled = false;
        isPaused = false;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;
        try {
            long downloadedLength = 0;
            String downloadUrl = strings[0];
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory + fileName);
            if (file.exists()){
                downloadedLength = file.length(); //已存在文件长度
            }
            long contentLength = getContentLength(downloadUrl); //获取到的文件长度
            if (contentLength == 0){
                return TYPE_FAILED;
            }else if (contentLength == downloadedLength){
                return TYPE_SUCCESS;
            }
            OkHttpClient client = OkhttpUtils.getOkHttp();
            Request request = new Request.Builder().addHeader("RANGE","bytes=" + downloadedLength + "-").url(downloadUrl).build();
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()){
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file,"rw");
                savedFile.seek(downloadedLength);
                byte[] bytes = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(bytes)) != -1){
                    if (isCanceled){
                        return TYPE_CANCELED;
                    }else if (isPaused){
                        return TYPE_PAUSED;
                    }else {
                        total += len;
                        savedFile.write(bytes,0,len);
                        int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (is != null){
                    is.close();
                }
                if (savedFile != null){
                    savedFile.close();
                }
                if (isCanceled && file != null){
                    file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    public int lastProgress = 0;

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int progress = values[0];
        if (progress > lastProgress){
            downloadListener.onProgress(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        switch (integer){
            case TYPE_SUCCESS:
                downloadListener.onSuccess();
                break;
            case TYPE_FAILED:
                downloadListener.onFailed();
                break;
            case TYPE_PAUSED:
                downloadListener.onPaused();
                break;
            case TYPE_CANCELED:
                downloadListener.onCanceled();
                break;
        }
    }

    private long getContentLength(String downloadUrl) {
        try {
            OkHttpClient client = OkhttpUtils.getOkHttp();
            Request request = new Request.Builder().url(downloadUrl).build();
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.close();
                return contentLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
