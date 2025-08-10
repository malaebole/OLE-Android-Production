package ae.oleapp.player;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleJoinedListAdapter;
import ae.oleapp.adapters.OlePadelRequestAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityReceivedRequestBinding;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleReceivedRequestActivity extends BaseActivity {

    private OleactivityReceivedRequestBinding binding;
    private OleJoinedListAdapter adapter;
    private OlePadelRequestAdapter olePadelRequestAdapter;
    private final List<OlePlayerInfo> playerList = new ArrayList<>();
    private String matchType = "";
    private String bookingId = "";
    private String requestStatus = "";
    private boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityReceivedRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.received_request);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            matchType = bundle.getString("match_type", "");
            bookingId = bundle.getString("booking_id", "");
            requestStatus = bundle.getString("req_status", "");
        }

        LinearLayoutManager joinLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(joinLayoutManager);
        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            olePadelRequestAdapter = new OlePadelRequestAdapter(getContext(), playerList);
            olePadelRequestAdapter.setOnItemClickListener(padelItemClickListener);
            binding.recyclerVu.setAdapter(olePadelRequestAdapter);
        }
        else {
            adapter = new OleJoinedListAdapter(getContext(), playerList, false, true);
            adapter.setOnItemClickListener(itemClickListener);
            binding.recyclerVu.setAdapter(adapter);
        }

        binding.bar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getRequestAPI(playerList.isEmpty());
    }

    OlePadelRequestAdapter.OnItemClickListener padelItemClickListener = new OlePadelRequestAdapter.OnItemClickListener() {
        @Override
        public void OnAcceptClick(View v, int pos) {
            acceptChallenge(pos);
        }

        @Override
        public void OnRejectClick(View v, int pos) {
            rejectChallenge(pos);
        }

        @Override
        public void openProfile(View v, String pId) {
            Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
            intent.putExtra("player_id", pId);
            startActivity(intent);
        }
    };

    private void rejectChallenge(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.request))
                .setMessage(R.string.do_you_want_to_reject_request)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rejectPadelChallengeAPI(true, playerList.get(pos).getId(), pos);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void acceptChallenge(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.request))
                .setMessage(R.string.do_you_want_to_accept_request)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        acceptPadelChallengeAPI(true, playerList.get(pos).getId(), pos);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    OleJoinedListAdapter.OnItemClickListener itemClickListener = new OleJoinedListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            isFirstTime = false;
            if (matchType.equalsIgnoreCase(Constants.kFriendlyGame)) {
                Intent intent = new Intent(getContext(), OleGameHistoryActivity.class);
                intent.putExtra("match_type", matchType);
                intent.putExtra("booking_id", bookingId);
                intent.putExtra("req_status", requestStatus);
                intent.putExtra("player_id", playerList.get(pos).getId());
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(getContext(), OleMatchHistoryActivity.class);
                intent.putExtra("match_type", matchType);
                intent.putExtra("booking_id", bookingId);
                intent.putExtra("req_status", requestStatus);
                intent.putExtra("player_id", playerList.get(pos).getId());
                startActivity(intent);
            }
        }

        @Override
        public void OnDeleteClick(View v, int pos) {

        }

        @Override
        public void OnAcceptClick(View v, int pos) {
            acceptRejectChallengeAPI(true, playerList.get(pos).getId(), "accept", pos);
        }

        @Override
        public void OnRateClick(View v, int pos) {

        }
    };

    private void getRequestAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getRequest(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            playerList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                playerList.add(gson.fromJson(arr.get(i).toString(), OlePlayerInfo.class));
                            }
                        }
                        else {
                            playerList.clear();
                            if (isFirstTime) {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
                        }
                        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                            olePadelRequestAdapter.notifyDataSetChanged();
                        }
                        else {
                            adapter.notifyDataSetChanged();
                        }
                        if (!isFirstTime && playerList.size() == 0) {
                            finish();
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

    private void acceptRejectChallengeAPI(boolean isLoader, String playerId, String flag, int pos) {
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
                            playerList.remove(pos);
                            adapter.notifyItemRemoved(pos);
                            adapter.notifyItemRangeChanged(pos, playerList.size());
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

    private void acceptPadelChallengeAPI(boolean isLoader, String playerId, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.acceptChallenge(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, playerId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            playerList.remove(pos);
                            olePadelRequestAdapter.notifyItemRemoved(pos);
                            olePadelRequestAdapter.notifyItemRangeChanged(pos, playerList.size());
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

    private void rejectPadelChallengeAPI(boolean isLoader, String playerId, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.cancelChallenge(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, playerId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            playerList.remove(pos);
                            olePadelRequestAdapter.notifyItemRemoved(pos);
                            olePadelRequestAdapter.notifyItemRangeChanged(pos, playerList.size());
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
