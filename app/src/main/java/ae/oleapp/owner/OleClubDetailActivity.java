package ae.oleapp.owner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.stfalcon.imageviewer.StfalconImageViewer;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleClubDetailDayAdapter;
import ae.oleapp.adapters.OleClubDetailFacAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityClubDetailBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.Day;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OleShiftTime;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleClubDetailActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityClubDetailBinding binding;
    private String clubId = "";
    private Club club;
    private OleClubDetailDayAdapter dayAdapter;
    private final List<Day> dayList = new ArrayList<>();
    private final List<OleClubFacility> clubFacilities = new ArrayList<>();
    private OleClubDetailFacAdapter facilityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityClubDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.club_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
        }

        LinearLayoutManager dayLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.daysRecyclerVu.setLayoutManager(dayLayoutManager);
        dayAdapter = new OleClubDetailDayAdapter(getContext(), dayList);
        dayAdapter.setOnItemClickListener(itemClickListener);
        binding.daysRecyclerVu.setAdapter(dayAdapter);

        GridLayoutManager facLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        binding.facRecyclerVu.setLayoutManager(facLayoutManager);
        facilityAdapter = new OleClubDetailFacAdapter(getContext(), clubFacilities);
        binding.facRecyclerVu.setAdapter(facilityAdapter);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);

    }

    OleClubDetailDayAdapter.OnItemClickListener itemClickListener = new OleClubDetailDayAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            Day day = dayList.get(pos);
            dayAdapter.setCurrentDayId(day.getDayId());
            dayAdapter.notifyDataSetChanged();
            showTime(day);
        }
    };

    private void showTime(Day day) {
        if (day.getShifting().size() == 2) {
            binding.vuShift1.setVisibility(View.VISIBLE);
            OleShiftTime oleShiftTime = day.getShifting().get(0);
            binding.tvOpenTime1.setText(getString(R.string.open_place, oleShiftTime.getOpening()));
            binding.tvCloseTime1.setText(getString(R.string.close_place, oleShiftTime.getClosing()));
            binding.vuShift2.setVisibility(View.VISIBLE);
            oleShiftTime = day.getShifting().get(1);
            binding.tvOpenTime2.setText(getString(R.string.open_place, oleShiftTime.getOpening()));
            binding.tvCloseTime2.setText(getString(R.string.close_place, oleShiftTime.getClosing()));
        } else if (day.getShifting().size() == 1) {
            binding.vuShift1.setVisibility(View.VISIBLE);
            OleShiftTime oleShiftTime = day.getShifting().get(0);
            binding.tvOpenTime1.setText(getString(R.string.open_place, oleShiftTime.getOpening()));
            binding.tvCloseTime1.setText(getString(R.string.close_place, oleShiftTime.getClosing()));
            binding.vuShift2.setVisibility(View.GONE);
        } else {
            binding.vuShift1.setVisibility(View.GONE);
            binding.vuShift2.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getClubAPI(club == null);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        } else if (v == binding.btnEdit) {
            editClicked();
        }
    }

    private void editClicked() {
        if (club != null) {
            Intent intent = new Intent(getContext(), OleAddClubActivity.class);
            intent.putExtra("is_edit", true);
            Gson gson = new Gson();
            intent.putExtra("club", gson.toJson(club));
            startActivity(intent);
        }
    }

    private void populateData() {
        if (!club.getCoverPath().isEmpty()) {
            setupSlider();
        }
        if (!club.getLogoPath().isEmpty()) {
            Glide.with(getApplicationContext()).load(club.getLogoPath()).into(binding.imgVuLogo);
        }
        binding.tvClubName.setText(club.getName());
        binding.tvLoc.setText(club.getCity().getName());
        binding.tvPhone.setText(club.getContact());
        binding.tvExpiry.setText(club.getClubExpiryDate());
        binding.vu1Hour.setVisibility(View.GONE);
        binding.vu15Hour.setVisibility(View.GONE);
        binding.vu2Hour.setVisibility(View.GONE);
        if (club.getSlots60().equalsIgnoreCase("1")) {
            binding.vu1Hour.setVisibility(View.VISIBLE);
        }
        if (club.getSlots90().equalsIgnoreCase("1")) {
            binding.vu15Hour.setVisibility(View.VISIBLE);
        }
        if (club.getSlots120().equalsIgnoreCase("1")) {
            binding.vu2Hour.setVisibility(View.VISIBLE);
        }
        dayList.clear();
        dayList.addAll(club.getTimings());
        if (dayList.size() > 0) {
            Day day = dayList.get(0);
            dayAdapter.setCurrentDayId(day.getDayId());
            showTime(day);
        }
        dayAdapter.notifyDataSetChanged();
        if (club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
            binding.tvType.setText(R.string.padel);
        } else {
            binding.tvType.setText(R.string.football);
        }
        clubFacilities.clear();
        clubFacilities.addAll(club.getFacilities());
        facilityAdapter.notifyDataSetChanged();
    }

    private void setupSlider() {
        List<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(club.getCoverPath(),  ScaleTypes.FIT));
        binding.slider.setImageList(imageList, ScaleTypes.FIT);
        binding.slider.setItemClickListener(new ItemClickListener() {
            @Override
            public void doubleClick(int i) {

            }

            @Override
            public void onItemSelected(int i) {
//                String[] arr = new String[]{club.getCoverPath()};

                String[] arr = new String[]{club.getCoverPath()};

                new StfalconImageViewer.Builder<>(getContext(), arr, (imageView, imageUrl) -> {
                    // Use Glide to load the image from the URL
                    Glide.with(getApplicationContext())
                            .load(imageUrl)
                            .into(imageView);
                }).withStartPosition(0).show();

//                new ImageViewer.Builder<>(getContext(), arr).setStartPosition(0).show();
            }
        });
    }


//    private void setupSlider() {
//        List<SlideModel> imageList = new ArrayList<>();
//        imageList.add(new SlideModel(club.getCoverPath(), ScaleTypes.FIT));
//        binding.slider.setImageList(imageList, ScaleTypes.FIT);
//        binding.slider.setItemClickListener(new ItemClickListener() {
//            @Override
//            public void doubleClick(int i) {
//
//            }
//
//            @Override
//            public void onItemSelected(int i) {
//                String[] arr = new String[]{club.getCoverPath()};
//                new ImageViewer.Builder<>(getContext(), arr).setStartPosition(0).show();
//            }
//        });
//    }

    private void getClubAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getClub(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId);
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
                            club = gson.fromJson(obj.toString(), Club.class);
                            populateData();
                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }
}
