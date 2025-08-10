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

import ae.oleapp.databinding.OleactivityCreatePadelMatchBinding;
import ae.oleapp.dialogs.OlePaymentDialogFragment;
import ae.oleapp.models.OlePadelSkillLevel;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.UserInfo;
import ae.oleapp.player.OlePlayerListActivity;
import ae.oleapp.player.OlePlayerMainTabsActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import io.apptik.widget.MultiSlider;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleCreatePadelMatchActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityCreatePadelMatchBinding binding;
    private String bookingId = "";
    private String clubId = "";
    private String bookingPaymentMethod = "";
    private String matchAllowed = "";
    private OlePlayerInfo partner;
    private OlePadelSkillsLevelAdapter adapter;
    private final List<OlePadelSkillLevel> skillLevels = new ArrayList<>();
    private String selectedLevelId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityCreatePadelMatchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.create_match);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bookingId = bundle.getString("booking_id", "");
            clubId = bundle.getString("club_id", "");
            bookingPaymentMethod = bundle.getString("payment_method", "");
            matchAllowed = bundle.getString("match_allow", "");
            if (bundle.containsKey("partner")) {
                partner = new Gson().fromJson(bundle.getString("partner", ""), OlePlayerInfo.class);
            }
        }

        binding.partnerVu.setVisibility(View.GONE);
        binding.tvPartnerDesc.setVisibility(View.VISIBLE);
        binding.btnCreate.setAlpha(0.5f);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnSelectPartner.setOnClickListener(this);
        binding.btnCreate.setOnClickListener(this);

        setupSliders();

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

    private void setupSliders() {
        UserInfo userInfo = Functions.getUserinfo(this);
        if (userInfo != null) {
            if (userInfo.getUserAge() != null && !userInfo.getUserAge().equalsIgnoreCase("")) {
                int age = Integer.parseInt(userInfo.getUserAge());
                if (age > 17) {
                    binding.slider.getThumb(0).setValue(17);
                    binding.slider.getThumb(1).setValue(55);
                    binding.tvMinAge.setText("17");
                    binding.tvMaxAge.setText("55");
                }
                else {
                    binding.slider.getThumb(0).setValue(12);
                    binding.slider.getThumb(1).setValue(16);
                    binding.tvMinAge.setText("12");
                    binding.tvMaxAge.setText("16");
                }
            }
            else {
                binding.slider.getThumb(0).setValue(17);
                binding.slider.getThumb(1).setValue(55);
                binding.tvMinAge.setText("17");
                binding.tvMaxAge.setText("55");
            }
        }
        binding.slider.setMax(60);
        binding.slider.setMin(12);
        binding.slider.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    binding.tvMinAge.setText(String.valueOf(thumb.getValue()));
                }
                else {
                    binding.tvMaxAge.setText(String.valueOf(thumb.getValue()));
                }
            }
        });
    }

    private void populateData() {
        if (partner == null) {
            binding.tvSelectPartner.setText(R.string.select_your_partner);
            binding.tvSelectPartner.setTextColor(getResources().getColor(R.color.blueColorNew));
            binding.btnSelectPartner.setBackgroundResource(R.drawable.blue_dotted_border);
            return;
        }
        Glide.with(getContext()).load(partner.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.playerImage);
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
        binding.btnCreate.setAlpha(1.0f);
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
            startActivityForResult(intent, 106);
        }
        else if (v == binding.btnCreate) {
            createMatchClicked();
        }
    }

    private void createMatchClicked() {
        if (partner == null) {
            return;
        }
        if (!matchAllowed.equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.club_not_allow_match), FancyToast.ERROR);
            return;
        }
        if (selectedLevelId.equalsIgnoreCase("")) {
            Functions.showToast(getContext(), getString(R.string.select_skills_level), FancyToast.ERROR);
            return;
        }
        boolean cashHide = !bookingPaymentMethod.equalsIgnoreCase("cash");
        openPaymentDialog("0", Functions.getPrefValue(getContext(), Constants.kCurrency), "", bookingId, "", cashHide, false, "1", getString(R.string.match_notes), clubId, new OlePaymentDialogFragment.PaymentDialogCallback() {
            @Override
            public void didConfirm(boolean status, String paymentMethod, String orderRef, String paidPrice, String walletPaid, String cardPaid) {
                createMatchAPI(true, partner.getId(), binding.tvMinAge.getText().toString(), binding.tvMaxAge.getText().toString(), orderRef, paymentMethod, cardPaid, walletPaid);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 106 && resultCode == RESULT_OK) {
            String str = data.getExtras().getString("players");
            Gson gson = new Gson();
            List<OlePlayerInfo> list = gson.fromJson(str, new TypeToken<List<OlePlayerInfo>>(){}.getType());
            if (list.size() > 0) {
                partner = list.get(0);
                populateData();
            }
        }
    }

    private void createMatchAPI(boolean isLoader, String partnerId, String minAge, String maxAge, String orderRef, String paymentMethod, String cardPaid, String walletPaid) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.createPadelMatch(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), bookingId, partnerId, minAge, maxAge, orderRef, cardPaid, walletPaid, selectedLevelId, paymentMethod, Functions.getIPAddress());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
                            intent.putExtra("tab_position", 1);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
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
        call.enqueue(new Callback<ResponseBody>() {
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