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
import ae.oleapp.databinding.OleactivityFootballMatchShareBinding;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.UserInfo;

public class OleFootballMatchShareActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityFootballMatchShareBinding binding;
    private UserInfo playerOne;
    private OlePlayerInfo playerTwo;
    private String clubName = "", date = "", time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityFootballMatchShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.match);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubName = bundle.getString("club_name", "");
            date = bundle.getString("date", "");
            time = bundle.getString("time", "");
            Gson gson = new Gson();
            playerOne = gson.fromJson(bundle.getString("player_one", ""), UserInfo.class);
            if (bundle.containsKey("player_two")) {
                playerTwo = gson.fromJson(bundle.getString("player_two", ""), OlePlayerInfo.class);
            }
        }

        populateData();

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
    }

    private void populateData() {
        Glide.with(getContext()).load(playerOne.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.p1ImgVu);
        binding.tvP1Name.setText(String.format(" %s ", playerOne.getNickName()));
        if (playerTwo != null) {
            binding.tvQMark.setVisibility(View.INVISIBLE);
            binding.p2ImgVu.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(playerTwo.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.p2ImgVu);
            binding.tvP2Name.setText(String.format(" %s ", playerTwo.getNickName()));
            if (playerTwo.getLevel() != null && !playerTwo.getLevel().isEmpty() && !playerTwo.getLevel().getValue().equalsIgnoreCase("")) {
                binding.tvP2Level.setVisibility(View.VISIBLE);
                binding.tvP2Level.setText(String.format("LV: %s", playerTwo.getLevel().getValue()));
            }
            else {
                binding.tvP2Level.setVisibility(View.INVISIBLE);
            }
        }
        else {
            binding.tvQMark.setVisibility(View.VISIBLE);
            binding.p2ImgVu.setVisibility(View.INVISIBLE);
            binding.tvP2Name.setText(String.format("  %s  ", getString(R.string.join_now)));
            binding.tvP2Level.setVisibility(View.INVISIBLE);
        }
        binding.tvClubName.setText(clubName);
        binding.tvTime.setText(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date dt = dateFormat.parse(date);
            dateFormat.applyPattern("EEEE, dd/MM/yyyy");
            binding.tvDate.setText(dateFormat.format(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (playerOne.getLevel() != null && !playerOne.getLevel().isEmpty() && !playerOne.getLevel().getValue().equalsIgnoreCase("")) {
            binding.tvP1Level.setVisibility(View.VISIBLE);
            binding.tvP1Level.setText(String.format("LV: %s", playerOne.getLevel().getValue()));
        }
        else {
            binding.tvP1Level.setVisibility(View.INVISIBLE);
        }
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
//                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "FootballMatch", null);
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