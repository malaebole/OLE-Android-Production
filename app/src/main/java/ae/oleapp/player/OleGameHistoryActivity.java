package ae.oleapp.player;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityGameHistoryBinding;
import ae.oleapp.models.OleGameHistory;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import io.github.rockerhieu.emojiconize.Emojiconize;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleGameHistoryActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityGameHistoryBinding binding;
    private String matchType = "";
    private String bookingId = "";
    private String playerId = "";
    private String requestStatus = "";
    private String playerConfirmed = "";
    private String bookingStatus = "";
    private String creatorId = "";
    private boolean isFromMatchDetail = false;
    private boolean isFromDetail = false;
    private OleGameHistory oleGameHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Emojiconize.activity(this).go();
        super.onCreate(savedInstanceState);
        binding = OleactivityGameHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.ole_game_history);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            matchType = bundle.getString("match_type", "");
            bookingId = bundle.getString("booking_id", "");
            playerId = bundle.getString("player_id", "");
            requestStatus = bundle.getString("req_status", "");
            playerConfirmed = bundle.getString("player_confirmed", "");
            bookingStatus = bundle.getString("booking_status", "");
            creatorId = bundle.getString("creator_id", "");
            isFromMatchDetail = bundle.getBoolean("match_detail", false);
            isFromDetail = bundle.getBoolean("booking_detail", false);
        }

        if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
            binding.btnReject.setVisibility(View.GONE);
            if (isFromDetail) {
                binding.tvAccept.setText(R.string.confirm);
                if (playerConfirmed.equalsIgnoreCase("1") || bookingStatus.equalsIgnoreCase(Constants.kFinishedBooking)) {
                    binding.btnAccept.setVisibility(View.GONE);
                } else {
                    binding.btnAccept.setVisibility(View.VISIBLE);
                }
            } else if (isFromMatchDetail) {
                binding.tvAccept.setText(R.string.confirm);
                if (playerConfirmed.equalsIgnoreCase("1") || bookingStatus.equalsIgnoreCase(Constants.kFinishedBooking) || !playerId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                    binding.btnAccept.setVisibility(View.GONE);
                } else {
                    binding.btnAccept.setVisibility(View.VISIBLE);
                }
            } else {
                binding.tvAccept.setText(R.string.accept);
                binding.btnReject.setVisibility(View.VISIBLE);
            }
        }
        else {
            binding.btnReject.setVisibility(View.GONE);
            binding.btnAccept.setVisibility(View.GONE);
        }

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.imgVu.setOnClickListener(this);
        binding.btnReject.setOnClickListener(this);
        binding.btnAccept.setOnClickListener(this);
        binding.relReviews.setOnClickListener(this);
        binding.btnCall.setOnClickListener(this);

        gameHistoryAPI(true);

    }

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.imgVu) {
            profileClicked();
        }
        else if (v == binding.btnReject) {
            rejectClicked();
        }
        else if (v == binding.btnAccept) {
            acceptClicked();
        }
        else if (v == binding.relReviews) {
            reviewsClicked();
        }
        else if (v == binding.btnCall) {
            phoneClicked();
        }
    }

    private void profileClicked() {
        if (oleGameHistory != null) {
            Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
            intent.putExtra("player_id", oleGameHistory.getId());
            startActivity(intent);
        }
    }

    private void rejectClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.request))
                .setMessage(getResources().getString(R.string.do_you_want_to_reject_request))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        acceptRejectChallengeAPI(true, "reject");
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void acceptClicked() {
        if (isFromDetail) {
            confirmPlayerAPI(true, playerId);
        }
        else if (isFromMatchDetail) {
            confirmPlayerAPI(true, creatorId);
        }
        else {
            acceptRejectChallengeAPI(true, "accept");
        }
    }

    private void reviewsClicked() {
        if (oleGameHistory == null) { return; }
        Intent intent = new Intent(getContext(), OlePlayerReviewsActivity.class);
        intent.putExtra("player_id", oleGameHistory.getId());
        startActivity(intent);
    }

    private void phoneClicked() {
        if (oleGameHistory == null) { return; }
        makeCall(oleGameHistory.getPhone());
    }

    private void populateData() {
        if (oleGameHistory == null) { return; }
        binding.tvName.setText(oleGameHistory.getName());
        binding.tvAge.setText(oleGameHistory.getAge());
        Glide.with(getApplicationContext()).load(oleGameHistory.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.imgVu);

        binding.tvFriendly.setText(oleGameHistory.getRatingData().getTotalGames());
        binding.tvBeforeTime.setText(oleGameHistory.getRatingData().getBeforTime());
        binding.tvOnTime.setText(oleGameHistory.getRatingData().getOnTime());
        binding.tvLate.setText(oleGameHistory.getRatingData().getLate());
        binding.tvNotCome.setText(oleGameHistory.getRatingData().getNotCome());
        if (oleGameHistory.getLevel().getValue().equalsIgnoreCase("1")) {
            binding.imgVuRank.setImageResource(R.drawable.rank_badge_one);
        }
        else if (oleGameHistory.getLevel().getValue().equalsIgnoreCase("2")) {
            binding.imgVuRank.setImageResource(R.drawable.rank_badge_two);
        }
        else if (oleGameHistory.getLevel().getValue().equalsIgnoreCase("3")) {
            binding.imgVuRank.setImageResource(R.drawable.rank_badge_three);
        }
        else {
            binding.rankVu.setVisibility(View.GONE);
        }
    }

    private void gameHistoryAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.gameHistoryOle(Functions.getAppLang(getContext()), playerId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            oleGameHistory = gson.fromJson(obj.toString(), OleGameHistory.class);
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

    private void acceptRejectChallengeAPI(boolean isLoader, String flag) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.acceptRejectChallenge(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, playerId, matchType, requestStatus, flag);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
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

    private void confirmPlayerAPI(boolean isLoader, String pId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.confirmPlayer(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, pId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            if (isFromMatchDetail) {
                                Intent intent = new Intent();
                                intent.putExtra("player_id", playerId);
                                setResult(RESULT_OK, intent);
                            }
                            finish();
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
