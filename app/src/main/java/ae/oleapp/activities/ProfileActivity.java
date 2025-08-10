package ae.oleapp.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityProfileBinding;
import ae.oleapp.dialogs.AddPlayerFragment;
import ae.oleapp.dialogs.LinkPlayerDialogFragment;
import ae.oleapp.models.FollowRequestModel;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.models.UserInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    private ActivityProfileBinding binding;
    private PlayerInfo playerInfo;
    private FollowRequestModel followRequestModel;
    private String playerId = "", friendShipId = "";
    private String dotPosition = "";
    private String freindId = "";
    private boolean isTeam = false, isUpdateDone = false, isCaptain = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            playerId = bundle.getString("player_id", "");
            friendShipId = bundle.getString("friendship_id", "");
            isTeam = bundle.getBoolean("is_team", false);
            isCaptain = bundle.getBoolean("is_captain", false);
            dotPosition = bundle.getString("dotPosition","");
            freindId = bundle.getString("friendId","");


        }
        if (dotPosition.equalsIgnoreCase("")){
            dotPosition = Functions.getCurrentPage(getContext(),Constants.kCurrentPage);
        }

        binding.winProgressbar.setThumbImage(R.drawable.profile_win_thumb, 70);
        binding.skillsProgressbar.setThumbImage(R.drawable.profile_skills_thumbl, 70);
        binding.winProgressbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        binding.skillsProgressbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        binding.btnEdit.setVisibility(View.INVISIBLE);
        binding.btnLink.setVisibility(View.GONE);
        binding.btnDelete.setVisibility(View.GONE);
        if (playerId.isEmpty()) {
            playerId = Functions.getPrefValue(getContext(), Constants.kUserID);
        }

        getProfileAPI(true);

        binding.btnClose.setOnClickListener(this);
        binding.btnReview.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.btnLink.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);
        binding.btnFollow.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (isUpdateDone) {
            Intent intent = new Intent();
            intent.putExtra("is_team", isTeam);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnClose) {
            onBackPressed();
        }
        else if (view == binding.btnEdit) {
            showEditPlayerDialog();
        }
        else if (view == binding.btnReview) {
            if (playerInfo != null) {
                Intent intent = new Intent(getContext(), PlayerReviewsActivity.class);
                intent.putExtra("player_id", playerInfo.getId());
                startActivity(intent);
            }
        }
        else if (view == binding.btnLink) {
            if (playerInfo != null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("LinkPlayerDialogFragment");
                if (fragment != null) {
                    fragmentTransaction.remove(fragment);
                }
                fragmentTransaction.addToBackStack(null);
                LinkPlayerDialogFragment dialogFragment = new LinkPlayerDialogFragment(playerInfo.getId(), playerInfo.getPhone(), playerInfo.getCountryCode());
                dialogFragment.show(fragmentTransaction, "LinkPlayerDialogFragment");
            }
        }
        else if (view == binding.btnDelete) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.delete))
                        .setMessage(getResources().getString(R.string.do_you_want_delete_player))
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (!freindId.isEmpty() && !freindId.equalsIgnoreCase( Functions.getPrefValue(getContext(),Constants.kUserID))){
                                    deletePlayerAPI(true, freindId, friendShipId); //remove code
                                    Functions.setCurrentPage(getContext(),"0");
                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    startActivity(intent);
                                }else{
                                    deletePlayerAPI(true, playerInfo.getId(), playerInfo.getFriendShipId());
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                builder.show();

        }
        else if (view == binding.btnFollow){
            playSoundFromAssets("followtap.wav");
            binding.btnFollowed.setVisibility(View.VISIBLE);
            binding.btnFollow.setVisibility(View.GONE);
            addOtherFriendsToMyList(true);
        }
    }
    protected void playSoundFromAssets(String fileName) {
        try {
            AssetFileDescriptor afd = getAssets().openFd(fileName);
            final MediaPlayer mp = new MediaPlayer();


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mp.setDataSource(afd);
            } else {
                FileDescriptor fd = afd.getFileDescriptor();
                mp.setDataSource(fd, afd.getStartOffset(), afd.getLength());
            }
            afd.close();
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void showEditPlayerDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("AddPlayerFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        AddPlayerFragment playerFragment = new AddPlayerFragment(playerInfo, isTeam);
        playerFragment.setDialogCallback(new AddPlayerFragment.AddPlayerDialogCallback() {
            @Override
            public void didAddPlayer(PlayerInfo userInfo) {

            }

            @Override
            public void didUpdatePlayer(PlayerInfo userInfo) {
                isUpdateDone = true;
                getProfileAPI(true);
            }

            @Override
            public void didDeletePlayer(String id) {
                Intent intent = new Intent();
                intent.putExtra("is_team", isTeam);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        playerFragment.show(fragmentTransaction, "AddPlayerFragment");
    }

    private void populateData() {
        Glide.with(getContext()).load(playerInfo.getEmojiUrl()).into(binding.emojiImgVu);
        Glide.with(getContext()).load(playerInfo.getBibUrl()).into(binding.shirtImgVu);
        binding.tvName.setText(playerInfo.getNickName());
        binding.tvLost.setText(playerInfo.getMatchLoss());
        binding.tvWin.setText(playerInfo.getMatchWon());
        binding.tvDraw.setText(playerInfo.getMatchDrawn());
        binding.tvPlayed.setText(playerInfo.getMatchPlayed());
        binding.tvReview.setText(String.format("%s %s", playerInfo.getReviews(), getString(R.string.reviews)));
        if (playerInfo.getWinPercentage().equalsIgnoreCase("0")) {
            binding.winProgressbar.setProgress(0);
            binding.tvWinPerc.setText("0%");
        }
        else {
            binding.winProgressbar.setProgress(Integer.parseInt(playerInfo.getWinPercentage()));
            binding.tvWinPerc.setText(String.format("%s%%", playerInfo.getWinPercentage()));
        }
        if (playerInfo.getRatingData().getPlayingLevel().equalsIgnoreCase("0")) {
            binding.skillsProgressbar.setProgress(0);
            binding.tvPlayingSkills.setText("0%");
        }
        else {
            binding.skillsProgressbar.setProgress(Integer.parseInt(playerInfo.getRatingData().getPlayingLevel()));
            binding.tvPlayingSkills.setText(String.format("%s%%", playerInfo.getRatingData().getPlayingLevel()));
        }
        binding.tvBeforeTime.setText(playerInfo.getRatingData().getBeforTime());
        binding.tvOnTime.setText(playerInfo.getRatingData().getOnTime());
        binding.tvLate.setText(playerInfo.getRatingData().getLate());
        binding.tvNotCome.setText(playerInfo.getRatingData().getNotCome());

        if (playerInfo.getIsLink().equalsIgnoreCase("1")) {
            binding.btnLink.setVisibility(View.GONE);
        }
        else {
            binding.btnLink.setVisibility(View.VISIBLE);
        }
        if (!playerInfo.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
            binding.btnLink.setVisibility(View.GONE);
        }

//        if (dotPosition.equalsIgnoreCase("") || dotPosition.equalsIgnoreCase("0")) {
//            binding.btnLink.setVisibility(View.VISIBLE); //checkx
//            binding.btnEdit.setVisibility(View.VISIBLE); //checkx
//        }else{
//            binding.btnLink.setVisibility(View.GONE);
//            binding.btnEdit.setVisibility(View.GONE);
//            binding.btnDelete.setVisibility(View.GONE);
//        }

        binding.btnDelete.setVisibility(View.GONE);
//        if (!playerInfo.getIsLink().equalsIgnoreCase("1") || playerInfo.getId().equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
//            if (isCaptain) {
//                binding.btnEdit.setVisibility(View.INVISIBLE);
//            }
//            else if (dotPosition.equalsIgnoreCase("") || dotPosition.equalsIgnoreCase("0")){
//                binding.btnEdit.setVisibility(View.VISIBLE);
//                binding.btnLink.setVisibility(View.GONE);
//            }
//        }
//        else {
//            binding.btnEdit.setVisibility(View.INVISIBLE);
//            if (dotPosition.equalsIgnoreCase("") || dotPosition.equalsIgnoreCase("0")){
//                binding.btnDelete.setVisibility(View.VISIBLE);
//            }
//
//        }

        if (playerInfo !=null){
            if (dotPosition.equalsIgnoreCase("0") || dotPosition.equalsIgnoreCase("")){
                if (playerInfo.getFreindshipStatus().equalsIgnoreCase("linked")){
                    binding.btnFollowed.setVisibility(View.GONE);
                    binding.btnDelete.setVisibility(View.VISIBLE);
                    binding.btnFollow.setVisibility(View.VISIBLE);
                    binding.btnEdit.setVisibility(View.GONE);
                    binding.btnLink.setVisibility(View.GONE);

                } else if (playerInfo.getFreindshipStatus().equalsIgnoreCase("followed")){
                    binding.btnFollowed.setVisibility(View.VISIBLE);
                    binding.btnDelete.setVisibility(View.VISIBLE);
                    binding.btnFollow.setVisibility(View.GONE);
                    binding.btnEdit.setVisibility(View.GONE);
                    binding.btnLink.setVisibility(View.GONE);

                } else if (playerInfo.getFreindshipStatus().equalsIgnoreCase("unlinked")){ //manual player
                    binding.btnFollowed.setVisibility(View.GONE);
                    binding.btnFollow.setVisibility(View.GONE);
                    binding.btnDelete.setVisibility(View.GONE);
                    binding.btnEdit.setVisibility(View.VISIBLE);
                    binding.btnLink.setVisibility(View.VISIBLE);

                } if (playerId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
                    binding.btnEdit.setVisibility(View.VISIBLE);
                    binding.btnFollowed.setVisibility(View.GONE);
                    binding.btnFollow.setVisibility(View.GONE);
                    binding.btnDelete.setVisibility(View.GONE);
                    binding.btnLink.setVisibility(View.GONE);
                }
            }else{
                if (playerInfo.getFreindshipStatus().equalsIgnoreCase("linked")){
                    binding.btnFollow.setVisibility(View.VISIBLE);
                    binding.btnFollowed.setVisibility(View.GONE);
                    binding.btnDelete.setVisibility(View.GONE);
                    binding.btnEdit.setVisibility(View.GONE);
                    binding.btnLink.setVisibility(View.GONE);

                } else if (playerInfo.getFreindshipStatus().equalsIgnoreCase("followed")){
                    binding.btnFollowed.setVisibility(View.VISIBLE);
                    binding.btnDelete.setVisibility(View.GONE);
                    binding.btnFollow.setVisibility(View.GONE);
                    binding.btnEdit.setVisibility(View.GONE);
                    binding.btnLink.setVisibility(View.GONE);

                } else if (playerInfo.getFreindshipStatus().equalsIgnoreCase("unlinked")){ //manual player
                    binding.btnFollowed.setVisibility(View.GONE);
                    binding.btnFollow.setVisibility(View.GONE);
                    binding.btnDelete.setVisibility(View.GONE);
                    binding.btnEdit.setVisibility(View.GONE);
                    binding.btnLink.setVisibility(View.GONE);

                } if (playerId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
                    binding.btnEdit.setVisibility(View.GONE);
                    binding.btnFollowed.setVisibility(View.GONE);
                    binding.btnFollow.setVisibility(View.GONE);
//                    binding.btnDelete.setVisibility(View.VISIBLE);
                    binding.btnLink.setVisibility(View.GONE);
                }
                //Hide delete button from captain scenario
          }
        }

//        if (playerId.length() > 3){
//            binding.btnFollowed.setVisibility(View.GONE);
//            binding.btnFollow.setVisibility(View.GONE);
//        }
//            if (freindId.equalsIgnoreCase(Functions.getPrefValue(getContext(),Constants.kUserID))){
//                if (playerInfo.getIsLink().equalsIgnoreCase("0")) {
//                    binding.btnLink.setVisibility(View.VISIBLE);
//                } else if (playerInfo.getIsLink().equalsIgnoreCase("1")) {
//                    binding.btnLink.setVisibility(View.GONE);
//                }
//            }
//            if (playerId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
//                binding.btnDelete.setVisibility(View.VISIBLE);
//                binding.btnFollow.setVisibility(View.GONE);
//                binding.btnFollowed.setVisibility(View.GONE);
//            }
//
//        if (playerId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) && dotPosition.equalsIgnoreCase("0") || dotPosition.equalsIgnoreCase("")){
//            binding.btnDelete.setVisibility(View.GONE);
//            binding.btnLink.setVisibility(View.GONE);
//            if (playerInfo.getIsLink().equalsIgnoreCase("0")) {
//                binding.btnLink.setVisibility(View.VISIBLE);
//            } else if (playerInfo.getIsLink().equalsIgnoreCase("1")) {
//                binding.btnLink.setVisibility(View.GONE);
//            }
//
//        } else if (!playerId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) &&  dotPosition.equalsIgnoreCase("0") || dotPosition.equalsIgnoreCase("")){
//            binding.btnDelete.setVisibility(View.VISIBLE);
//            if (playerInfo.getIsLink().equalsIgnoreCase("0")) {
//                binding.btnDelete.setVisibility(View.GONE);
//            } else if (playerInfo.getIsLink().equalsIgnoreCase("1")) {
//                binding.btnDelete.setVisibility(View.VISIBLE);
//            }
//        }
//        String kCurrentpage = Functions.getCurrentPage(getContext(), Constants.kCurrentPage);
//
//        if (!kCurrentpage.equalsIgnoreCase("")){
//            binding.btnDelete.setVisibility(View.VISIBLE);
//            if (playerId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) || !freindId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID)) ){
//                binding.btnDelete.setVisibility(View.GONE);
//                if (!dotPosition.equalsIgnoreCase("0") && playerId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
//                    binding.btnDelete.setVisibility(View.VISIBLE);
//                }
//                if (dotPosition.equalsIgnoreCase("0") || dotPosition.equalsIgnoreCase("") && playerId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
//                    binding.btnDelete.setVisibility(View.GONE);
//                }
//            }
//        }else{
//          if (playerId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))){
//              binding.btnDelete.setVisibility(View.GONE);
//          }else if (playerId.length() > 3) {
//              binding.btnDelete.setVisibility(View.GONE);
//          }else{
//              binding.btnDelete.setVisibility(View.VISIBLE);
//          }
//
//        }

    }

    private void getProfileAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getUserProfile(Functions.getAppLang(getContext()), playerId,friendShipId, Functions.getPrefValue(getContext(),Constants.kAppModule));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Gson gson = new Gson();
                            playerInfo = gson.fromJson(object.getJSONObject(Constants.kData).toString(), PlayerInfo.class);
                            if (playerId.equalsIgnoreCase(Functions.getPrefValue(getContext(), Constants.kUserID))) {
                                UserInfo userInfo = gson.fromJson(object.getJSONObject(Constants.kData).toString(), UserInfo.class);
                                Functions.saveUserinfo(getContext(), userInfo);
                            }
                            populateData();

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

    private void addOtherFriendsToMyList(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addOtherFriendsToMyList(Functions.getAppLang(getContext()),  Functions.getPrefValue(getContext(), Constants.kUserID), playerId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Gson gson = new Gson();
                            followRequestModel = gson.fromJson(object.getJSONObject(Constants.kData).toString(), FollowRequestModel.class);
                        }
                        else {
                            binding.btnFollow.setVisibility(View.GONE);
                            binding.btnFollowed.setVisibility(View.GONE);
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR); //checkx
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

    private void deletePlayerAPI(boolean isLoader, String id, String friendShipId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deletePlayer(Functions.getAppLang(getContext()),  Functions.getPrefValue(getContext(), Constants.kUserID), id, friendShipId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            Intent intent = new Intent();
                            intent.putExtra("is_team", isTeam);
                            setResult(RESULT_OK, intent);
                            //finish();
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