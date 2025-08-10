package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Product;

public class HotTrendingAdapter extends RecyclerView.Adapter<HotTrendingAdapter.ViewHolder> {

    private final Context context;
    private final List<Product> list;
    private ItemClickListener itemClickListener;

    public HotTrendingAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hot_trending, parent, false);
        setItemWidth(v, parent);
        return new ViewHolder(v);
    }

    private void setItemWidth(View view, ViewGroup parent) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.width = (parent.getMeasuredWidth() / 2);
        params.height = (parent.getMeasuredWidth() / 2);
        view.setLayoutParams(params);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = list.get(position);
        Glide.with(context).load(product.getThumbnail()).into(holder.imageView);

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

        CardView layout;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img_vu);
            layout = itemView.findViewById(R.id.main_layout);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}