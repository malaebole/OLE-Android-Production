package ae.oleapp.dialogs;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.FragmentPaymentDialogBinding;
import ae.oleapp.models.OleUserCard;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class OlePaymentDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentPaymentDialogBinding binding;
    private final int CASH = 1;
    private final int CARD = 2;
    private final int WALLET = 3;
    private final int SPAY = 4;
    private final List<OleUserCard> oleUserCards = new ArrayList<>();
    private String selectedCardId = "";
    private int paymentType = CASH;
    private PaymentDialogCallback dialogCallback;
    private String price = "";
    private String currency = "";
    private String bookingId = "";
    private String clubId = "";
    private String totalPlayers = "";
    private String isFromMatch = "1";
    private String note = "";
    private boolean isCashHide = false;
    private boolean isWalletHide = false;
    private double walletAmount = 0.0;
    private String payFullAmount = "";

    public OlePaymentDialogFragment() {
        // Required empty public constructor
    }

    public OlePaymentDialogFragment(String price, String currency, String date, String bookingId, String totalPlayers, boolean isCashHide, boolean isWalletHide, String isFromMatch, String clubId, String payFullAmount) {
        this.price = price;
        this.currency = currency;
        this.bookingId = bookingId;
        this.totalPlayers = totalPlayers;
        this.isCashHide = isCashHide;
        this.isWalletHide = isWalletHide;
        this.isFromMatch = isFromMatch;
        this.clubId = clubId;
        this.payFullAmount = payFullAmount;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDialogCallback(PaymentDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPaymentDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.bar.toolbarTitle.setText(R.string.payment);

        binding.relWallet.setVisibility(isWalletHide ? View.GONE : View.VISIBLE);
        binding.relCash.setVisibility(isCashHide ? View.GONE : View.VISIBLE);
        if (isCashHide) {
            cardClicked();
        }
        else {
            cashClicked();
        }

        try {
            ((BaseActivity) getActivity()).getWalletDataAPI(clubId, new BaseActivity.WalletDataCallback() {
                @Override
                public void getWalletData(String amount, String paymentMethod, String currency, String shopPaymentMethod) {
                    if (!amount.isEmpty()) {
                        walletAmount = Double.parseDouble(amount);
                        binding.tvCredit.setText(String.format("%s %s", amount, currency));
                    } else {
                        binding.tvCredit.setText(String.format("0 %s", currency));
                    }
                    if (!paymentMethod.isEmpty()) {
                        setPaymentMethod(paymentMethod);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.tvNote.setText(note);

        binding.tvPrice.setText(String.format("%s %s %s", getString(R.string.confirm), price, currency));

        binding.bar.backBtn.setOnClickListener(this);
        binding.relCard.setOnClickListener(this);
        binding.relCash.setOnClickListener(this);
        binding.relSpay.setOnClickListener(this);
        binding.relWallet.setOnClickListener(this);
        binding.relAddCard.setOnClickListener(this);
        binding.imgDrop.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setPaymentMethod(String method) {
        if (method.equalsIgnoreCase("cash")) {
            binding.relWallet.setVisibility(View.GONE);
            binding.relAddCard.setVisibility(View.GONE);
            binding.relCard.setVisibility(View.GONE);
            binding.relCvv.setVisibility(View.GONE);
            binding.relSpay.setVisibility(View.GONE);
            cashClicked();
        }
        if (method.equalsIgnoreCase("card")) {
            binding.relCash.setVisibility(View.GONE);
            cardClicked();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            dismiss();
        }
        else if (v == binding.relCard) {
            cardClicked();
        }
        else if (v == binding.relCash) {
            cashClicked();
        }
        else if (v == binding.relSpay) {
            spayClicked();
        }
        else if (v == binding.relWallet) {
            walletClicked();
        }
        else if (v == binding.relAddCard) {
            addCardClicked();
        }
        else if (v == binding.imgDrop) {
            imgDropdownClicked();
        }
        else if (v == binding.btnConfirm) {
            btnConfirmClicked();
        }
    }

    private void cardClicked() {
        paymentType = CARD;
        binding.imgVuCash.setImageResource(R.drawable.uncheck);
        binding.imgVuCard.setImageResource(R.drawable.check);
        binding.imgVuWallet.setImageResource(R.drawable.uncheck);
        binding.imgVuSpay.setImageResource(R.drawable.uncheck);
        if (!bookingId.equalsIgnoreCase("")) {
            getMatchFeeAPI("card");
        }
    }

    private void cashClicked() {
        paymentType = CASH;
        binding.imgVuCash.setImageResource(R.drawable.check);
        binding.imgVuCard.setImageResource(R.drawable.uncheck);
        binding.imgVuWallet.setImageResource(R.drawable.uncheck);
        binding.imgVuSpay.setImageResource(R.drawable.uncheck);
        binding.tvCard.setText(R.string.card);
        binding.relAddCard.setVisibility(View.GONE);
        binding.relCvv.setVisibility(View.GONE);
        if (!bookingId.equalsIgnoreCase("")) {
            getMatchFeeAPI("cash");
        }
    }

    private void spayClicked() {
        paymentType = SPAY;
        binding.imgVuCash.setImageResource(R.drawable.uncheck);
        binding.imgVuCard.setImageResource(R.drawable.uncheck);
        binding.imgVuWallet.setImageResource(R.drawable.uncheck);
        binding.imgVuSpay.setImageResource(R.drawable.check);
    }

    private void walletClicked() {
        paymentType = WALLET;
        binding.imgVuCash.setImageResource(R.drawable.uncheck);
        binding.imgVuCard.setImageResource(R.drawable.uncheck);
        binding.imgVuWallet.setImageResource(R.drawable.check);
        binding.imgVuSpay.setImageResource(R.drawable.uncheck);
        binding.tvCard.setText(R.string.card);
        binding.relAddCard.setVisibility(View.GONE);
        binding.relCvv.setVisibility(View.GONE);
        if (!bookingId.equalsIgnoreCase("")) {
            getMatchFeeAPI("wallet");
        }
    }

    private void addCardClicked() {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        Fragment prev = getChildFragmentManager().findFragmentByTag("AddCardDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        OleAddCardDialogFragment dialogFragment = new OleAddCardDialogFragment();
        dialogFragment.setDialogCallback(new OleAddCardDialogFragment.AddCardDialogCallback() {
            @Override
            public void didAddCard() {
                getCardsAPI();
            }
        });
        dialogFragment.show(fragmentTransaction, "AddCardDialogFragment");
    }

    private void imgDropdownClicked() {
        if (paymentType == CARD && oleUserCards.size() > 0) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            Fragment prev = getChildFragmentManager().findFragmentByTag("CardListDialogFragment");
            if (prev != null) {
                fragmentTransaction.remove(prev);
            }
            fragmentTransaction.addToBackStack(null);
            OleCardListDialogFragment dialogFragment = new OleCardListDialogFragment(oleUserCards);
            dialogFragment.setCardListDialogCallback(new OleCardListDialogFragment.CardListDialogCallback() {
                @Override
                public void didSelectCard(OleUserCard oleUserCard) {
                    binding.tvCard.setText(oleUserCard.getCardNumber());
                    selectedCardId = oleUserCard.getCardId();
                }
            });
            dialogFragment.show(fragmentTransaction, "CardListDialogFragment");
        }
    }

    private void btnConfirmClicked() {
        if (paymentType == CARD) {
            ((BaseActivity)getActivity()).makePayment(bookingId, "card", price, currency, new BaseActivity.PaymentCallback() {
                @Override
                public void paymentStatus(boolean status, String orderRef) {
                    dialogCallback.didConfirm(status,"card", orderRef, price, "", "");
                    dismiss();
                }
            });
        }
        else if (paymentType == SPAY) {
            ((BaseActivity)getActivity()).makePayment(bookingId, "samsung", price, currency, new BaseActivity.PaymentCallback() {
                @Override
                public void paymentStatus(boolean status, String orderRef) {
                    dialogCallback.didConfirm(status,"samsung", orderRef, price, "", "");
                    dismiss();
                }
            });
        }
        else if (paymentType == WALLET) {
            if (!price.isEmpty()) {
                if (walletAmount < Double.parseDouble(price)) {
                    halfWalletHalfCard(Double.parseDouble(price));
                }
                else {
                    dialogCallback.didConfirm(true, "wallet", "", price, "", "");
                    dismiss();
                }
            }
        }
        else {
            dialogCallback.didConfirm(true, "cash", "", price, "", "");
            dismiss();
        }
    }

    private void halfWalletHalfCard(double finalPrice) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.payment))
                .setMessage(getResources().getString(R.string.insufficient_balance_wallet_remaining_amount_card))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        double remainAmount = finalPrice - walletAmount;
                        ((BaseActivity)getActivity()).makePayment("", "card", String.valueOf(remainAmount), currency, new BaseActivity.PaymentCallback() {
                            @Override
                            public void paymentStatus(boolean status, String orderRef) {
                                dialogCallback.didConfirm(true, "card", orderRef, String.valueOf(finalPrice), String.valueOf(walletAmount), String.valueOf(remainAmount));
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

    private void getCardsAPI() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.userCards(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            oleUserCards.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                oleUserCards.add(gson.fromJson(arr.get(i).toString(), OleUserCard.class));
                            }
                            if (oleUserCards.size()>0) {
                                binding.tvCard.setText(oleUserCards.get(0).getCardNumber());
                                selectedCardId = oleUserCards.get(0).getCardId();
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

    private void getMatchFeeAPI(String method) {
        binding.btnConfirm.setVisibility(View.INVISIBLE);
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getMatchFee(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, isFromMatch, totalPlayers, method, payFullAmount, Functions.getPrefValue(getContext(), Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            price = obj.getString("fee");
                            binding.tvPrice.setText(String.format("%s %s %s", getString(R.string.confirm), price, currency));
                            binding.btnConfirm.setVisibility(View.VISIBLE);
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

    public interface PaymentDialogCallback {
        void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid);
    }
}
