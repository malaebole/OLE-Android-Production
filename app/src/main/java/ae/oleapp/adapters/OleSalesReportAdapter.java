package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleInventoryItem;

public class OleSalesReportAdapter extends RecyclerView.Adapter<OleSalesReportAdapter.ViewHolder> {

    private final Context context;
    private final List<OleInventoryItem> list;

    public OleSalesReportAdapter(Context context, List<OleInventoryItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olesales_report, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleInventoryItem item = list.get(position);
        holder.tvName.setText(item.getName());
        holder.tvQty.setText(item.getQuantity());
        holder.tvPrice.setText(String.format("%s %s", item.getSalePrice(), item.getCurrency()));
        if (list.size() == position + 1) {
            holder.sepVu.setVisibility(View.INVISIBLE);
        }
        else {
            holder.sepVu.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvQty, tvName, tvPrice;
        View sepVu;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvQty = itemView.findViewById(R.id.tv_qty);
            tvPrice = itemView.findViewById(R.id.tv_price);
            sepVu = itemView.findViewById(R.id.sep_vu);
            layout = itemView.findViewById(R.id.linear);
        }
    }
}