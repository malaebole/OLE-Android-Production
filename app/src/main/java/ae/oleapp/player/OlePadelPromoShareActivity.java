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
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityPadelPromoShareBinding;
import ae.oleapp.models.OlePromoCode;

public class OlePadelPromoShareActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPadelPromoShareBinding binding;
    private OlePromoCode olePromoCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPadelPromoShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.promo_code);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            olePromoCode = new Gson().fromJson(bundle.getString("promo", ""), OlePromoCode.class);
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
        binding.tvClubName.setText(olePromoCode.getClubName());
        double oneHalfHourPrice = Double.parseDouble(olePromoCode.getOneHalfHourPrice());
        double twoHourPrice = Double.parseDouble(olePromoCode.getTwoHourPrice());
        if (olePromoCode.getDiscountType().equalsIgnoreCase("amount")) {
            double oneHalfHourD = Double.parseDouble(olePromoCode.getOneHalfHourDiscount());
            double twoHourD = Double.parseDouble(olePromoCode.getTwoHourDiscount());
            binding.tvOneHalfHour.setText(String.format(Locale.ENGLISH, "%.0f %s", oneHalfHourPrice - oneHalfHourD, olePromoCode.getCurrency()));
            binding.tvTwoHour.setText(String.format(Locale.ENGLISH, "%.0f %s", twoHourPrice - twoHourD, olePromoCode.getCurrency()));
        }
        else {
            double discount = Double.parseDouble(olePromoCode.getDiscount());
            binding.tvOneHalfHour.setText(String.format(Locale.ENGLISH, "%.0f %s", oneHalfHourPrice - ((discount / 100) * oneHalfHourPrice), olePromoCode.getCurrency()));
            binding.tvTwoHour.setText(String.format(Locale.ENGLISH, "%.0f %s", twoHourPrice - ((discount / 100) * twoHourPrice), olePromoCode.getCurrency()));
        }
        binding.tvPromo.setText(olePromoCode.getCouponCode());
        if (olePromoCode.getPromoImage().equalsIgnoreCase("")) {
            binding.imgVuBanner.setVisibility(View.GONE);
        }
        else {
            binding.imgVuBanner.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(olePromoCode.getPromoImage()).into(binding.imgVuBanner);
        }
        binding.tvDate.setText(getResources().getString(R.string.valid_till_place, olePromoCode.getExpiry()));
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