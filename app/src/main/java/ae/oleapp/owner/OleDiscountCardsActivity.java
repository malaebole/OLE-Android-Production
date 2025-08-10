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
import ae.oleapp.adapters.OleDiscountCardsAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityDiscountCardsBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleDiscountCard;
import ae.oleapp.player.OleLoyaltyCardShareActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleDiscountCardsActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityDiscountCardsBinding binding;
    private OleDiscountCardsAdapter adapter;
    private final List<OleDiscountCard> oleDiscountCards = new ArrayList<>();
    private OleRankClubAdapter oleRankClubAdapter;
    private String clubId = "", filter = "";
    private final List<Club> clubList = new ArrayList<>();
    private boolean isShare = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityDiscountCardsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.loyalty_cards);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isShare = bundle.getBoolean("is_share", false);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleDiscountCardsAdapter(getContext(), oleDiscountCards, isShare);
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

        binding.noDataVu.setVisibility(View.GONE);
        binding.btnCreate.setVisibility(View.GONE);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnCreate.setOnClickListener(this);
        binding.btnNewDiscount.setOnClickListener(this);
        binding.btnFilter.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDiscountCardsAPI(oleDiscountCards.isEmpty());
    }

    OleDiscountCardsAdapter.ItemClickListener itemClickListener = new OleDiscountCardsAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            if (isShare) {
                Intent intent = new Intent(getContext(), OleLoyaltyCardShareActivity.class);
                Gson gson = new Gson();
                intent.putExtra("card", gson.toJson(oleDiscountCards.get(pos)));
                startActivity(intent);
            }
            else {
                adapter.binderHelper.closeLayout(String.valueOf(pos));
                Intent intent = new Intent(getContext(), OleCreateDiscountCardActivity.class);
                Gson gson = new Gson();
                intent.putExtra("card", gson.toJson(oleDiscountCards.get(pos)));
                startActivity(intent);
            }
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
                            deleteDiscountCardAPI(oleDiscountCards.get(pos).getId(), pos);
                        }
                    }
                }).show();
    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            clubId = clubList.get(pos).getId();
            getDiscountCardsAPI(true);
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnCreate || v == binding.btnNewDiscount) {
            createClicked();
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
                            getDiscountCardsAPI(true);
                        }
                    }).show();
        }
    }

    private void createClicked() {
        Intent intent = new Intent(getContext(), OleCreateDiscountCardActivity.class);
        intent.putExtra("club_id", clubId);
        startActivity(intent);
    }

    private void getDiscountCardsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getDiscountCards(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), clubId, filter);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            oleDiscountCards.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                oleDiscountCards.add(gson.fromJson(arr.get(i).toString(), OleDiscountCard.class));
                            }
                            adapter.notifyDataSetChanged();
                            if (isShare) {
                                if (oleDiscountCards.isEmpty()) {
                                    Functions.showToast(getContext(), getString(R.string.no_loyalty_card), FancyToast.ERROR);
                                }
                            }
                            else {
                                if (oleDiscountCards.isEmpty()) {
                                    binding.noDataVu.setVisibility(View.VISIBLE);
                                    binding.btnCreate.setVisibility(View.GONE);
                                }
                                else {
                                    binding.noDataVu.setVisibility(View.GONE);
                                    binding.btnCreate.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        else {
                            oleDiscountCards.clear();
                            adapter.notifyDataSetChanged();
                            if (isShare) {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
                            else {
                                binding.noDataVu.setVisibility(View.VISIBLE);
                                binding.btnCreate.setVisibility(View.GONE);
                            }
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

    private void deleteDiscountCardAPI(String discountCardId, int pos) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteDiscountCard(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), discountCardId);
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
                            oleDiscountCards.remove(pos);
                            adapter.notifyItemRemoved(pos);
                            adapter.notifyItemRangeChanged(pos, oleDiscountCards.size());
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