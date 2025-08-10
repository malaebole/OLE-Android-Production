package ae.oleapp.player;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleGroupListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityGroupListBinding;
import ae.oleapp.dialogs.OleCreateGroupDialogFragment;
import ae.oleapp.models.OlePlayersGroup;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleGroupListActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityGroupListBinding binding;
    private OleGroupListAdapter adapter;
    private final List<OlePlayersGroup> groupList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityGroupListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.groups);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleGroupListAdapter(getContext(), groupList);
        adapter.setOnItemClickListener(groupClicked);
        binding.recyclerVu.setAdapter(adapter);

        binding.bar.backBtn.setOnClickListener(this);
        binding.btnCreate.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getGroupListAPI(groupList.isEmpty());
    }

    @Override
    public void onClick(View view) {
        if (view == binding.bar.backBtn) {
            finish();
        }
        else if (view == binding.btnCreate) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("CreateGroupDialogFragment");
            if (prev != null) {
                fragmentTransaction.remove(prev);
            }
            fragmentTransaction.addToBackStack(null);
            OleCreateGroupDialogFragment dialogFragment = new OleCreateGroupDialogFragment();
            dialogFragment.setDialogCallback(new OleCreateGroupDialogFragment.CreateGroupDialogCallback() {
                @Override
                public void groupCreated(String groupId, String name) {
                    Intent intent = new Intent(getContext(), OleGroupPlayersActivity.class);
                    intent.putExtra("group_id", groupId);
                    intent.putExtra("group_name", name);
                    startActivity(intent);
                }
            });
            dialogFragment.show(fragmentTransaction, "CreateGroupDialogFragment");
        }
    }

    OleGroupListAdapter.OnItemClickListener groupClicked = new OleGroupListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            Intent intent = new Intent(getContext(), OleGroupPlayersActivity.class);
            intent.putExtra("group_id", groupList.get(pos).getId());
            intent.putExtra("group_name", groupList.get(pos).getName());
            startActivity(intent);
        }

        @Override
        public void OnDeleteClick(View v, int pos) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.confirmation))
                    .setMessage(getResources().getString(R.string.do_you_want_to_delete_group))
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteGroupAPI(true, groupList.get(pos).getId(), pos);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            builder.show();
        }
    };

    private void getGroupListAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getGroupList(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
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
                            groupList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                groupList.add(gson.fromJson(arr.get(i).toString(), OlePlayersGroup.class));
                            }
                        }
                        else {
                            groupList.clear();
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                        adapter.notifyDataSetChanged();
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

    private void deleteGroupAPI(boolean isLoader, String groupId, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteGroup(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), groupId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            groupList.remove(pos);
                            adapter.notifyItemRemoved(pos);
                            adapter.notifyItemRangeChanged(pos, groupList.size());
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