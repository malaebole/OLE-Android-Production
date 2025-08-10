package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;


import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.ProductBrand;
import ae.oleapp.models.ProductCategory;

public class ProductListCatAdapter extends RecyclerView.Adapter<ProductListCatAdapter.ViewHolder> {

    private final Context context;
    private List<Object> list;
    private ItemClickListener itemClickListener;
    private int selectedIndex = -1;

    public ProductListCatAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setDataSource(List<Object> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.variant_option, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (list.get(position) instanceof ProductCategory) {
            ProductCategory category = (ProductCategory)list.get(position);
            holder.tvName.setText(category.getName());
            if (selectedIndex == position) {
                holder.cardView.setStrokeColor(context.getResources().getColor(R.color.blueColorNew));
                holder.tvName.setTextColor(context.getResources().getColor(R.color.blueColorNew));
            } else {
                holder.cardView.setStrokeColor(context.getResources().getColor(R.color.separatorColor));
                holder.tvName.setTextColor(context.getResources().getColor(R.color.darkTextColor));
            }
        }
        else {
            ProductBrand brand = (ProductBrand) list.get(position);
            holder.tvName.setText(brand.getName());
            if (selectedIndex == position) {
                holder.cardView.setStrokeColor(context.getResources().getColor(R.color.blueColorNew));
                holder.tvName.setTextColor(context.getResources().getColor(R.color.blueColorNew));
            } else {
                holder.cardView.setStrokeColor(context.getResources().getColor(R.color.separatorColor));
                holder.tvName.setTextColor(context.getResources().getColor(R.color.darkTextColor));
            }
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
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

    class ViewHolder extends RecyclerView.ViewHolder{

        MaterialCardView cardView;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_vu);
            tvName = itemView.findViewById(R.id.tv_value);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}