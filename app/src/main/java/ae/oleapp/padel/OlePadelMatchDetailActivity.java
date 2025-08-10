package ae.oleapp.padel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleFacilityAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityPadelMatchDetailBinding;
import ae.oleapp.dialogs.OlePaymentDialogFragment;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.OlePlayerMatch;
import ae.oleapp.player.OleChatActivity;
import ae.oleapp.player.OlePlayerListActivity;
import ae.oleapp.player.OlePlayerProfileActivity;
import ae.oleapp.player.OlePadelMatchShareActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePadelMatchDetailActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPadelMatchDetailBinding binding;
    private String bookingId = "";
    private OlePlayerMatch matchDetail;
    private final List<OleClubFacility> facilityList = new ArrayList<>();
    private boolean isCheck = false;
    private OleFacilityAdapter oleFacilityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPadelMatchDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.match_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingId = bundle.getString("booking_id", "");
        }

        binding.btnChangePartner.setVisibility(View.GONE);
        binding.btnChat.setVisibility(View.GONE);
        binding.toolbarBadge.setVisibility(View.GONE);
        binding.coverVu.setVisibility(View.VISIBLE);

        LinearLayoutManager facLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.facRecyclerVu.setLayoutManager(facLayoutManager);
        oleFacilityAdapter = new OleFacilityAdapter(getContext(), facilityList, true);
        binding.facRecyclerVu.setAdapter(oleFacilityAdapter);

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.checkVu.setOnClickListener(this);
        binding.mapVu.setOnClickListener(this);
        binding.btnChallenge.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.btnChat.setOnClickListener(this);
        binding.btnChangePartner.setOnClickListener(this);
        binding.btnPay.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMatchAPI(true);
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("receive_new_msg"));
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("receive_new_msg")) {
                Bundle bundle = intent.getExtras();
                String bookId = bundle.getString("booking_id", "");
                String recId = bundle.getString("receiver_id", "");
                if (bookId.equalsIgnoreCase(bookingId) && recId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                    String count = bundle.getString("unread_chat_count", "");
                    if (!count.equalsIgnoreCase("")) {
                        setBadgeValue(count);
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.myProfileVu) {
            gotoProfile(matchDetail.getCreatedBy().getId());
        }
        else if (v == binding.myPartnerProfileVu) {
            gotoProfile(matchDetail.getCreatorPartner().getId());
        }
        else if (v == binding.opponentProfileVu) {
            gotoProfile(matchDetail.getPlayerTwo().getId());
        }
        else if (v == binding.opponentPartnerProfileVu) {
            gotoProfile(matchDetail.getPlayerTwoPartner().getId());
        }
        else if (v == binding.checkVu) {
            checkClicked();
        }
        else if (v == binding.mapVu) {
            locClicked();
        }
        else if (v == binding.btnChallenge) {
            challengeClicked();
        }
        else if (v == binding.btnCancel) {
            cancelClicked();
        }
        else if (v == binding.btnChat) {
            chatClicked();
        }
        else if (v == binding.btnChangePartner) {
            if (matchDetail != null) {
                if (matchDetail.getPlayerTwo().getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                    Intent intent = new Intent(getContext(), OlePlayerListActivity.class);
                    intent.putExtra("is_selection", true);
                    intent.putExtra("is_single_selection", true);
                    startActivityForResult(intent, 106);
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.you_cannot_change_partner), FancyToast.ERROR);
                }
            }
        }
        else if (v == binding.btnPay) {
            payClicked();
        }
        else if (v == binding.btnShare) {
            shareClicked();
        }
    }

    private void shareClicked() {
        if (matchDetail != null) {
            Intent intent = new Intent(getContext(), OlePadelMatchShareActivity.class);
            intent.putExtra("club_name", matchDetail.getClubName());
            intent.putExtra("date", matchDetail.getBookingDate());
            intent.putExtra("time", matchDetail.getBookingTime());
            Gson gson = new Gson();
            intent.putExtra("player_one", gson.toJson(matchDetail.getCreatedBy()));
            intent.putExtra("player_one_partner", gson.toJson(matchDetail.getCreatorPartner()));
            if (matchDetail.getPlayerTwo() != null && !matchDetail.getPlayerTwo().isEmpty()) {
                intent.putExtra("player_two", gson.toJson(matchDetail.getPlayerTwo()));
            }
            if (matchDetail.getPlayerTwoPartner() != null && !matchDetail.getPlayerTwoPartner().isEmpty()) {
                intent.putExtra("player_two_partner", gson.toJson(matchDetail.getPlayerTwoPartner()));
            }
            startActivity(intent);
        }
    }

    private void payClicked() {
        if (matchDetail != null) {
            openPaymentDialog(matchDetail.getJoiningFee(), Functions.getPrefValue(getContext(), Constants.kCurrency), "", "", "", true, false, matchDetail.getClubId(), new OlePaymentDialogFragment.PaymentDialogCallback() {
                @Override
                public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                    opponentPaymentAPI(true, paymentMethod, orderRef, paidPrice, walletPaid, cardPaid);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 106 && resultCode == RESULT_OK) {
            String str = data.getExtras().getString("players");
            Gson gson = new Gson();
            List<OlePlayerInfo> list = gson.fromJson(str, new TypeToken<List<OlePlayerInfo>>() {
            }.getType());
            if (list.size() > 0) {
                changePartnerAPI(true, list.get(0), matchDetail.getPlayerTwoPartner().getId());
            }
        }
    }

    private void gotoProfile(String pId) {
        Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
        intent.putExtra("player_id", pId);
        startActivity(intent);
    }

    private void checkClicked() {
        if (isCheck) {
            isCheck = false;
            binding.imgVuCheck.setImageResource(R.drawable.uncheck);
        }
        else {
            isCheck = true;
            binding.imgVuCheck.setImageResource(R.drawable.p_check);
        }
    }

    private void locClicked() {
        String uri = "http://maps.google.com/maps?daddr="+matchDetail.getClubLatitude()+","+matchDetail.getClubLongitude();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    private void challengeClicked() {
        if (matchDetail != null) {
            if (matchDetail.getMyStatus().equalsIgnoreCase("no_request")) {
                if (isCheck) {
                    Intent intent = new Intent(getContext(), OlePadelChallengeActivity.class);
                    intent.putExtra("match", new Gson().toJson(matchDetail));
                    startActivity(intent);
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.requset_check), FancyToast.ERROR);
                }
            }
        }
    }

    private  void cancelClicked() {
        if (matchDetail != null) {
            cancelMatch();
        }
    }

    private void cancelMatch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.match))
                .setMessage(getResources().getString(R.string.do_you_want_to_cancel_match))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (matchDetail != null) {
                            if (matchDetail.getMyStatus().equalsIgnoreCase("pending")) {
                                cancelRequestAPI(true);
                            }
                            else if (matchDetail.getMyStatus().equalsIgnoreCase("accepted")) {
                                cancelMatchAPI(true, matchDetail.getCreatedBy().getId());
                            }
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void chatClicked() {
        if (matchDetail == null) {
            return;
        }
        if (matchDetail.getMyStatus().equalsIgnoreCase("accepted") || matchDetail.getMyStatus().equalsIgnoreCase("match_end")) {
            Intent intent = new Intent(getContext(), OleChatActivity.class);
            intent.putExtra("booking_id", matchDetail.getBookingId());
            intent.putExtra("booking_status", matchDetail.getBookingStatus());
            intent.putExtra("booking_type", matchDetail.getBookingType());
            intent.putExtra("is_match_detail", true);
            Gson gson = new Gson();
            intent.putExtra("p1_info", gson.toJson(matchDetail.getCreatedBy()));
            if (matchDetail.getJoinedPlayers().size() > 0) {
                intent.putExtra("p2_info", gson.toJson(matchDetail.getJoinedPlayers().get(0)));
            }
            startActivity(intent);
        }
    }

    private void setBadgeValue(String chatCount) {
        int count = Integer.parseInt(chatCount);
        if (count > 0) {
            binding.toolbarBadge.setVisibility(View.VISIBLE);
            binding.toolbarBadge.setNumber(count);
        }
        else  {
            binding.toolbarBadge.setVisibility(View.GONE);
        }
    }

    private void populateData() {
        if (matchDetail == null) { return; }
        binding.coverVu.setVisibility(View.GONE);
        binding.myProfileVu.populateData(matchDetail.getCreatedBy().getNickName(), matchDetail.getCreatedBy().getPhotoUrl(), matchDetail.getCreatedBy().getLevel(), true);
        binding.myProfileVu.setOnClickListener(this);
        binding.myPartnerProfileVu.populateData(matchDetail.getCreatorPartner().getNickName(), matchDetail.getCreatorPartner().getPhotoUrl(), matchDetail.getCreatorPartner().getLevel(), true);
        binding.myPartnerProfileVu.setOnClickListener(this);
        binding.tvClubName.setText(matchDetail.getClubName());
        binding.tvFieldName.setText(matchDetail.getFieldName());
        binding.tvTime.setText(String.format("%s (%s)", matchDetail.getBookingTime().split("-")[0], matchDetail.getDuration()));
        binding.tvCity.setText(matchDetail.getCity());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse(matchDetail.getBookingDate());
            dateFormat.applyPattern("EEEE, dd/MM/yyyy");
            binding.tvDate.setText(dateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        binding.tvSkillLevel.setText(matchDetail.getSkillLevel());
        binding.tvPayment.setText(String.format("%s %s", matchDetail.getJoiningFee(), matchDetail.getCurrency()));
        binding.tvPaymentCheck.setText(getResources().getString(R.string.match_payment_check, matchDetail.getJoiningFee(), matchDetail.getCurrency()));
        facilityList.clear();
        facilityList.addAll(matchDetail.getFacilities());
        oleFacilityAdapter.notifyDataSetChanged();

        if (matchDetail.getPlayerTwo() != null && !matchDetail.getPlayerTwo().isEmpty()) {
            binding.opponentProfileVu.populateData(matchDetail.getPlayerTwo().getNickName(), matchDetail.getPlayerTwo().getPhotoUrl(), matchDetail.getPlayerTwo().getLevel(), true);
            binding.opponentProfileVu.setOnClickListener(this);
        }
        else {
            binding.opponentProfileVu.populateData("?", "", null, false);
        }
        if (matchDetail.getPlayerTwoPartner() != null && !matchDetail.getPlayerTwoPartner().isEmpty()) {
            binding.opponentPartnerProfileVu.populateData(matchDetail.getPlayerTwoPartner().getNickName(), matchDetail.getPlayerTwoPartner().getPhotoUrl(), matchDetail.getPlayerTwoPartner().getLevel(), true);
            binding.opponentPartnerProfileVu.setOnClickListener(this);
        }
        else {
            binding.opponentPartnerProfileVu.populateData("?", "", null, false);
        }

        if (!matchDetail.getClubLatitude().equalsIgnoreCase("") && !matchDetail.getClubLongitude().equalsIgnoreCase("")) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            String url = "https://maps.google.com/maps/api/staticmap?center=" + matchDetail.getClubLatitude() + "," + matchDetail.getClubLongitude() + "&zoom=16&size="+width+"x300&sensor=false&key="+getString(R.string.maps_api_key);
            Glide.with(getContext()).load(url).into(binding.mapVu);
        }

        if (matchDetail.getPlayerTwo() != null && !matchDetail.getPlayerTwo().isEmpty() && matchDetail.getPlayerTwo().getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) &&
                (matchDetail.getPlayerTwoPartner() != null && !matchDetail.getPlayerTwoPartner().isEmpty())) {
            binding.btnChangePartner.setVisibility(View.VISIBLE);
        }

        binding.paymentStatusVu.setVisibility(View.GONE);
        if (matchDetail.getMyStatus().equalsIgnoreCase("no_request")) {
            binding.tvStatus.setText(R.string.open);
            binding.tvStatus.setTextColor(Color.parseColor("#0084ff"));
            binding.btnChallenge.setVisibility(View.VISIBLE);
            binding.btnCancel.setVisibility(View.GONE);
            binding.checkVu.setVisibility(View.VISIBLE);
            binding.tvChallenge.setText(R.string.challenge);
        }
        else if (matchDetail.getMyStatus().equalsIgnoreCase("pending")) {
            binding.tvStatus.setText(R.string.pending);
            binding.tvStatus.setTextColor(Color.parseColor("#ff9f00"));
            binding.btnChallenge.setVisibility(View.GONE);
            binding.checkVu.setVisibility(View.GONE);
            binding.btnCancel.setVisibility(View.VISIBLE);
        }
        else if (matchDetail.getMyStatus().equalsIgnoreCase("accepted")) {
            binding.tvStatus.setText(R.string.accepted);
            binding.tvStatus.setTextColor(Color.parseColor("#49d483"));
            showPaymentStatus();
            binding.btnChallenge.setVisibility(View.GONE);
            if (matchDetail.getCreatorPartner().getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) ||
                    (matchDetail.getPlayerTwoPartner() != null && !matchDetail.getPlayerTwoPartner().isEmpty() && matchDetail.getPlayerTwoPartner().getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)))) {
                binding.btnCancel.setVisibility(View.GONE);
            }
            else {
                binding.btnCancel.setVisibility(View.VISIBLE);
            }
            binding.checkVu.setVisibility(View.GONE);
            setBadgeValue(matchDetail.getUnreadChatCount());
        }
        else if (matchDetail.getMyStatus().equalsIgnoreCase("rejected")) {
            binding.tvStatus.setText(R.string.rejected);
            binding.tvStatus.setTextColor(Color.parseColor("#f02301"));
            binding.btnChallenge.setVisibility(View.GONE);
            binding.btnCancel.setVisibility(View.GONE);
            binding.checkVu.setVisibility(View.GONE);
            binding.btnChangePartner.setVisibility(View.GONE);
        }
        else if (matchDetail.getMyStatus().equalsIgnoreCase("match_started")) {
            binding.tvStatus.setText(R.string.match_started);
            binding.tvStatus.setTextColor(Color.parseColor("#0084ff"));
            binding.btnChallenge.setVisibility(View.GONE);
            binding.btnCancel.setVisibility(View.GONE);
            binding.checkVu.setVisibility(View.GONE);
        }
        else if (matchDetail.getMyStatus().equalsIgnoreCase("match_end")) {
            binding.tvStatus.setText(R.string.match_end);
            binding.tvStatus.setTextColor(Color.parseColor("#0084ff"));
            binding.btnChallenge.setVisibility(View.GONE);
            binding.btnCancel.setVisibility(View.GONE);
            binding.checkVu.setVisibility(View.GONE);
            setBadgeValue(matchDetail.getUnreadChatCount());
            binding.btnChangePartner.setVisibility(View.GONE);
        }
    }

    private void showPaymentStatus() {
        if (matchDetail.getPlayerTwo() != null && !matchDetail.getPlayerTwo().isEmpty() && matchDetail.getPlayerTwo().getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
            binding.paymentStatusVu.setVisibility(View.VISIBLE);
            if (matchDetail.getPlayerTwo().getPaymentStatus().equalsIgnoreCase("1")) {
                binding.tvPaymentStatus.setText(R.string.paid);
                binding.btnPay.setVisibility(View.GONE);
                binding.tvPaymentStatus.setTextColor(getResources().getColor(R.color.greenColor));
            } else {
                binding.tvPaymentStatus.setText(R.string.unpaid);
                binding.tvPaymentStatus.setTextColor(Color.parseColor("#ff9f00"));
                if (matchDetail.getClubPaymentMethod().equalsIgnoreCase("cash")) {
                    binding.btnPay.setVisibility(View.GONE);
                } else {
                    binding.btnPay.setVisibility(View.VISIBLE);
                }
            }
        }
        else {
            binding.paymentStatusVu.setVisibility(View.GONE);
        }
    }

    private void getMatchAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.matchDetail(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, Functions.getPrefValue(getContext(), Constants.kAppModule));
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
                            matchDetail = gson.fromJson(obj.toString(), OlePlayerMatch.class);
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

    private void cancelRequestAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.cancelRequest(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId);
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

    private void cancelMatchAPI(boolean isLoader, String playerId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.cancelAcceptedChallenge(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, playerId);
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

    private void changePartnerAPI(boolean isLoader, OlePlayerInfo partner, String oldPartnerId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.changePartner(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, partner.getId(), oldPartnerId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            matchDetail.setPlayerTwoPartner(partner);
                            binding.opponentPartnerProfileVu.populateData(partner.getNickName(), partner.getPhotoUrl(), partner.getLevel(), true);
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

    private void opponentPaymentAPI(boolean isLoader, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.opponentPayment(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), orderRef, paymentMethod, paidPrice, walletPaid, cardPaid, bookingId, Functions.getIPAddress());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            getMatchAPI(true);
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