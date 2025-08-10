package ae.oleapp.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentFriendRequestMessageDialogBinding;
import ae.oleapp.models.ContactPlayers;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.util.Functions;


public class FriendRequestMessageDialogFragment extends DialogFragment implements View.OnClickListener{
        private FragmentFriendRequestMessageDialogBinding binding;
        private ResultDialogCallback dialogCallback;


        private String name;


    public FriendRequestMessageDialogFragment() {
                // Required empty public constructor
                }
    public FriendRequestMessageDialogFragment(String name) {
        this.name = name;
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
            binding = FragmentFriendRequestMessageDialogBinding.inflate(inflater, container, false);
            View view = binding.getRoot();
            getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
                binding.etMessage.setText("Hi, I'm "+name+" Please accept my friend request!");
                binding.btnClose.setOnClickListener(this);
                binding.btnSend.setOnClickListener(this);

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
                        dialogCallback.didSubmitResult(FriendRequestMessageDialogFragment.this, binding.etMessage.getText().toString());
                    }
                    else if (v == binding.btnSend) {
                        if (binding.etMessage.getText().toString().isEmpty()){
                            Functions.showToast(getContext(),"Message Cannot Be Empty", FancyToast.ERROR);
                            return;
                        }

                        dialogCallback.didSubmitResult(FriendRequestMessageDialogFragment.this, binding.etMessage.getText().toString());
                    }

                }


        public interface ResultDialogCallback {
            void didSubmitResult(DialogFragment df, String message);
        }



}