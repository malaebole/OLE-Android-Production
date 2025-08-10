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
import ae.oleapp.models.OlePurchaseHistory;

public class OlePurchaseReportAdapter extends RecyclerView.Adapter<OlePurchaseReportAdapter.ViewHolder> {

    private final Context context;
    private final List<OlePurchaseHistory> list;
    private ItemClickListener itemClickListener;

    public OlePurchaseReportAdapter(Context context, List<OlePurchaseHistory> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olepurchase_report, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePurchaseHistory history = list.get(position);
        holder.tvItemName.setText(history.getName());
        holder.tvDate.setText(history.getPurchasedDate());
        holder.tvPurchaseBy.setText(String.format("%s: %s", context.getString(R.string.added_by), history.getPurchasedBy()));
        holder.tvPrice.setText(String.format("%s %s", history.getPurchasePrice(), history.getCurrency()));
        holder.tvQty.setText(String.format("%s: %s", context.getString(R.string.qty), history.getPurchaseNewStock()));
        if (Integer.parseInt(history.getPurchaseNewStock()) > 0) {
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

        TextView tvItemName, tvQty, tvDate, tvPurchaseBy, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvQty = itemView.findViewById(R.id.tv_qty);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvPurchaseBy = itemView.findViewById(R.id.tv_added_by);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}