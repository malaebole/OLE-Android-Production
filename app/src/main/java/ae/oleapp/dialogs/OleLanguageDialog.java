package ae.oleapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;

import ae.oleapp.databinding.OlelanguageDialogBinding;


public class OleLanguageDialog extends Dialog implements View.OnClickListener {

    private final Context context;
    private LanguageCallbacks languageCallbacks;
    private OlelanguageDialogBinding binding;


    public void setLanguageCallbacks(LanguageCallbacks languageCallbacks) {
        this.languageCallbacks = languageCallbacks;
    }

    public OleLanguageDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OlelanguageDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.btnAr.setOnClickListener(this);
        binding.btnEn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnAr) {
            languageCallbacks.selectLanguage(this, "ar");
        }
        else if (v == binding.btnEn) {
            languageCallbacks.selectLanguage(this, "en");
        }
    }

    public interface LanguageCallbacks {
        void selectLanguage(OleLanguageDialog dialog, String lang);
    }
}
