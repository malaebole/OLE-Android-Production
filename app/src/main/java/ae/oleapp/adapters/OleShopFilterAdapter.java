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
import ae.oleapp.models.OleShopFilter;

public class OleShopFilterAdapter extends RecyclerView.Adapter<OleShopFilterAdapter.ViewHolder> {

    private final Context context;
    private final List<OleShopFilter> list;
    private List<OleShopFilter> selectedFilter;
    private ItemClickListener itemClickListener;

    public OleShopFilterAdapter(Context context, List<OleShopFilter> list) {
        this.context = context;
        this.list = list;
    }

    public List<OleShopFilter> getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedFilter(List<OleShopFilter> selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleshop_filter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleShopFilter filter = list.get(position);
        holder.tvTitle.setText(filter.getTitle());
        holder.tvValue.setText("");
        for (OleShopFilter oleShopFilter : selectedFilter) {
            if (oleShopFilter.getTitle().equalsIgnoreCase(filter.getTitle())) {
                holder.tvValue.setText(oleShopFilter.getValue());
                break;
            }
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvValue;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvValue = itemView.findViewById(R.id.tv_value);
            layout = itemView.findViewById(R.id.main);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}