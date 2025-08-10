package ae.oleapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Arrays;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.databinding.OlefragmentAddFacilityBottomDialogBinding;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;

/**
 * A simple {@link Fragment} subclass.
 */
public class OleAddFacilityBottomDialog extends DialogFragment implements View.OnClickListener {

    private OlefragmentAddFacilityBottomDialogBinding binding;
    private AddFacilityBottomDialogCallback dialogCallback;
    private OleClubFacility facility;
    private boolean isFree = false;

    public void setDialogCallback(AddFacilityBottomDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public OleAddFacilityBottomDialog() {
        // Required empty public constructor
    }

    public OleAddFacilityBottomDialog(OleClubFacility facility) {
        this.facility = facility;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentAddFacilityBottomDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        binding.tvCurrency.setText(Functions.getPrefValue(getContext(), Constants.kCurrency));

        paidClicked();

        binding.freeVu.setOnClickListener(this);
        binding.paidVu.setOnClickListener(this);
        binding.btnDone.setOnClickListener(this);
        binding.etType.setOnClickListener(this);
        binding.etUnit.setOnClickListener(this);

        return  view;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.freeVu) {
            freeClicked();
        }
        else if (v == binding.paidVu) {
            paidClicked();
        }
        else if (v == binding.btnDone) {
            doneClicked();
        }
        else if (v == binding.etType) {
            typeClicked();
        }
        else if (v == binding.etUnit) {
            unitClicked();
        }
    }

    private void freeClicked() {
        isFree = true;
        binding.imgFree.setImageResource(R.drawable.check);
        binding.imgPaid.setImageResource(R.drawable.uncheck);
        binding.paidDetail.setVisibility(View.GONE);
    }

    private void paidClicked() {
        isFree = false;
        binding.imgFree.setImageResource(R.drawable.uncheck);
        binding.imgPaid.setImageResource(R.drawable.check);
        binding.paidDetail.setVisibility(View.VISIBLE);
    }

    private void doneClicked() {
        if (isFree) {
            facility.setPrice("");
            dialogCallback.didAddFacility(facility);
        }
        else {
            if (binding.etPrice.getText().toString().equalsIgnoreCase("")) {
                Functions.showToast(getContext(), getString(R.string.enter_price), FancyToast.ERROR);
                return;
            }
            if (binding.etType.getText().toString().equalsIgnoreCase("")) {
                Functions.showToast(getContext(), getString(R.string.select_type), FancyToast.ERROR);
                return;
            }
            if (facility.getType().equalsIgnoreCase("countable")) {
                if (binding.etUnit.getText().toString().equalsIgnoreCase("")) {
                    Functions.showToast(getContext(), getString(R.string.select_unit), FancyToast.ERROR);
                    return;
                }
                if (binding.etQty.getText().toString().equalsIgnoreCase("")) {
                    Functions.showToast(getContext(), getString(R.string.enter_max_qty), FancyToast.ERROR);
                    return;
                }
            }
            facility.setPrice(binding.etPrice.getText().toString());
            facility.setMaxQuantity(binding.etQty.getText().toString());
            dialogCallback.didAddFacility(facility);
        }
        dismiss();
    }

    private void typeClicked() {
        List<OleSelectionList> oleSelectionList = Arrays.asList(new OleSelectionList("fixed", getString(R.string.fixed)), new OleSelectionList("countable", getString(R.string.selectable)));
        OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_type), false);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<OleSelectionList> selectedItems) {
                OleSelectionList selectedItem = selectedItems.get(0);
                facility.setType(selectedItem.getId());
                binding.etType.setText(selectedItem.getValue());
                if (selectedItem.getId().equalsIgnoreCase("fixed")) {
                    binding.relQty.setVisibility(View.GONE);
                    binding.relUnit.setVisibility(View.GONE);
                }
                else {
                    binding.relQty.setVisibility(View.VISIBLE);
                    binding.relUnit.setVisibility(View.VISIBLE);
                }
            }
        });
        dialog.show();
    }

    private void unitClicked() {
        List<OleSelectionList> oleSelectionList = Arrays.asList(new OleSelectionList("qty", getString(R.string.per_item)), new OleSelectionList("box", getString(R.string.box)));
        OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_unit), false);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<OleSelectionList> selectedItems) {
                OleSelectionList selectedItem = selectedItems.get(0);
                facility.setUnit(selectedItem.getId());
                binding.etUnit.setText(selectedItem.getValue());
            }
        });
        dialog.show();
    }

    public interface AddFacilityBottomDialogCallback {
        void didAddFacility(OleClubFacility facility);
    }
}
