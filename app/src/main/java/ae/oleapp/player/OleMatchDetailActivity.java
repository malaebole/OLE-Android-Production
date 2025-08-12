package ae.oleapp.player;

import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
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
import ae.oleapp.databinding.OleactivityMatchDetailBinding;
import ae.oleapp.dialogs.OlePaymentDialogFragment;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.OlePlayerMatch;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleMatchDetailActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityMatchDetailBinding binding;
    private String bookingId = "";
    private OlePlayerMatch matchDetail;
    private final List<OleClubFacility> facilityList = new ArrayList<>();
    private boolean isCheck = false;
    private OleFacilityAdapter oleFacilityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityMatchDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.match_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingId = bundle.getString("booking_id", "");
        }

        binding.btnReject.setVisibility(View.GONE);
        binding.relChat.setVisibility(View.GONE);
        binding.toolbarBadge.setVisibility(View.GONE);
        LinearLayoutManager facLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.facRecyclerVu.setLayoutManager(facLayoutManager);
        oleFacilityAdapter = new OleFacilityAdapter(getContext(), facilityList, true);
        binding.facRecyclerVu.setAdapter(oleFacilityAdapter);

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.profileVu1.setOnClickListener(this);
        binding.profileVu2.setOnClickListener(this);
        binding.checkVu.setOnClickListener(this);
        binding.btnCall1.setOnClickListener(this);
        binding.btnCall2.setOnClickListener(this);
        binding.mapVu.setOnClickListener(this);
        binding.btnFav.setOnClickListener(this);
        binding.btnAccept.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.btnReject.setOnClickListener(this);
        binding.btnChat.setOnClickListener(this);
        binding.btnPay.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);

        getMatchAPI(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        else if (v == binding.profileVu1) {
            imgVuClicked();
        }
        else if (v == binding.profileVu2) {
            imgVu2Clicked();
        }
        else if (v == binding.checkVu) {
            checkClicked();
        }
        else if (v == binding.btnCall1) {
            phone1Clicked();
        }
        else if (v == binding.btnCall2) {
            phone2Clicked();
        }
        else if (v == binding.mapVu) {
            locClicked();
        }
        else if (v == binding.btnFav) {
            favClicked();
        }
        else if (v == binding.btnAccept) {
            acceptClicked();
        }
        else if (v == binding.btnCancel) {
            cancelClicked();
        }
        else if (v == binding.btnReject) {
            rejectClicked();
        }
        else if (v == binding.btnChat) {
            chatClicked();
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
            Intent intent = new Intent(getContext(), OleFootballMatchShareActivity.class);
            intent.putExtra("club_name", matchDetail.getClubName());
            intent.putExtra("date", matchDetail.getBookingDate());
            intent.putExtra("time", matchDetail.getBookingTime());
            Gson gson = new Gson();
            intent.putExtra("player_one", gson.toJson(matchDetail.getCreatedBy()));
            if (matchDetail.getJoinedPlayers().size()>0) {
                intent.putExtra("player_two", gson.toJson(matchDetail.getJoinedPlayers().get(0)));
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

    private void imgVuClicked() {
        if (matchDetail==null) {
            return;
        }
        Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
        intent.putExtra("player_id", matchDetail.getCreatedBy().getId());
        startActivity(intent);
    }

    private void imgVu2Clicked() {
        if (matchDetail==null || matchDetail.getJoinedPlayers().size() == 0) {
            return;
        }
        Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
        intent.putExtra("player_id", matchDetail.getJoinedPlayers().get(0).getId());
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

    private void phone1Clicked() {
        if (matchDetail != null) {
            makeCall(matchDetail.getCreatedBy().getPhone());
        }
    }

    private void phone2Clicked() {
        if (matchDetail != null && matchDetail.getJoinedPlayers().size() > 0) {
            makeCall(matchDetail.getJoinedPlayers().get(0).getPhone());
        }
    }

    private void locClicked() {
        String uri = "http://maps.google.com/maps?daddr="+matchDetail.getClubLatitude()+","+matchDetail.getClubLongitude();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    private void favClicked() {
        if (matchDetail == null) { return; }
        if (matchDetail.getCreatedBy().getFavorite().equalsIgnoreCase("1")) {
            addRemoveFav(true, matchDetail.getCreatedBy().getId(), "0");
        }
        else {
            addRemoveFav(true, matchDetail.getCreatedBy().getId(), "1");
        }
    }

    private void acceptClicked() {
        if (matchDetail != null) {
            if (matchDetail.getRequestStatus().equalsIgnoreCase("request_sent") && matchDetail.getMyStatus().equalsIgnoreCase("no_request")) {
                if (isCheck) {
                    joinChallenge(matchDetail.getBookingId(), matchDetail.getBookingType(), matchDetail.getJoiningFee(), matchDetail.getClubId());
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.requset_check), FancyToast.ERROR);
                }
            }
            else if (matchDetail.getRequestStatus().equalsIgnoreCase("request_received") && matchDetail.getMyStatus().equalsIgnoreCase("pending")) {
                if (isCheck) {
                    acceptChallenge(matchDetail.getBookingType(), matchDetail.getRequestStatus(), matchDetail.getJoiningFee(), matchDetail.getClubId());
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

    private void acceptChallenge(String bookingType, String requestStatus, String price, String clubId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.match))
                .setMessage(R.string.do_you_want_to_accept_request)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openPaymentDialog(price, Functions.getPrefValue(getContext(), Constants.kCurrency), "", "", "", false, false, clubId, new OlePaymentDialogFragment.PaymentDialogCallback() {
                            @Override
                            public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                                acceptRejectChallengeAPI(true, "", bookingType, requestStatus, "accept", paymentMethod, orderRef, price, cardPaid, walletPaid);
                            }
                        });
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void joinChallenge(String bookingId, String bookingType, String price, String clubId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.match))
                .setMessage(R.string.want_to_send_challenge)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openPaymentDialog(price, Functions.getPrefValue(getContext(), Constants.kCurrency), "", "", "", false, false, clubId, new OlePaymentDialogFragment.PaymentDialogCallback() {
                            @Override
                            public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                                joinChallengeAPI(true, bookingId, bookingType, paymentMethod, orderRef, price, cardPaid, walletPaid);
                            }
                        });
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
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
                                acceptRejectChallengeAPI(true, matchDetail.getCreatedBy().getId(), matchDetail.getBookingType(), matchDetail.getRequestStatus(), "reject", "", "", "", "", "");
                            }
                            else if (matchDetail.getMyStatus().equalsIgnoreCase("accepted")) {
                                cancelMatchAPI(true, matchDetail.getCreatedBy().getId(), matchDetail.getBookingType());
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

    private void rejectClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.request))
                .setMessage(getResources().getString(R.string.do_you_want_to_reject_request))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (matchDetail != null && matchDetail.getMyStatus().equalsIgnoreCase("pending")) {
                            acceptRejectChallengeAPI(true, "", matchDetail.getBookingType(), matchDetail.getRequestStatus(), "reject", "", "", "", "", "");
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
        binding.profileVu1.populateData(matchDetail.getCreatedBy().getNickName(), matchDetail.getCreatedBy().getPhotoUrl(), matchDetail.getCreatedBy().getLevel(), true);
        binding.profileVu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoProfile(matchDetail.getCreatedBy().getId());
            }
        });
        binding.tvClubName.setText(matchDetail.getClubName());
        binding.tvFieldName.setText(String.format("%s (%s)", matchDetail.getFieldName(), matchDetail.getFieldSize()));
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
        binding.tvPayment.setText(String.format("%s %s", matchDetail.getJoiningFee(), matchDetail.getCurrency()));
        binding.tvPaymentCheck.setText(getResources().getString(R.string.match_payment_check, matchDetail.getJoiningFee(), matchDetail.getCurrency()));
        facilityList.clear();
        facilityList.addAll(matchDetail.getFacilities());
        oleFacilityAdapter.notifyDataSetChanged();
        if (matchDetail.getCreatedBy().getFavorite().equalsIgnoreCase("1")) {
            binding.btnFav.setImageResource(R.drawable.fav_green);
            binding.tvFav.setText(R.string.remove_from_fav);
        }
        else {
            binding.btnFav.setImageResource(R.drawable.club_unfav);
            binding.tvFav.setText(R.string.add_to_fav);
        }

        if (!matchDetail.getClubLatitude().equalsIgnoreCase("") && !matchDetail.getClubLongitude().equalsIgnoreCase("")) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            String url = "https://maps.google.com/maps/api/staticmap?center=" + matchDetail.getClubLatitude() + "," + matchDetail.getClubLongitude() + "&zoom=16&size="+width+"x300&sensor=false&key="+getString(R.string.maps_api_key);
            Glide.with(getApplicationContext()).load(url).into(binding.mapVu);
        }

        if (matchDetail.getJoinedPlayers().size()>0) {
            binding.btnCall2.setVisibility(View.VISIBLE);
            OlePlayerInfo player2 = matchDetail.getJoinedPlayers().get(0);
            showPaymentStatus(player2);
            binding.profileVu2.populateData(player2.getNickName(), player2.getPhotoUrl(), player2.getLevel(), true);
            binding.profileVu2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoProfile(player2.getId());
                }
            });
        }
        else {
            binding.btnCall2.setVisibility(View.GONE);
            binding.paymentStatusVu.setVisibility(View.GONE);
            binding.profileVu2.populateData("?", "", null, false);
        }

        binding.btnReject.setVisibility(View.GONE);
        if (matchDetail.getMyStatus().equalsIgnoreCase("no_request")) {
            binding.tvStatus.setText(R.string.open);
            binding.tvStatus.setTextColor(Color.parseColor("#0084ff"));
            binding.btnAccept.setVisibility(View.VISIBLE);
            binding.btnCancel.setVisibility(View.GONE);
            binding.checkVu.setVisibility(View.VISIBLE);
            binding.tvAccept.setText(R.string.challenge);
        }
        else if (matchDetail.getRequestStatus().equalsIgnoreCase("request_sent") && matchDetail.getMyStatus().equalsIgnoreCase("pending")) {
            binding.tvStatus.setText(R.string.pending);
            binding.tvStatus.setTextColor(Color.parseColor("#ff9f00"));
            binding.btnAccept.setVisibility(View.GONE);
            binding.checkVu.setVisibility(View.GONE);
            binding.btnCancel.setVisibility(View.VISIBLE);
        }
        else if (matchDetail.getRequestStatus().equalsIgnoreCase("request_received") && matchDetail.getMyStatus().equalsIgnoreCase("pending")) {
            binding.tvStatus.setText(R.string.pending);
            binding.tvStatus.setTextColor(Color.parseColor("#ff9f00"));
            binding.btnAccept.setVisibility(View.VISIBLE);
            binding.btnCancel.setVisibility(View.GONE);
            binding.checkVu.setVisibility(View.VISIBLE);
            binding.tvAccept.setText(R.string.accept);
            binding.btnReject.setVisibility(View.VISIBLE);
        }
        else if (matchDetail.getMyStatus().equalsIgnoreCase("accepted")) {
            binding.tvStatus.setText(R.string.accepted);
            binding.tvStatus.setTextColor(Color.parseColor("#49d483"));
            binding.btnAccept.setVisibility(View.GONE);
            binding.btnCancel.setVisibility(View.VISIBLE);
            binding.checkVu.setVisibility(View.GONE);
            binding.relChat.setVisibility(View.VISIBLE);
            setBadgeValue(matchDetail.getUnreadChatCount());
        }
        else if (matchDetail.getMyStatus().equalsIgnoreCase("rejected")) {
            binding.tvStatus.setText(R.string.rejected);
            binding.tvStatus.setTextColor(Color.parseColor("#f02301"));
            binding.btnAccept.setVisibility(View.GONE);
            binding.btnCancel.setVisibility(View.GONE);
            binding.checkVu.setVisibility(View.GONE);
        }
        else if (matchDetail.getMyStatus().equalsIgnoreCase("match_started")) {
            binding.tvStatus.setText(R.string.match_started);
            binding.tvStatus.setTextColor(Color.parseColor("#0084ff"));
            binding.btnAccept.setVisibility(View.GONE);
            binding.btnCancel.setVisibility(View.GONE);
            binding.checkVu.setVisibility(View.GONE);
        }
        else if (matchDetail.getMyStatus().equalsIgnoreCase("match_end")) {
            binding.tvStatus.setText(R.string.match_end);
            binding.tvStatus.setTextColor(Color.parseColor("#0084ff"));
            binding.btnAccept.setVisibility(View.GONE);
            binding.btnCancel.setVisibility(View.GONE);
            binding.checkVu.setVisibility(View.GONE);
            binding.relChat.setVisibility(View.VISIBLE);
            setBadgeValue(matchDetail.getUnreadChatCount());
        }
    }

    private void showPaymentStatus(OlePlayerInfo olePlayerInfo) {
        if (olePlayerInfo.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
            binding.paymentStatusVu.setVisibility(View.VISIBLE);
            if (olePlayerInfo.getPaymentStatus().equalsIgnoreCase("1")) {
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

    private void gotoProfile(String pId) {
        Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
        intent.putExtra("player_id", pId);
        startActivity(intent);
    }

    private void getMatchAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.matchDetail(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, Functions.getPrefValue(getContext(), Constants.kAppModule));
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

    private void addRemoveFav(boolean isLoader, String playerId, String status) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        addRemoveFavAPI(playerId, status, "player", new BaseActivity.FavCallback() {
            @Override
            public void addRemoveFav(boolean success, String msg) {
                Functions.hideLoader(hud);
                if (success) {
                    Functions.showToast(getContext(), msg, FancyToast.SUCCESS);
                    matchDetail.getCreatedBy().setFavorite(status);
                    if (status.equalsIgnoreCase("1")) {
                        binding.tvFav.setText(R.string.remove_from_fav);
                        binding.btnFav.setImageResource(R.drawable.fav_green);
                    }
                    else {
                        binding.tvFav.setText(R.string.add_to_fav);
                        binding.btnFav.setImageResource(R.drawable.club_unfav);
                    }
                }
                else {
                    Functions.showToast(getContext(), msg, FancyToast.ERROR);
                }
            }
        });
    }

    private void acceptRejectChallengeAPI(boolean isLoader, String playerId, String matchType, String requestStatus, String flag, String paymentMethod, String orderRef, String joinFee, String cardPaid, String walletPaid) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.acceptRejectChallenge(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, playerId, matchType, requestStatus, flag, orderRef, cardPaid, walletPaid, paymentMethod, Functions.getIPAddress(), joinFee);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            if (flag.equalsIgnoreCase("accept")) {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                                matchDetail.setMyStatus("accepted");
                                binding.btnAccept.setVisibility(View.GONE);
                                binding.checkVu.setVisibility(View.GONE);
                            }
                            else {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                                finish();
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

    private void joinChallengeAPI(boolean isLoader, String bookingId, String matchType, String paymentMethod, String orderRef, String joinFee, String cardPaid, String walletPaid) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.joinChallenge(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, matchType, orderRef, cardPaid, walletPaid, paymentMethod, Functions.getIPAddress(), joinFee);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            matchDetail.setMyStatus("pending");
                            binding.btnAccept.setVisibility(View.GONE);
                            binding.checkVu.setVisibility(View.GONE);
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

    private void cancelMatchAPI(boolean isLoader, String playerId, String matchType) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.cancelAcceptedMatch(Functions.getAppLang(getContext()), playerId, bookingId, matchType, Functions.getPrefValue(getContext(), Constants.kUserID));
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

    private void opponentPaymentAPI(boolean isLoader, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.opponentPayment(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), orderRef, paymentMethod, paidPrice, walletPaid, cardPaid, bookingId, Functions.getIPAddress());
        call.enqueue(new Callback<>() {
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
