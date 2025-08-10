package ae.oleapp.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.actionsheet.ActionSheet;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleBookingFieldAdapter;
import ae.oleapp.adapters.OleBookingSlotAdapter;
import ae.oleapp.adapters.WaitingUserAdapter;
import ae.oleapp.databinding.FragmentAddWaitingUserDialogBinding;
import ae.oleapp.databinding.FragmentShowWaitingUsersListDialogBinding;
import ae.oleapp.models.BookingWaitingList;
import ae.oleapp.models.OleBookingSlot;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class showWaitingUsersListDialogFragment extends DialogFragment implements View.OnClickListener{

    private FragmentShowWaitingUsersListDialogBinding binding;
    private ResultDialogCallback dialogCallback;
    private String bookingId;
    private OleBookingSlot oleBookingSlot;
    private WaitingUserAdapter adapter;
    private final List<BookingWaitingList> bookingWaitingLists = new ArrayList<>();



    public showWaitingUsersListDialogFragment() {


    }

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public showWaitingUsersListDialogFragment(OleBookingSlot oleBookingSlot) {
        this.oleBookingSlot = oleBookingSlot;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShowWaitingUsersListDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        bookingWaitingLists.clear();
        for (int i =0; i < oleBookingSlot.getWaitingList().size(); i++){
            bookingWaitingLists.add(oleBookingSlot.getWaitingList().get(i));
        }

        if (bookingWaitingLists.isEmpty()){
            binding.empty.setVisibility(View.VISIBLE);
        }
        else{
            binding.empty.setVisibility(View.GONE);
        }


        LinearLayoutManager slotLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(slotLayoutManager);
        adapter = new WaitingUserAdapter(getContext(), bookingWaitingLists, oleBookingSlot);
        adapter.setItemClickListener(itemClickListner);
        binding.recyclerVu.setAdapter(adapter);

        binding.btnClose.setOnClickListener(this);


        return view;
    }

    WaitingUserAdapter.ItemClickListener itemClickListner = new WaitingUserAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

        }
        @Override
        public void phoneClicked(View view, int pos) {
            makeCall(bookingWaitingLists.get(pos).getUserPhone());
        }
        @Override
        public void bookingClicked(View view, int pos) {
            dialogCallback.didSubmitResult(showWaitingUsersListDialogFragment.this,"booking","", bookingWaitingLists.get(pos).getUserName(), bookingWaitingLists.get(pos).getUserPhone());
        }
        @Override
        public void deleteClicked(View view, int pos) {
            removeWaitingUser(pos);
        }
    };


    private void removeWaitingUser(int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.remove_waiting_title))
                .setMessage(getString(R.string.remove_waiting_user))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogCallback.didSubmitResult(showWaitingUsersListDialogFragment.this,"remove", bookingWaitingLists.get(pos).getId(), "","");
                        bookingWaitingLists.remove(pos);
                        adapter.notifyDataSetChanged();
                        dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    public void makeCall(String phone) {
        Intent callIntent = new Intent(Intent.ACTION_VIEW);
        callIntent.setData(Uri.parse("tel:" + phone));
        startActivity(callIntent);
    }



    @Override
    public void onClick(View v) {

        if (v == binding.btnClose){
            dismiss();
        }

    }


    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, String type, String id, String name, String phone);
    }
}