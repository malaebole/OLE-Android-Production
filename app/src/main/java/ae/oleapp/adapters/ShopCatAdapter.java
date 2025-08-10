package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.ProductCategory;

public class ShopCatAdapter extends RecyclerView.Adapter<ShopCatAdapter.ViewHolder> {

    private final Context context;
    private final List<ProductCategory> list;
    private ItemClickListener itemClickListener;

    public ShopCatAdapter(Context context, List<ProductCategory> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_cat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductCategory category = list.get(position);
        holder.tvName.setText(category.getName());
        Glide.with(context).load(category.getIcon()).into(holder.imageView);

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

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        LinearLayout layout;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img_vu);
            tvName = itemView.findViewById(R.id.tv_name);
            layout = itemView.findViewById(R.id.main);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}