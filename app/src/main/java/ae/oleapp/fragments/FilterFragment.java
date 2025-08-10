package ae.oleapp.fragments;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.google.gson.Gson;
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
import ae.oleapp.adapters.ClubsBanksAdapter;
import ae.oleapp.databinding.FragmentFilterBinding;
import ae.oleapp.models.BankEarning;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.IncomeHistory;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FilterFragment extends DialogFragment implements View.OnClickListener{

    private FragmentFilterBinding binding;
    private ResultDialogCallback dialogCallback;

    private ClubsBanksAdapter clubsBanksAdapter;
    private final List<ClubBankLists> clubBankLists = new ArrayList<>();
    private String from = "", to = "", clubId = "", filterBy="", bankId="";
    private Boolean employeeFilter = false;
    private Boolean statsCheck = false;


    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public FilterFragment(String clubId) {
        // Required empty public constructor
        this.clubId = clubId;
    }
    public FilterFragment(Boolean statsCheck) {
        // Required empty public constructor
        this.statsCheck = statsCheck;
    }
    public FilterFragment(String clubId, Boolean employeeFilter) {
        // Required empty public constructor
        this.clubId = clubId;
        this.employeeFilter = employeeFilter;
    }
    public FilterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFilterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
            if (!statsCheck){

                binding.banksTv.setVisibility(View.VISIBLE);
                binding.banksRecyclerVu.setVisibility(View.VISIBLE);
                binding.textSortby.setVisibility(View.VISIBLE);
                binding.relWeek.setVisibility(View.VISIBLE);
                binding.relThisMonth.setVisibility(View.VISIBLE);
                binding.relThreeMonth.setVisibility(View.VISIBLE);

                if (employeeFilter){
                    binding.banksRecyclerVu.setVisibility(View.GONE);
                    binding.banksTv.setVisibility(View.GONE);
                }else{
                    getClubBanksList(clubId);
                    binding.banksRecyclerVu.setVisibility(View.VISIBLE);
                    binding.banksTv.setVisibility(View.VISIBLE);
                }
            }
            else{
                binding.banksTv.setVisibility(View.GONE);
                binding.banksRecyclerVu.setVisibility(View.GONE);
                binding.textSortby.setVisibility(View.GONE);
                binding.relWeek.setVisibility(View.GONE);
                binding.relThisMonth.setVisibility(View.GONE);
                binding.relThreeMonth.setVisibility(View.GONE);
            }




        LinearLayoutManager teamLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.banksRecyclerVu.setLayoutManager(teamLayoutManager);
        clubsBanksAdapter = new ClubsBanksAdapter(getContext(), clubBankLists);
        clubsBanksAdapter.setItemClickListener(itemClickListener);
        binding.banksRecyclerVu.setAdapter(clubsBanksAdapter);

        binding.btnClose.setOnClickListener(this);
        binding.relWeek.setOnClickListener(this);
        binding.relThisMonth.setOnClickListener(this);
        binding.relThreeMonth.setOnClickListener(this);
        binding.etFromDate.setOnClickListener(this);
        binding.etToDate.setOnClickListener(this);
        binding.btnApply.setOnClickListener(this);
        binding.btnReset.setOnClickListener(this);


        return view;
    }

    ClubsBanksAdapter.ItemClickListener itemClickListener = new ClubsBanksAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            clubsBanksAdapter.setSelectedId(clubBankLists.get(pos).getId());
            bankId = clubBankLists.get(pos).getId();

        }
    };

    private void getClubBanksList(String club_id) {
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.getClubBanksList(Functions.getAppLang(getContext()), club_id,"");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONArray data = object.getJSONArray(Constants.kData);
                                Gson gson = new Gson();
                                for (int i = 0; i < data.length(); i++) {
                                    clubBankLists.add(gson.fromJson(data.get(i).toString(), ClubBankLists.class));
                                }
                                clubsBanksAdapter.notifyDataSetChanged();
                            }
                            else {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
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

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose){
             dismiss();
        }
        else if (v == binding.relWeek) {
           filterBy = "weekly";
           binding.weekTick.setVisibility(View.VISIBLE);
           binding.monthTick.setVisibility(View.GONE);
           binding.threeMonthTick.setVisibility(View.GONE);
        }
        else if (v == binding.relThisMonth) {
            filterBy = "monthly";
            binding.weekTick.setVisibility(View.GONE);
            binding.monthTick.setVisibility(View.VISIBLE);
            binding.threeMonthTick.setVisibility(View.GONE);

        }
        else if (v == binding.relThreeMonth) {
            filterBy = "quarterly";
            binding.weekTick.setVisibility(View.GONE);
            binding.monthTick.setVisibility(View.GONE);
            binding.threeMonthTick.setVisibility(View.VISIBLE);
        }
        else if (v == binding.etFromDate) {
            fromDateClicked();
        }
        else if (v == binding.etToDate) {
            toDateClicked();
        }
        else if (v == binding.btnApply) {
            applyClicked();
        }
        else if (v == binding.btnReset) {
            resetClicked();
        }

    }
    private void fromDateClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                binding.etFromDate.setText(formatter.format(calendar.getTime()));
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    private void toDateClicked() {
        if (binding.etFromDate.getText().toString().equalsIgnoreCase("")) {
            Functions.showToast(getContext(), getString(R.string.select_from_date), FancyToast.ERROR);
            return;
        }
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                binding.etToDate.setText(formatter.format(calendar.getTime()));
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    private void applyClicked() {
        dialogCallback.didSubmitResult(FilterFragment.this, filterBy, bankId, binding.etFromDate.getText().toString(), binding.etToDate.getText().toString());
    }

    private void resetClicked() {
        from = "";
        to = "";
        clubId = "";
        filterBy="";
        binding.etFromDate.setText("");
        binding.etToDate.setText("");
        Functions.showToast(getContext(), "Reset Successfully!", FancyToast.SUCCESS);
        binding.weekTick.setVisibility(View.GONE);
        binding.monthTick.setVisibility(View.GONE);
        binding.threeMonthTick.setVisibility(View.GONE);
        clubsBanksAdapter.setSelectedId("");

    }




    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, String filterBy, String bankId, String from, String to);
    }
}