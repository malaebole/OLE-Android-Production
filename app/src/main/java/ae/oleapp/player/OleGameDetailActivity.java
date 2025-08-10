package ae.oleapp.player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import android.view.ViewTreeObserver;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.woxthebox.draglistview.BoardView;
import com.woxthebox.draglistview.ColumnProperties;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleFacilityAdapter;
import ae.oleapp.adapters.OleTeamAdapter;
import ae.oleapp.adapters.OleTeamDragItem;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityGameDetailBinding;
import ae.oleapp.dialogs.OlePaymentDialogFragment;
import ae.oleapp.dialogs.OlePositionDialogFragment;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OleDataModel;
import ae.oleapp.models.OleGameTeam;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.OlePlayerMatch;
import ae.oleapp.models.OlePlayerPosition;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleGameDetailActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityGameDetailBinding binding;
    private String bookingId = "";
    private OlePlayerMatch matchDetail;
    private final List<OleClubFacility> facilityList = new ArrayList<>();
    private final List<OlePlayerInfo> playerList = new ArrayList<>();
    private boolean isCheck = false;
    private OleFacilityAdapter oleFacilityAdapter;
    private final List<OlePlayerInfo> allPlayerList = new ArrayList<>();
    private final List<OlePlayerInfo> teamAList = new ArrayList<>();
    private final List<OlePlayerInfo> teamBList = new ArrayList<>();
    private boolean isCaptainAvailable = false;
    private String captainId = "";
    private boolean isMyTurn = true;
    private DatabaseReference databaseReference;
    private ValueEventListener databaseHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityGameDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.game_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingId = bundle.getString("booking_id", "");
        }

        LinearLayoutManager facLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.facRecyclerVu.setLayoutManager(facLayoutManager);
        oleFacilityAdapter = new OleFacilityAdapter(getContext(), facilityList, true);
        binding.facRecyclerVu.setAdapter(oleFacilityAdapter);

        binding.relChat.setVisibility(View.GONE);
        binding.toolbarBadge.setVisibility(View.GONE);

        detailVuClicked();
        binding.tvTurn.setVisibility(View.GONE);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    detailVuClicked();
                }
                else {
                    formationVuClicked();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.phoneVu.setOnClickListener(this);
        binding.mapVu.setOnClickListener(this);
        binding.btnFav.setOnClickListener(this);
        binding.profileVu.setOnClickListener(this);
        binding.joinedPlayerVu.setOnClickListener(this);
        binding.checkVu.setOnClickListener(this);
        binding.btnPreview.setOnClickListener(this);
        binding.btnJoin.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.btnChat.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);

        getMatchAPI(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (binding.formationInfoVu.getVisibility() == View.VISIBLE) {
            if (matchDetail!=null && !matchDetail.getBookingStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
                observeChange();
            }
        }
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
        if (databaseReference != null && databaseHandle != null) {
            databaseReference.child("match").child(bookingId).removeEventListener(databaseHandle);
        }
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
        else if (v == binding.phoneVu) {
            phoneClicked();
        }
        else if (v == binding.mapVu) {
            locClicked();
        }
        else if (v == binding.btnFav) {
            favClicked();
        }
        else if (v == binding.profileVu) {
            imgVuClicked();
        }
        else if (v == binding.joinedPlayerVu) {
            joinedPlayerClicked();
        }
        else if (v == binding.checkVu) {
            checkClicked();
        }
        else if (v == binding.btnPreview) {
            previewClicked();
        }
        else if (v == binding.btnJoin) {
            joinClicked();
        }
        else if (v == binding.btnCancel) {
            cancelClicked();
        }
        else if (v == binding.btnChat) {
            chatClicked();
        }
        else if (v == binding.btnShare) {
            shareClicked();
        }
    }

    private void shareClicked() {
        if (matchDetail != null) {
            Intent intent = new Intent(getContext(), OleFriendlyGameShareActivity.class);
            intent.putExtra("club_name", matchDetail.getClubName());
            intent.putExtra("date", matchDetail.getBookingDate());
            intent.putExtra("time", matchDetail.getBookingTime());
            Gson gson = new Gson();
            intent.putExtra("player_one", gson.toJson(matchDetail.getCreatedBy()));
            intent.putExtra("player_list", gson.toJson(matchDetail.getJoinedPlayers()));
            int totalPlayers = 0;
            if (matchDetail.getTotalPlayers() == null || matchDetail.getTotalPlayers().isEmpty()) {
                totalPlayers = 0;
            }
            else {
                totalPlayers = Integer.parseInt(matchDetail.getTotalPlayers());
            }
            intent.putExtra("req_players", totalPlayers);
            startActivity(intent);
        }
    }

    private void phoneClicked() {
        if (!binding.tvPhone.getText().toString().isEmpty()) {
            makeCall(binding.tvPhone.getText().toString());
        }
    }

    private void detailVuClicked() {
        binding.detailInfoVu.setVisibility(View.VISIBLE);
        binding.formationInfoVu.setVisibility(View.GONE);
    }

    private void formationVuClicked() {
        binding.detailInfoVu.setVisibility(View.GONE);
        binding.formationInfoVu.setVisibility(View.VISIBLE);
        if (matchDetail!=null && !matchDetail.getBookingStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
            observeChange();
        }
        setupBoard();
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

    private void imgVuClicked() {
        if (matchDetail==null) {
            return;
        }
        openInfoDialog(matchDetail.getCreatedBy().getId());
    }

    private void joinedPlayerClicked() {
        if (matchDetail != null) {
            Intent intent = new Intent(getContext(), OleJoinedPlayersActivity.class);
            intent.putExtra("match_type", matchDetail.getBookingType());
            intent.putExtra("booking_id", matchDetail.getBookingId());
            intent.putExtra("req_status", matchDetail.getRequestStatus());
            intent.putExtra("booking_status", matchDetail.getBookingStatus());
            intent.putExtra("rem_amount", "");
            intent.putExtra("currency", "");
            intent.putExtra("matchCreatedId", matchDetail.getCreatedBy().getId());
            intent.putExtra("isFromMatchDetail", true);
            startActivity(intent);
        }
    }

    private void openInfoDialog(String playerId) {
        Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
        intent.putExtra("player_id", playerId);
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

    private void previewClicked() {
        if (matchDetail != null) {
            Intent intent = new Intent(getContext(), OlePreviewFieldActivity.class);
            intent.putExtra("is_from_booking", false);
            intent.putExtra("is_captain", isCaptainAvailable);
            intent.putExtra("booking_id", bookingId);
            intent.putExtra("creator_id", matchDetail.getCreatedBy().getId());
            Gson gson = new Gson();
            intent.putExtra("team", gson.toJson(matchDetail.getGameTeam()));
            startActivity(intent);
        }
    }

    private void joinClicked() {
        if (!isCheck) {
            Functions.showToast(getContext(), getString(R.string.requset_check), FancyToast.ERROR);
            return;
        }
        if (matchDetail != null && matchDetail.getMyStatus().equalsIgnoreCase("no_request")) {
            if (matchDetail.getRemainingPlayers().isEmpty() || matchDetail.getRemainingPlayers().equalsIgnoreCase("0")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.alert))
                        .setMessage(getResources().getString(R.string.player_joined_you_want_notification))
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                gameAvailRequestAPI(matchDetail.getBookingId());
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                builder.show();
            }
            else {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("PositionDialogFragment");
                if (prev != null) {
                    fragmentTransaction.remove(prev);
                }
                fragmentTransaction.addToBackStack(null);
                OlePositionDialogFragment dialogFragment = new OlePositionDialogFragment(matchDetail.getJoiningFee(), matchDetail.getCurrency());
                dialogFragment.setDialogCallback(new OlePositionDialogFragment.PositionDialogCallback() {
                    @Override
                    public void confirmClicked(OlePlayerPosition olePlayerPosition) {
                        openPaymentDialog(matchDetail.getJoiningFee(), Functions.getPrefValue(getContext(), Constants.kCurrency), "", "", "", true, false, matchDetail.getClubId(), new OlePaymentDialogFragment.PaymentDialogCallback() {
                            @Override
                            public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                                joinGameAPI(true, matchDetail.getBookingId(), olePlayerPosition.getPositionId(), matchDetail.getJoiningFee(), paymentMethod, orderRef, cardPaid, walletPaid);
                            }
                        });
                    }
                });
                dialogFragment.show(fragmentTransaction, "PositionDialogFragment");
            }
        }
    }

    private void cancelClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.match))
                .setMessage(getResources().getString(R.string.do_you_want_to_cancel_match))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (matchDetail != null) {
                            if (matchDetail.getMyStatus().equalsIgnoreCase("pending")) {
                                acceptRejectChallengeAPI(true, matchDetail.getCreatedBy().getId(), matchDetail.getBookingType(), matchDetail.getRequestStatus(), "reject");
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
            intent.putExtra("join_players", gson.toJson(playerList));
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
        binding.profileVu.populateData(matchDetail.getCreatedBy().getNickName(), matchDetail.getCreatedBy().getPhotoUrl(), matchDetail.getCreatedBy().getLevel(), true);
        binding.tvClubName.setText(matchDetail.getClubName());
        binding.tvFieldName.setText(String.format("%s (%s)", matchDetail.getFieldName(), matchDetail.getFieldSize()));
        binding.tvTime.setText(String.format("%s (%s)", matchDetail.getBookingTime().split("-")[0], matchDetail.getDuration()));
        binding.tvPhone.setText(matchDetail.getCreatedBy().getPhone());
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
        if (facilityList.size() > 0) {
            binding.facRecyclerVu.setVisibility(View.VISIBLE);
            oleFacilityAdapter.notifyDataSetChanged();
        }
        else {
            binding.facRecyclerVu.setVisibility(View.GONE);
        }

        binding.tvJoinedCount.setText(String.valueOf(matchDetail.getJoinedPlayers().size()));
        playerList.clear();
        playerList.addAll(matchDetail.getJoinedPlayers());

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
            Glide.with(getContext()).load(url).into(binding.mapVu);
        }

        checkTeamCreatedOrNot();

        if (matchDetail.getMyStatus().equalsIgnoreCase("no_request")) {
            binding.tvStatus.setText(R.string.open);
            binding.tvStatus.setTextColor(Color.parseColor("#0084ff"));
            binding.btnJoin.setVisibility(View.VISIBLE);
            binding.btnCancel.setVisibility(View.GONE);
            binding.checkVu.setVisibility(View.VISIBLE);
        }
        else if (matchDetail.getMyStatus().equalsIgnoreCase("pending")) {
            binding.tvStatus.setText(R.string.pending);
            binding.tvStatus.setTextColor(Color.parseColor("#ff9f00"));
            binding.btnJoin.setVisibility(View.GONE);
            binding.btnCancel.setVisibility(View.VISIBLE);
            binding.checkVu.setVisibility(View.GONE);
        }
        else if (matchDetail.getMyStatus().equalsIgnoreCase("accepted")) {
            binding.tvStatus.setText(R.string.accepted);
            binding.tvStatus.setTextColor(Color.parseColor("#49d483"));
            binding.btnJoin.setVisibility(View.GONE);
            binding.btnCancel.setVisibility(View.VISIBLE);
            binding.checkVu.setVisibility(View.GONE);
            binding.relChat.setVisibility(View.VISIBLE);
            setBadgeValue(matchDetail.getUnreadChatCount());
        }
        else if (matchDetail.getMyStatus().equalsIgnoreCase("rejected")) {
            binding.tvStatus.setText(R.string.rejected);
            binding.tvStatus.setTextColor(Color.parseColor("#f02301"));
            binding.btnJoin.setVisibility(View.GONE);
            binding.btnCancel.setVisibility(View.GONE);
            binding.checkVu.setVisibility(View.GONE);
        }
        else if (matchDetail.getMyStatus().equalsIgnoreCase("match_end")) {
            binding.tvStatus.setText(R.string.match_end);
            binding.tvStatus.setTextColor(Color.parseColor("#0084ff"));
            binding.btnJoin.setVisibility(View.GONE);
            binding.btnCancel.setVisibility(View.GONE);
            binding.checkVu.setVisibility(View.GONE);
            binding.relChat.setVisibility(View.VISIBLE);
            setBadgeValue(matchDetail.getUnreadChatCount());
        }
    }

    private void checkTeamCreatedOrNot() {
        if (matchDetail == null) { return; }
        if (matchDetail.getGameTeam().isEmpty()) {
            binding.tabVu.setVisibility(View.GONE);
        }
        else {
            binding.tabVu.setVisibility(View.VISIBLE);
            binding.boardVu.setSnapToColumnsWhenScrolling(true);
            binding.boardVu.setSnapToColumnWhenDragging(true);
            binding.boardVu.setSnapDragItemToTouch(true);
            binding.boardVu.setSnapToColumnInLandscape(false);
            binding.boardVu.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER);
            binding.boardVu.setBoardListener(boardListener);
            binding.boardVu.setBoardCallback(boardCallback);

            OleGameTeam oleGameTeam = matchDetail.getGameTeam();
            binding.tvTeamA.setText(oleGameTeam.getTeamAName());
            binding.tvTeamB.setText(oleGameTeam.getTeamBName());
            binding.vuColorA.setCardBackgroundColor(Color.parseColor(oleGameTeam.getTeamAColor()));
            binding.vuColorB.setCardBackgroundColor(Color.parseColor(oleGameTeam.getTeamBColor()));
            teamAList.clear();
            teamAList.addAll(oleGameTeam.getTeamAPlayers());
            teamBList.clear();
            teamBList.addAll(oleGameTeam.getTeamBPlayers());
            isCaptainAvailable = false;
            checkCaptainAvailable();
            allPlayerList.clear();
            if (isCaptainAvailable) {
                allPlayerList.addAll(matchDetail.getJoinedPlayers());
                allPlayerList.addAll(matchDetail.getManualPlayers());
                Iterator<OlePlayerInfo> iterator = allPlayerList.iterator();
                while (iterator.hasNext()) {
                    OlePlayerInfo info = iterator.next();
                    int index = checkPlayerExistInTeamA(info.getId());
                    if (index != -1) {
                        iterator.remove();
                    }
                    index = checkPlayerExistInTeamB(info.getId());
                    if (index != -1) {
                        iterator.remove();
                    }
                }
            }

            showHideTeamPlaceholder();
            if (binding.tabLayout.getSelectedTabPosition() == 1) {
                // if formation vu is showing then need it
                setupBoard();
            }
        }
    }

    private void setupBoard() {
        binding.boardVu.clearBoard();
        binding.boardVu.setCustomDragItem(new OleTeamDragItem(getContext() , R.layout.oleteam_player));
        binding.boardVu.setCustomColumnDragItem(null);
        binding.boardVu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.boardVu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int boardWidth = binding.boardVu.getWidth();
                if (boardWidth<=0) {
                    return;
                }
                int width = boardWidth -(int)getResources().getDimension(R.dimen._10sdp);
                binding.boardVu.setColumnWidth(width/3);
                addTeamAList();
                addAllPlayerList();
                addTeamBList();
            }
        });
    }

    BoardView.BoardListener boardListener = new BoardView.BoardListenerAdapter() {
        @Override
        public void onItemChangedColumn(int oldColumn, int newColumn) {

        }

        @Override
        public void onItemDragEnded(int fromColumn, int fromRow, int toColumn, int toRow) {
            if (fromColumn != toColumn) {
                OleGameTeam oleGameTeam = matchDetail.getGameTeam();
                OlePlayerInfo olePlayerInfo = (OlePlayerInfo) binding.boardVu.getAdapter(toColumn).getItemList().get(toRow);
                if (olePlayerInfo != null) {
                    if (toColumn == 2) {
                        // team B
                        addRemovePlayerToTeam(true, oleGameTeam.getTeamAId(), oleGameTeam.getTeamBId(), oleGameTeam.getTeamBId(), olePlayerInfo.getId(), "add");
                    } else if (toColumn == 1) {
                        // no team
                        addRemovePlayerToTeam(true, oleGameTeam.getTeamAId(), oleGameTeam.getTeamBId(), "", olePlayerInfo.getId(), "remove");
                    }
                }
                showHideTeamPlaceholder();
            }
        }
    };

    BoardView.BoardCallback boardCallback = new BoardView.BoardCallback() {
        @Override
        public boolean canDragItemAtPosition(int column, int row) {
            if (matchDetail.getBookingStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
                return false;
            }
            if (isCaptainAvailable && (column == 1 || column == 2)) {
                if (column == 2 && teamBList.get(row).getId().equalsIgnoreCase(captainId)) {
                    return false;
                }
                else {
                    return isMyTurn;
                }
            }
            else {
                return false;
            }
        }

        @Override
        public boolean canDropItemAtPosition(int oldColumn, int oldRow, int newColumn, int newRow) {
            if (matchDetail.getBookingStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
                return false;
            }
            if (isCaptainAvailable && (newColumn == 1 || newColumn  == 2)) {
                return isMyTurn;
            }
            else {
                return false;
            }
        }
    };

    private void addAllPlayerList() {
        OleTeamAdapter oleTeamAdapter = new OleTeamAdapter(getContext(), allPlayerList, R.layout.oleteam_player, R.id.item_layout, true, 1);
        oleTeamAdapter.setItemClickListener(teamItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ColumnProperties columnProperties = ColumnProperties.Builder.newBuilder(oleTeamAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(true)
                .setColumnBackgroundColor(Color.TRANSPARENT)
                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                .build();

        binding.boardVu.addColumn(columnProperties);
    }

    private void addTeamAList() {
        OleTeamAdapter oleTeamAdapter = new OleTeamAdapter(getContext(), teamAList, R.layout.oleteam_player, R.id.item_layout, true, 0);
        oleTeamAdapter.setItemClickListener(teamItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ColumnProperties columnProperties = ColumnProperties.Builder.newBuilder(oleTeamAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(true)
                .setColumnBackgroundColor(Color.TRANSPARENT)
                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                .build();

        binding.boardVu.addColumn(columnProperties);
    }

    private void addTeamBList() {
        OleTeamAdapter oleTeamAdapter = new OleTeamAdapter(getContext(), teamBList, R.layout.oleteam_player, R.id.item_layout, true, 2);
        oleTeamAdapter.setItemClickListener(teamItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ColumnProperties columnProperties = ColumnProperties.Builder.newBuilder(oleTeamAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(true)
                .setColumnBackgroundColor(Color.TRANSPARENT)
                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                .build();

        binding.boardVu.addColumn(columnProperties);
    }

    OleTeamAdapter.OnItemClickListener teamItemClickListener = new OleTeamAdapter.OnItemClickListener() {
        @Override
        public void itemClicked(View view, int pos, int columnIndex) {

        }
    };

    private int checkPlayerExistInTeamA(String id) {
        int result = -1;
        for (int i = 0; i < teamAList.size(); i++) {
            if (teamAList.get(i).getId().equalsIgnoreCase(id)) {
                result = i;
                break;
            }
        }
        return result;
    }

    private int checkPlayerExistInTeamB(String id) {
        int result = -1;
        for (int i = 0; i < teamBList.size(); i++) {
            if (teamBList.get(i).getId().equalsIgnoreCase(id)) {
                result = i;
                break;
            }
        }
        return result;
    }

    private void showHideTeamPlaceholder() {
        if (teamAList.size() > 0) {
            binding.teamAPlace.setVisibility(View.GONE);
        }
        else {
            binding.teamAPlace.setVisibility(View.VISIBLE);
        }
        if (teamBList.size() > 0) {
            binding.teamBPlace.setVisibility(View.GONE);
        }
        else {
            binding.teamBPlace.setVisibility(View.VISIBLE);
        }
    }

    private void checkCaptainAvailable() {
        for (OlePlayerInfo info : teamBList) {
            if (info.getIsCaptain().equalsIgnoreCase("1") && info.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                isCaptainAvailable = true;
                captainId = info.getId();
                // firebase
                break;
            }
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

    private void cancelMatchAPI(boolean isLoader, String playerId, String matchType) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.cancelAcceptedMatch(Functions.getAppLang(getContext()), playerId, bookingId, matchType, Functions.getPrefValue(getContext(), Constants.kUserID));
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

    private void acceptRejectChallengeAPI(boolean isLoader, String playerId, String matchType, String requestStatus, String flag) {
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

    private void joinGameAPI(boolean isLoader, String bookingId, String position, String fee, String paymentMethod, String orderRef, String cardPaid, String walletPaid) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.joinGame(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, position, fee, orderRef, cardPaid, walletPaid, paymentMethod, Functions.getIPAddress());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            matchDetail.setMyStatus("pending");
                            binding.tvStatus.setText(R.string.pending);
                            binding.btnJoin.setVisibility(View.GONE);
                            binding.btnCancel.setVisibility(View.VISIBLE);
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

    private void addRemovePlayerToTeam(boolean isLoader, String teamAId, String teamBId, String targetTeamId, String playerId, String type) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addRemovePlayerFromTeam(Functions.getAppLang(getContext()), teamAId, teamBId, targetTeamId, playerId, "0", type);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            if (isLoader) {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            }
                            if (isCaptainAvailable) {
                                saveData(playerId, type);
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

    private void saveData(String playerId, String type) {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("turn", "captain");
        map.put("type", type);
        map.put("player_id", playerId);
        map.put("booking_id", bookingId);
        databaseReference.child("match").child(bookingId).setValue(map);
        if (allPlayerList.size() == 0) {
            binding.tvTurn.setVisibility(View.GONE);
            isMyTurn = true;
        }
        else {
            binding.tvTurn.setVisibility(View.VISIBLE);
            isMyTurn = false;
        }
        binding.tvTurn.setVisibility(View.VISIBLE);
        binding.tvTurn.setText(R.string.captain_turn);
    }

    private void observeChange() {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        databaseHandle = databaseReference.child("match").child(bookingId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    binding.tvTurn.setVisibility(View.GONE);
                    return;
                }

                try {
                    OleDataModel object = snapshot.getValue(OleDataModel.class);
                    if (object!=null) {
                        String bookId = object.getBooking_id();
                        if (!bookId.equalsIgnoreCase(bookingId)) {
                            binding.tvTurn.setVisibility(View.GONE);
                            return;
                        }
                        String type = object.getType();
                        String turn = object.getTurn();
                        if (turn.equalsIgnoreCase("creator")) {
                            if (isCaptainAvailable) {
                                binding.tvTurn.setVisibility(View.VISIBLE);
                                binding.tvTurn.setText(R.string.your_turn);
                                if (allPlayerList.size() == 0) {
                                    binding.tvTurn.setVisibility(View.GONE);
                                }
                            }
                            else {
                                binding.tvTurn.setVisibility(View.GONE);
                            }
                            if (type.equalsIgnoreCase("captain_created") || type.equalsIgnoreCase("captain_removed") || type.equalsIgnoreCase("add_player_to_game")) {
                                if (type.equalsIgnoreCase("captain_removed")) {
                                    isCaptainAvailable = false;
                                    captainId = "";
                                    databaseReference.removeValue();
                                    binding.tvTurn.setVisibility(View.GONE);
                                }
                                getMatchAPI(true);
                                if (type.equalsIgnoreCase("captain_created") || type.equalsIgnoreCase("add_player_to_game")) {
                                    isMyTurn = false;
                                    if (type.equalsIgnoreCase("captain_created")) {
                                        isCaptainAvailable = true;
                                    }
                                }
                                else {
                                    isMyTurn = true;
                                }
                            }
                            else if (type.equalsIgnoreCase("add")) {
                                if (isCaptainAvailable) {
                                    String playerId = object.getPlayer_id();
                                    // creator can just add player in team A, so here need to add this player in team A for sync
                                    for (int i = 0; i < allPlayerList.size(); i++) {
                                        OlePlayerInfo info = allPlayerList.get(i);
                                        if (info.getId().equalsIgnoreCase(playerId)) {
                                            teamAList.add(info);
                                            allPlayerList.remove(i);
                                            binding.boardVu.getAdapter(0).notifyDataSetChanged();
                                            binding.boardVu.getAdapter(1).notifyDataSetChanged();
                                            isMyTurn = true;
                                            if (allPlayerList.size() == 0) {
                                                binding.tvTurn.setVisibility(View.GONE);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                            else if (type.equalsIgnoreCase("remove")) {
                                if (isCaptainAvailable) {
                                    String playerId = object.getPlayer_id();
                                    // creator can just remove player from team A, so here need to remove this player from team A for sync
                                    for (int i = 0; i < teamAList.size(); i++) {
                                        OlePlayerInfo info = teamAList.get(i);
                                        if (info.getId().equalsIgnoreCase(playerId)) {
                                            allPlayerList.add(info);
                                            teamAList.remove(i);
                                            binding.boardVu.getAdapter(0).notifyDataSetChanged();
                                            binding.boardVu.getAdapter(1).notifyDataSetChanged();
                                            isMyTurn = true;
                                            binding.tvTurn.setVisibility(View.GONE);
                                            break;
                                        }
                                    }
                                }
                            }
                            else if (type.equalsIgnoreCase("remove_player_from_game")) {
                                if (isCaptainAvailable) {
                                    String playerId = object.getPlayer_id();
                                    for (int i = 0; i < allPlayerList.size(); i++) {
                                        OlePlayerInfo info = allPlayerList.get(i);
                                        if (info.getId().equalsIgnoreCase(playerId)) {
                                            allPlayerList.remove(i);
                                            binding.boardVu.getAdapter(1).notifyDataSetChanged();
                                            isMyTurn = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            else if (type.equalsIgnoreCase("captain_choose")) {
                                if (isCaptainAvailable) {
                                    isMyTurn = true;
                                    binding.tvTurn.setVisibility(View.VISIBLE);
                                    binding.tvTurn.setText(R.string.your_turn);
                                }
                            }
                            else if (type.equalsIgnoreCase("creator_choose")) {
                                if (isCaptainAvailable) {
                                    isMyTurn = false;
                                    binding.tvTurn.setVisibility(View.VISIBLE);
                                    binding.tvTurn.setText(R.string.captain_turn);
                                }
                            }
                        }
                        else {
                            if (allPlayerList.size() == 0) {
                                binding.tvTurn.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
