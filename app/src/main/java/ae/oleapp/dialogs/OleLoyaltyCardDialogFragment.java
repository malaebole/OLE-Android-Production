package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.databinding.OlefragmentLoyaltyCardDialogBinding;
import ae.oleapp.models.Club;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleLoyaltyCardDialogFragment extends DialogFragment implements View.OnClickListener {

    private OlefragmentLoyaltyCardDialogBinding binding;
    private Club club;
    private String discountValue = "", targetBookings = "", expiryDate = "", playerBookings = "", remainingBookings = "", popupId = "", popupType = "";
    private LoyaltyCardDialogCallback dialogCallback;

    public OleLoyaltyCardDialogFragment() {
        // Required empty public constructor
    }

    public OleLoyaltyCardDialogFragment(Club club, String discountValue, String targetBookings, String expiryDate, String playerBookings, String remainingBookings, String popupId, String popupType) {
        this.club = club;
        this.discountValue = discountValue;
        this.targetBookings = targetBookings;
        this.expiryDate = expiryDate;
        this.playerBookings = playerBookings;
        this.remainingBookings = remainingBookings;
        this.popupId = popupId;
        this.popupType = popupType;
    }

    public void setDialogCallback(LoyaltyCardDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentLoyaltyCardDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            binding.btnImg.setImageResource(R.drawable.padel_green_btn);
        }
        else {
            binding.btnImg.setImageResource(R.drawable.green_btn_bg);
        }

        if (!playerBookings.equalsIgnoreCase("") && !playerBookings.equalsIgnoreCase("0")) {
            binding.stepView.setCompletedPosition(Integer.parseInt(playerBookings)-1);
            binding.stepView.setProgressColorIndicator(getResources().getColor(R.color.yellowColor));
        }
        else {
            binding.stepView.setProgressColorIndicator(getResources().getColor(R.color.separatorColor));
        }
        if (!targetBookings.equalsIgnoreCase("")) {
            int value = Integer.parseInt(targetBookings);
            String[] arr = new String[value];
            for (int i = 0; i < value; i++) {
                arr[i] = String.valueOf(i+1);
            }
            binding.stepView.setLabels(arr);
        }
        if (remainingBookings.equalsIgnoreCase("0")) {
            binding.tvLeftBooking.setText(R.string.discount_on_next_booking);
            binding.tvMsg.setText(getResources().getString(R.string.loyalty_card_desc_place, playerBookings, discountValue, expiryDate));
        }
        else {
            binding.tvLeftBooking.setText(String.format(getResources().getString(R.string.booking_remaining_discount), remainingBookings, playerBookings, targetBookings));
            binding.tvMsg.setText(getResources().getString(R.string.loyalty_card_desc_remain_place, playerBookings, remainingBookings, expiryDate));
        }

        if (popupType.equalsIgnoreCase("canceled")) {
            binding.tvTitle.setText(R.string.oops);
            binding.relBg.setBackgroundColor(getResources().getColor(R.color.redColor));
            binding.bgImgVu.setImageResource(R.drawable.loyalty_red_bg);
            binding.imgVuSmiley.setImageResource(R.drawable.loyalty_red_smiley);
            binding.leftBookingVu.setCardBackgroundColor(Color.parseColor("#BF1B00"));
            binding.tvLeftBooking.setText(R.string.lost_loyalty_discount_booking);
            binding.tvMsg.setText(getResources().getString(R.string.loyalty_card_lost_desc_remain_place, remainingBookings, expiryDate));
        }

        binding.tvPerc.setText(discountValue);

        binding.tvClub.setText(club.getName());
        binding.tvLoc.setText(club.getCity().getName());
        Glide.with(getContext()).load(club.getLogoPath()).into(binding.logoVu);
        Glide.with(getContext()).load(club.getCoverPath()).into(binding.clubBanner);

        binding.btnBook.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBook) {
            dismissAPI(true, false);
        }
        else if (v == binding.btnClose) {
            dismissAPI(true, true);
        }
    }

    private void dismissAPI(boolean isLoader, boolean isDismiss) {
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.dismissLoyaltyPopup(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), popupId);
            call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            if (isDismiss) {
                                dialogCallback.onDismiss(OleLoyaltyCardDialogFragment.this);
                            }
                            else {
                                dialogCallback.bookClicked(OleLoyaltyCardDialogFragment.this);
                            }
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            dialogCallback.onDismiss(OleLoyaltyCardDialogFragment.this);
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

    public interface LoyaltyCardDialogCallback {
        void bookClicked(DialogFragment df);
        void onDismiss(DialogFragment df);
    }
}