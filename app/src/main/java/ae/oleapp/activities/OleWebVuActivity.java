package ae.oleapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityWebVuBinding;
import ae.oleapp.databinding.OleactivityWebVuBinding;
import ae.oleapp.util.Functions;

@SuppressLint("SetJavaScriptEnabled")
public class OleWebVuActivity extends BaseActivity {

    private OleactivityWebVuBinding binding;
    private String loadUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityWebVuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.term_condition);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            loadUrl = bundle.getString("url", "");
        }

        binding.webVu.getSettings().setJavaScriptEnabled(true);
        if (loadUrl.equalsIgnoreCase("tc")){
            if (Functions.getAppLangStr(getContext()).equalsIgnoreCase("ar")) {
                binding.webVu.loadUrl("https://www.ole-sports.com/terms/ar");
            }
            else {
                binding.webVu.loadUrl("https://www.ole-sports.com/terms");
            }
        }
        else if (loadUrl.equalsIgnoreCase("pp")){
            binding.webVu.loadUrl("https://ole-sports.com/privacy-policy");
        }

        binding.bar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
