package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ae.oleapp.R;

import ae.oleapp.databinding.OlefragmentReservationDetailDialogBinding;
import ae.oleapp.models.OleReservationDetail;

public class OleReservationDetailDialogFragment extends DialogFragment {

    private OleReservationDetail oleReservationDetail;
    private OlefragmentReservationDetailDialogBinding binding;

    public OleReservationDetailDialogFragment() {
        // Required empty public constructor
    }

    public OleReservationDetailDialogFragment(OleReservationDetail oleReservationDetail) {
        this.oleReservationDetail = oleReservationDetail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentReservationDetailDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        populateData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void populateData() {
        binding.tvBookingBy.setText(oleReservationDetail.getBookingBy());
        binding.tvBookingDate.setText(oleReservationDetail.getBookingAdded());
        binding.tvCompleteBy.setText(oleReservationDetail.getCompletedBy());
        binding.tvCompleteDate.setText(oleReservationDetail.getCompletedAt());
        if (!oleReservationDetail.getPlayerConfirmed().isEmpty()) {
            binding.tvConfirmBy.setText(R.string.player);
            binding.tvConfirmDate.setText(oleReservationDetail.getPlayerConfirmed());
        }
        if (!oleReservationDetail.getOwnerConfirmed().isEmpty()) {
            binding.tvConfirmBy.setText(R.string.owner);
            binding.tvConfirmDate.setText(oleReservationDetail.getOwnerConfirmed());
        }
        if (!oleReservationDetail.getEmployeeConfirmedAt().isEmpty()) {
            binding.tvConfirmBy.setText(oleReservationDetail.getConfirmedEmployeeName());
            binding.tvConfirmDate.setText(oleReservationDetail.getEmployeeConfirmedAt());
        }
        if (!oleReservationDetail.getPlayerCanceled().isEmpty()) {
            binding.tvCancelBy.setText(R.string.player);
            binding.tvCancelDate.setText(oleReservationDetail.getPlayerCanceled());
        }
        if (!oleReservationDetail.getOwnerCanceled().isEmpty()) {
            binding.tvCancelBy.setText(R.string.owner);
            binding.tvCancelDate.setText(oleReservationDetail.getOwnerCanceled());
        }
        if (!oleReservationDetail.getEmployeeCanceledAt().isEmpty()) {
            binding.tvCancelBy.setText(oleReservationDetail.getCanceledEmployeeName());
            binding.tvCancelDate.setText(oleReservationDetail.getEmployeeCanceledAt());
        }
        if (binding.tvConfirmBy.getText().toString().isEmpty() || binding.tvConfirmDate.getText().toString().isEmpty()) {
            binding.confirmVu.setVisibility(View.GONE);
        }
        if (binding.tvCompleteBy.getText().toString().isEmpty() || binding.tvCompleteDate.getText().toString().isEmpty()) {
            binding.completeVu.setVisibility(View.GONE);
        }
        if (binding.tvCancelBy.getText().toString().isEmpty() || binding.tvCancelDate.getText().toString().isEmpty()) {
            binding.cancelVu.setVisibility(View.GONE);
        }
    }
}