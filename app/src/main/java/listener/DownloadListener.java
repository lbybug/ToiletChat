package listener;

/**
 * Created by Lee on 2019/3/11.
 */


public interface DownloadListener {

    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();

}
