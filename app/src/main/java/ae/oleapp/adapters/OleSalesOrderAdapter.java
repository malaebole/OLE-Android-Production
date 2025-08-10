package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleInventoryOrder;

public class OleSalesOrderAdapter extends RecyclerView.Adapter<OleSalesOrderAdapter.ViewHolder> {

    private final Context context;
    private final List<OleInventoryOrder> list;
    private ItemClickListener itemClickListener;

    public OleSalesOrderAdapter(Context context, List<OleInventoryOrder> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olesales_order, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleInventoryOrder order = list.get(position);
        holder.tvOrderNo.setText(context.getString(R.string.order_no_place, order.getCode()));
        holder.tvDate.setText(order.getAddedDate());
        holder.tvAddedBy.setText(String.format("%s: %s", context.getString(R.string.sold_by), order.getSoldBy()));
        holder.tvPrice.setText(String.format("%s %s", order.getGrandTotal(), order.getCurrency()));
        holder.tvQty.setText(String.format("%s: %s", context.getString(R.string.qty), order.getTotalQty()));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvOrderNo, tvQty, tvDate, tvAddedBy, tvPrice;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderNo = itemView.findViewById(R.id.tv_order_no);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvQty = itemView.findViewById(R.id.tv_qty);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvAddedBy = itemView.findViewById(R.id.tv_added_by);
            cardView = itemView.findViewById(R.id.rel_main);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}