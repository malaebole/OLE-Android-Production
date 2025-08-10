package ae.oleapp.owner;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleOfferListAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityOfferListBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleOfferData;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleOfferListActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityOfferListBinding binding;
    private final List<OleOfferData> offerList = new ArrayList<>();
    private OleOfferListAdapter adapter;
    private OleRankClubAdapter oleRankClubAdapter;
    private String clubId = "", filter = "";
    private final List<Club> clubList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityOfferListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.offers);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleOfferListAdapter(getContext(), offerList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        Club club = new Club();
        club.setId("");
        club.setName(getString(R.string.all));
        clubList.add(club);
        clubList.addAll(AppManager.getInstance().clubs);
        LinearLayoutManager ageLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(ageLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), clubList, 0, false);
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);

        binding.noOfferVu.setVisibility(View.GONE);
        binding.btnAdd.setVisibility(View.GONE);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.btnAddOffer.setOnClickListener(this);
        binding.btnFilter.setOnClickListener(this);
    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            clubId = clubList.get(pos).getId();
            getOffersAPI(true);
        }
    };

    OleOfferListAdapter.ItemClickListener itemClickListener = new OleOfferListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            adapter.binderHelper.closeLayout(String.valueOf(pos));
            Intent intent = new Intent(getContext(), OleCreateOfferActivity.class);
            Gson gson = new Gson();
            intent.putExtra("offer", gson.toJson(offerList.get(pos)));
            startActivity(intent);
        }

        @Override
        public void deleteClicked(View view, int pos) {
            deleteItem(pos);
        }
    };

    private void deleteItem(int pos) {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.delete))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                        adapter.binderHelper.closeLayout(String.valueOf(pos));
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            deleteOfferAPI(offerList.get(pos).getId(), pos);
                        }
                    }
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOffersAPI(offerList.isEmpty());
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnAdd || v == binding.btnAddOffer) {
            addClicked();
        }
        else if (v == binding.btnFilter) {
            ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                    .setCancelButtonTitle(getResources().getString(R.string.cancel))
                    .setOtherButtonTitles(getResources().getString(R.string.active), getResources().getString(R.string.expired))
                    .setCancelableOnTouchOutside(true)
                    .setListener(new ActionSheet.ActionSheetListener() {
                        @Override
                        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                        }

                        @Override
                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            if (index == 0) {
                                filter = "active";
                            }
                            else {
                                filter = "expired";
                            }
                            getOffersAPI(true);
                        }
                    }).show();
        }
    }

    private void addClicked() {
        Intent intent = new Intent(getContext(), OleCreateOfferActivity.class);
        intent.putExtra("club_id", clubId);
        startActivity(intent);
    }

    private void getOffersAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getOffers(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, filter);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            offerList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                offerList.add(gson.fromJson(arr.get(i).toString(), OleOfferData.class));
                            }
                            adapter.notifyDataSetChanged();
                            if (offerList.isEmpty()) {
                                binding.noOfferVu.setVisibility(View.VISIBLE);
                                binding.btnAdd.setVisibility(View.GONE);
                            }
                            else {
                                binding.noOfferVu.setVisibility(View.GONE);
                                binding.btnAdd.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            offerList.clear();
                            adapter.notifyDataSetChanged();
                            binding.noOfferVu.setVisibility(View.VISIBLE);
                            binding.btnAdd.setVisibility(View.GONE);
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

    private void deleteOfferAPI(String offerId, int pos) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteOffer(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), offerId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            adapter.binderHelper.closeLayout(String.valueOf(pos));
                            offerList.remove(pos);
                            adapter.notifyItemRemoved(pos);
                            adapter.notifyItemRangeChanged(pos, offerList.size());
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