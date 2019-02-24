package utils;

import android.util.Log;

/**
 * Created by Lee on 2019/2/24.
 */

public class LoggerUtils {

    private static final String TAG = "LoggerUtils";

    public static final int VERBOSE = 0;

    public static final int DEBUG = 1;

    public static final int INFO = 2;

    public static final int WARM = 3;

    public static final int ERROR = 4;

    public static final int VERSION = 5;

    public static void v(String content){
        if (VERBOSE < VERSION){
            Log.v(TAG,content);
        }
    }

    public static void d(String content){
        if (DEBUG < VERSION){
            Log.d(TAG,content);
        }
    }

    public static void i(String content){
        if (INFO < VERSION){
            Log.i(TAG,content);
        }
    }

    public static void w(String content){
        if (WARM < VERSION){
            Log.w(TAG,content);
        }
    }

    public static void e(String content){
        if (ERROR < VERSION){
            Log.e(TAG,content);
        }
    }

}
