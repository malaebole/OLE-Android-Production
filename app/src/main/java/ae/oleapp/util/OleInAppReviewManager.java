package ae.oleapp.util;

import static ae.oleapp.util.OleInAppReviewPreferenceHelper.getInstallDate;
import static ae.oleapp.util.OleInAppReviewPreferenceHelper.getLaunchTimes;
import static ae.oleapp.util.OleInAppReviewPreferenceHelper.getRemindInterval;
import static ae.oleapp.util.OleInAppReviewPreferenceHelper.isFirstLaunch;
import static ae.oleapp.util.OleInAppReviewPreferenceHelper.setInstallDate;
import static ae.oleapp.util.OleInAppReviewPreferenceHelper.setRemindIntervalDate;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

//import com.google.android.play.core.review.ReviewInfo;
//import com.google.android.play.core.review.ReviewManager;
//import com.google.android.play.core.review.ReviewManagerFactory;
//import com.google.android.play.core.tasks.Task;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import java.util.Date;

public class OleInAppReviewManager {

    private static OleInAppReviewManager singleton;
    private final Context context;
    private int installDate = 10;
    private int launchTimes = 10;
    private int remindInterval = 1;
    private boolean isDebug = false;

    private OleInAppReviewManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static OleInAppReviewManager with(Context context) {
        if (singleton == null) {
            synchronized (OleInAppReviewManager.class) {
                if (singleton == null) {
                    singleton = new OleInAppReviewManager(context);
                }
            }
        }
        return singleton;
    }

    public static boolean showRateDialogIfMeetsConditions(Activity activity) {
        boolean isMeetsConditions = singleton.isDebug || singleton.shouldShowRateDialog();
        if (isMeetsConditions) {
            singleton.showRateDialog(activity);
        }
        return isMeetsConditions;
    }

    private static boolean isOverDate(long targetDate, int threshold) {
        return new Date().getTime() - targetDate >= (long) threshold * 24 * 60 * 60 * 1000;
    }

    public OleInAppReviewManager setLaunchTimes(int launchTimes) {
        this.launchTimes = launchTimes;
        return this;
    }

    public OleInAppReviewManager setInstallDays(int installDate) {
        this.installDate = installDate;
        return this;
    }

    public OleInAppReviewManager setRemindInterval(int remindInterval) {
        this.remindInterval = remindInterval;
        return this;
    }

    public void monitor() {
        if (isFirstLaunch(context)) {
            setInstallDate(context);
        }
        OleInAppReviewPreferenceHelper.setLaunchTimes(context, getLaunchTimes(context) + 1);
    }

    public void showRateDialog(Activity activity) {
        if (!activity.isFinishing()) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showDialog(activity);
                }
            }, 5000);
        }
    }

    public boolean shouldShowRateDialog() {
        return isOverLaunchTimes() &&
                isOverInstallDate() &&
                isOverRemindDate();
    }

    private boolean isOverLaunchTimes() {
        return getLaunchTimes(context) >= launchTimes;
    }

    private boolean isOverInstallDate() {
        return isOverDate(getInstallDate(context), installDate);
    }

    private boolean isOverRemindDate() {
        return isOverDate(getRemindInterval(context), remindInterval);
    }

    public boolean isDebug() {
        return isDebug;
    }

    public OleInAppReviewManager setDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    public void showDialog(Activity activity) {
        ReviewManager manager = ReviewManagerFactory.create(context);
        Task<ReviewInfo> request = manager.requestReviewFlow();

        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                startReview(activity, manager, reviewInfo);
            } else {
                // There was some problem, continue regardless of the result.
                setRemindIntervalDate(context);
            }
        });
    }

    public void startReview(Activity activity, ReviewManager manager, ReviewInfo reviewInfo) {
        Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);
        flow.addOnCompleteListener(task -> {
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
            setRemindIntervalDate(context);
        });
    }
}
