package ae.oleapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleDocumentListAdapter;
import ae.oleapp.adapters.OleIncomeHistoryAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityIncomeHistoryBinding;
import ae.oleapp.databinding.ActivityUniversalDocBinding;
import ae.oleapp.fragments.DocDetailsBottomSheetDialogFragment;
import ae.oleapp.fragments.FilterFragment;
import ae.oleapp.fragments.IncomeHistoryBottomSheetDialogFragment;
import ae.oleapp.models.DocDetails;
import ae.oleapp.models.DocList;
import ae.oleapp.models.FinanceHome;
import ae.oleapp.models.IncomeHistory;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UniversalDocActivity extends BaseActivity implements View.OnClickListener {

    private ActivityUniversalDocBinding binding;
    private OleDocumentListAdapter oleDocumentListAdapter;

    private final List<DocList> docLists = new ArrayList<>();
    private DocDetails docDetails;
    private String clubId = "", typeID="", type="",fileId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUniversalDocBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
            typeID = bundle.getString("type_id", "");
            type = bundle.getString("type", "");
        }


        LinearLayoutManager OleDocumentLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.docRecyclerVu.setLayoutManager(OleDocumentLayoutManager);
        oleDocumentListAdapter = new OleDocumentListAdapter(getContext(), docLists, type);
        oleDocumentListAdapter.setItemClickListener(itemClickListener);
        binding.docRecyclerVu.setAdapter(oleDocumentListAdapter);

        binding.backBtn.setOnClickListener(this);
        binding.editDoc.setOnClickListener(this);
        binding.btnAddDoc.setOnClickListener(this);

    }

    OleDocumentListAdapter.ItemClickListener itemClickListener = new OleDocumentListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            fileId = docLists.get(pos).getId();
            showDocDetail(docLists.get(pos).getId());

        }
    };



    @Override
    public void onClick(View v) {
        if (v == binding.backBtn){
            finish();
        }
        else if (v == binding.editDoc) {
            Intent intent = new Intent(this, AddNewFolderActivity.class);
            intent.putExtra("is_update",true);
            intent.putExtra("docDetails", new Gson().toJson(docDetails));
            intent.putExtra("club_id",clubId);
            intent.putExtra("typeId",typeID);
            startActivity(intent);
            finish();
        }
        else if (v == binding.btnAddDoc) {
            Intent intent = new Intent(UniversalDocActivity.this, AddDocumentActivity.class);
            intent.putExtra("is_update",false);
            intent.putExtra("type", type);
            intent.putExtra("typeId", typeID);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getFolderDetails(docLists.isEmpty(), clubId, type, typeID);
    }

    protected void showDocDetail(String fileID) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("DocDetailsBottomSheetDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        DocDetailsBottomSheetDialogFragment dialogFragment = new DocDetailsBottomSheetDialogFragment(fileID, clubId, docDetails);
        dialogFragment.setDialogCallback((df, isEditClicked, fileId, docDetails) -> {
            df.dismiss();
            if (isEditClicked) {
                Intent intent = new Intent(UniversalDocActivity.this, AddDocumentActivity.class);
                intent.putExtra("is_update",true);
                intent.putExtra("docDetails", new Gson().toJson(docDetails));
                intent.putExtra("fileId", fileId);
                intent.putExtra("type", type);
                intent.putExtra("typeId", typeID);
                intent.putExtra("club_id", clubId);
                startActivity(intent);
                //finish();
            }

        });
        dialogFragment.show(fragmentTransaction, "DocDetailsBottomSheetDialogFragment");
    }

    private void getFolderDetails(Boolean isLoader, String clubId, String type, String typeID) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.folderDetails(Functions.getAppLang(getContext()), clubId, type, typeID);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONArray data = object.getJSONArray(Constants.kData);
                                Gson gson = new Gson();
                                docLists.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    docLists.add(gson.fromJson(data.get(i).toString(), DocList.class));
                                }

                                JSONObject obj = object.getJSONObject("details");
                                Gson gson1 = new Gson();
                                docDetails = gson1.fromJson(obj.toString(), DocDetails.class);
                                oleDocumentListAdapter.notifyDataSetChanged();
                                populateData();

                            }
                            else {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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


    private void populateData() {


        if (type.equalsIgnoreCase("partners") || type.equalsIgnoreCase("employees")){
            binding.tvName.setText(docDetails.getName());
            binding.tvEmail.setText(docDetails.getEmail());
            binding.tvPhone.setText(docDetails.getPhone());
            binding.editDoc.setVisibility(View.GONE);
        } else {
            binding.tvName.setText(docDetails.getName());
            binding.tvName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            binding.editDoc.setVisibility(View.VISIBLE);
            binding.emailLayout.setVisibility(View.GONE);
            binding.callLayout.setVisibility(View.GONE);
        }

        if (type.equalsIgnoreCase("folder") || type.equalsIgnoreCase("employees")) {

            if (!docDetails.getPhotoUrl().isEmpty()){
                Glide.with(getContext()).load(docDetails.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(binding.imgVu);
            }

        }else{
            if (!docDetails.getPhotoUrl().isEmpty()){
                Glide.with(getContext()).load(docDetails.getPhotoUrl()).into(binding.imgVu);
            }
        }


        oleDocumentListAdapter.notifyDataSetChanged();

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}