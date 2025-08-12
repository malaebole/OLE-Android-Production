package ae.oleapp.padel;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OlePadelSkillsLevelAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityPadelChallengeBinding;
import ae.oleapp.dialogs.OlePaymentDialogFragment;
import ae.oleapp.models.OlePadelSkillLevel;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.OlePlayerMatch;
import ae.oleapp.player.OlePlayerListActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePadelChallengeActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPadelChallengeBinding binding;
    private OlePlayerInfo partner;
    private OlePlayerMatch matchDetail;
    private OlePadelSkillsLevelAdapter adapter;
    private final List<OlePadelSkillLevel> skillLevels = new ArrayList<>();
    private String selectedLevelId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPadelChallengeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.challenge);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            matchDetail = new Gson().fromJson(bundle.getString("match", ""), OlePlayerMatch.class);
        }

        binding.partnerVu.setVisibility(View.GONE);
        binding.tvPartnerDesc.setVisibility(View.VISIBLE);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnSelectPartner.setOnClickListener(this);
        binding.btnChallenge.setOnClickListener(this);

        populateData();
        getLevelsAPi(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OlePadelSkillsLevelAdapter(getContext(), skillLevels);
        adapter.setOnItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);
    }

    OlePadelSkillsLevelAdapter.OnItemClickListener itemClickListener = new OlePadelSkillsLevelAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            selectedLevelId = skillLevels.get(pos).getId();
            adapter.setSelectedLevelId(selectedLevelId);
        }
    };

    private void populateData() {
        if (partner == null) {
            binding.tvSelectPartner.setText(R.string.select_your_partner);
            binding.tvSelectPartner.setTextColor(getResources().getColor(R.color.blueColorNew));
            binding.btnSelectPartner.setBackgroundResource(R.drawable.blue_dotted_border);
            return;
        }
        Glide.with(getApplicationContext()).load(partner.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.playerImage);
        binding.tvName.setText(partner.getNickName());
        if (partner.getLevel() != null && !partner.getLevel().isEmpty() && !partner.getLevel().getValue().equalsIgnoreCase("")) {
            binding.tvRank.setVisibility(View.VISIBLE);
            binding.tvRank.setText(String.format("LV: %s", partner.getLevel().getValue()));
        }
        else {
            binding.tvRank.setVisibility(View.INVISIBLE);
        }
        binding.tvAge.setText(String.format("%s %s", getString(R.string.age), partner.getAge()));
        binding.tvSelectPartner.setText(R.string.change_your_partner);
        binding.tvSelectPartner.setTextColor(getResources().getColor(R.color.redColor));
        binding.btnSelectPartner.setBackgroundResource(R.drawable.red_dotted_border);
        binding.partnerVu.setVisibility(View.VISIBLE);
        binding.tvPartnerDesc.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.btnSelectPartner) {
            Intent intent = new Intent(getContext(), OlePlayerListActivity.class);
            intent.putExtra("is_selection", true);
            intent.putExtra("is_single_selection", true);
            intent.putExtra("is_select_partner", true);
            startActivityForResult(intent, 107);
        }
        else if (v == binding.btnChallenge) {
            challengeClicked();
        }
    }

    private void challengeClicked() {
        if (partner == null) {
            Functions.showToast(getContext(), getString(R.string.select_your_partner), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        if (selectedLevelId.equalsIgnoreCase("")) {
            Functions.showToast(getContext(), getString(R.string.select_skills_level), FancyToast.ERROR);
            return;
        }
        if (partner.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) ||
                partner.getId().equalsIgnoreCase(matchDetail.getCreatedBy().getId()) ||
                partner.getId().equalsIgnoreCase(matchDetail.getCreatorPartner().getId())) {
            Functions.showToast(getContext(), getString(R.string.choose_different_partner_who_not_in_match), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        openPaymentDialog(matchDetail.getJoiningFee(), Functions.getPrefValue(getContext(), Constants.kCurrency), "", "", "", false, false, matchDetail.getClubId(), new OlePaymentDialogFragment.PaymentDialogCallback() {
            @Override
            public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                joinChallengeAPI(true, matchDetail.getBookingId(), partner.getId(), paymentMethod, orderRef, matchDetail.getJoiningFee(), cardPaid, walletPaid);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 107 && resultCode == RESULT_OK) {
            String str = data.getExtras().getString("players");
            Gson gson = new Gson();
            List<OlePlayerInfo> list = gson.fromJson(str, new TypeToken<List<OlePlayerInfo>>() {
            }.getType());
            if (list.size() > 0) {
                partner = list.get(0);
                populateData();
            }
        }
    }

    private void joinChallengeAPI(boolean isLoader, String bookingId, String partnerId, String paymentMethod, String orderRef, String joinFee, String cardPaid, String walletPaid) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.challengePadel(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, partnerId, orderRef, cardPaid, walletPaid, joinFee, selectedLevelId, Functions.getIPAddress(), paymentMethod);
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

    private void getLevelsAPi(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getLevels(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            skillLevels.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                skillLevels.add(gson.fromJson(arr.get(i).toString(), OlePadelSkillLevel.class));
                            }
                            adapter.notifyDataSetChanged();
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