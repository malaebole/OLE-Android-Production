package ae.oleapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import ae.oleapp.databinding.OleblockReasonDialogBinding;


public class OleBlockReasonDialog extends Dialog {

    private OleblockReasonDialogBinding binding;
    private final Context context;
    private BlockReasonDialogCallback dialogCallback;

    public void setDialogCallback(BlockReasonDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public OleBlockReasonDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleblockReasonDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitClicked();
            }
        });
    }

    private void submitClicked() {
        dialogCallback.enteredReason(binding.etReason.getText().toString());
        dismiss();
    }

    public interface BlockReasonDialogCallback {
        void enteredReason(String reason);
    }
}
