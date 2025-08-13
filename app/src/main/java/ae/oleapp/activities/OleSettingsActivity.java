package ae.oleapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shashank.sony.fancytoastlib.FancyToast;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivitySettingsBinding;
import ae.oleapp.dialogs.OleLanguageDialog;
import ae.oleapp.dialogs.OleUpdatePassDialog;
import ae.oleapp.owner.OleEmployeeListActivity;
import ae.oleapp.owner.OleOwnerMainTabsActivity;
import ae.oleapp.owner.OleUserRolesActivity;
import ae.oleapp.player.OlePlayerMainTabsActivity;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;

public class OleSettingsActivity extends BaseActivity implements OleLanguageDialog.LanguageCallbacks, View.OnClickListener {

    private OleactivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
            setTheme(R.style.AppThemePlayer);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        binding = OleactivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        applyEdgeToEdge(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.settings);

        if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
            binding.relBlockUser.setVisibility(View.GONE);
            binding.relEmployee.setVisibility(View.GONE);
            binding.relUserRole.setVisibility(View.GONE);
            binding.relBookingDatesLimit.setVisibility(View.GONE);
        }

        setLabelText(Functions.getAppLangStr(getContext()));

        binding.bar.backBtn.setOnClickListener(this);
        binding.relBlockUser.setOnClickListener(this);
        binding.relEmployee.setOnClickListener(this);
        binding.relUserRole.setOnClickListener(this);
        binding.relBookingDatesLimit.setOnClickListener(this);
        binding.relPass.setOnClickListener(this);
        binding.relLang.setOnClickListener(this);
        binding.relTc.setOnClickListener(this);
        binding.relContact.setOnClickListener(this);

    }

    private void setLabelText(String lang) {
        if (lang.equalsIgnoreCase("ar")) {
            binding.tvLang.setText("العربية");

        } else {
            binding.tvLang.setText("English");
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        } else if (v == binding.relBlockUser) {
            blockClicked();
        } else if (v == binding.relEmployee) {
            employeeClicked();
        } else if (v == binding.relUserRole) {
            userRolesClicked();
        } else if (v == binding.relPass) {
            passClicked();
        } else if (v == binding.relLang) {
            langClicked();
        } else if (v == binding.relBookingDatesLimit) {
            startActivity(new Intent(getContext(), OleBookingDatesLimitActivity.class));
        } else if (v == binding.relTc) {
            startActivity(new Intent(getContext(), OleWebVuActivity.class));
        } else if (v == binding.relContact) {
            startActivity(new Intent(getContext(), OleContactUsActivity.class));
        }
    }

    private void blockClicked() {
        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        startActivity(new Intent(getContext(), OleBlockedUsersActivity.class));
    }

    private void employeeClicked() {
        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        startActivity(new Intent(getContext(), OleEmployeeListActivity.class));
    }

    private void userRolesClicked() {
        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        startActivity(new Intent(getContext(), OleUserRolesActivity.class));
    }

    private void passClicked() {
        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        OleUpdatePassDialog oleUpdatePassDialog = new OleUpdatePassDialog(getContext());
        oleUpdatePassDialog.setCanceledOnTouchOutside(false);
        oleUpdatePassDialog.show();
    }

    private void langClicked() {
        OleLanguageDialog oleLanguageDialog = new OleLanguageDialog(getContext());
        oleLanguageDialog.setLanguageCallbacks(this);
        oleLanguageDialog.show();
    }

    @Override
    public void selectLanguage(OleLanguageDialog dialog, String lang) {
        dialog.dismiss();
        if (Functions.getAppLangStr(getContext()).equalsIgnoreCase(lang)) {
            return;
        }
        setLabelText(lang);
        if (lang.equalsIgnoreCase("ar")) {
            Functions.setAppLangAr(getContext(), "ar");
        } else {
            Functions.setAppLangAr(getContext(), "en");
        }
        Functions.setAppLang(getContext(), lang);
        Functions.changeLanguage(getContext(), lang);
        sendAppLangApi();
        if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
            Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            finishAffinity();
            finish();
            startActivity(intent);
        } else if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
            Intent intent = new Intent(getContext(), OleOwnerMainTabsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            finishAffinity();
            finish();
            startActivity(intent);
        }
    }
}
