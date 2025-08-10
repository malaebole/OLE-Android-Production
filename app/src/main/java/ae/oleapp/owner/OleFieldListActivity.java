package ae.oleapp.owner;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleFieldListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityFieldListBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.Field;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleFieldListActivity extends BaseActivity {

    private OleactivityFieldListBinding binding;
    private final List<Field> fieldList = new ArrayList<>();
    private OleFieldListAdapter adapter;
    private Club club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityFieldListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.fields);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            club = gson.fromJson(bundle.getString("club", ""), Club.class);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleFieldListAdapter(getContext(), fieldList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.bar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllFieldsAPI(fieldList.isEmpty());
    }

    OleFieldListAdapter.ItemClickListener itemClickListener = new OleFieldListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), OleBookingActivity.class);
            intent.putExtra("field_id", fieldList.get(pos).getId());
            Gson gson = new Gson();
            intent.putExtra("club", gson.toJson(club));
            startActivity(intent);
        }

        @Override
        public void menuClicked(View view, int pos) {
            PopupMenu popup = new PopupMenu(getContext(), view);
            popup.inflate(R.menu.field_menu);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popup.setGravity(Gravity.END);
            }
            if (club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
                popup.getMenu().findItem(R.id.hide_field).setTitle(getString(R.string.girls_time_slots));
            }
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.hide_field:
                            if (club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
                                Intent intent = new Intent(getContext(), OleGirlsTimeSlotsActivity.class);
                                intent.putExtra("club_id", club.getId());
                                intent.putExtra("field_id", fieldList.get(pos).getId());
                                startActivity(intent);
                            }
                            else {
                                Intent intent = new Intent(getContext(), OleHiddenFieldsActivity.class);
                                intent.putExtra("club_id", club.getId());
                                intent.putExtra("field_id", fieldList.get(pos).getId());
                                startActivity(intent);
                            }
                            return true;
                        case R.id.edit:
                            Intent addFieldIntent = new Intent(getContext(), OleAddFieldActivity.class);
                            addFieldIntent.putExtra("club_id", club.getId());
                            addFieldIntent.putExtra("field_id", fieldList.get(pos).getId());
                            if (club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
                                addFieldIntent.putExtra("is_football_update", false);
                                addFieldIntent.putExtra("is_padel_update", true);
                            }
                            else {
                                addFieldIntent.putExtra("is_football_update", true);
                                addFieldIntent.putExtra("is_padel_update", false);
                            }
                            startActivity(addFieldIntent);
                            return true;
                        case R.id.delete:
                            deleteField(fieldList.get(pos).getId(), pos);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.show();
        }
    };

    private void deleteField(String fieldId, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.delete_field))
                .setMessage(getResources().getString(R.string.do_you_want_to_delete_field))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFieldAPI(true, fieldId, pos);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void getAllFieldsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getAllFields(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), club.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            fieldList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                Field field = gson.fromJson(arr.get(i).toString(), Field.class);
                                fieldList.add(field);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            fieldList.clear();
                            adapter.notifyDataSetChanged();
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

    private void deleteFieldAPI(boolean isLoader, String fieldId, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteField(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), fieldId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            fieldList.remove(pos);
                            adapter.notifyItemRemoved(pos);
                            adapter.notifyItemRangeChanged(pos, fieldList.size());
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
