package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hbb20.CCPCountry;
import com.kaopiz.kprogresshud.KProgressHUD;
import  ae.oleapp.R;
import  ae.oleapp.databinding.FragmentLinkPlayerDialogBinding;
import  ae.oleapp.models.PlayerInfo;
import ae.oleapp.util.AppManager;
import  ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LinkPlayerDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentLinkPlayerDialogBinding binding;
    private String playerId = "", phone = "", code = "";
    private PlayerInfo profileInfo;

    public LinkPlayerDialogFragment() {
        // Required empty public constructor
    }

    public LinkPlayerDialogFragment(String playerId, String phone, String code) {
        this.playerId = playerId;
        this.phone = phone;
        this.code = code;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLinkPlayerDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setDimAmount(0.5f);
        }

        CCPCountry.setDialogTitle(getString(R.string.select_country_region));
        CCPCountry.setSearchHintMessage(getString(R.string.search_hint));

        if (code != null && !code.isEmpty()) {
            String p = phone.substring(code.length());
            binding.etPhone.setText(p);
            int c = Integer.parseInt(code);
            binding.ccp.setCountryForPhoneCode(c);
        }

        binding.profileVu.setVisibility(View.GONE);

        binding.btnClose.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
        binding.btnAddPlayer.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnClose) {
            dismiss();
        }
        else if (view == binding.btnSubmit) {
            String countryCode = binding.ccp.getSelectedCountryCodeWithPlus();
            if (countryCode.isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.select_country_code), FancyToast.ERROR);
                return;
            }
            if (binding.etPhone.getText().toString().isEmpty()) {
                Functions.showToast(getContext(), getString(R.string.enter_phone), FancyToast.ERROR);
                return;
            }
            if (binding.etPhone.getText().toString().startsWith("0")) {
                Functions.showToast(getContext(), getString(R.string.phone_not_start_0), FancyToast.ERROR);
                return;
            }
            searchPlayerAPI(true, String.format("%s%s", countryCode, binding.etPhone.getText().toString()), countryCode);
        }
        else if (view == binding.btnAddPlayer) {
            if (profileInfo != null) {
                linkPlayerAPI(true, profileInfo.getPhone(), "");
            }
        }
    }

    private void searchPlayerAPI(boolean isLoader, String phone, String countryCode) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.manualPhoneSearch(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), playerId, phone, countryCode);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            if (obj.length() > 0) {
                                binding.btnSubmit.setVisibility(View.GONE);
                                binding.profileVu.setVisibility(View.VISIBLE);
                                profileInfo = new Gson().fromJson(obj.toString(), PlayerInfo.class);
                                binding.tvName.setText(profileInfo.getNickName());
                                binding.tvPhone.setText(profileInfo.getPhone());
                                Glide.with(getContext()).load(profileInfo.getEmojiUrl()).into(binding.pEmojiImgVu);
                                Glide.with(getContext()).load(profileInfo.getBibUrl()).into(binding.pShirtImgVu);
                            }
                            else {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                                dismiss();
                            }
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            dismiss();
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

    private void linkPlayerAPI(boolean isLoader, String phone, String countryCode) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.linkPlayer(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), playerId, phone, countryCode);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            dismiss();
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