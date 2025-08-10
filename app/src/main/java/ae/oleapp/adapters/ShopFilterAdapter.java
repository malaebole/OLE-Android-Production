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
import ae.oleapp.models.ShopFilter;

public class ShopFilterAdapter extends RecyclerView.Adapter<ShopFilterAdapter.ViewHolder> {

    private final Context context;
    private final List<ShopFilter> list;
    private List<ShopFilter> selectedFilter;
    private ItemClickListener itemClickListener;

    public ShopFilterAdapter(Context context, List<ShopFilter> list) {
        this.context = context;
        this.list = list;
    }

    public List<ShopFilter> getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedFilter(List<ShopFilter> selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_filter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShopFilter filter = list.get(position);
        holder.tvTitle.setText(filter.getTitle());
        holder.tvValue.setText("");
        for (ShopFilter shopFilter: selectedFilter) {
            if (shopFilter.getTitle().equalsIgnoreCase(filter.getTitle())) {
                holder.tvValue.setText(shopFilter.getValue());
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