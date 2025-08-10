package ae.oleapp.owner;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityRankBinding;
import ae.oleapp.fragments.OlePadelRankFragment;
import ae.oleapp.fragments.OleRankFragment;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;

public class OleRankActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityRankBinding binding;
    private final OleRankFragment oleRankFragment = new OleRankFragment();
    private boolean isPadel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
            if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                setTheme(R.style.LoginTheme);
            }
            else {
                setTheme(R.style.AppThemePlayer);
            }
        }
        else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        binding = OleactivityRankBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.rank);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isPadel = bundle.getBoolean("is_padel", false);
        }

        if (isPadel) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.container, new OlePadelRankFragment(), "PadelRankFragment");
            transaction.commit();
            binding.bar.setVisibility(View.GONE);
            makeStatusbarTransperant();
        }
        else {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.container, oleRankFragment, "RankFragment");
            transaction.commit();
        }

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.calendarBtn.setOnClickListener(this);
        binding.titleBar.sepVu.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.calendarBtn) {
            calendarClicked();
        }
    }

    private void calendarClicked() {
        oleRankFragment.calendarClicked();
    }
}
