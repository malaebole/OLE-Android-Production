package ae.oleapp.player;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.willy.ratingbar.BaseRatingBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleEmployeeRateAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityEmployeeRateBinding;
import ae.oleapp.dialogs.OleEmployeeRateDialogFragment;
import ae.oleapp.models.Employee;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleEmployeeRateActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityEmployeeRateBinding binding;
    private String bookingId = "", clubId = "", isRated = "", rating = "0";
    private OleEmployeeRateAdapter adapter;
    private int ratingScore = 5;
    private final List<Employee> employeeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityEmployeeRateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingId = bundle.getString("booking_id", "");
            clubId = bundle.getString("club_id", "");
            isRated = bundle.getString("is_rated", "");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleEmployeeRateAdapter(getContext(), employeeList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.ratingBar.setClickable(isRated.equalsIgnoreCase("0"));

        binding.ratingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating, boolean fromUser) {
                ratingScore = (int) rating;
                populateRatingData(ratingScore);
            }
        });


//
//        binding.ratingBar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
//            @Override
//            public void onRatingChange(float ratingCount) {
//                ratingScore = (int) ratingCount;
////                switch (ratingScore){
////                    case 1:
////                        Glide.with(getApplicationContext()).load(R.drawable.upset_star).into(binding.starImg);
////                        binding.ratingText.setText("Upset!");
////                        binding.ratingText.setTextColor(getResources().getColor(R.color.red));
////                        break;
////                    case 2:
////                        Glide.with(getApplicationContext()).load(R.drawable.sad_star).into(binding.starImg);
////                        binding.ratingText.setTextColor(getResources().getColor(R.color.red));
////                        binding.ratingText.setText("Sad!");
////                        break;
////                    case 3:
////                        Glide.with(getApplicationContext()).load(R.drawable.wow_star).into(binding.starImg);
////                        binding.ratingText.setTextColor(getResources().getColor(R.color.red));
////                        binding.ratingText.setText("Wow!");
////                        break;
////                    case 4:
////                        Glide.with(getApplicationContext()).load(R.drawable.awesome_star).into(binding.starImg);
////                        binding.ratingText.setTextColor(getResources().getColor(R.color.greenColor));
////                        binding.ratingText.setText("Awesome!");
////                        break;
////                    case 5:
////                        Glide.with(getApplicationContext()).load(R.drawable.excellent_star).into(binding.starImg);
////                        binding.ratingText.setTextColor(getResources().getColor(R.color.greenColor));
////                        binding.ratingText.setText("Excellent!");
////                        break;
////                    default:
////                        break;
////                }
//                populateRatingData(ratingScore);
//                //rateClubAPI(ratingCount);
//            }
//        });

        getEmployeesAPI(true);

        binding.btnClose.setOnClickListener(this);
        binding.btnContinue.setOnClickListener(this);
    }

    private void populateRatingData(int rating) {
        switch (rating) {
            case 1:
                binding.ratingBar.setRating(1);
                Glide.with(getApplicationContext()).load(R.drawable.upset_star).into(binding.starImg);
                binding.ratingText.setText(getString(R.string.upset));
                binding.ratingText.setTextColor(getResources().getColor(R.color.red));
                break;
            case 2:
                binding.ratingBar.setRating(2);
                Glide.with(getApplicationContext()).load(R.drawable.sad_star).into(binding.starImg);
                binding.ratingText.setTextColor(getResources().getColor(R.color.red));
                binding.ratingText.setText(getString(R.string.sad));
                break;
            case 3:
                binding.ratingBar.setRating(3);
                Glide.with(getApplicationContext()).load(R.drawable.wow_star).into(binding.starImg);
                binding.ratingText.setTextColor(getResources().getColor(R.color.red));
                binding.ratingText.setText(getString(R.string.wow));
                break;
            case 4:
                binding.ratingBar.setRating(4);
                Glide.with(getApplicationContext()).load(R.drawable.awesome_star).into(binding.starImg);
                binding.ratingText.setTextColor(getResources().getColor(R.color.greenColor));
                binding.ratingText.setText(getString(R.string.awesome));
                break;
            case 5:
                binding.ratingBar.setRating(6);
                Glide.with(getApplicationContext()).load(R.drawable.excellent_star).into(binding.starImg);
                binding.ratingText.setTextColor(getResources().getColor(R.color.greenColor));
                binding.ratingText.setText(getString(R.string.excellent));
                break;
            default:
                break;
        }
        binding.ratingBar.refreshDrawableState();
        if (isRated.equalsIgnoreCase("0")) {
            rateClubAPI(rating);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            finish();
        } else if (v == binding.btnContinue) {
            if (adapter.getSelectedList().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_employee), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment prevFrag = getSupportFragmentManager().findFragmentByTag("EmployeeRateDialogFragment");
            if (prevFrag != null) {
                fragmentTransaction.remove(prevFrag);
            }
            fragmentTransaction.addToBackStack(null);
            OleEmployeeRateDialogFragment dialogFragment = new OleEmployeeRateDialogFragment(adapter.getSelectedList(), bookingId, clubId);
            dialogFragment.setFragmentCallback(new OleEmployeeRateDialogFragment.EmployeeRateDialogFragmentCallback() {
                @Override
                public void ratingDone(DialogFragment dialogFragment) {
                    dialogFragment.dismiss();
                    finish();
                }
            });
            dialogFragment.show(fragmentTransaction, "EmployeeRateDialogFragment");
        }
    }

    private final OleEmployeeRateAdapter.ItemClickListener itemClickListener = new OleEmployeeRateAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            adapter.selectItem(employeeList.get(pos));
        }
    };

    private void getEmployeesAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getEmployees(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, "");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            employeeList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                employeeList.add(gson.fromJson(arr.get(i).toString(), Employee.class));
                            }
                            binding.rating.setVisibility(View.VISIBLE);
                            binding.ratingDesc.setVisibility(View.VISIBLE);
                            binding.btnContinue.setVisibility(View.VISIBLE);
                        } else {
                            employeeList.clear();
                            binding.rating.setVisibility(View.GONE);
                            binding.ratingDesc.setVisibility(View.GONE);
                            binding.btnContinue.setVisibility(View.GONE);
                        }
                        binding.clubText.setText(getString(R.string.how_was_your_exp) + " " + object.getString("club_name") + " ?");
                        rating = object.getString("rating");
                        populateRatingData(Integer.parseInt(rating));

//                        if (isRated.equalsIgnoreCase("1")) {
//                            //binding.clubRateVu.setVisibility(View.GONE);
//                            binding.ratingBar.setEnabled(false);
//                            Glide.with(getApplicationContext()).load(R.drawable.excellent_star).into(binding.starImg);
//                            binding.ratingText.setTextColor(getResources().getColor(R.color.greenColor));
//                            binding.ratingText.setText("Excellent!");
//                            binding.ratingBar.setStar(Float.parseFloat(rating));
//                            binding.ratingBar.refreshDrawableState();
//
//                        }
//                        else {
//                            Glide.with(getApplicationContext()).load(R.drawable.excellent_star).into(binding.starImg);
//                            binding.ratingText.setTextColor(getResources().getColor(R.color.greenColor));
//                            binding.ratingText.setText("Excellent!");
//                            binding.ratingBar.setStar(5);
//                            binding.ratingBar.setEnabled(true);
//
//                        }
                        adapter.notifyDataSetChanged();

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

    private void rateClubAPI(float rating) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.rateClub(Functions.getAppLang(getContext()), userId, clubId, rating);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getContext().getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getContext().getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }
}