package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.dialogs.OleSelectionListDialog;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OleSelectionList;
import ae.oleapp.util.Functions;

public class OleClubFacilityListAdapter extends RecyclerView.Adapter<OleClubFacilityListAdapter.ViewHolder> {

    private final Context context;
    private final List<OleClubFacility> list;
    private OnItemClickListener onItemClickListener;
    public List<OleClubFacility> selectedFacility = new ArrayList<>();
    private int currentSelectedPosition = -1;

    public OleClubFacilityListAdapter(Context context, List<OleClubFacility> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setCurrentSelectedPosition(int currentSelectedPosition) {
        this.currentSelectedPosition = currentSelectedPosition;
        notifyDataSetChanged();
    }

    public int isExistInSelected(String facId) {
        int index = -1;
        for (int i = 0; i < selectedFacility.size(); i++) {
            if (selectedFacility.get(i).getId().equalsIgnoreCase(facId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleclub_facility_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleClubFacility facility = list.get(position);
        holder.tvTitle.setText(facility.getName());
        if (facility.getPrice().equalsIgnoreCase("")) {
            holder.tvPrice.setText("");
        } else {
            holder.tvPrice.setText(String.format("%s %s", facility.getPrice(), facility.getCurrency()));
        }

        int index = isExistInSelected(facility.getId());
        if (index == -1) {
            holder.mySwitch.setChecked(false);
            Glide.with(context).load(facility.getIcon()).into(holder.imgVu);
            holder.tvPrice.setText("");
            if (currentSelectedPosition == position) {
                showBtmVu(holder);
            }
            else {
                holder.btmVu.setVisibility(View.GONE);
            }
        }
        else {
            holder.btmVu.setVisibility(View.GONE);
            facility = selectedFacility.get(index);
            holder.mySwitch.setChecked(true);
            Glide.with(context).load(facility.getActiveIcon()).into(holder.imgVu);
            if (facility.getPrice().equalsIgnoreCase("")) {
                holder.tvPrice.setText(R.string.free);
            }
            else {
                holder.tvPrice.setText(String.format("%s %s", facility.getPrice(), facility.getCurrency()));
            }
        }

        if (holder.tvPrice.getText().toString().isEmpty()) {
            holder.tvPrice.setVisibility(View.GONE);
        }
        else {
            holder.tvPrice.setVisibility(View.VISIBLE);
        }

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
                }
            }
        });
    }

    private void showBtmVu(ViewHolder holder) {
        holder.btmVu.setVisibility(View.VISIBLE);
        holder.freeVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.isFree = true;
                holder.freeImgVu.setImageResource(R.drawable.check);
                holder.paidImgVu.setImageResource(R.drawable.uncheck);
                holder.paidDetailVu.setVisibility(View.GONE);
            }
        });
        holder.paidVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.isFree = false;
                holder.freeImgVu.setImageResource(R.drawable.uncheck);
                holder.paidImgVu.setImageResource(R.drawable.check);
                holder.paidDetailVu.setVisibility(View.VISIBLE);
            }
        });
        holder.etType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeClicked(holder, list.get(holder.getAdapterPosition()));
            }
        });
        holder.etUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unitClicked(holder, list.get(holder.getAdapterPosition()));
            }
        });
        holder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.isFree) {
                    list.get(holder.getAdapterPosition()).setPrice("");
                    selectedFacility.add(list.get(holder.getAdapterPosition()));
                    currentSelectedPosition = -1;
                    notifyDataSetChanged();
                }
                else {
                    if (holder.etPrice.getText().toString().equalsIgnoreCase("")) {
                        Functions.showToast(context, context.getString(R.string.enter_price), FancyToast.ERROR);
                        return;
                    }
                    if (holder.etType.getText().toString().equalsIgnoreCase("")) {
                        Functions.showToast(context, context.getString(R.string.select_type), FancyToast.ERROR);
                        return;
                    }
                    if (list.get(holder.getAdapterPosition()).getType().equalsIgnoreCase("countable")) {
                        if (holder.etUnit.getText().toString().equalsIgnoreCase("")) {
                            Functions.showToast(context, context.getString(R.string.select_unit), FancyToast.ERROR);
                            return;
                        }
                        if (holder.etQty.getText().toString().equalsIgnoreCase("")) {
                            Functions.showToast(context, context.getString(R.string.enter_max_qty), FancyToast.ERROR);
                            return;
                        }
                    }
                    list.get(holder.getAdapterPosition()).setPrice(holder.etPrice.getText().toString());
                    list.get(holder.getAdapterPosition()).setMaxQuantity(holder.etQty.getText().toString());
                    selectedFacility.add(list.get(holder.getAdapterPosition()));
                    currentSelectedPosition = -1;
                    notifyDataSetChanged();
                }
            }
        });
    }

    private void typeClicked(ViewHolder holder, OleClubFacility facility) {
        List<OleSelectionList> oleSelectionList = Arrays.asList(new OleSelectionList("fixed", context.getString(R.string.fixed)), new OleSelectionList("countable", context.getString(R.string.selectable)));
        OleSelectionListDialog dialog = new OleSelectionListDialog(context, context.getString(R.string.select_type), false);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<OleSelectionList> selectedItems) {
                OleSelectionList selectedItem = selectedItems.get(0);
                facility.setType(selectedItem.getId());
                holder.etType.setText(selectedItem.getValue());
                if (selectedItem.getId().equalsIgnoreCase("fixed")) {
                    holder.relQty.setVisibility(View.GONE);
                    holder.relUnit.setVisibility(View.GONE);
                }
                else {
                    holder.relQty.setVisibility(View.VISIBLE);
                    holder.relUnit.setVisibility(View.VISIBLE);
                }
            }
        });
        dialog.show();
    }

    private void unitClicked(ViewHolder holder, OleClubFacility facility) {
        List<OleSelectionList> oleSelectionList = Arrays.asList(new OleSelectionList("qty", context.getString(R.string.per_item)), new OleSelectionList("box", context.getString(R.string.box)));
        OleSelectionListDialog dialog = new OleSelectionListDialog(context, context.getString(R.string.select_unit), false);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<OleSelectionList> selectedItems) {
                OleSelectionList selectedItem = selectedItems.get(0);
                facility.setUnit(selectedItem.getId());
                holder.etUnit.setText(selectedItem.getValue());
            }
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgVu, freeImgVu, paidImgVu;
        TextView tvTitle, tvPrice;
        LinearLayout relMain, paidDetailVu;
        SwitchCompat mySwitch;
        CardView btmVu;
        MaterialCardView freeVu, paidVu, btnSave;
        EditText etPrice, etType, etUnit, etQty;
        TextView tvCurrency;
        RelativeLayout relUnit, relQty;
        boolean isFree = false;

        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPrice = itemView.findViewById(R.id.tv_price);
            relMain = itemView.findViewById(R.id.main);
            mySwitch = itemView.findViewById(R.id.my_switch);
            mySwitch.setClickable(false);

            btmVu = itemView.findViewById(R.id.btm_vu);
            freeVu = itemView.findViewById(R.id.free_vu);
            paidVu = itemView.findViewById(R.id.paid_vu);
            freeImgVu = itemView.findViewById(R.id.img_free);
            paidImgVu = itemView.findViewById(R.id.img_paid);
            paidDetailVu = itemView.findViewById(R.id.paid_detail);
            btnSave = itemView.findViewById(R.id.btn_save);
            etPrice = itemView.findViewById(R.id.et_price);
            etType = itemView.findViewById(R.id.et_type);
            etUnit = itemView.findViewById(R.id.et_unit);
            etQty = itemView.findViewById(R.id.et_qty);
            tvCurrency = itemView.findViewById(R.id.tv_currency);
            relUnit = itemView.findViewById(R.id.rel_unit);
            relQty = itemView.findViewById(R.id.rel_qty);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}