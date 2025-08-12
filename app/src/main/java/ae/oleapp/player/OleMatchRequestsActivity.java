package ae.oleapp.player;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleMatchListAdapter;
import ae.oleapp.adapters.OleMatchDateAdapter;
import ae.oleapp.adapters.OleMatchRequestClubAdapter;
import ae.oleapp.adapters.OleProfilePadelMatchHistoryAdapter;
import ae.oleapp.adapters.OleResultListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityMatchRequestsBinding;
import ae.oleapp.dialogs.OlePaymentDialogFragment;
import ae.oleapp.dialogs.OlePositionDialogFragment;
import ae.oleapp.dialogs.OleResultFilterDialogFragment;
import ae.oleapp.fragments.OleHomeFragment;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleMatchResults;
import ae.oleapp.models.OlePadelMatchResults;
import ae.oleapp.models.OlePlayerMatch;
import ae.oleapp.models.OlePlayerPosition;
import ae.oleapp.padel.OlePadelChallengeActivity;
import ae.oleapp.padel.OlePadelMatchDetailActivity;
import ae.oleapp.padel.OleProfilePadelMatchHistoryDetailsActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.LocationHelperFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleMatchRequestsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityMatchRequestsBinding binding;
    private OleMatchListAdapter adapter;
    private OleResultListAdapter oleResultListAdapter;
    private OleProfilePadelMatchHistoryAdapter padelMatchAdapter;
    private final List<OlePlayerMatch> matchList = new ArrayList<>();
    private final List<OleMatchResults> resultList = new ArrayList<>();
    private final List<OlePadelMatchResults> olePadelMatchResults = new ArrayList<>();
    private final List<Club> clubList = new ArrayList<>();
    private Location location;
    private boolean isResult = false;
    private OleMatchDateAdapter rankDateAdapter;
    private OleMatchRequestClubAdapter clubAdapter;
    private final String kDateFormat = "dd/MM/yyyy";
    private List<Date> dateList = new ArrayList<>();
    private int selectedDateIndex = 0;
    private String fromDate = "";
    private String toDate = "";
    private String name = "";
    private String clubId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityMatchRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.match_request);

        boolean isResultOpen = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isResultOpen = bundle.getBoolean("is_result", false);
        }

        LinearLayoutManager catLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(catLayoutManager);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        String year = dateFormat.format(new Date());
        dateFormat.applyPattern(kDateFormat);
        String todayDate = dateFormat.format(new Date());
        dateList = Functions.getMonthAndYearBetween("01/01/"+year, todayDate, kDateFormat);
        dateList.add(0, null);
        selectedDateIndex = dateList.size()-1;
        setDates(dateList.get(selectedDateIndex));
        LinearLayoutManager daysLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.dateRecyclerVu.setLayoutManager(daysLayoutManager);
        rankDateAdapter = new OleMatchDateAdapter(getContext(), dateList, selectedDateIndex);
        rankDateAdapter.setOnItemClickListener(dateClickListener);
        binding.dateRecyclerVu.setAdapter(rankDateAdapter);
        if (selectedDateIndex != -1) {
            binding.dateRecyclerVu.scrollToPosition(selectedDateIndex);
        }

        binding.pullRefresh.setColorSchemeColors(getResources().getColor(R.color.blueColor));
        binding.pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLocationAndCallAPI();
            }
        });

        clubList.clear();
        clubList.addAll(AppManager.getInstance().clubs);
        Club club = new Club();
        club.setId("");
        club.setName(getString(R.string.all));
        clubList.add(0, club);
        LinearLayoutManager durationLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(durationLayoutManager);
        clubAdapter = new OleMatchRequestClubAdapter(getContext(), clubList, clubId);
        clubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(clubAdapter);

        if (isResultOpen) {
            resultClicked();
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1), true);
        }
        else {
            allClicked();
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0), true);
        }
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    allClicked();
                }
                else {
                    resultClicked();
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
        binding.btnFilter.setOnClickListener(this);

    }

    OleMatchDateAdapter.OnItemClickListener dateClickListener = new OleMatchDateAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            rankDateAdapter.setSelectedDateIndex(pos);
            setDates(dateList.get(pos));
        }
    };

    OleMatchRequestClubAdapter.OnItemClickListener clubClickListener = new OleMatchRequestClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            clubId = clubList.get(pos).getId();
            clubAdapter.setSelectedClubId(clubId);
            getMatchListAPI(true);
        }
    };

    private void setDates(Date date) {
        if (date == null) {
            fromDate = "";
            toDate = "";
            name = "";
        }
        else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(kDateFormat, Locale.ENGLISH);
            fromDate = dateFormat.format(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date toD = calendar.getTime();
            toDate = dateFormat.format(toD);
            name = "";
        }
        getLocationAndCallAPI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocationAndCallAPI();
    }

     private void allClicked() {
        isResult = false;
        adapter = new OleMatchListAdapter(getContext(), matchList);
        adapter.setItemClickListener(clickListener);
        binding.recyclerVu.setAdapter(adapter);
        binding.btnFilter.setVisibility(View.INVISIBLE);
        binding.dateRecyclerVu.setVisibility(View.GONE);
    }

    private void resultClicked() {
        isResult = true;
        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            padelMatchAdapter = new OleProfilePadelMatchHistoryAdapter(getContext(), olePadelMatchResults);
            padelMatchAdapter.setItemClickListener(padelItemClickListener);
            binding.recyclerVu.setAdapter(padelMatchAdapter);
        }
        else {
            oleResultListAdapter = new OleResultListAdapter(getContext(), resultList);
            oleResultListAdapter.setItemClickListener(itemClickListener);
            binding.recyclerVu.setAdapter(oleResultListAdapter);
        }
        binding.btnFilter.setVisibility(View.VISIBLE);
        binding.dateRecyclerVu.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnFilter) {
            filterClicked();
        }
    }

    private void filterClicked() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("ResultFilterDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleResultFilterDialogFragment dialogFragment = new OleResultFilterDialogFragment();
        dialogFragment.setDialogCallback(new OleResultFilterDialogFragment.DialogFragmentCallback() {
            @Override
            public void applyFilter(String name, String fromDate, String toDate) {
                if (fromDate.equalsIgnoreCase("") && toDate.equalsIgnoreCase("")) {
                    OleMatchRequestsActivity.this.name = name;
                }
                else {
                    OleMatchRequestsActivity.this.name = name;
                    OleMatchRequestsActivity.this.fromDate = fromDate;
                    OleMatchRequestsActivity.this.toDate = toDate;
                    rankDateAdapter.setSelectedDateIndex(-1);
                }
                getMatchListAPI(true);
            }
        });
        dialogFragment.show(fragmentTransaction, "ResultFilterDialogFragment");
    }

    private void getLocationAndCallAPI() {
        LocationHelperFragment helper = LocationHelperFragment.getInstance(getSupportFragmentManager());
        helper.startLocationRequest(new LocationHelperFragment.LocationCallback() {
                @Override
                public void onLocationRetrieved(Location location) {
                    OleMatchRequestsActivity.this.location = location;
                    if (isResult) {
                        getMatchListAPI(resultList.isEmpty());
                    }
                    else {
                        getMatchListAPI(matchList.isEmpty());
                    }
                }

                @Override
                public void onLocationError(String message) {
                    binding.pullRefresh.setRefreshing(false);
                    if (isResult) {
                        getMatchListAPI(resultList.isEmpty());
                    }
                    else {
                        getMatchListAPI(matchList.isEmpty());
                    }
                }
            });

    }


//    private void getLocationAndCallAPI() {
//        new AirLocation(getContext(), true, false, new AirLocation.Callbacks() {
//            @Override
//            public void onSuccess(Location loc) {
//                // do something
//                location = loc;
//                if (isResult) {
//                    getMatchListAPI(resultList.isEmpty());
//                }
//                else {
//                    getMatchListAPI(matchList.isEmpty());
//                }
//            }
//
//            @Override
//            public void onFailed(AirLocation.LocationFailedEnum locationFailedEnum) {
//                // do something
//                binding.pullRefresh.setRefreshing(false);
//                if (isResult) {
//                    getMatchListAPI(resultList.isEmpty());
//                }
//                else {
//                    getMatchListAPI(matchList.isEmpty());
//                }
//            }
//        });
//    }

    OleResultListAdapter.ItemClickListener itemClickListener = new OleResultListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            Intent intent = new Intent(getContext(), OleHistoryDetailActivity.class);
            intent.putExtra("playerId_1", resultList.get(pos).getPlayerOne().getId());
            intent.putExtra("playerId_2", resultList.get(pos).getPlayerTwo().getId());
            startActivity(intent);
        }
    };

    OleProfilePadelMatchHistoryAdapter.ItemClickListener padelItemClickListener = new OleProfilePadelMatchHistoryAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            Intent intent = new Intent(getContext(), OleProfilePadelMatchHistoryDetailsActivity.class);
            intent.putExtra("match", new Gson().toJson(olePadelMatchResults.get(pos)));
            intent.putExtra("player_id", olePadelMatchResults.get(pos).getCreatedBy().getId());
            startActivity(intent);
        }
    };

    OleMatchListAdapter.ItemClickListener clickListener = new OleMatchListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            OlePlayerMatch olePlayerMatch = matchList.get(pos);
            if (olePlayerMatch.getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
                Intent intent = new Intent(getContext(), OleGameDetailActivity.class);
                intent.putExtra("booking_id", olePlayerMatch.getBookingId());
                startActivity(intent);
            }
            else {
                if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                    Intent intent = new Intent(getContext(), OlePadelMatchDetailActivity.class);
                    intent.putExtra("booking_id", matchList.get(pos).getBookingId());
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getContext(), OleMatchDetailActivity.class);
                    intent.putExtra("booking_id", matchList.get(pos).getBookingId());
                    startActivity(intent);
                }
            }
        }

        @Override
        public void joinClicked(View view, int pos) {
            // football challenge
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            OlePlayerMatch match = matchList.get(pos);
            if (match.getRemainingPlayers().isEmpty() || match.getRemainingPlayers().equalsIgnoreCase("0")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.alert))
                        .setMessage(getResources().getString(R.string.player_joined_you_want_notification))
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                gameAvailRequestAPI(match.getBookingId());
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
                OlePositionDialogFragment dialogFragment = new OlePositionDialogFragment(match.getJoiningFee(), match.getCurrency());
                dialogFragment.setDialogCallback(new OlePositionDialogFragment.PositionDialogCallback() {
                    @Override
                    public void confirmClicked(OlePlayerPosition olePlayerPosition) {
                        openPaymentDialog(match.getJoiningFee(), Functions.getPrefValue(getContext(), Constants.kCurrency), "", "", "", true, false, match.getClubId(), new OlePaymentDialogFragment.PaymentDialogCallback() {
                            @Override
                            public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                                joinGameAPI(true, match.getBookingId(), olePlayerPosition.getPositionId(), match.getJoiningFee(), pos, paymentMethod, orderRef, cardPaid, walletPaid);
                            }
                        });
                    }
                });
                dialogFragment.show(fragmentTransaction, "PositionDialogFragment");
            }
        }

        @Override
        public void acceptClicked(View view, int pos) {
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            OlePlayerMatch olePlayerMatch = matchList.get(pos);
            if (olePlayerMatch.getRequestStatus().equalsIgnoreCase("request_received")) {
                acceptChallenge(olePlayerMatch.getBookingId(), olePlayerMatch.getBookingType(), olePlayerMatch.getRequestStatus(), olePlayerMatch.getJoiningFee(), olePlayerMatch.getClubId(), pos);
            }
            else {
                joinChallenge(olePlayerMatch.getBookingId(), olePlayerMatch.getBookingType(), olePlayerMatch.getJoiningFee(), olePlayerMatch.getClubId(), pos);
            }
        }

        @Override
        public void challengeClicked(View view, int pos) {
            // padel challenge
            if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            Intent intent = new Intent(getContext(), OlePadelChallengeActivity.class);
            intent.putExtra("match", new Gson().toJson(matchList.get(pos)));
            startActivity(intent);
        }
    };

    private void acceptChallenge(String bookingId, String bookingType, String requestStatus, String price, String clubId, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.match))
                .setMessage(R.string.do_you_want_to_accept_request)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openPaymentDialog(price, Functions.getPrefValue(getContext(), Constants.kCurrency), "", "", "", false, false, clubId, new OlePaymentDialogFragment.PaymentDialogCallback() {
                            @Override
                            public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                                acceptRejectChallengeAPI(true, bookingId, bookingType, requestStatus, "accept", pos, paymentMethod, orderRef, price, cardPaid, walletPaid);
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

    private void joinChallenge(String bookingId, String bookingType, String price, String clubId, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.match))
                .setMessage(R.string.want_to_send_challenge)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openPaymentDialog(price, Functions.getPrefValue(getContext(), Constants.kCurrency), "", "", "", false, false, clubId, new OlePaymentDialogFragment.PaymentDialogCallback() {
                            @Override
                            public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                                joinChallengeAPI(true, bookingId, bookingType, pos, paymentMethod, orderRef, price, cardPaid, walletPaid);
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

    private void getMatchListAPI(boolean isLoader) {
        Call<ResponseBody> call;
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        if (location == null) {
            call = AppManager.getInstance().apiInterface.allMatches(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), 0, 0, name, fromDate, toDate, clubId, Functions.getPrefValue(getContext(), Constants.kAppModule));
        }
        else {
            call = AppManager.getInstance().apiInterface.allMatches(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), location.getLatitude(), location.getLongitude(), name, fromDate, toDate, clubId, Functions.getPrefValue(getContext(), Constants.kAppModule));
        }
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                binding.pullRefresh.setRefreshing(false);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            JSONArray arrR = object.getJSONArray("matches_result");
                            JSONArray arrPR = object.getJSONArray("padel_matches_result");
                            Gson gson = new Gson();
                            matchList.clear();
                            resultList.clear();
                            olePadelMatchResults.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                OlePlayerMatch match = gson.fromJson(arr.get(i).toString(), OlePlayerMatch.class);
                                if (!match.getMyStatus().equalsIgnoreCase("canceled")) {
                                    matchList.add(match);
                                }
                            }
                            for (int i = 0; i < arrR.length(); i++) {
                                resultList.add(gson.fromJson(arrR.get(i).toString(), OleMatchResults.class));
                            }
                            for (int i = 0; i < arrPR.length(); i++) {
                                olePadelMatchResults.add(gson.fromJson(arrPR.get(i).toString(), OlePadelMatchResults.class));
                            }
                        }
                        else {
                            matchList.clear();
                            resultList.clear();
                            olePadelMatchResults.clear();
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                        if (isResult) {
                            if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                                padelMatchAdapter.notifyDataSetChanged();
                            }
                            else {
                                oleResultListAdapter.notifyDataSetChanged();
                            }
                        }
                        else {
                            adapter.notifyDataSetChanged();
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
                binding.pullRefresh.setRefreshing(false);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void acceptRejectChallengeAPI(boolean isLoader, String bookingId, String matchType, String requestStatus, String flag, int pos, String paymentMethod, String orderRef, String joinFee, String cardPaid, String walletPaid) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.acceptRejectChallenge(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, "", matchType, requestStatus, flag, orderRef, cardPaid, walletPaid, paymentMethod, Functions.getIPAddress(), joinFee);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            matchList.get(pos).setMyStatus("accepted");
                            adapter.notifyItemChanged(pos);
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

    private void joinChallengeAPI(boolean isLoader, String bookingId, String matchType, int pos, String paymentMethod, String orderRef, String joinFee, String cardPaid, String walletPaid) {
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
                            matchList.get(pos).setMyStatus("pending");
                            adapter.notifyItemChanged(pos);
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

    private void joinGameAPI(boolean isLoader, String bookingId, String position, String fee, int pos, String paymentMethod, String orderRef, String cardPaid, String walletPaid) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.joinGame(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, position, fee, orderRef, cardPaid, walletPaid, paymentMethod, Functions.getIPAddress());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            matchList.get(pos).setMyStatus("pending");
                            adapter.notifyItemChanged(pos);
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
