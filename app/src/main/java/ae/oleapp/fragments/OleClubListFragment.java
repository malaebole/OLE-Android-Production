package ae.oleapp.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.activities.DocumentHomeActivity;
import ae.oleapp.activities.GiftsActivity;
import ae.oleapp.activities.OleFinanceActivity;
import ae.oleapp.activities.StatisticsActivity;
import ae.oleapp.adapters.OleClubListAdapter;
import ae.oleapp.adapters.OleClubNameAdapter;
import ae.oleapp.base.BaseFragment;
import ae.oleapp.databinding.OlefragmentClubListBinding;
import ae.oleapp.inventory.OleInventoryActivity;
import ae.oleapp.models.Club;
import ae.oleapp.models.UserInfo;
import ae.oleapp.owner.OleAddClubActivity;
import ae.oleapp.owner.OleAddFieldActivity;
import ae.oleapp.owner.OleBookingCountActivity;
import ae.oleapp.owner.OleCashDepositActivity;
import ae.oleapp.owner.OleClubDetailActivity;
import ae.oleapp.owner.OleClubPricingActivity;
import ae.oleapp.owner.OleClubSettingsActivity;
import ae.oleapp.owner.OleCreatePromotionActivity;
import ae.oleapp.owner.OleEmployeeListActivity;
import ae.oleapp.owner.OleFastBookingActivity;
import ae.oleapp.owner.OleFieldListActivity;
import ae.oleapp.owner.OleOwnerMainTabsActivity;
import ae.oleapp.owner.OleScheduleListActivity;
import ae.oleapp.player.OleShareActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OleClubListFragment extends BaseFragment implements View.OnClickListener {

    private OlefragmentClubListBinding binding;
    private final List<Club> clubList = new ArrayList<>();
     private OleClubNameAdapter oleClubNameAdapter;

    private String selectedClubId = "";
    private int selectedIndex = 0;

    private final boolean isFootballAvailable = false;
    private final boolean isPadelAvailable = false;

    public OleClubListFragment() {
        //Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        binding = OlefragmentClubListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();



        LinearLayoutManager oleClubNameLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubNameRecyclerVu.setLayoutManager(oleClubNameLayoutManager);
        oleClubNameAdapter = new OleClubNameAdapter(getContext(), clubList);
        oleClubNameAdapter.setItemClickListener(clubNameClickListener);
        binding.clubNameRecyclerVu.setAdapter(oleClubNameAdapter);


//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        binding.recyclerVu.setLayoutManager(layoutManager);
//        adapter = new OleClubListAdapter(getContext(), clubList,0);
//        //adapter.setCustomItemClickListener(customItemClickListener);
//        //adapter.setClubNameClicked(clubNameClickListner);
//        adapter.setItemClickListener(itemClickListener);
//        binding.recyclerVu.setAdapter(adapter);



        binding.noStadiumVu.setVisibility(View.GONE);
        binding.relNotif.setOnClickListener(this);
        binding.relMenu.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);

        return view;
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getClubList(clubList.isEmpty());
        setBadgeValue();
        //clubName.clear();  13/07/23
    }

    @Override
    public void onClick(View v) {
        if (v == binding.relNotif) {
            notifClicked();
        }
        else if (v == binding.relMenu) {
            menuClicked();
        }
        else if (v == binding.btnAdd) {
            addClicked();
        }
    }

    private void addClicked() {
        startActivity(new Intent(getContext(), OleAddClubActivity.class));
    }

    private void menuClicked() {
        if (getActivity() instanceof OleOwnerMainTabsActivity) {
            ((OleOwnerMainTabsActivity) getActivity()).menuClicked();
        }
    }

    private void notifClicked() {
        if (getActivity() instanceof OleOwnerMainTabsActivity) {
            ((OleOwnerMainTabsActivity) getActivity()).notificationsClicked();
        }
    }

    public void setBadgeValue() {
        if (AppManager.getInstance().notificationCount > 0) {
            binding.toolbarBadge.setVisibility(View.VISIBLE);
            binding.toolbarBadge.setNumber(AppManager.getInstance().notificationCount);
        }
        else  {
            binding.toolbarBadge.setVisibility(View.GONE);
        }
    }


    OleClubNameAdapter.ItemClickListener clubNameClickListener = new OleClubNameAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            selectedIndex = pos;
            selectedClubId = clubList.get(selectedIndex).getId(); //check this
            oleClubNameAdapter.setSelectedId(selectedClubId);
            populateClubData(selectedIndex);
        }
    };

    private void populateClubData (int pos){

        UserInfo userInfo = Functions.getUserinfo(getContext());
        if (userInfo.getPhotoUrl().isEmpty()){
            Glide.with(getContext())
                    .load(R.drawable.partner_temp_img)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.profileImgVu);
        }else{
            Glide.with(getContext())
                    .load(userInfo.getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.profileImgVu);
        }

            // if (clubList.get(pos).getId().equalsIgnoreCase(selectedClubId)) {
            Club club = clubList.get(pos);
            if (!club.getCoverPath().isEmpty()) {
                Glide.with(getActivity()).load(club.getCoverPath()).into(binding.imgVu);
            }
            //holder.tvName.setText(club.getName());
            binding.tvLoc.setText(club.getCity().getName());
            if (club.getRating().isEmpty()) {
                binding.tvRate.setText("0.0");
            }
            else {
                binding.tvRate.setText(club.getRating());
            }
            if (club.getIsOffer().equalsIgnoreCase("1")) {
                binding.offerVu.setVisibility(View.VISIBLE);
                binding.tvOffer.setText(club.getOffer());
            }
            else {
                binding.offerVu.setVisibility(View.GONE);
            }
            if (club.getIsFeatured().equalsIgnoreCase("1")) {
                binding.featureVu.setVisibility(View.VISIBLE);
            }
            else {
                binding.featureVu.setVisibility(View.GONE);
            }
            if (club.getFavoriteCount() == null || club.getFavoriteCount().isEmpty()) {
                binding.tvFavCount.setText("0");
            }
            else {
                binding.tvFavCount.setText(club.getFavoriteCount());
            }

           if (clubList.get(pos).getClubType().equalsIgnoreCase(Constants.kPadelModule)){
               binding.padelFastVu.setVisibility(View.VISIBLE);
               binding.footballFastVu.setVisibility(View.GONE);
           }else{
               binding.padelFastVu.setVisibility(View.GONE);
               binding.footballFastVu.setVisibility(View.VISIBLE);
           }

//            if (isPadelAvailable) {
//                binding.btnPadelBooking.setVisibility(View.VISIBLE);
//            }
//            else {
//                binding.btnPadelBooking.setVisibility(View.GONE);
//            }
//            if (isFootballAvailable) {
//                binding.btnFootballBooking.setVisibility(View.VISIBLE);
//            }
//            else {
//                binding.btnFootballBooking.setVisibility(View.GONE);
//            }

            binding.tvEarning.setText(String.format("%s %s", club.getTodayEarning(), club.getCurrency()));
            binding.tvBookings.setText(String.valueOf(club.getBookingCount()));
            binding.tvHours.setText(club.getTotalHours());
            binding.tvConfirmed.setText(club.getTotalConfirmed());
            binding.tvPending.setText(club.getWaitingUserCount());
            binding.tvNew.setText(club.getNewPlayersCount());

            binding.warningTag.setVisibility(View.GONE);
            binding.btnRenew.setVisibility(View.GONE);
            if (club.getIsExpired().equalsIgnoreCase("1")) {
                binding.warningTag.setVisibility(View.VISIBLE);
                binding.btnRenew.setVisibility(View.VISIBLE);
                binding.tvExpire.setText(getActivity().getString(R.string.membership_expired));
                binding.tvExpire.setTextColor(getActivity().getResources().getColor(R.color.redColor));
            }
            else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                try {
                    Date expiryDate = dateFormat.parse(club.getClubExpiryDate());
                    int days = Functions.getDateDifferenceInDays(new Date(), expiryDate);
                    days = days + 1;
                    if (days <= 0) {
                        binding.warningTag.setVisibility(View.VISIBLE);
                        binding.btnRenew.setVisibility(View.VISIBLE);
                        binding.tvExpire.setText(getActivity().getString(R.string.membership_expired));
                        binding.tvExpire.setTextColor(getActivity().getResources().getColor(R.color.redColor));
                    }
                    else if (days <= 10) {
                        binding.warningTag.setVisibility(View.VISIBLE);
                        binding.btnRenew.setVisibility(View.VISIBLE);
                        binding.tvExpire.setText(getActivity().getString(R.string.membership_expire_place_days, days));
                        binding.tvExpire.setTextColor(getActivity().getResources().getColor(R.color.redColor));
                    }
                    else {
                        binding.warningTag.setVisibility(View.GONE);
                        binding.btnRenew.setVisibility(View.GONE);
                        binding.tvExpire.setText(getActivity().getString(R.string.membership_expire_place_days, days));
                        binding.tvExpire.setTextColor(getActivity().getResources().getColor(R.color.subTextColor));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (club.getFieldsCount() > 0) {
                binding.infoVu.setVisibility(View.VISIBLE);
                binding.noFieldVu.setVisibility(View.GONE);
            }
            else {
                binding.infoVu.setVisibility(View.GONE);
                binding.noFieldVu.setVisibility(View.VISIBLE);
            }
                    binding.relMain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if (clubList.get(pos).getFieldsCount() > 0) {
                                    Intent intent = new Intent(getContext(), OleFieldListActivity.class);
                                    Gson gson = new Gson();
                                    intent.putExtra("club", gson.toJson(clubList.get(pos)));
                                    startActivity(intent);
                                }

                        }
                    });

                    binding.relFinance.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!clubList.get(pos).getId().isEmpty()) {
                                Intent intent = new Intent(getContext(), OleFinanceActivity.class);
                                intent.putExtra("club", clubList.get(pos).getId());
                                startActivity(intent);
                            }
                        }
                    });
                    binding.relInventory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent inventoryIntent = new Intent(getContext(), OleInventoryActivity.class);
                            inventoryIntent.putExtra("club_id", clubList.get(pos).getId());
                            startActivity(inventoryIntent);
                        }
                    });
                    binding.relDeposit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent depositIntent = new Intent(getContext(), OleCashDepositActivity.class);
                            depositIntent.putExtra("club_id", clubList.get(pos).getId());
                            startActivity(depositIntent);
                        }
                    });
                    binding.relGift.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), GiftsActivity.class);
                            intent.putExtra("club_id", clubList.get(pos).getId());
                            startActivity(intent);

                        }
                    });
                    binding.relPromotion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), OleCreatePromotionActivity.class);
                            startActivity(intent);
                        }
                    });
                    binding.relStatistics.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), StatisticsActivity.class);
                            intent.putExtra("club_id", clubList.get(pos).getId());
                            startActivity(intent);

                        }
                    });

                    binding.relEmployees.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), OleEmployeeListActivity.class);
                            intent.putExtra("club_id", clubList.get(pos).getId());
                            startActivity(intent);
//                            startActivity(new Intent(getContext(), OleEmployeeListActivity.class));

                        }
                    });

                    binding.relSettings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), OleClubSettingsActivity.class);
                            intent.putExtra("club", new Gson().toJson(clubList.get(pos)));
                            startActivity(intent);

                        }
                    });
                    binding.relShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getContext(), OleShareActivity.class));
                        }
                    });
                    binding.relContinuous.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            startActivity(new Intent(getContext(), OleScheduleListActivity.class));
                            Intent intent = new Intent(getContext(), OleScheduleListActivity.class);
                            intent.putExtra("club_id", clubList.get(pos).getId());
                            startActivity(intent);
                        }
                    });
                    binding.relPlayers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getContext(), OleBookingCountActivity.class));

                        }
                    });

                    binding.relTournament.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    binding.btnAddField.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), OleAddFieldActivity.class);
                            intent.putExtra("club_id", clubList.get(pos).getId());
                            startActivity(intent);
                        }
                    });
                    binding.btnRenew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), OleClubPricingActivity.class);
                            intent.putExtra("club_id", clubList.get(pos).getId());
                            intent.putExtra("can_choose", true);
                            startActivity(intent);
                        }
                    });

                    binding.btnPadelBooking.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), OleFastBookingActivity.class);
                            intent.putExtra("is_padel", true);
                            Gson gson = new Gson();
                            intent.putExtra("club", gson.toJson(clubList.get(selectedIndex)));
                            startActivity(intent);
                        }
                    });
                    binding.btnFootballBooking.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), OleFastBookingActivity.class);
                            intent.putExtra("is_padel", false);
                            Gson gson = new Gson();
                            intent.putExtra("club", gson.toJson(clubList.get(selectedIndex)));
                            startActivity(intent);
                        }
                    });
                    binding.relDocuments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), DocumentHomeActivity.class);
                            intent.putExtra("club_id", clubList.get(pos).getId());
                            startActivity(intent);
                        }
                    });
       // }
    }


    private void getClubList(boolean isLoader) {
        Call<ResponseBody> call;
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        call = AppManager.getInstance().apiInterface.getMyClubs(Functions.getAppLang(getActivity()),Functions.getPrefValue(getContext(), Constants.kUserID), "");
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
                            clubList.clear();
                            //clubName.clear();
                            AppManager.getInstance().clubs.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                Club club = gson.fromJson(arr.get(i).toString(), Club.class);
                                clubList.add(club);
                               // clubName.add(club);
                                AppManager.getInstance().clubs.add(club);
                            }

                            oleClubNameAdapter.setSelectedIndex(selectedIndex);
                            selectedClubId = clubList.get(selectedIndex).getId();
                            oleClubNameAdapter.setSelectedId(selectedClubId);
                            populateClubData(selectedIndex);

//                            if (clubList.size() > 0) {
//                                clubList.add(1, null);
//                            }

                            if (clubList.isEmpty()) {
                                binding.noStadiumVu.setVisibility(View.VISIBLE);
                            }
                            else {
                                binding.noStadiumVu.setVisibility(View.GONE);
                            }
                            //adapter.setAvailable(isFootball, isPadel);
                            //adapter.notifyDataSetChanged();
                        }
                        else {
                            binding.noStadiumVu.setVisibility(View.VISIBLE);
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
