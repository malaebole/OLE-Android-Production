package ae.oleapp.player;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.adapters.OleProfileLevelDetailAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityProfileLevelDetailsBinding;
import ae.oleapp.dialogs.OleCustomAlertDialog;
import ae.oleapp.models.OlePlayerLevel;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleProfileLevelDetailsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityProfileLevelDetailsBinding binding;
    private String levelId = "";
    private String playerId = "";
    private String module = "";
    private OlePlayerLevel olePlayerLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityProfileLevelDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            levelId = bundle.getString("level_id", "");
            playerId = bundle.getString("player_id", "");
            module = bundle.getString("module", "");
        }

        binding.rewardsVu.setVisibility(View.GONE);
        binding.tvPrevLevelNumber.setText("");
        binding.tvNextLevelNumber.setText("");

        if (module.equalsIgnoreCase(Constants.kPadelModule)) {
            binding.headerImgVu.setImageResource(R.drawable.padel_profile_header);
            binding.cardImgVu.setImageResource(R.drawable.padel_level_bg);
            binding.prevLevelNumberBg.setImageResource(R.drawable.padel_level_number_bg_yellow);
            binding.nextLevelNumberBg.setImageResource(R.drawable.padel_level_number_bg_green);
            binding.btnImg.setVisibility(View.INVISIBLE);
            binding.btnCollect.setCardBackgroundColor(getResources().getColor(R.color.yellowColor));
            binding.tvCollect.setTextColor(Color.parseColor("#845700"));
        }
        else {
            binding.headerImgVu.setImageResource(R.drawable.profile_header);
            binding.cardImgVu.setImageResource(R.drawable.level_bg);
            binding.prevLevelNumberBg.setImageResource(R.drawable.football_level_number_bg_yellow);
            binding.nextLevelNumberBg.setImageResource(R.drawable.football_level_number_bg_green);
            binding.btnImg.setVisibility(View.VISIBLE);
            binding.btnCollect.setCardBackgroundColor(Color.TRANSPARENT);
            binding.tvCollect.setTextColor(Color.WHITE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);

        getLevelAPI(true);

        binding.backBtn.setOnClickListener(this);
        binding.btnCollect.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.backBtn) {
            finish();
        }
        else if (view == binding.btnCollect) {
            if (olePlayerLevel != null) {
                if (olePlayerLevel.getRewardType().equalsIgnoreCase("other")) {
                    Functions.showAlert(getContext(), getString(R.string.collect_reward), olePlayerLevel.getCollectionMsg(), null);
                }
                else if (olePlayerLevel.getRewardType().equalsIgnoreCase("cashback")) {
                    collectRewardAPI(true, olePlayerLevel.getId(), olePlayerLevel.getRewardAmount());
                }
            }
        }
    }

    private void populateData() {
        if (olePlayerLevel == null) {
            return;
        }
        binding.toolbarTitle.setText(olePlayerLevel.getTitle());
        binding.tvPrevLevelNumber.setText(olePlayerLevel.getPrevLevelValue());
        binding.tvNextLevelNumber.setText(olePlayerLevel.getNextLevelValue());
        binding.seekBar.setProgress(Math.round(olePlayerLevel.getCompletionPercentage()));
        binding.seekBar.setEnabled(false);
        binding.tvRewardsTitle.setText(String.format("Level %s Reward", olePlayerLevel.getNextLevelValue()));
        binding.tvRewardsDesc.setText(olePlayerLevel.getNextLevelReward());
        if (olePlayerLevel.getNextLevelReward().equalsIgnoreCase("")) {
            binding.rewardsVu.setVisibility(View.GONE);
        }
        else {
            binding.rewardsVu.setVisibility(View.VISIBLE);
        }
        if (!olePlayerLevel.getRewardType().isEmpty() && playerId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
            if (olePlayerLevel.getRewardCollected().equalsIgnoreCase("1")) {
                binding.btnCollect.setVisibility(View.GONE);
            }
            else {
                binding.btnCollect.setVisibility(View.VISIBLE);
            }
        }
        else {
            binding.btnCollect.setVisibility(View.GONE);
        }
        OleProfileLevelDetailAdapter adapter = new OleProfileLevelDetailAdapter(getContext(), olePlayerLevel.getTargets(), module);
        binding.recyclerVu.setAdapter(adapter);
    }

    private void getLevelAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.profileLevelDetails(Functions.getAppLang(getContext()), playerId, levelId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            olePlayerLevel = gson.fromJson(obj.toString(), OlePlayerLevel.class);
                            populateData();
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void collectRewardAPI(boolean isLoader, String levelId, String amount) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.cashbackToPlayer(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), levelId, amount, Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showAlert(getContext(), getString(R.string.success), object.getString(Constants.kMsg), new OleCustomAlertDialog.OnDismiss() {
                                @Override
                                public void dismiss() {
                                    finish();
                                }
                            });
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }
}