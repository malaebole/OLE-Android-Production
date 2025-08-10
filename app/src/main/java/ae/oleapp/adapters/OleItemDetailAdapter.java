package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleStockHistory;

public class OleItemDetailAdapter extends RecyclerView.Adapter<OleItemDetailAdapter.ViewHolder> {

    private final Context context;
    private final List<OleStockHistory> list;
    private ItemClickListener itemClickListener;

    public OleItemDetailAdapter(Context context, List<OleStockHistory> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleitem_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleStockHistory stock = list.get(position);
        holder.tvName.setText(stock.getAddedBy());
        holder.tvDate.setText(stock.getAddedDate());
        holder.tvPurchasePrice.setText(String.format("%s: %s %s", context.getString(R.string.purchase_price), stock.getPurchasedPrice(), stock.getCurrency()));
        holder.tvSalePrice.setText(String.format("%s: %s %s", context.getString(R.string.sale_price), stock.getSalePrice(), stock.getCurrency()));
        holder.tvQty.setText(String.format("%s: %s", context.getString(R.string.qty), stock.getStock()));
        if (Integer.parseInt(stock.getStock()) > 0) {
            holder.tvQty.setTextColor(context.getResources().getColor(R.color.subTextColor));
        }
        else {
            holder.tvQty.setTextColor(context.getResources().getColor(R.color.redColor));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvQty, tvDate, tvPurchasePrice, tvSalePrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvQty = itemView.findViewById(R.id.tv_qty);
            tvSalePrice = itemView.findViewById(R.id.tv_sale_price);
            tvPurchasePrice = itemView.findViewById(R.id.tv_purchase_price);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}