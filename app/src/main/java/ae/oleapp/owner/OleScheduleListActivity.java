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
import ae.oleapp.adapters.OleClubNameAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.adapters.OleScheduleListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityScheduleListBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleScheduleList;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleScheduleListActivity extends BaseActivity {

    private OleactivityScheduleListBinding binding;
    private final List<OleScheduleList> oleScheduleList = new ArrayList<>();
    private OleScheduleListAdapter adapter;
    private String clubId = "";
    private final List<Club> clubList = new ArrayList<>();
    private final String selectedClubId = "";
    private OleRankClubAdapter oleRankClubAdapter;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityScheduleListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.continuous_booking);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id","");
        }

        if (clubId.isEmpty()){
            clubId = "2";
        }

        for (int i =0; i<AppManager.getInstance().clubs.size(); i++){
            if (clubId.equalsIgnoreCase(AppManager.getInstance().clubs.get(i).getId())){
                index = i;
            }
        }
        LinearLayoutManager clubLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(clubLayoutManager);
        oleRankClubAdapter = new OleRankClubAdapter(getContext(), AppManager.getInstance().clubs, index, false); // clublist
        oleRankClubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(oleRankClubAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleScheduleListAdapter(getContext(), oleScheduleList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.bar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    OleRankClubAdapter.OnItemClickListener clubClickListener = new OleRankClubAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            oleRankClubAdapter.setSelectedIndex(pos);
            clubId = AppManager.getInstance().clubs.get(pos).getId();
            getScheduleListAPI(true, clubId);


        }
    };

    OleScheduleListAdapter.ItemClickListener itemClickListener = new OleScheduleListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            OleScheduleList obj = oleScheduleList.get(pos);
            Intent intent = new Intent(getContext(), OleScheduleDetailActivity.class);
            intent.putExtra("ids", obj.getId());
            intent.putExtra("from", obj.getFromDate());
            intent.putExtra("to", obj.getToDate());
            startActivity(intent);
        }

        @Override
        public void delClicked(View view, int pos) {
            deleteSchedule(pos);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!clubId.isEmpty()){
            getScheduleListAPI(oleScheduleList.isEmpty(), clubId);
        }
    }

    private void deleteSchedule(int pos) {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.delete))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            deleteScheduleAPI(true, oleScheduleList.get(pos).getId(), pos);
                        }
                    }
                }).show();
    }

    private void getScheduleListAPI(boolean isLoader, String clubId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getScheduleList(Functions.getAppLang(getContext()), clubId, Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            oleScheduleList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                OleScheduleList schedule = gson.fromJson(arr.get(i).toString(), OleScheduleList.class);
                                oleScheduleList.add(schedule);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            oleScheduleList.clear();
                            adapter.notifyDataSetChanged();
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

    private void deleteScheduleAPI(boolean isLoader, String ids, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteSchedule(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), ids);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            oleScheduleList.remove(pos);
                            adapter.notifyItemRemoved(pos);
                            adapter.notifyItemRangeChanged(pos, oleScheduleList.size());
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
