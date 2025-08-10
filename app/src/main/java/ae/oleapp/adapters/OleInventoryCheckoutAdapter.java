package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.OleInventoryItem;

public class OleInventoryCheckoutAdapter extends RecyclerView.Adapter<OleInventoryCheckoutAdapter.ViewHolder> {

    private final Context context;
    private final List<OleInventoryItem> list;
    private boolean isFromOrder = false;

    public OleInventoryCheckoutAdapter(Context context, List<OleInventoryItem> list) {
        this.context = context;
        this.list = list;
        this.isFromOrder = false;
    }

    public OleInventoryCheckoutAdapter(Context context, List<OleInventoryItem> list, boolean isFromOrder) {
        this.context = context;
        this.list = list;
        this.isFromOrder = isFromOrder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleinventory_checkout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleInventoryItem item = list.get(position);
        holder.tvName.setText(item.getName());
        if (isFromOrder) {
            holder.tvQty.setText(String.valueOf(item.getQuantity()));
            holder.tvPrice.setText(String.format("%s %s", item.getSalePrice(), item.getCurrency()));
        }
        else {
            holder.tvQty.setText(String.valueOf(item.getQty()));
            double price = Double.parseDouble(item.getSalePrice());
            holder.tvPrice.setText(String.format(Locale.ENGLISH, "%.2f %s", price * item.getQty(), item.getCurrency()));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvQty, tvName, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQty = itemView.findViewById(R.id.tv_qty);
        }
    }
}