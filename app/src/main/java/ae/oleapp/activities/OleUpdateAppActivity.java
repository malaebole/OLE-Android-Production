package ae.oleapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.shashank.sony.fancytoastlib.FancyToast;

import ae.oleapp.BuildConfig;
import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityUpdateAppBinding;
import ae.oleapp.util.Functions;

public class OleUpdateAppActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityUpdateAppBinding binding;
    private String version = "";
    private String forceUpdate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityUpdateAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bgVuColor));// set status background white

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            version = bundle.getString("version", "");
            forceUpdate = bundle.getString("force_update", "");
        }

        binding.btnUpdate.setVisibility(View.GONE);
        binding.btnNotNow.setVisibility(View.GONE);

        new Handler().postDelayed(this::inAppUpdates, 1000);


        binding.btnUpdate.setOnClickListener(this);
        binding.btnNotNow.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (version.equalsIgnoreCase(BuildConfig.VERSION_NAME)) {
            finish();
        }
    }

    @Override
    protected void finishActivityIfNecessary() {
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnUpdate) {
//            updateClicked();
        } else if (v == binding.btnNotNow) {
            notNowClicked();
        }
    }

    private void updateClicked() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void notNowClicked() {
        if (forceUpdate.equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.major_changes_in_app), FancyToast.ERROR);
//            inAppUpdates(true);
        } else {
            finish();
        }
    }
}
