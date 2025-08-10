package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleEmpRatingPagerAdapter;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OlefragmentEmployeeRateDialogBinding;
import ae.oleapp.external.OleLinePagerIndicatorDecoration;
import ae.oleapp.models.Employee;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleEmployeeRateDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentEmployeeRateDialogBinding binding;
    private List<Employee> employeeList = new ArrayList<>();
    private String bookingId = "", clubId = "";
    private OleEmpRatingPagerAdapter adapter;
    private EmployeeRateDialogFragmentCallback fragmentCallback;

    public OleEmployeeRateDialogFragment() {
        // Required empty public constructor
    }

    public OleEmployeeRateDialogFragment(List<Employee> employeeList, String bookingId, String clubId) {
        this.employeeList = employeeList;
        this.bookingId = bookingId;
        this.clubId = clubId;
    }

    public void setFragmentCallback(EmployeeRateDialogFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentEmployeeRateDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setDimAmount(0.9f);
            getDialog().setCanceledOnTouchOutside(false);
        }

        binding.recyclerVu.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.recyclerVu);
        binding.recyclerVu.addItemDecoration(new OleLinePagerIndicatorDecoration());
        adapter = new OleEmpRatingPagerAdapter(getContext(), employeeList);
        adapter.setOnItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.progressVu.setVisibility(View.GONE);
        binding.btnClose.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            dismiss();
        }
    }

    OleEmpRatingPagerAdapter.OnItemClickListener itemClickListener = new OleEmpRatingPagerAdapter.OnItemClickListener() {
        @Override
        public void onSubmitClick(View v, int pos, float rating, String feedback, int tip) {
            if (tip > 0) {
                ((BaseActivity)getActivity()).openPaymentDialog(String.valueOf(tip), Functions.getPrefValue(getContext(), Constants.kCurrency), "", "", "", true, false, "", new OlePaymentDialogFragment.PaymentDialogCallback() {
                    @Override
                    public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                        rateEmployeeAPI(true, employeeList.get(pos).getId(), feedback, rating, String.valueOf(tip), paymentMethod, orderRef, pos);
                    }
                });
            }
            else {
                rateEmployeeAPI(true, employeeList.get(pos).getId(), feedback, rating, "", "", "", pos);
            }
        }
    };

    private void rateEmployeeAPI(boolean isLoader, String empId, String feedback, float rate, String amount, String paymentMethod, String orderRef, int pos) {
        binding.progressVu.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addEmployeeRating(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), empId, clubId, bookingId, rate, feedback, amount, paymentMethod, orderRef, Functions.getIPAddress());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                binding.progressVu.setVisibility(View.GONE);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            int p = pos + 1;
                            if (p < employeeList.size()) {
                                binding.recyclerVu.scrollToPosition(p);
                            }
                            else {
                                fragmentCallback.ratingDone(OleEmployeeRateDialogFragment.this);
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
                binding.progressVu.setVisibility(View.GONE);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    public interface EmployeeRateDialogFragmentCallback {
        void ratingDone(DialogFragment dialogFragment);
    }
}