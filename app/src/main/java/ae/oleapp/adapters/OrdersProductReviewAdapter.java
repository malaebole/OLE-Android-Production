package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Product;

public class OrdersProductReviewAdapter extends RecyclerView.Adapter<OrdersProductReviewAdapter.ViewHolder> {

    private final Context context;
    private final List<Product> list;
    private ItemClickListener itemClickListener;

    public OrdersProductReviewAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = list.get(position);
        Glide.with(context).load(product.getThumbnail()).into(holder.imageView);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format("%s %s", product.getSalePrice(), product.getCurrency()));
        holder.tvQty.setText(context.getString(R.string.items_place, product.getQty()));
        if (product.getIsReviewed().equalsIgnoreCase("1")) {
            holder.btnReview.setVisibility(View.GONE);
        }
        else {
            holder.btnReview.setVisibility(View.VISIBLE);
        }

        holder.btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.reviewClicked(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvQty, tvPrice;
        RelativeLayout btnReview;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvQty = itemView.findViewById(R.id.tv_qty);
            btnReview = itemView.findViewById(R.id.btn_review);
            tvPrice = itemView.findViewById(R.id.tv_price);
            imageView = itemView.findViewById(R.id.img_vu);
        }
    }

    public interface ItemClickListener {
        void reviewClicked(View view, int pos);
    }
}