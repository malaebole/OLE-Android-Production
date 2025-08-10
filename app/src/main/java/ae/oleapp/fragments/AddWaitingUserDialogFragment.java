package ae.oleapp.fragments;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.shashank.sony.fancytoastlib.FancyToast;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentAddWaitingUserDialogBinding;
import ae.oleapp.databinding.FragmentIncomeHistoryBottomSheetDialogBinding;
import ae.oleapp.models.IncomeDetailsModel;
import ae.oleapp.util.Functions;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;


public class AddWaitingUserDialogFragment extends DialogFragment implements View.OnClickListener{

    private FragmentAddWaitingUserDialogBinding binding;
    private ResultDialogCallback dialogCallback;
    private final String bookingId;



    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public AddWaitingUserDialogFragment(String bookingId) {
        this.bookingId = bookingId;
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddWaitingUserDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        binding.btnClose.setOnClickListener(this);
        binding.btnDone.setOnClickListener(this);


        return view;
    }



    @Override
    public void onClick(View v) {

        if (v == binding.btnClose){
            dismiss();

        }
        else if (v == binding.btnDone){
            if (binding.etPhone.getText().toString().isEmpty()){
                Functions.showToast(getContext(), "Phone number cannot be empty!", FancyToast.ERROR);
                return;
            }
            dialogCallback.didSubmitResult(AddWaitingUserDialogFragment.this, binding.etName.getText().toString(), binding.etPhone.getText().toString());
        }

    }


    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, String name, String phone);
    }
}