package ae.oleapp.models;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shashank.sony.fancytoastlib.FancyToast;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.util.Functions;

public class RewardedAdManager extends BaseActivity {

    private final Context context;
    private RewardedAd rewardedAd;

    public Boolean status = false;

    public RewardedAdManager(Context context) {
        this.context = context;
    }

    public void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(
                context,
                context.getString(R.string.test_jersey_reward_ad), // Replace with your own ad unit ID test_jersey_reward_ad
                adRequest,
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedAd ad) {
                        rewardedAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        // Handle ad loading failure
                        rewardedAd = null;
                    }
                }
        );
    }

    public void showRewardedAd() {
        if (rewardedAd != null) {
            rewardedAd.show((Activity) context, rewardItem -> {
                // Handle the user's completion of the rewarded ad
                // Unlock the jersey here
                int rewardAmount = rewardItem.getAmount();
                String rewardType = rewardItem.getType();
                //unlockOneJersey(shirtId);
                setStatus(true);
                Functions.showToast(context, "Reward Granted! Jersey unlocked.", FancyToast.SUCCESS);

                loadRewardedAd();
            });
        } else {
            Functions.showToast(context, "Ad is not available. Please try again later.", FancyToast.ERROR);
            setStatus(false);
        }
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }


}
