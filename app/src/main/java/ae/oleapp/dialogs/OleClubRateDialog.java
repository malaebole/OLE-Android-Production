package ae.oleapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.willy.ratingbar.BaseRatingBar;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.databinding.OleclubRatingDialogBinding;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleClubRateDialog extends Dialog {

    private OleclubRatingDialogBinding binding;
    private final Context context;
    private String clubId = "";
    private float rating = 5;

    public OleClubRateDialog(@NonNull Context context, String clubId) {
        super(context);
        this.context = context;
        this.clubId = clubId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = OleclubRatingDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.ratingBar.setRating(rating);
        binding.ratingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float ratings, boolean fromUser) {
                rating = ratings;

            }
        });

        binding.btnSubmit.setOnClickListener(v -> submitClicked());
    }

    private void submitClicked() {
        if (rating > 0) {
            rateClubAPI();
        }
        else {
            Functions.showToast(context, context.getString(R.string.rating_not_zero), FancyToast.ERROR);
        }
    }

    private void rateClubAPI() {
        String userId = Functions.getPrefValue(context, Constants.kUserID);
        KProgressHUD hud = Functions.showLoader(context, "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.rateClub(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), clubId, rating);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(context, object.getString(Constants.kMsg), FancyToast.SUCCESS);
                        }
                        else {
                            Functions.showToast(context, object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                        dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(context, e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(context, context.getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), context.getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }
}
