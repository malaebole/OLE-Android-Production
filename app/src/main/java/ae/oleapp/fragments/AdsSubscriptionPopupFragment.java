package ae.oleapp.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.BestPlayerAdapter;
import ae.oleapp.databinding.FragmentAdsSubscriptionPopupBinding;
import ae.oleapp.databinding.FragmentBestPlayerDialogBinding;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdsSubscriptionPopupFragment extends DialogFragment implements View.OnClickListener{

    private FragmentAdsSubscriptionPopupBinding binding;
    private ResultDialogCallback dialogCallback;
    private String photoUrl = "";

    private final String shirtId = "";

    public AdsSubscriptionPopupFragment() {
        // Required empty public constructor
    }

    public AdsSubscriptionPopupFragment(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdsSubscriptionPopupBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        binding.btnClose.setOnClickListener(this);
        binding.adBtn.setOnClickListener(this);
        binding.subscribeBtn.setOnClickListener(this);


        Glide.with(getActivity()).load(photoUrl).into(binding.shirtImgVuAd);
        Glide.with(getActivity()).load(photoUrl).into(binding.shirtImgVuSubs);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    @Override
    public void onClick(View v) {
        if (v == binding.btnClose){
            this.dismiss();
        }
        else if (v == binding.adBtn) {
            dialogCallback.didSubmitResult(AdsSubscriptionPopupFragment.this, 1);
        } else if (v == binding.subscribeBtn) {
            dialogCallback.didSubmitResult(AdsSubscriptionPopupFragment.this, 0);
        }

    }


    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df,  int choice);
    }



}