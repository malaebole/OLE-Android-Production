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

public class OleStockReportAdapter extends RecyclerView.Adapter<OleStockReportAdapter.ViewHolder> {

    private final Context context;
    private final List<OleInventoryItem> list;
    private ItemClickListener itemClickListener;

    public OleStockReportAdapter(Context context, List<OleInventoryItem> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olestock_report, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleInventoryItem item = list.get(position);
        holder.tvName.setText(item.getName());
        holder.tvQty.setText(item.getCurrentStock());
        if (list.size() == position + 1) {
            holder.sepVu.setVisibility(View.INVISIBLE);
        }
        else {
            holder.sepVu.setVisibility(View.VISIBLE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
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

        TextView tvQty, tvName;
        View sepVu;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvQty = itemView.findViewById(R.id.tv_qty);
            sepVu = itemView.findViewById(R.id.sep_vu);
            layout = itemView.findViewById(R.id.linear);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}