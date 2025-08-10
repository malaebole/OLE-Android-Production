package ae.oleapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import androidx.annotation.NonNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ae.oleapp.databinding.OlebookingDoneDialogBinding;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;

public class OleBookingDoneDialog extends Dialog implements View.OnClickListener {

    private OlebookingDoneDialogBinding binding;
    private BookingDialogCallback dialogCallback;
    private String clubName;
    private String date;
    private String time;
    private String bookingId;

    public OleBookingDoneDialog(@NonNull Context context) {
        super(context);
    }

    public OleBookingDoneDialog(@NonNull Context context, String clubName, String date, String time, String bookingId) {
        super(context);
        this.clubName = clubName;
        this.date = date;
        this.time = time;
        this.bookingId = bookingId;
    }

    public void setDialogCallback(BookingDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = OlebookingDoneDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.tvClubName.setText(clubName);
        binding.tvTime.setText(time);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date dt = dateFormat.parse(date);
            dateFormat.applyPattern("EEE, dd/MM/yyyy");
            binding.tvDate.setText(dateFormat.format(dt));
        } catch (ParseException e) {
            binding.tvDate.setText("");
            e.printStackTrace();
        }

        if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            binding.btnFormation.setVisibility(View.GONE);
        }

        binding.btnClose.setOnClickListener(this);
        binding.btnCreate.setOnClickListener(this);
        binding.btnBookingList.setOnClickListener(this);
        binding.btnFormation.setOnClickListener(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dialogCallback.onDismiss();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnCreate) {
            createClicked();
        }
        else if (v == binding.btnBookingList) {
            bookingListClicked();
        }
        else if (v == binding.btnClose) {
            btnCloseClicked();
        }
        else if (v == binding.btnFormation) {
            dialogCallback.formationClicked(bookingId);
            dismiss();
        }
    }

    private void createClicked() {
        dialogCallback.createMatchClicked(bookingId);
        dismiss();
    }

    private void bookingListClicked() {
        dialogCallback.bookingListClicked();
        dismiss();
    }

    private void btnCloseClicked() {
        dismiss();
    }

    public interface BookingDialogCallback {
        void onDismiss();
        void createMatchClicked(String bookingId);
        void bookingListClicked();
        void formationClicked(String bookingId);
    }
}
