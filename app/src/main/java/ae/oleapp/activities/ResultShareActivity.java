package ae.oleapp.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityResultShareBinding;
import ae.oleapp.models.GameHistory;
import ae.oleapp.util.Functions;

public class ResultShareActivity extends BaseActivity implements View.OnClickListener {

    private ActivityResultShareBinding binding;
    private GameHistory result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            result = new Gson().fromJson(bundle.getString("result", ""), GameHistory.class);
        }

        populateData();

        binding.btnClose.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnClose) {
            finish();
        }
        else if (view == binding.btnShare) {
            shareClicked();
        }
    }

    private void shareClicked() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                Bitmap bitmap = getBitmapFromView(binding.shareVu, null);
                try {
                    Uri uri = saveBitmap(getContext(), bitmap);

                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*");
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share"));
                }
                catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Functions.showToast(getContext(), "Install app first!", FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void populateData() {
        binding.tvGroupName.setText(result.getGroupName());
        binding.tvDate.setText(String.format("%s, %s", result.getGameDate(), result.getGameTime()));
        binding.tvTeamA.setText(result.getTeamA().getTeamAName());
        binding.tvTeamB.setText(result.getTeamB().getTeamBName());
        binding.tvTeamAP.setText(result.getTeamAPlayer().getNickName());
        binding.tvTeamBP.setText(result.getTeamBPlayer().getNickName());
        binding.tvP1Player.setText(result.getTeamAPlayer().getNickName());
        binding.tvP2Player.setText(result.getTeamBPlayer().getNickName());
        Glide.with(getContext()).load(result.getTeamAPlayer().getEmojiUrl()).into(binding.emojiImgVuP1);
        Glide.with(getContext()).load(result.getTeamBPlayer().getEmojiUrl()).into(binding.emojiImgVuP2);
        Glide.with(getContext()).load(result.getTeamAPlayer().getBibUrl()).into(binding.shirtImgVuP1);
        Glide.with(getContext()).load(result.getTeamBPlayer().getBibUrl()).into(binding.shirtImgVuP2);
        if (result.getTeamAPlayer().getIsCaptain().equalsIgnoreCase("1")) {
            binding.teamACaptain.setVisibility(View.VISIBLE);
        }
        else {
            binding.teamACaptain.setVisibility(View.INVISIBLE);
        }
        if (result.getTeamBPlayer().getIsCaptain().equalsIgnoreCase("1")) {
            binding.teamBCaptain.setVisibility(View.VISIBLE);
        }
        else {
            binding.teamBCaptain.setVisibility(View.INVISIBLE);
        }

        if (result.getTeamA().getStatus().equalsIgnoreCase("WON")) {
            binding.teamAWin.setVisibility(View.VISIBLE);
            binding.teamAWinCup.setVisibility(View.VISIBLE);
            binding.teamAVu.setAlpha(1.0f);
            binding.tvP1Score.setText(R.string.win);
        }
        else if (result.getTeamA().getStatus().equalsIgnoreCase("LOST")) {
            binding.teamAWin.setVisibility(View.INVISIBLE);
            binding.teamAWinCup.setVisibility(View.GONE);
            binding.teamAVu.setAlpha(0.5f);
            binding.tvP1Score.setText(R.string.lost);
        }
        else if (result.getTeamA().getStatus().equalsIgnoreCase("DRAW")) {
            binding.teamAWin.setVisibility(View.INVISIBLE);
            binding.teamAWinCup.setVisibility(View.GONE);
            binding.teamAVu.setAlpha(1.0f);
            binding.tvP1Score.setText(R.string.draw);
        }

        if (result.getTeamB().getStatus().equalsIgnoreCase("WON")) {
            binding.teamBWin.setVisibility(View.VISIBLE);
            binding.teamBWinCup.setVisibility(View.VISIBLE);
            binding.teamBVu.setAlpha(1.0f);
            binding.tvP2Score.setText(R.string.win);
        }
        else if (result.getTeamB().getStatus().equalsIgnoreCase("LOST")) {
            binding.teamBWin.setVisibility(View.INVISIBLE);
            binding.teamBWinCup.setVisibility(View.GONE);
            binding.teamBVu.setAlpha(0.5f);
            binding.tvP2Score.setText(R.string.lost);
        }
        else if (result.getTeamB().getStatus().equalsIgnoreCase("DRAW")) {
            binding.teamBWin.setVisibility(View.INVISIBLE);
            binding.teamBWinCup.setVisibility(View.GONE);
            binding.teamBVu.setAlpha(1.0f);
            binding.tvP2Score.setText(R.string.draw);
        }
    }
}