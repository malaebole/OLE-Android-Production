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
import ae.oleapp.models.OleInventoryItem;

public class OleProfitReportAdapter extends RecyclerView.Adapter<OleProfitReportAdapter.ViewHolder> {

    private final Context context;
    private final List<OleInventoryItem> list;
    private ItemClickListener itemClickListener;

    public OleProfitReportAdapter(Context context, List<OleInventoryItem> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleprofit_report, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleInventoryItem item = list.get(position);
        holder.tvItemName.setText(item.getName());
        holder.tvPrice.setText(String.format("%s: %s %s", context.getString(R.string.sale_price), item.getSalePrice(), item.getCurrency()));
        holder.tvQty.setText(String.format("%s: %s", context.getString(R.string.sold_qty), item.getQuantity()));
        holder.tvProfit.setText(String.format("%s %s", item.getProfitAmount(), item.getCurrency()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvItemName, tvQty, tvProfit, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvQty = itemView.findViewById(R.id.tv_sold_qty);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvProfit = itemView.findViewById(R.id.tv_profit);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}