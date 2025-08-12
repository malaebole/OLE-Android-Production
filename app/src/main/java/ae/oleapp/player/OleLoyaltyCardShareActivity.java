package ae.oleapp.player;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.google.gson.Gson;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.IOException;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityLoyaltyCardShareBinding;
import ae.oleapp.models.OleDiscountCard;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;

public class OleLoyaltyCardShareActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityLoyaltyCardShareBinding binding;
    private OleDiscountCard oleDiscountCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= OleactivityLoyaltyCardShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.loyalty_card);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            oleDiscountCard = new Gson().fromJson(bundle.getString("card", ""), OleDiscountCard.class);
        }

        if (Functions.getAppLang(getContext()).equalsIgnoreCase(Constants.kArLang)) {
            binding.relAr.setVisibility(View.VISIBLE);
            binding.relEn.setVisibility(View.INVISIBLE);
        }
        else {
            binding.relAr.setVisibility(View.INVISIBLE);
            binding.relEn.setVisibility(View.VISIBLE);
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
        binding.tvOfferName.setText(oleDiscountCard.getTitle());
        binding.tvDiscountValue.setText(oleDiscountCard.getDiscountValue());
        if (oleDiscountCard.getDiscountType().equalsIgnoreCase("amount")) {
            binding.tvCurrency.setText(oleDiscountCard.getCurrency());
        }
        else {
            binding.tvCurrency.setText("%");
        }
        String unit = "";
        int targetBooking = Integer.parseInt(oleDiscountCard.getTargetBooking());
        if (targetBooking >= 6) {
            binding.relCircle1.setVisibility(View.VISIBLE);
            binding.relCircle2.setVisibility(View.VISIBLE);
            binding.relCircle3.setVisibility(View.VISIBLE);
            binding.relCircle4.setVisibility(View.VISIBLE);
            binding.relCircle5.setVisibility(View.VISIBLE);
            binding.relCircle6.setVisibility(View.VISIBLE);
            if (oleDiscountCard.getDiscountType().equalsIgnoreCase("amount")) {
                binding.tvDiscountAr.setText(String.format(Locale.getDefault(), "احصل على خصم %s %s على حجزك السابع", oleDiscountCard.getDiscountValue(), oleDiscountCard.getCurrency()));
            }
            else {
                binding.tvDiscountAr.setText(String.format("احصل على خصم %%%s على حجزك السابع", oleDiscountCard.getDiscountValue()));
            }
            unit = "th";
        }
        else if (targetBooking == 5) {
            binding.relCircle1.setVisibility(View.VISIBLE);
            binding.relCircle2.setVisibility(View.VISIBLE);
            binding.relCircle3.setVisibility(View.VISIBLE);
            binding.relCircle4.setVisibility(View.VISIBLE);
            binding.relCircle5.setVisibility(View.VISIBLE);
            binding.relCircle6.setVisibility(View.GONE);
            if (oleDiscountCard.getDiscountType().equalsIgnoreCase("amount")) {
                binding.tvDiscountAr.setText(String.format(Locale.getDefault(), "احصل على خصم %s %s على حجزك السادس", oleDiscountCard.getDiscountValue(), oleDiscountCard.getCurrency()));
            }
            else {
                binding.tvDiscountAr.setText(String.format("احصل على خصم %%%s على حجزك السادس", oleDiscountCard.getDiscountValue()));
            }
            unit = "th";
        }
        else if (targetBooking == 4) {
            binding.relCircle1.setVisibility(View.VISIBLE);
            binding.relCircle2.setVisibility(View.VISIBLE);
            binding.relCircle3.setVisibility(View.VISIBLE);
            binding.relCircle4.setVisibility(View.VISIBLE);
            binding.relCircle5.setVisibility(View.GONE);
            binding.relCircle6.setVisibility(View.GONE);
            if (oleDiscountCard.getDiscountType().equalsIgnoreCase("amount")) {
                binding.tvDiscountAr.setText(String.format(Locale.getDefault(), "احصل على خصم %s %s على حجزك الخامس", oleDiscountCard.getDiscountValue(), oleDiscountCard.getCurrency()));
            }
            else {
                binding.tvDiscountAr.setText(String.format("احصل على خصم %%%s على حجزك الخامس", oleDiscountCard.getDiscountValue()));
            }
            unit = "th";
        }
        else if (targetBooking == 3) {
            binding.relCircle1.setVisibility(View.VISIBLE);
            binding.relCircle2.setVisibility(View.VISIBLE);
            binding.relCircle3.setVisibility(View.VISIBLE);
            binding.relCircle4.setVisibility(View.GONE);
            binding.relCircle5.setVisibility(View.GONE);
            binding.relCircle6.setVisibility(View.GONE);
            if (oleDiscountCard.getDiscountType().equalsIgnoreCase("amount")) {
                binding.tvDiscountAr.setText(String.format(Locale.getDefault(), "احصل على خصم %s %s على حجزك الرابع", oleDiscountCard.getDiscountValue(), oleDiscountCard.getCurrency()));
            }
            else {
                binding.tvDiscountAr.setText(String.format("احصل على خصم %%%s على حجزك الرابع", oleDiscountCard.getDiscountValue()));
            }
            unit = "rd";
        }
        else if (targetBooking == 2) {
            binding.relCircle1.setVisibility(View.VISIBLE);
            binding.relCircle2.setVisibility(View.VISIBLE);
            binding.relCircle3.setVisibility(View.GONE);
            binding.relCircle4.setVisibility(View.GONE);
            binding.relCircle5.setVisibility(View.GONE);
            binding.relCircle6.setVisibility(View.GONE);
            if (oleDiscountCard.getDiscountType().equalsIgnoreCase("amount")) {
                binding.tvDiscountAr.setText(String.format(Locale.getDefault(), "احصل على خصم %s %s على حجزك الثالث", oleDiscountCard.getDiscountValue(), oleDiscountCard.getCurrency()));
            }
            else {
                binding.tvDiscountAr.setText(String.format("احصل على خصم %%%s على حجزك الثالث", oleDiscountCard.getDiscountValue()));
            }
            unit = "nd";
        }
        else if (targetBooking == 1) {
            binding.relCircle1.setVisibility(View.VISIBLE);
            binding.relCircle2.setVisibility(View.GONE);
            binding.relCircle3.setVisibility(View.GONE);
            binding.relCircle4.setVisibility(View.GONE);
            binding.relCircle5.setVisibility(View.GONE);
            binding.relCircle6.setVisibility(View.GONE);
            if (oleDiscountCard.getDiscountType().equalsIgnoreCase("amount")) {
                binding.tvDiscountAr.setText(String.format(Locale.getDefault(), "احصل على خصم %s %s على حجزك الثاني", oleDiscountCard.getDiscountValue(), oleDiscountCard.getCurrency()));
            }
            else {
                binding.tvDiscountAr.setText(String.format("احصل على خصم %%%s على حجزك الثاني", oleDiscountCard.getDiscountValue()));
            }
            unit = "st";
        }
        binding.bookingCount.setText(Html.fromHtml((targetBooking+1)+"<sup>"+unit+"</sup>"));
        binding.tvDate.setText(getString(R.string.valid_till_place, oleDiscountCard.getToDate()));
        binding.tvClubName.setText(oleDiscountCard.getClubName());
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