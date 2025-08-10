package ae.oleapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import androidx.annotation.NonNull;
import ae.oleapp.databinding.CustomAlertDialogBinding;

public class OleCustomAlertDialog extends Dialog {

    private CustomAlertDialogBinding binding;
    private OnDismiss onDismiss;
    private final Context context;
    private String title = "";
    private String desc = "";

    public OleCustomAlertDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public OleCustomAlertDialog(Context context, String title, String desc) {
        super(context);
        this.context = context;
        this.title = title;
        this.desc = desc;
    }

    public void setOnDismiss(OnDismiss onDismiss) {
        this.onDismiss = onDismiss;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = CustomAlertDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.tvTitle.setText(title);
        binding.tvDesc.setText(desc);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (onDismiss != null) {
            onDismiss.dismiss();
        }
    }

    public interface OnDismiss {
        void dismiss();
    }
}
