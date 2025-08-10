package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import ae.oleapp.adapters.OlePositionListAdapter;
import ae.oleapp.databinding.OlefragmentPositionDialogBinding;
import ae.oleapp.models.OlePlayerPosition;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class OlePositionDialogFragment extends DialogFragment {

    private String joiningFee = "", currency = "";
    private final List<OlePlayerPosition> positionList = new ArrayList<>();
    private PositionDialogCallback dialogCallback;
    private int selectedIndex = -1;
    private OlePositionListAdapter adapter;
    private OlefragmentPositionDialogBinding binding;

    public OlePositionDialogFragment() {
        // Required empty public constructor
    }

    public OlePositionDialogFragment(String joiningFee, String currency) {
        this.joiningFee = joiningFee;
        this.currency = currency;
    }

    public void setDialogCallback(PositionDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentPositionDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (joiningFee.isEmpty() || currency.isEmpty()) {
            binding.tvNote.setText("");
        }
        else {
            binding.tvNote.setText(getResources().getString(R.string.note_join_game, joiningFee, currency));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OlePositionListAdapter(getContext(), R.layout.oleposition_list, positionList, selectedIndex);
        adapter.setItemClickListener(clickListener);
        binding.recyclerVu.setAdapter(adapter);

        getPlayerPositionAPI(true);

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmClicked();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    OlePositionListAdapter.ItemClickListener clickListener = new OlePositionListAdapter.ItemClickListener() {
        @Override
        public void onItemClicked(View view, int pos) {
            selectedIndex = pos;
            adapter.setSelectedIndex(pos);
        }
    };

    private void confirmClicked() {
        if (selectedIndex == -1) {
            return;
        }
        dismiss();
        dialogCallback.confirmClicked(positionList.get(selectedIndex));
    }

    private void getPlayerPositionAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getPlayerPosition(Functions.getAppLang(getActivity()));
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
                            positionList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                positionList.add(gson.fromJson(arr.get(i).toString(), OlePlayerPosition.class));
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

    public interface PositionDialogCallback {
        void confirmClicked(OlePlayerPosition olePlayerPosition);
    }
}
