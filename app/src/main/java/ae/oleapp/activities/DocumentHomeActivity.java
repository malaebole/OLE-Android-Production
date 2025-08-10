package ae.oleapp.activities;

import androidx.recyclerview.widget.LinearLayoutManager;

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
import ae.oleapp.adapters.DocAdapter;
import ae.oleapp.adapters.EmployeeDocAdapter;
import ae.oleapp.adapters.PartnerDocAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityDocumentHomeBinding;
import ae.oleapp.models.DocModel;
import ae.oleapp.models.DocumentModel;
import ae.oleapp.models.EmployeeDocModel;
import ae.oleapp.models.PartnerDocModel;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentHomeActivity extends BaseActivity implements View.OnClickListener {

    private ActivityDocumentHomeBinding binding;
    private PartnerDocAdapter partnerDocAdapter;
    private EmployeeDocAdapter employeeDocAdapter;
    private DocAdapter docAdapter;
    private String clubId = "";

    private final List<PartnerDocModel> partnerDocModelList = new ArrayList<>();
    private final List<EmployeeDocModel> employeeDocModelList = new ArrayList<>();
    private final List<DocModel> docModelList = new ArrayList<>();
    private DocumentModel documentModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocumentHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id", "");
        }

        LinearLayoutManager partnerDocumentLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.partnerDocRecyclerVu.setLayoutManager(partnerDocumentLayoutManager);
        partnerDocAdapter = new PartnerDocAdapter(getContext(), partnerDocModelList);
        partnerDocAdapter.setItemClickListener(partnerDocClickListener);
        binding.partnerDocRecyclerVu.setAdapter(partnerDocAdapter);

        LinearLayoutManager employeeDocumentLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.employeeDocRecyclerVu.setLayoutManager(employeeDocumentLayoutManager);
        employeeDocAdapter = new EmployeeDocAdapter(getContext(), employeeDocModelList);
        employeeDocAdapter.setItemClickListener(employeeDocClickListener);
        binding.employeeDocRecyclerVu.setAdapter(employeeDocAdapter);

        LinearLayoutManager documentLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.documentsRecyclerVu.setLayoutManager(documentLayoutManager);
        docAdapter = new DocAdapter(getContext(), docModelList);
        docAdapter.setItemClickListener(docClickListener);
        binding.documentsRecyclerVu.setAdapter(docAdapter);


        binding.backBtn.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);

    }

    PartnerDocAdapter.ItemClickListener partnerDocClickListener = new PartnerDocAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

            Intent intent = new Intent(getContext(), UniversalDocActivity.class);
            intent.putExtra("club_id", clubId);
            intent.putExtra("type_id", partnerDocModelList.get(pos).getId());
            intent.putExtra("type", "partners");
            startActivity(intent);

        }
    };

    EmployeeDocAdapter.ItemClickListener employeeDocClickListener = new EmployeeDocAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

            Intent intent = new Intent(getContext(), UniversalDocActivity.class);
            intent.putExtra("club_id", clubId);
            intent.putExtra("type_id", employeeDocModelList.get(pos).getId());
            intent.putExtra("type", "employees");
            startActivity(intent);

        }
    };

    DocAdapter.ItemClickListener docClickListener = new DocAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

            Intent intent = new Intent(getContext(), UniversalDocActivity.class);
            intent.putExtra("club_id", clubId);
            intent.putExtra("type_id", docModelList.get(pos).getId());
            intent.putExtra("type", "folder");
            startActivity(intent);


        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        documentsHome(documentModel == null, clubId);
    }

    private void documentsHome(boolean isLoader, String clubId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(this, "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.documentsHome(Functions.getAppLang(this), clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            documentModel = gson.fromJson(obj.toString(), DocumentModel.class);
                            //popuplatedata();
                            JSONArray partners = obj.getJSONArray("partners");
                            JSONArray employees = obj.getJSONArray("employees");
                            JSONArray assets = obj.getJSONArray("assets");
                            partnerDocModelList.clear();
                            employeeDocModelList.clear();
                            docModelList.clear();
                            Gson gson1 = new Gson();
                            for (int i = 0; i < partners.length(); i++) {
                                partnerDocModelList.add(gson1.fromJson(partners.get(i).toString(), PartnerDocModel.class));
                            }
                            for (int i = 0; i < employees.length(); i++) {
                                employeeDocModelList.add(gson1.fromJson(employees.get(i).toString(), EmployeeDocModel.class));
                            }
                            for (int i = 0; i < assets.length(); i++) {
                                docModelList.add(gson1.fromJson(assets.get(i).toString(), DocModel.class));
                            }

                            partnerDocAdapter.notifyDataSetChanged();
                            employeeDocAdapter.notifyDataSetChanged();
                            docAdapter.notifyDataSetChanged();
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(DocumentHomeActivity.this, e.getLocalizedMessage(), FancyToast.ERROR);
                    }

                } else {
                    Functions.showToast(DocumentHomeActivity.this, getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(DocumentHomeActivity.this, getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(DocumentHomeActivity.this, t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == binding.backBtn){
            finish();
        }
        else if (v == binding.btnSubmit){
         Intent intent = new Intent(this, AddNewFolderActivity.class);
         intent.putExtra("is_update",false);
         intent.putExtra("club_id",clubId);
         startActivity(intent);
        }

    }


}