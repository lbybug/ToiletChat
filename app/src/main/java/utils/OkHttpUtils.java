package utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpUtils {

    public static OkHttpClient client;

    public static OkHttpClient getOkHttp(){
        if (client == null) {
            client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10,TimeUnit.SECONDS)
                    .writeTimeout(10,TimeUnit.SECONDS).build();
        }
        return client;
    }

    public static void clean(){
        if (client != null) {
            client = null;
        }
    }

}
