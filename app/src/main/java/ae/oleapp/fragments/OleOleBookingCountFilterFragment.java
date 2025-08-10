package ae.oleapp.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.base.BaseFragment;

import ae.oleapp.databinding.OlefragmentBookingCountFilterBinding;
import ae.oleapp.dialogs.OleClubListDialogFragment;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.Club;
import ae.oleapp.models.Field;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Functions;

public class OleOleBookingCountFilterFragment extends BaseFragment implements View.OnClickListener {

    private OlefragmentBookingCountFilterBinding binding;
    private String from = "", to = "", clubId = "", paymentType = "";
    private BookingCountFilterFragmentCallBack fragmentCallBack;
    private boolean isClubFilterVisible = false;

    public OleOleBookingCountFilterFragment() {
        // Required empty public constructor
    }

    public OleOleBookingCountFilterFragment(String from, String to, String clubId, boolean isClubFilterVisible, String paymentType) {
        this.from = from;
        this.to = to;
        this.clubId = clubId;
        this.isClubFilterVisible = isClubFilterVisible;
        this.paymentType = paymentType;
    }

    public void setFragmentCallBack(BookingCountFilterFragmentCallBack fragmentCallBack) {
        this.fragmentCallBack = fragmentCallBack;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentBookingCountFilterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.etFromDate.setText(from);
        binding.etToDate.setText(to);
        Club club = findClub(clubId);
        if (club != null) {
            binding.etClub.setText(club.getName());
        }
        if (paymentType.equalsIgnoreCase("cash")) {
            binding.etPaymentMethod.setText(R.string.cash);
        }
        else if (paymentType.equalsIgnoreCase("card")) {
            binding.etPaymentMethod.setText(R.string.card);
        }
        else {
            binding.etPaymentMethod.setText("");
        }

        if (isClubFilterVisible) {
            binding.relClub.setVisibility(View.VISIBLE);
            binding.relPaymentMethod.setVisibility(View.VISIBLE);
        }
        else {
            binding.relClub.setVisibility(View.GONE);
            binding.relPaymentMethod.setVisibility(View.GONE);
        }

        binding.relWeek.setOnClickListener(this);
        binding.relThisMonth.setOnClickListener(this);
        binding.relLastMonth.setOnClickListener(this);
        binding.etFromDate.setOnClickListener(this);
        binding.etToDate.setOnClickListener(this);
        binding.etClub.setOnClickListener(this);
        binding.etPaymentMethod.setOnClickListener(this);
        binding.btnApply.setOnClickListener(this);
        binding.btnReset.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private Club findClub(String id) {
        Club c = null;
        for (Club club : AppManager.getInstance().clubs) {
            if (club.getId().equalsIgnoreCase(id)) {
                c = club;
                break;
            }
        }
        return c;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.relWeek) {
            weekClicked();
        }
        else if (v == binding.relThisMonth) {
            thisMonthClicked();
        }
        else if (v == binding.relLastMonth) {
            lastMonthClicked();
        }
        else if (v == binding.etFromDate) {
            fromDateClicked();
        }
        else if (v == binding.etToDate) {
            toDateClicked();
        }
        else if (v == binding.etClub) {
            clubClicked();
        }
        else if (v == binding.btnApply) {
            applyClicked();
        }
        else if (v == binding.btnReset) {
            resetClicked();
        }
        else if (v == binding.etPaymentMethod) {
            paymentMethodClicked();
        }
    }

    private void paymentMethodClicked() {
        List<OleSelectionList> oleSelectionList = new ArrayList<>();
        oleSelectionList.add(new OleSelectionList("", getString(R.string.all)));
        oleSelectionList.add(new OleSelectionList("cash", getString(R.string.cash)));
        oleSelectionList.add(new OleSelectionList("card", getString(R.string.card)));
        OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.payment_method), false);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<OleSelectionList> selectedItems) {
                OleSelectionList item = selectedItems.get(0);
                binding.etPaymentMethod.setText(item.getValue());
                paymentType = item.getId();
            }
        });
        dialog.show();
    }

    private void weekClicked() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        to = dateFormat.format(date);
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        from = dateFormat.format(calendar.getTime());
        fragmentCallBack.getFilters(from, to, clubId, paymentType);
    }

    private void thisMonthClicked() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        from = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        to = dateFormat.format(calendar.getTime());
        fragmentCallBack.getFilters(from, to, clubId, paymentType);
    }

    private void lastMonthClicked() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        from = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        to = dateFormat.format(calendar.getTime());
        fragmentCallBack.getFilters(from, to, clubId, paymentType);
    }

    private void fromDateClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                binding.etFromDate.setText(formatter.format(calendar.getTime()));
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    private void toDateClicked() {
        if (binding.etFromDate.getText().toString().equalsIgnoreCase("")) {
            Functions.showToast(getContext(), getString(R.string.select_from_date), FancyToast.ERROR);
            return;
        }
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                binding.etToDate.setText(formatter.format(calendar.getTime()));
            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    private void clubClicked() {
        if (AppManager.getInstance().clubs.size() > 0) {
            openPopup();
        }
    }

    private void openPopup() {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        Fragment prev = getChildFragmentManager().findFragmentByTag("ClubListDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        List<Club> clubs = new ArrayList<>();
        clubs.addAll(AppManager.getInstance().clubs);
        OleClubListDialogFragment dialogFragment = new OleClubListDialogFragment(clubs, null, false);
        dialogFragment.setFragmentCallback(new OleClubListDialogFragment.ClubListDialogFragmentCallback() {
            @Override
            public void didSelectClub(Club club) {
                clubId = club.getId();
                binding.etClub.setText(club.getName());
            }

            @Override
            public void didSelectField(Field field) {

            }
        });
        dialogFragment.show(fragmentTransaction, "ClubListDialogFragment");
    }

    private void applyClicked() {
        fragmentCallBack.getFilters(binding.etFromDate.getText().toString(), binding.etToDate.getText().toString(), clubId, paymentType);
    }

    private void resetClicked() {
        from = "";
        to = "";
        clubId = "";
        paymentType = "";
        fragmentCallBack.getFilters(from, to, clubId, paymentType);
    }

    public interface BookingCountFilterFragmentCallBack {
        void getFilters(String from, String to, String clubId, String paymentType);
    }
}