package ae.oleapp.dialogs;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kaopiz.kprogresshud.KProgressHUD;
import  ae.oleapp.R;
import ae.oleapp.databinding.FragmentStartLineupDialogBinding;
import ae.oleapp.models.SelectionList;
import ae.oleapp.util.AppManager;
import  ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartLineupDialogFragment extends DialogFragment implements View.OnClickListener {

    private FragmentStartLineupDialogBinding binding;
    private final List<SelectionList> playerList = new ArrayList<>();
    private String noOfPlayers = "";
    private StartLineupDialogCallback dialogCallback;

    public StartLineupDialogFragment() {
        // Required empty public constructor
    }

    public void setDialogCallback(StartLineupDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStartLineupDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        playerList.add(new SelectionList("8", "4 vs 4"));
        playerList.add(new SelectionList("10", "5 vs 5"));
        playerList.add(new SelectionList("12", "6 vs 6"));
        playerList.add(new SelectionList("14", "7 vs 7"));
        playerList.add(new SelectionList("16", "8 vs 8"));
        playerList.add(new SelectionList("18", "9 vs 9"));
        playerList.add(new SelectionList("20", "10 vs 10"));
        playerList.add(new SelectionList("22", "11 vs 11"));

        binding.btnClose.setOnClickListener(this);
        binding.btnStart.setOnClickListener(this);
        binding.etDate.setOnClickListener(this);
        binding.etTime.setOnClickListener(this);
        binding.etPlayers.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose) {
            dismiss();
        }
        else if (v == binding.btnStart) {
            if (binding.etDate.getText().toString().equalsIgnoreCase("")) {
                Functions.showToast(getContext(), getString(R.string.select_date), FancyToast.ERROR);
                return;
            }
            if (binding.etTime.getText().toString().equalsIgnoreCase("")) {
                Functions.showToast(getContext(), getString(R.string.select_time), FancyToast.ERROR);
                return;
            }
            if (noOfPlayers.equalsIgnoreCase("")) {
                Functions.showToast(getContext(), getString(R.string.select_no_of_players), FancyToast.ERROR);
                return;
            }

            startLineupAPI(true, binding.etDate.getText().toString(), binding.etTime.getText().toString(), binding.etStadiumName.getText().toString(), binding.etCityName.getText().toString());
        }
        else if (v == binding.etDate) {
            dateClicked();
        }
        else if (v == binding.etTime) {
            timeClicked();
        }
        else if (v == binding.etPlayers) {
            SelectionListDialog dialog = new SelectionListDialog(getContext(), getString(R.string.select_no_of_players), false);
            dialog.setLists(playerList);
            dialog.setOnItemSelected(new SelectionListDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<SelectionList> selectedItems) {
                    SelectionList item = selectedItems.get(0);
                    noOfPlayers = item.getId();
                    binding.etPlayers.setText(item.getValue());
                }
            });
            dialog.show();
        }
    }

    private void dateClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                binding.etDate.setText(formatter.format(calendar.getTime()));

            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
        pickerDialog.show();
    }

    private void timeClicked() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mma", Locale.ENGLISH);
                binding.etTime.setText(formatter.format(calendar.getTime()));
            }
        }, hour, minute, false);
        timePickerDialog.enableSeconds(false);
        timePickerDialog.setTimeInterval(1, 30);
        timePickerDialog.show(getChildFragmentManager(), "Datepickerdialog");
    }

    private void startLineupAPI(boolean isLoader, String date, String time, String stadiumName, String cityName) {
        this.dismiss();
        dialogCallback.didStartLineup(date, time, noOfPlayers, stadiumName, cityName);

//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.createGame(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), date, time, noOfPlayers, stadiumName, cityName);
//        call.enqueue(new Callback<>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
//                            String gameId = object.getJSONObject(Constants.kData).getString("game_id");
//                            dismiss();
//                            dialogCallback.didStartLineup(gameId, date, time, noOfPlayers, stadiumName, cityName );
//                        }
//                        else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
//                    }
//                }
//                else {
//                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
//                if (t instanceof UnknownHostException) {
//                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
//                }
//                else {
//                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                }
//            }
//        });
    }

    public interface StartLineupDialogCallback {
        void didStartLineup(String date, String time, String players, String stadiumName, String cityName);
    }
}