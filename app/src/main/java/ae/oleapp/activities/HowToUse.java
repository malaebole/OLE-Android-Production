package ae.oleapp.activities;

import android.os.Bundle;
import android.view.View;

import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityWebVuBinding;

public class HowToUse extends BaseActivity {

    private ActivityWebVuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebVuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

//        binding.webVu.getSettings().setJavaScriptEnabled(true);
//        if (Functions.getAppLangStr(getContext()).equalsIgnoreCase("ar")) {
//            binding.webVu.loadUrl(Constants.BASE_URL+"lineup/how_to_use/ar");
//        }
//        else {
//            binding.webVu.loadUrl(Constants.BASE_URL+"lineup/how_to_use");
//        }

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}