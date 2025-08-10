package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.Product;

public class FlashDealsAdapter extends RecyclerView.Adapter<FlashDealsAdapter.ViewHolder> {

    private final Context context;
    private final List<Product> list;
    private ItemClickListener itemClickListener;

    public FlashDealsAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.flash_deal, parent, false);
        setItemWidth(v, parent);
        return new ViewHolder(v);
    }

    private void setItemWidth(View view, ViewGroup parent) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.width = (int)((parent.getMeasuredWidth() / 2) * 0.9);
        view.setLayoutParams(params);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = list.get(position);
        holder.tvName.setText(product.getName());
        setPrice(product, holder);
        holder.tvSold.setText(context.getString(R.string.sold_place, product.getSold()));
        holder.tvSold.setVisibility(View.GONE);
        Glide.with(context).load(product.getThumbnail()).into(holder.imageView);
        if (product.getDiscountType().equalsIgnoreCase("amount")) {
            holder.tvPerc.setText(String.format("-%s %s", product.getDiscount(), product.getCurrency()));
        }
        else {
            holder.tvPerc.setText(String.format("-%s%%", product.getDiscount()));
        }

        if (product.getFastDelivery().equalsIgnoreCase("1")) {
            holder.fastDeliveryVu.setVisibility(View.VISIBLE);
        }
        else {
            holder.fastDeliveryVu.setVisibility(View.GONE);
        }

        Double total = Double.parseDouble(product.getTotalInOffer());
        Double sold = Double.parseDouble(product.getSold());
        if (total > 0) {
            holder.roundCornerProgressBar.setProgress((float)(sold/total));
        }
        else {
            holder.roundCornerProgressBar.setProgress(0);
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

    private void setPrice(Product product, ViewHolder holder) {
        double discount = 0;
        if (!product.getDiscount().isEmpty()) {
            discount = Double.parseDouble(product.getDiscount());
        }
        if (discount > 0) {
            double actualPrice = Double.parseDouble(product.getSalePrice());
            if (product.getDiscountType().equalsIgnoreCase("amount")) {
                double price = actualPrice - discount;
                holder.tvPrice.setText(String.format(Locale.ENGLISH,"%.2f %s", price, product.getCurrency()));
            }
            else {
                double price = actualPrice - ((discount / 100) * actualPrice);
                holder.tvPrice.setText(String.format(Locale.ENGLISH,"%.2f %s", price, product.getCurrency()));
            }
        }
        else {
            holder.tvPrice.setText(String.format("%s %s", product.getSalePrice(), product.getCurrency()));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvPrice, tvPerc, tvSold;
        CardView layout;
        ImageView imageView, fastDeliveryVu;
        RoundCornerProgressBar roundCornerProgressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img_vu);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvSold = itemView.findViewById(R.id.tv_sold);
            tvPerc = itemView.findViewById(R.id.tv_perc);
            roundCornerProgressBar = itemView.findViewById(R.id.progressbar);
            layout = itemView.findViewById(R.id.main_layout);
            fastDeliveryVu = itemView.findViewById(R.id.fast_delivery_vu);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}