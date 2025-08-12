package ae.oleapp.signup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.shashank.sony.fancytoastlib.FancyToast;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;

import ae.oleapp.databinding.OleactivityUserTypeBinding;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;

public class UserTypeActivity extends BaseActivity implements View.OnClickListener {

    private String userType = "", userModule = "";
    private OleactivityUserTypeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityUserTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        makeStatusbarTransperant();

        userModule = Functions.getPrefValue(getContext(), Constants.kUserModule);

        binding.btnBack.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
        binding.relPlayer.setOnClickListener(this);
        binding.relOwner.setOnClickListener(this);
        //binding.relReferee.setOnClickListener(this);
        if (!userModule.equalsIgnoreCase("all")){
            binding.relOwner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack) {
            finish();
        }
        else if (v == binding.btnNext) {
            nextClicked();
        }
        else if (v == binding.relPlayer) {
            playerClicked();
        }
        else if (v == binding.relOwner) {
            ownerClicked();
        }
//        else if (v == binding.relReferee) {
//            refereeClicked();
//        }
    }

    private void nextClicked() {
        if (userType.equalsIgnoreCase(Constants.kPlayerType)) {
            Intent intent = new Intent(this, PlayerSignupActivity.class);
            intent.putExtra("is_referee", false);
            startActivity(intent);
        }
//        else if (userType.equalsIgnoreCase(Constants.kRefereeType)) {
//            Functions.showToast(getContext(), getString(R.string.coming_soon), FancyToast.SUCCESS);
////            Intent intent = new Intent(this, PlayerSignupActivity.class);
////            intent.putExtra("is_referee", true);
////            startActivity(intent);
//        }
        else if (userType.equalsIgnoreCase(Constants.kOwnerType)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.club_owner))
                    .setMessage(getResources().getString(R.string.owner_signup_desc))
                    .setPositiveButton(getResources().getString(R.string.continue_), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getContext(), OwnerSignupActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            builder.show();
        }
    }

    private void playerClicked() {
        userType = Constants.kPlayerType;
        binding.imgPlayer.setImageResource(R.drawable.player_active);
       // binding.imgReferee.setImageResource(R.drawable.referee_inactive);
        binding.imgOwner.setImageResource(R.drawable.owner_inactive);
        binding.relPlayer.setBackgroundResource(R.drawable.user_type_selected);
       // binding.relReferee.setBackgroundResource(R.drawable.user_type_unselected);
        binding.relOwner.setBackgroundResource(R.drawable.user_type_unselected);
        binding.tvPlayer.setTextColor(getResources().getColor(R.color.blueColorNew));
       // binding.tvReferee.setTextColor(getResources().getColor(R.color.darkTextColor));
        binding.tvOwner.setTextColor(getResources().getColor(R.color.darkTextColor));
        binding.btnNext.setAlpha(1.0f);
    }

    private void refereeClicked() {
        userType = Constants.kRefereeType;
        binding.imgPlayer.setImageResource(R.drawable.player_inactive);
        //binding.imgReferee.setImageResource(R.drawable.referee_active);
        binding.imgOwner.setImageResource(R.drawable.owner_inactive);
        binding.relPlayer.setBackgroundResource(R.drawable.user_type_unselected);
        //binding.relReferee.setBackgroundResource(R.drawable.user_type_selected);
        binding.relOwner.setBackgroundResource(R.drawable.user_type_unselected);
        binding.tvPlayer.setTextColor(getResources().getColor(R.color.darkTextColor));
       //binding.tvReferee.setTextColor(getResources().getColor(R.color.blueColorNew));
        binding.tvOwner.setTextColor(getResources().getColor(R.color.darkTextColor));
        binding.btnNext.setAlpha(1.0f);
    }

    private void ownerClicked() {
        userType = Constants.kOwnerType;
        binding.imgPlayer.setImageResource(R.drawable.player_inactive);
        //binding.imgReferee.setImageResource(R.drawable.referee_inactive);
        binding.imgOwner.setImageResource(R.drawable.owner_active);
        binding.relPlayer.setBackgroundResource(R.drawable.user_type_unselected);
        //binding.relReferee.setBackgroundResource(R.drawable.user_type_unselected);
        binding.relOwner.setBackgroundResource(R.drawable.user_type_selected);
        binding.tvPlayer.setTextColor(getResources().getColor(R.color.darkTextColor));
        //binding.tvReferee.setTextColor(getResources().getColor(R.color.darkTextColor));
        binding.tvOwner.setTextColor(getResources().getColor(R.color.blueColorNew));
        binding.btnNext.setAlpha(1.0f);
    }
}
