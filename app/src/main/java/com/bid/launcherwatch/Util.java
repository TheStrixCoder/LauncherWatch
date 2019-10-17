package com.bid.launcherwatch;



import android.app.Activity;
import java.util.LinkedList;
import java.util.List;

public class Util {
    public static List<Activity> activityList = new LinkedList();

    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity) {
        if (activityList != null) {
            boolean bResult = activityList.remove(activity);
            while (bResult) {
                bResult = activityList.remove(activity);
            }
        }
    }

    public static void exic() {
        if (activityList.size() > 0) {
            for (Activity activitys : activityList) {
                try {
                    activitys.finish();
                } catch (Exception e) {
                }
            }
        }
        System.exit(0);
    }
}
