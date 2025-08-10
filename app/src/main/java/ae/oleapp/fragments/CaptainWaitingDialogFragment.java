package ae.oleapp.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentCaptainWaitingDialogBinding;


public class CaptainWaitingDialogFragment extends DialogFragment implements View.OnClickListener{

    private FragmentCaptainWaitingDialogBinding binding;
    private ResultDialogCallback dialogCallback;
    private String photoUrl = "";

    private final String shirtId = "";

    public CaptainWaitingDialogFragment() {
        // Required empty public constructor
    }

    public CaptainWaitingDialogFragment(String photoUrl) {
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
        binding = FragmentCaptainWaitingDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        binding.btnClose.setOnClickListener(this);

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
//        else if (v == binding.adBtn) {
//            dialogCallback.didSubmitResult(AdsSubscriptionPopupFragment.this, 1);
//        }

    }


    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df);
    }



}