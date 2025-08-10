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
import ae.oleapp.databinding.OleactivityPadelMatchShareBinding;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.UserInfo;

public class OlePadelMatchShareActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPadelMatchShareBinding binding;
    private UserInfo playerOne;
    private OlePlayerInfo playerOnePartner;
    private UserInfo playerTwo;
    private OlePlayerInfo playerTwoPartner;
    private String clubName = "", date = "", time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPadelMatchShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.match);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubName = bundle.getString("club_name", "");
            date = bundle.getString("date", "");
            time = bundle.getString("time", "");
            Gson gson = new Gson();
            playerOne = gson.fromJson(bundle.getString("player_one", ""), UserInfo.class);
            playerOnePartner = gson.fromJson(bundle.getString("player_one_partner", ""), OlePlayerInfo.class);
            if (bundle.containsKey("player_two")) {
                playerTwo = gson.fromJson(bundle.getString("player_two", ""), UserInfo.class);
            }
            if (bundle.containsKey("player_two_partner")) {
                playerTwoPartner = gson.fromJson(bundle.getString("player_two_partner", ""), OlePlayerInfo.class);
            }
        }

        populateData();

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
    }

    private void populateData() {
        Glide.with(getContext()).load(playerOne.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.p1ImgVu);
        binding.tvP1Name.setText(String.format(" %s ", playerOne.getNickName()));
        Glide.with(getContext()).load(playerOnePartner.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.p1PartnerImgVu);
        binding.tvP1PartnerName.setText(String.format(" %s ", playerOnePartner.getNickName()));
        binding.tvP1Skill.setText(playerOne.getSkillLevel());
        if (playerTwo != null) {
            binding.tvOpponentQMark.setVisibility(View.INVISIBLE);
            binding.opponentImgVu.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(playerTwo.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.opponentImgVu);
            binding.tvOpponentName.setText(String.format(" %s ", playerTwo.getNickName()));
            if (playerTwo.getLevel() != null && !playerTwo.getLevel().isEmpty() && !playerTwo.getLevel().getValue().equalsIgnoreCase("")) {
                binding.tvOpponentLevel.setVisibility(View.VISIBLE);
                binding.tvOpponentLevel.setText(String.format("LV: %s", playerTwo.getLevel().getValue()));
            }
            else {
                binding.tvOpponentLevel.setVisibility(View.INVISIBLE);
            }
            binding.tvOpponentSkill.setText(playerTwo.getSkillLevel());
        }
        else {
            binding.tvOpponentQMark.setVisibility(View.VISIBLE);
            binding.opponentImgVu.setVisibility(View.INVISIBLE);
            binding.tvOpponentName.setText(getString(R.string.join_now));
            binding.tvOpponentLevel.setVisibility(View.INVISIBLE);
            binding.tvOpponentSkill.setText("");
        }
        if (playerTwoPartner != null) {
            binding.tvOpponentPartnerQMark.setVisibility(View.INVISIBLE);
            binding.opponentPartnerImgVu.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(playerTwoPartner.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.opponentPartnerImgVu);
            binding.tvOpponentPartnerName.setText(String.format(" %s ", playerTwoPartner.getNickName()));
            if (playerTwoPartner.getLevel() != null && !playerTwoPartner.getLevel().isEmpty() && !playerTwoPartner.getLevel().getValue().equalsIgnoreCase("")) {
                binding.tvOpponentPartnerLevel.setVisibility(View.VISIBLE);
                binding.tvOpponentPartnerLevel.setText(String.format("LV: %s", playerTwoPartner.getLevel().getValue()));
            }
            else {
                binding.tvOpponentPartnerLevel.setVisibility(View.INVISIBLE);
            }
        }
        else {
            binding.tvOpponentPartnerQMark.setVisibility(View.VISIBLE);
            binding.opponentPartnerImgVu.setVisibility(View.INVISIBLE);
            binding.tvOpponentPartnerName.setText(getString(R.string.join_now));
            binding.tvOpponentPartnerLevel.setVisibility(View.INVISIBLE);
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
        if (playerOnePartner.getLevel() != null && !playerOnePartner.getLevel().isEmpty() && !playerOnePartner.getLevel().getValue().equalsIgnoreCase("")) {
            binding.tvP1PartnerLevel.setVisibility(View.VISIBLE);
            binding.tvP1PartnerLevel.setText(String.format("LV: %s", playerOnePartner.getLevel().getValue()));
        }
        else {
            binding.tvP1PartnerLevel.setVisibility(View.INVISIBLE);
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