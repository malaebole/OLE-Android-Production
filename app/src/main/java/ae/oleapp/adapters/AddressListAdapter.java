package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.ShopAddress;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ViewHolder> {

    private final Context context;
    private List<ShopAddress> list;
    private ItemClickListener itemClickListener;
    private int selectedIndex = -1;
    private boolean isPickup = false;

    public AddressListAdapter(Context context, List<ShopAddress> list, boolean isPickup) {
        this.context = context;
        this.list = list;
        this.isPickup = isPickup;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    public void setDatasource(List<ShopAddress> list, boolean isPickup) {
        this.list = list;
        this.isPickup = isPickup;
        this.selectedIndex = -1;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShopAddress address = list.get(position);
        holder.tvName.setText(address.getName());
        holder.tvPhone.setText(address.getPhone());
        holder.tvAddress.setText(String.format("%s - %s - %s", address.getAddress(), address.getArea(), address.getCity()));
        if (address.getIsHome().equalsIgnoreCase("1")) {
            holder.tvFlat.setText(context.getString(R.string.flat_no_place, address.getHouseNo()));
        }
        else {
            holder.tvFlat.setText(context.getString(R.string.office_no_place, address.getOfficeNo()));
        }
        if (selectedIndex == position) {
            holder.imgVuCheck.setImageResource(R.drawable.checkl);
        }
        else {
            holder.imgVuCheck.setImageResource(R.drawable.uncheckl);
        }

        if (isPickup) {
            holder.btnVu.setVisibility(View.GONE);
        }
        else {
            holder.btnVu.setVisibility(View.VISIBLE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getAdapterPosition());
            }
        });
        holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.deleteClicked(v, holder.getAdapterPosition());
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.editClicked(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvPhone, tvAddress, tvFlat;
        CardView layout;
        ImageView imgVuCheck;
        CardView btnDel, btnEdit;
        LinearLayout btnVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVuCheck = itemView.findViewById(R.id.img_vu_check);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvFlat = itemView.findViewById(R.id.tv_flat_no);
            tvAddress = itemView.findViewById(R.id.tv_address);
            layout = itemView.findViewById(R.id.main_layout);
            btnDel = itemView.findViewById(R.id.btn_del);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnVu = itemView.findViewById(R.id.linear);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void deleteClicked(View view, int pos);
        void editClicked(View view, int pos);
    }
}