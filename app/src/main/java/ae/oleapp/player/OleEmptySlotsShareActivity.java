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
import ae.oleapp.adapters.OleEmptySlotShareAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityEmptySlotsShareBinding;
import ae.oleapp.models.OleBookingSlot;
import ae.oleapp.models.Club;
import ae.oleapp.util.Constants;

public class OleEmptySlotsShareActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityEmptySlotsShareBinding binding;
    private Club club;
    private List<OleBookingSlot> slotList = new ArrayList<>();
    private String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityEmptySlotsShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.empty_slots);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            club = gson.fromJson(bundle.getString("club", ""), Club.class);
            slotList = gson.fromJson(bundle.getString("slots", ""), new TypeToken<List<OleBookingSlot>>(){}.getType());
            date = bundle.getString("date");
        }

        populateData();

        boolean isPadel = club.getClubType().equalsIgnoreCase(Constants.kPadelModule);

        binding.recyclerVu.setLayoutManager(new GridLayoutManager(getContext(), 3));
        OleEmptySlotShareAdapter adapter = new OleEmptySlotShareAdapter(getContext(), slotList, isPadel);
        binding.recyclerVu.setAdapter(adapter);

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
        if (!club.getCoverPath().isEmpty()) {
            Glide.with(getApplicationContext()).load(club.getCoverPath()).into(binding.imgVu);
        }
        binding.tvName.setText(club.getName());
        binding.tvLoc.setText(club.getCity().getName());
        if (club.getStartPrice().isEmpty()) {
            binding.tvPrice.setText(String.format("0 %s", club.getCurrency()));
        }
        else {
            binding.tvPrice.setText(String.format("%s %s", club.getStartPrice(), club.getCurrency()));
        }
        if (club.getFavoriteCount().isEmpty()) {
            binding.tvFavCount.setText("0");
        }
        else {
            binding.tvFavCount.setText(club.getFavoriteCount());
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date dt = dateFormat.parse(date);
            dateFormat.applyPattern("EEEE");
            String day = dateFormat.format(dt);
            dateFormat.applyPattern("dd/MM/yyyy");
            String dateStr = dateFormat.format(dt);
            binding.tvDate.setText(String.format("%s\n%s", day, dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
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