package ae.oleapp.padel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OlePadelPlayerListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityPartnerListBinding;
import ae.oleapp.dialogs.OlePaymentDialogFragment;
import ae.oleapp.models.OlePlayerBookingList;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.player.OlePlayerListActivity;
import ae.oleapp.player.OlePlayerProfileActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePartnerListActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPartnerListBinding binding;
    private OlePlayerBookingList bookingDetail;
    private boolean isPay = false;
    private final List<OlePlayerInfo> paymentPlayerList = new ArrayList<>();
    private List<OlePlayerInfo> playerList = new ArrayList<>();
    private double totalAmount = 0;
    private OlePadelPlayerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPartnerListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        applyEdgeToEdge(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.your_partners);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            bookingDetail = gson.fromJson(bundle.getString("data", ""), OlePlayerBookingList.class);
        }

        playerList = bookingDetail.getJoinedPlayers();

        if (bookingDetail.getCreatedBy().getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
            if (playerList.size() > 2) {
                binding.selectPartnerVu.setVisibility(View.GONE);
            } else {
                binding.selectPartnerVu.setVisibility(View.VISIBLE);
            }

            binding.btnPay.setVisibility(View.GONE);
            binding.barBtn.setVisibility(View.INVISIBLE);
            for (OlePlayerInfo info : playerList) {
                if (!info.getPaymentStatus().equalsIgnoreCase("1")) {
                    binding.barBtn.setVisibility(View.VISIBLE);
                    break;
                }
            }

            binding.btnPay.setOnClickListener(this);
            binding.selectPartnerVu.setOnClickListener(this);
            binding.barBtn.setOnClickListener(this);
        } else {
            binding.selectPartnerVu.setVisibility(View.GONE);
            binding.btnPay.setVisibility(View.GONE);
            binding.barBtn.setVisibility(View.INVISIBLE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OlePadelPlayerListAdapter(getContext(), playerList);
        adapter.setOnItemClickListener(onItemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.bar.backBtn.setOnClickListener(this);
    }

    OlePadelPlayerListAdapter.OnItemClickListener onItemClickListener = new OlePadelPlayerListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            if (isPay) {
                adapter.selectItem(paymentPlayerList.get(pos));
                setBtnText();
            } else {
                if (bookingDetail.getCreatedBy().getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                    showActionSheet(pos);
                } else {
                    gotoProfile(playerList.get(pos).getId());
                }
            }
        }

        @Override
        public void OnDeleteClick(View v, int pos) {

        }
    };

    private void showActionSheet(int pos) {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.profile), getResources().getString(R.string.remove))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    int ind = 0;

                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                        if (!isCancel) {
                            if (ind == 0) {
                                gotoProfile(playerList.get(pos).getId());
                            }
                        }
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        ind = index;
                        if (index == 0) {
                            actionSheet.dismiss();
                        } else {
                            removePartnerAPI(true, playerList.get(pos).getId(), pos);
                        }
                    }
                }).show();
    }

    private void gotoProfile(String pId) {
        Intent intent = new Intent(getContext(), OlePlayerProfileActivity.class);
        intent.putExtra("player_id", pId);
        startActivity(intent);
    }

    private void setBtnText() {
        totalAmount = 0;
        for (OlePlayerInfo info : adapter.selectedList) {
            totalAmount = totalAmount + Double.parseDouble(info.getPaidAmount());
        }
        binding.tvPrice.setText(String.format("%s %s %s", getString(R.string.pay), totalAmount, bookingDetail.getCurrency()));
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        } else if (v == binding.btnPay) {
            payClicked();
        } else if (v == binding.selectPartnerVu) {
            Intent intent = new Intent(getContext(), OlePlayerListActivity.class);
            intent.putExtra("is_selection", true);
            intent.putExtra("is_three_selection", true);
            startActivityForResult(intent, 106);
        } else if (v == binding.barBtn) {
            barBtnPayClicked();
        }
    }

    private void payClicked() {
        if (adapter.selectedList.size() == 0 || totalAmount == 0) {
            Functions.showToast(getContext(), getString(R.string.select_one_player), FancyToast.ERROR);
            return;
        }

        openPaymentDialog(String.format(Locale.ENGLISH, "%.2f", totalAmount), bookingDetail.getCurrency(), "", "", "", true, false, "0", "", new OlePaymentDialogFragment.PaymentDialogCallback() {
            @Override
            public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                String pIds = "";
                for (OlePlayerInfo info : adapter.selectedList) {
                    if (pIds.isEmpty()) {
                        pIds = info.getId();
                    } else {
                        pIds = String.format("%s,%s", pIds, info.getId());
                    }
                }
                updatePaymentStatusAPI(true, pIds, String.format(Locale.ENGLISH, "%.2f", totalAmount), paymentMethod, orderRef, cardPaid, walletPaid);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 106 && resultCode == RESULT_OK) {
            String str = data.getExtras().getString("players");
            Gson gson = new Gson();
            List<OlePlayerInfo> list = gson.fromJson(str, new TypeToken<List<OlePlayerInfo>>() {
            }.getType());
            String pIds = "";
            for (OlePlayerInfo info : list) {
                if (pIds.isEmpty()) {
                    pIds = info.getId();
                } else {
                    pIds = String.format("%s,%s", pIds, info.getId());
                }
            }
            addPartnerAPI(true, pIds);
        }
    }

    private void barBtnPayClicked() {
        if (isPay) {
            isPay = false;
            binding.btnPay.setVisibility(View.GONE);
            binding.barBtn.setText(R.string.pay);
            binding.tvPrice.setText(R.string.pay);
            adapter.selectedList.clear();
            if (playerList.size() > 2) {
                binding.selectPartnerVu.setVisibility(View.GONE);
            } else {
                binding.selectPartnerVu.setVisibility(View.VISIBLE);
            }
            adapter.setDatasource(playerList, isPay);
        } else {
            isPay = true;
            binding.btnPay.setVisibility(View.VISIBLE);
            binding.selectPartnerVu.setVisibility(View.GONE);
            binding.barBtn.setText(R.string.cancel);
            paymentPlayerList.clear();
            for (OlePlayerInfo info : playerList) {
                if (!info.getPaymentStatus().equalsIgnoreCase("1")) {
                    paymentPlayerList.add(info);
                }
            }
            adapter.setDatasource(paymentPlayerList, isPay);
        }
    }

    private void addPartnerAPI(boolean isLoader, String pIds) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addPartner(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingDetail.getBookingId(), pIds, "booking");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            playerList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                playerList.add(gson.fromJson(arr.get(i).toString(), OlePlayerInfo.class));
                            }
                            bookingDetail.setJoinedPlayers(playerList);
                            adapter.setDatasource(playerList, isPay);
                            if (playerList.size() > 2) {
                                binding.selectPartnerVu.setVisibility(View.GONE);
                            } else {
                                binding.selectPartnerVu.setVisibility(View.VISIBLE);
                            }
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

    private void removePartnerAPI(boolean isLoader, String pIds, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.removePartner(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingDetail.getBookingId(), pIds);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            playerList.remove(pos);
                            adapter.notifyDataSetChanged();
                            if (playerList.size() > 2) {
                                binding.selectPartnerVu.setVisibility(View.GONE);
                            } else {
                                binding.selectPartnerVu.setVisibility(View.VISIBLE);
                            }
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

    private void updatePaymentStatusAPI(boolean isLoader, String pIds, String amount, String paymentMethod, String orderRef, String cardPaid, String walletPaid) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updatePaymentStatus(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingDetail.getBookingId(), pIds, amount, paymentMethod, Functions.getIPAddress(), orderRef, cardPaid, walletPaid);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
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