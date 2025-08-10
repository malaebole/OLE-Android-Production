package ae.oleapp.player;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleJoinedGridAdapter;
import ae.oleapp.adapters.OleJoinedListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityJoinedPlayersBinding;
import ae.oleapp.dialogs.OlePlayerRateDialogFragment;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleJoinedPlayersActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityJoinedPlayersBinding binding;
    private final List<OlePlayerInfo> playerList = new ArrayList<>();
    private String bookingType = "";
    private String bookingId = "";
    private String requestStatus = "";
    private String bookingStatus = "";
    private String remainingAmount = "";
    private String currency = "";
    private String matchCreatedId = "";
    private boolean isFromMatchDetail = false;
    private OleJoinedGridAdapter oleJoinedGridAdapter;
    private OleJoinedListAdapter oleJoinedListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityJoinedPlayersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.joined_players);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingType = bundle.getString("match_type", "");
            bookingId = bundle.getString("booking_id", "");
            requestStatus = bundle.getString("req_status", "");
            bookingStatus = bundle.getString("booking_status", "");
            remainingAmount = bundle.getString("rem_amount", "");
            currency = bundle.getString("currency", "");
            matchCreatedId = bundle.getString("matchCreatedId", "");
            isFromMatchDetail = bundle.getBoolean("isFromMatchDetail", false);
        }

        if (bookingType.equalsIgnoreCase(Constants.kFriendlyGame)) {
            binding.recyclerVu.setLayoutManager(new GridLayoutManager(getContext(), 2));
            oleJoinedGridAdapter = new OleJoinedGridAdapter(getContext(), playerList, !isFromMatchDetail);
            oleJoinedGridAdapter.setOnItemClickListener(gridItemClickListener);
            oleJoinedGridAdapter.setBookingStatus(bookingStatus);
            binding.recyclerVu.setAdapter(oleJoinedGridAdapter);
            binding.tvRemainAmount.setText(getResources().getString(R.string.remaining_amount_place, remainingAmount, currency));
            if (isFromMatchDetail) {
                binding.tvRemainAmount.setVisibility(View.GONE);
            }
            getJoinedPlayersAPI(true);
        }
        else if (bookingType.equalsIgnoreCase(Constants.kPublicChallenge)) {
            binding.tvRemainAmount.setVisibility(View.GONE);
            setRecyclerVu();
            getJoinedPlayersAPI(true);
        }
        else {
            binding.bar.toolbarTitle.setText(R.string.invited_players);
            binding.tvRemainAmount.setVisibility(View.GONE);
            setRecyclerVu();
            getRequestAPI(true);
        }

        binding.bar.backBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
    }

    private void setRecyclerVu() {
        LinearLayoutManager joinLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(joinLayoutManager);
        oleJoinedListAdapter = new OleJoinedListAdapter(getContext(), playerList, true, true);
        oleJoinedListAdapter.setOnItemClickListener(itemClickListener);
        oleJoinedListAdapter.setBookingStatus(bookingStatus);
        oleJoinedListAdapter.setBookingType(bookingType);
        binding.recyclerVu.setAdapter(oleJoinedListAdapter);
    }

    OleJoinedGridAdapter.OnItemClickListener gridItemClickListener = new OleJoinedGridAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            gotoGameHistory(playerList.get(pos));
        }

        @Override
        public void OnDeleteClick(View v, int pos) {
            ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                    .setCancelButtonTitle(getResources().getString(R.string.cancel))
                    .setOtherButtonTitles(getResources().getString(R.string.delete))
                    .setCancelableOnTouchOutside(true)
                    .setListener(new ActionSheet.ActionSheetListener() {
                        @Override
                        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                        }

                        @Override
                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            if (index == 0) {
                                removePlayer(true, playerList.get(pos).getId(), bookingType, pos);
                            }
                        }
                    }).show();
        }

        @Override
        public void OnRateClick(View v, int pos) {
            gotoRate(pos);
        }
    };

    OleJoinedListAdapter.OnItemClickListener itemClickListener = new OleJoinedListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            gotoProfile(playerList.get(pos).getId());
        }

        @Override
        public void OnDeleteClick(View v, int pos) {
            ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                    .setCancelButtonTitle(getResources().getString(R.string.cancel))
                    .setOtherButtonTitles(getResources().getString(R.string.delete))
                    .setCancelableOnTouchOutside(true)
                    .setListener(new ActionSheet.ActionSheetListener() {
                        @Override
                        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                            oleJoinedListAdapter.binderHelper.closeLayout(String.valueOf(pos));
                        }

                        @Override
                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            if (index == 0) {
                                if (bookingType.equalsIgnoreCase(Constants.kPrivateChallenge)) {
                                    removeInvitedPlayer(true, playerList.get(pos).getId(), pos);
                                }
                                else {
                                    removePlayer(true, playerList.get(pos).getId(), bookingType, pos);
                                }
                            }
                        }
                    }).show();
        }

        @Override
        public void OnAcceptClick(View v, int pos) {

        }

        @Override
        public void OnRateClick(View v, int pos) {

        }
    };

    private void gotoProfile(String pId) {
        Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
        intent.putExtra("player_id", pId);
        startActivity(intent);
    }

    private void gotoGameHistory(OlePlayerInfo olePlayerInfo) {
        Intent intent = new Intent(getContext(), OleGameHistoryActivity.class);
        intent.putExtra("match_type", bookingType);
        intent.putExtra("booking_id", bookingId);
        intent.putExtra("req_status", requestStatus);
        intent.putExtra("player_id", olePlayerInfo.getId());
        intent.putExtra("player_confirmed", olePlayerInfo.getPlayerConfirmed());
        intent.putExtra("booking_status", bookingStatus);
        if (isFromMatchDetail) {
            intent.putExtra("creator_id", matchCreatedId);
            intent.putExtra("match_detail", true);
            startActivityForResult(intent, 111);
        }
        else {
            intent.putExtra("creator_id", "");
            intent.putExtra("booking_detail", true);
            intent.putExtra("match_detail", false);
            startActivity(intent);
        }
    }

    private void gotoRate(int pos) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("PlayerRateDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OlePlayerRateDialogFragment dialogFragment = new OlePlayerRateDialogFragment(playerList.get(pos).getId(), bookingId);
        dialogFragment.setDialogCallback(new OlePlayerRateDialogFragment.PlayerRateDialogCallback() {
            @Override
            public void didRatePlayer() {
                playerList.get(pos).setIsRated("1");
                if (bookingType.equalsIgnoreCase(Constants.kFriendlyGame)) {
                    oleJoinedGridAdapter.notifyDataSetChanged();
                }
                else {
                    oleJoinedListAdapter.notifyItemChanged(pos);
                }
            }
        });
        dialogFragment.show(fragmentTransaction, "PlayerRateDialogFragment");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String pId = bundle.getString("player_id");
            for (OlePlayerInfo olePlayerInfo : playerList) {
                if (olePlayerInfo.getId().equalsIgnoreCase(pId)) {
                    olePlayerInfo.setPlayerConfirmed("1");
                    break;
                }
            }
            oleJoinedGridAdapter.notifyDataSetChanged();
        }
    }

    private void getJoinedPlayersAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getJoinedPlayers(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId);
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
                            if (!isFromMatchDetail) {
                                for (int i = 0; i < playerList.size(); i++) {
                                    if (playerList.get(i).getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                        playerList.remove(i);
                                        break;
                                    }
                                }
                            }
                            if (bookingType.equalsIgnoreCase(Constants.kFriendlyGame)) {
                                oleJoinedGridAdapter.notifyDataSetChanged();
                            }
                            else {
                                oleJoinedListAdapter.notifyDataSetChanged();
                            }
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
                            oleJoinedListAdapter.notifyDataSetChanged();
                        }
                        else {
                            playerList.clear();
                            oleJoinedListAdapter.notifyDataSetChanged();
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

    private void removePlayer(boolean isLoader, String playerId, String bookingType, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.cancelAcceptedMatch(Functions.getAppLang(getContext()), playerId, bookingId, bookingType, Functions.getPrefValue(getContext(), Constants.kUserID));
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
                            if (bookingType.equalsIgnoreCase(Constants.kFriendlyGame)) {
                                oleJoinedGridAdapter.notifyItemRemoved(pos);
                                oleJoinedGridAdapter.notifyItemRangeChanged(pos, playerList.size());
                            }
                            else {
                                oleJoinedListAdapter.notifyItemRemoved(pos);
                                oleJoinedListAdapter.notifyItemRangeChanged(pos, playerList.size());
                            }
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

    private void removeInvitedPlayer(boolean isLoader, String playerId, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteInvitedPlayer(Functions.getAppLang(getContext()), playerId, bookingId, Functions.getPrefValue(getContext(), Constants.kUserID));
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
                            oleJoinedListAdapter.notifyItemRemoved(pos);
                            oleJoinedListAdapter.notifyItemRangeChanged(pos, playerList.size());
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