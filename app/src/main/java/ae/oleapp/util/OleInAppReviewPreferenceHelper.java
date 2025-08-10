package ae.oleapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class OleInAppReviewPreferenceHelper {

    private OleInAppReviewPreferenceHelper() {
    }

    static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    static SharedPreferences.Editor getPreferencesEditor(Context context) {
        return getPreferences(context).edit();
    }

    static void setRemindIntervalDate(Context context) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.remove(Constants.kAppRemindInterval);
        editor.putLong(Constants.kAppRemindInterval, new Date().getTime());
        editor.apply();
    }

    static long getRemindInterval(Context context) {
        return getPreferences(context).getLong(Constants.kAppRemindInterval, 0);
    }

    static void setInstallDate(Context context) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putLong(Constants.kAppInstallDate, new Date().getTime());
        editor.apply();
    }

    static long getInstallDate(Context context) {
        return getPreferences(context).getLong(Constants.kAppInstallDate, 0);
    }

    static void setLaunchTimes(Context context, int launchTimes) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putInt(Constants.kAppLaunchTimes, launchTimes);
        editor.apply();
    }

    static int getLaunchTimes(Context context) {
        return getPreferences(context).getInt(Constants.kAppLaunchTimes, 0);
    }

    static boolean isFirstLaunch(Context context) {
        return getPreferences(context).getLong(Constants.kAppInstallDate, 0) == 0L;
    }
}
