package utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lee on 2019/2/23.
 */


public class ActivityCollectorUtils {

    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity){
        if (activity != null) {
            activities.add(activity);
        }
    }

    public static void removeActivity(Activity activity){
        if (activity != null) {
            activities.remove(activity);
        }
    }

    public static void finishAll(){
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }


}
