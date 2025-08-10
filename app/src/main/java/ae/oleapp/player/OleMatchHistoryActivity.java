package ae.oleapp.player;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleMatchHistoryAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityMatchHistoryBinding;
import ae.oleapp.models.OleMatchHistory;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleMatchHistoryActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityMatchHistoryBinding binding;
    private String matchType = "";
    private String bookingId = "";
    private String playerId = "";
    private String requestStatus = "";
    private String phone = "";
    private final List<OleMatchHistory> historyList = new ArrayList<>();
    private OleMatchHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityMatchHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.match_history);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            matchType = bundle.getString("match_type", "");
            bookingId = bundle.getString("booking_id", "");
            playerId = bundle.getString("player_id", "");
            requestStatus = bundle.getString("req_status", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleMatchHistoryAdapter(getContext(), historyList);
        adapter.setOnItemClickListener(clickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnReject.setOnClickListener(this);
        binding.btnCall.setOnClickListener(this);
        binding.imgVu.setOnClickListener(this);
        binding.btnAccept.setOnClickListener(this);

        matchHistoryAPI(true);
    }

    OleMatchHistoryAdapter.OnItemClickListener clickListener = new OleMatchHistoryAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            Intent intent = new Intent(getContext(), OleHistoryDetailActivity.class);
            intent.putExtra("playerId_1", historyList.get(pos).getUserData().getId());
            intent.putExtra("playerId_2", historyList.get(pos).getPlayerTwo().getId());
            startActivity(intent);
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnReject) {
            rejectClicked();
        }
        else if (v == binding.btnCall) {
            phoneClicked();
        }
        else if (v == binding.imgVu) {
            profileClicked();
        }
        else if (v == binding.btnAccept) {
            acceptClicked();
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

    private void phoneClicked() {
        if (!phone.isEmpty()) {
            makeCall(phone);
        }
    }

    private void profileClicked() {
        Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
        intent.putExtra("player_id", playerId);
        startActivity(intent);
    }

    private void acceptClicked() {
        acceptRejectChallengeAPI(true, "accept");
    }

    private void populateData(OlePlayerInfo olePlayerInfo) {
        binding.tvName.setText(olePlayerInfo.getName());
        phone = olePlayerInfo.getPhone();
        Glide.with(getContext()).load(olePlayerInfo.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.imgVu);
        if (olePlayerInfo.getLevel() != null && !olePlayerInfo.getLevel().isEmpty() && !olePlayerInfo.getLevel().getValue().equalsIgnoreCase("")) {
            binding.tvRank.setVisibility(View.VISIBLE);
            binding.tvRank.setText(String.format("LV: %s", olePlayerInfo.getLevel().getValue()));
        }
        else {
            binding.tvRank.setVisibility(View.INVISIBLE);
        }
    }

    private void matchHistoryAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.matchHistory(Functions.getAppLang(getContext()), playerId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            binding.tvDraw.setText(object.getString("total_draw"));
                            binding.tvWon.setText(object.getString("total_won"));
                            binding.tvPlayed.setText(object.getString("total_played"));
                            binding.tvChallenge.setText(object.getString("total_lost"));
                            binding.tvPoints.setText(object.getString("total_goals"));
                            Gson gson = new Gson();
                            populateData(gson.fromJson(object.getJSONObject("user").toString(), OlePlayerInfo.class));
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            historyList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                historyList.add(gson.fromJson(arr.get(i).toString(), OleMatchHistory.class));
                            }
                            adapter.notifyDataSetChanged();
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
        call.enqueue(new Callback<ResponseBody>() {
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
}
