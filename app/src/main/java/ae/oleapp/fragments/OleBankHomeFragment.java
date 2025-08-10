package ae.oleapp.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.activities.AddBankActivity;
import ae.oleapp.activities.OleFinanceActivity;
import ae.oleapp.adapters.BanksAdapter;
import ae.oleapp.adapters.OleIncomeHistoryAdapter;
import ae.oleapp.base.BaseFragment;
import ae.oleapp.databinding.FragmentOleBankHomeBinding;
import ae.oleapp.databinding.FragmentOleFinanceHomeBinding;
import ae.oleapp.databinding.FragmentPartnerHistoryBottomSheetDialogBinding;
import ae.oleapp.dialogs.PartnerHistoryBottomSheetDialogFragment;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.PartnerHistoryModel;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OleBankHomeFragment extends BaseFragment implements View.OnClickListener {

        private FragmentOleBankHomeBinding binding;
        private BanksAdapter banksAdapter;
        private final List<ClubBankLists> clubBankLists = new ArrayList<>();

        private String clubId = "";



        public OleBankHomeFragment() {
                //Required empty public constructor
                }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            binding = FragmentOleBankHomeBinding.inflate(inflater, container, false);
            View view = binding.getRoot();

            OleFinanceActivity activity = (OleFinanceActivity)getActivity();
            Bundle results = activity.getClubId();
            if (results !=null){
                clubId = results.getString("club_id");
            }



            LinearLayoutManager oleBanksLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            binding.bankRecyclerVu.setLayoutManager(oleBanksLayoutManager);
            banksAdapter = new BanksAdapter(getContext(), clubBankLists);
            banksAdapter.setItemClickListener(itemClickListener);
            binding.bankRecyclerVu.setAdapter(banksAdapter);

            binding.backBtn.setOnClickListener(this);
            binding.btnAdd.setOnClickListener(this);

            return view;
        }


        BanksAdapter.ItemClickListener itemClickListener = new BanksAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
                Intent intent = new Intent(getActivity(), AddBankActivity.class);
                intent.putExtra("is_update",true);
                intent.putExtra("club_id", clubId);
                intent.putExtra("bank", new Gson().toJson(clubBankLists.get(pos)));
                startActivity(intent);

                }
        };



        @Override
        public void onDestroyView() {
                super.onDestroyView();
                binding = null;
        }

        @Override
        public void onClick(View v) {
            if (v == binding.backBtn) {
                getActivity().finish();
            }
            else if (v == binding.btnAdd) {
                Intent intent = new Intent(getActivity(), AddBankActivity.class);
                intent.putExtra("is_update",false);
                intent.putExtra("club_id", clubId);
                startActivity(intent);
            }

        }

    @Override
    public void onResume() {
        super.onResume();
        getClubBanksList(clubBankLists.isEmpty(), clubId);

    }
    private void getClubBanksList(boolean isLoader, String clubId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing") : null;
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        if (userId!=null){
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.getClubBanksList(Functions.getAppLang(getContext()), clubId,"");
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
                                clubBankLists.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    clubBankLists.add(gson.fromJson(data.get(i).toString(), ClubBankLists.class));
                                }
                                banksAdapter.notifyDataSetChanged();
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

//    private void popuplatedata(){
//
//                binding.tvCurrency.setText(financeHomeData.getCurrency());
//                binding.tvTotalEarning.setText(financeHomeData.getAvailableEarnings() + ".00");
//                binding.incomeTv.setText(financeHomeData.getTotalIncomes());
//                binding.expenseTv.setText(financeHomeData.getTotalExpenses());
//                binding.upcomingExpenseTv.setText(financeHomeData.getUpcomingExpenses());
//
//        }


}
