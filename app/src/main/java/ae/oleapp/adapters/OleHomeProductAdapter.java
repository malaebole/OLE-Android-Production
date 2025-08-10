package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.Product;

public class OleHomeProductAdapter extends RecyclerView.Adapter<OleHomeProductAdapter.ViewHolder> {

    private final Context context;
    private final List<Product> list;
    private ItemClickListener itemClickListener;

    public OleHomeProductAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olehome_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = list.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format("%s %s", product.getSalePrice(), product.getCurrency()));
        holder.tvSold.setText(context.getString(R.string.sold_place, product.getSold()));
        holder.tvSold.setVisibility(View.GONE);
        Glide.with(context).load(product.getThumbnail()).into(holder.imageView);
        if (product.getIsFavorite().equalsIgnoreCase("1")) {
            holder.btnFav.setImageResource(R.drawable.shop_fav_ic);
        }
        else {
            holder.btnFav.setImageResource(R.drawable.shop_unfav_ic);
        }
        if (product.getDiscountType().equalsIgnoreCase("amount")) {
            holder.tvPerc.setText(String.format("-%s %s", product.getDiscount(), product.getCurrency()));
        }
        else {
            holder.tvPerc.setText(String.format("-%s%%", product.getDiscount()));
        }

        setActualPrice(product, holder);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            }
        });

        holder.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.favClicked(v, holder.getAdapterPosition());
                }
            }
        });
    }

    private void setActualPrice(Product product, ViewHolder holder) {
        double discount = 0;
        if (!product.getDiscount().isEmpty()) {
            discount = Double.parseDouble(product.getDiscount());
        }
        if (discount > 0) {
            holder.tvActualPrice.setVisibility(View.VISIBLE);
            holder.percVu.setVisibility(View.VISIBLE);
            double actualPrice = Double.parseDouble(product.getSalePrice());
            if (product.getDiscountType().equalsIgnoreCase("amount")) {
                double price = actualPrice - discount;
                holder.tvPrice.setText(String.format(Locale.ENGLISH,"%.2f %s", price, product.getCurrency()));
            }
            else {
                double price = actualPrice - ((discount / 100) * actualPrice);
                holder.tvPrice.setText(String.format(Locale.ENGLISH,"%.2f %s", price, product.getCurrency()));
            }
            holder.tvActualPrice.setText(product.getSalePrice());
            holder.tvActualPrice.setPaintFlags(holder.tvActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            holder.tvActualPrice.setVisibility(View.GONE);
            holder.percVu.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvPrice, tvActualPrice, tvSold, tvPerc;
        ImageButton btnFav;
        CardView layout;
        ImageView imageView;
        RelativeLayout percVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img_vu);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvActualPrice = itemView.findViewById(R.id.tv_actual_price);
            tvSold = itemView.findViewById(R.id.tv_sold);
            tvPerc = itemView.findViewById(R.id.tv_perc);
            btnFav = itemView.findViewById(R.id.btn_fav);
            layout = itemView.findViewById(R.id.rel_main);
            percVu = itemView.findViewById(R.id.perc_vu);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void favClicked(View view, int pos);
    }
}