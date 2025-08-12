package ae.oleapp.player;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityFootballResultShareBinding;
import ae.oleapp.models.OleMatchResults;
import ae.oleapp.models.OlePlayerInfo;

public class OleFootballResultShareActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityFootballResultShareBinding binding;
    private OleMatchResults result;
    private String clubName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityFootballResultShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.match_result);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            result = gson.fromJson(bundle.getString("result", ""), OleMatchResults.class);
            clubName = bundle.getString("club_name", "");
        }

        populateData();

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnShare) {
            shareClicked();
        }
    }

    private void populateData() {
        binding.tvClubName.setText(clubName);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date dt = dateFormat.parse(result.getMatchDate());
            dateFormat.applyPattern("EEEE, dd/MM/yyyy");
            binding.tvDate.setText(dateFormat.format(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        OlePlayerInfo playerOne = result.getPlayerOne();
        Glide.with(getApplicationContext()).load(playerOne.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.p1ImgVu);
        binding.tvP1Name.setText(String.format(" %s ", playerOne.getNickName()));
        binding.tvP1Player.setText(String.format(" %s ", playerOne.getNickName()));
        binding.tvP1Score.setText(playerOne.getGoals());
        if (playerOne.getLevel() != null && !playerOne.getLevel().isEmpty() && !playerOne.getLevel().getValue().equalsIgnoreCase("")) {
            binding.tvP1Level.setVisibility(View.VISIBLE);
            binding.tvP1Level.setText(String.format("LV: %s", playerOne.getLevel().getValue()));
        }
        else {
            binding.tvP1Level.setVisibility(View.INVISIBLE);
        }
        OlePlayerInfo playerTwo = result.getPlayerTwo();
        Glide.with(getApplicationContext()).load(playerTwo.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.p2ImgVu);
        binding.tvP2Name.setText(String.format(" %s ", playerTwo.getNickName()));
        binding.tvP2Player.setText(String.format(" %s ", playerTwo.getNickName()));
        binding.tvP2Score.setText(playerTwo.getGoals());
        if (playerTwo.getLevel() != null && !playerTwo.getLevel().isEmpty() && !playerTwo.getLevel().getValue().equalsIgnoreCase("")) {
            binding.tvP2Level.setVisibility(View.VISIBLE);
            binding.tvP2Level.setText(String.format("LV: %s", playerTwo.getLevel().getValue()));
        }
        else {
            binding.tvP2Level.setVisibility(View.INVISIBLE);
        }
    }

    private void shareClicked() {
        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        }else{
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        }
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                Bitmap bitmap = getBitmapFromView(binding.shareVu);
//                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "FriendlyGame", null);
//                Uri uri = Uri.parse(path);

                try {
                    Uri uri = saveBitmap(getContext(), bitmap);

                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*");
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}