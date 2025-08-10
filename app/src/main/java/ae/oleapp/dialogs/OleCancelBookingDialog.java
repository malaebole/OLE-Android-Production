package ae.oleapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;

import ae.oleapp.databinding.OlecancelBookingDialogBinding;

public class OleCancelBookingDialog extends Dialog {

    private OlecancelBookingDialogBinding binding;
    private final Context context;
    private CancelBookingDialogCallback dialogCallback;

    public void setDialogCallback(CancelBookingDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public OleCancelBookingDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OlecancelBookingDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelClicked();
            }
        });
    }

    private void cancelClicked() {
        dialogCallback.enteredNote(binding.etNote.getText().toString());
        dismiss();
    }

    public interface CancelBookingDialogCallback {
        void enteredNote(String note);
    }
}
