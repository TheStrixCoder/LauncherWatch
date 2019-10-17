package com.bid.launcherwatch.view;



import android.util.Log;
import java.text.DecimalFormat;

public class CalculatorUtil {
    private static String TAG = "CalculatorUtil";
    public static DecimalFormat df_1 = new DecimalFormat("###.0");
    public static DecimalFormat df_2 = new DecimalFormat("###.00");
    public static DecimalFormat df_4 = new DecimalFormat("###.0000");

    public static String getDistance(int paramInt1, double paramDouble, int paramInt2) {
        if (paramInt2 == 1) {
            Log.d(TAG, "getDistance:  distance1 = " + getStringMath((((0.415d * paramDouble) * ((double) paramInt1)) / 100000.0d) + ""));
            return getStringMath((((0.415d * paramDouble) * ((double) paramInt1)) / 100000.0d) + "");
        }
        Log.d(TAG, "getDistance:  distance2 = " + getStringMath((((0.413d * paramDouble) * ((double) paramInt1)) / 100000.0d) + ""));
        return getStringMath((((0.413d * paramDouble) * ((double) paramInt1)) / 100000.0d) + "");
    }

    public static String getDistance(int paramInt, User paramUser) {
        Log.d(TAG, "getDistance:  step = " + paramInt + "  user = " + paramUser);
        if (paramUser == null) {
            return getStringMath(((((double) paramInt) * 0.7d) / 1000.0d) + "");
        }
        return getDistance(paramInt, paramUser.getHeight(), paramUser.getSex());
    }

    public static String getStringMath(String s) {
        if (s.contains("E")) {
            s = "0.0";
        } else if (s.contains(",")) {
            return s.substring(0, s.lastIndexOf(",") + 2);
        } else {
            if (s.contains(".")) {
                return s.substring(0, s.lastIndexOf(".") + 2);
            }
        }
        return s;
    }
}

