package ae.oleapp.player;

import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleFriendlyGameShareAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityFriendlyGameShareBinding;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.models.UserInfo;

public class OleFriendlyGameShareActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityFriendlyGameShareBinding binding;
    private UserInfo playerOne;
    private List<OlePlayerInfo> playerList = new ArrayList<>();
    private String clubName = "", date = "", time = "";
    private int reqPlayers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityFriendlyGameShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.friendly_game);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubName = bundle.getString("club_name", "");
            date = bundle.getString("date", "");
            time = bundle.getString("time", "");
            reqPlayers = bundle.getInt("req_players", 0);
            Gson gson = new Gson();
            playerOne = gson.fromJson(bundle.getString("player_one", ""), UserInfo.class);
            playerList = gson.fromJson(bundle.getString("player_list", ""), new TypeToken<List<OlePlayerInfo>>() {}.getType());
        }

        binding.recyclerVu.setLayoutManager(new GridLayoutManager(getContext(), 6));

        populateData();

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);

    }

    private void populateData() {
        Glide.with(getApplicationContext()).load(playerOne.getPhotoUrl()).placeholder(R.drawable.player_active).into(binding.p1ImgVu);
        binding.tvP1Name.setText(String.format(" %s ", playerOne.getNickName()));
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

        OleFriendlyGameShareAdapter adapter = new OleFriendlyGameShareAdapter(getContext(), playerList, reqPlayers);
        binding.recyclerVu.setAdapter(adapter);
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