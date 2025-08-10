package ae.oleapp.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kaopiz.kprogresshud.KProgressHUD;

import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityFullImageBinding;
import ae.oleapp.util.Functions;

public class OleFullImageActivity extends BaseActivity {

    private OleactivityFullImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityFullImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String urlStr = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            urlStr = bundle.getString("URL", "");
            KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
            Glide.with(this).load(urlStr).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Functions.hideLoader(hud);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    Functions.hideLoader(hud);
                    return false;
                }
            }).into(binding.imageView);
        }

        binding.imageView.setOnTouchListener(new ImageMatrixTouchHandler(getContext()));

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
