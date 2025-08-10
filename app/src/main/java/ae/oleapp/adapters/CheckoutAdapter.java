package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Cart;
import ae.oleapp.models.Product;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {

    private final Context context;
    private final List<Object> list;

    public CheckoutAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object obj = list.get(position);
        if (obj instanceof Cart) {
            populateData((Cart) obj, holder);
        }
        else {
            populateData((Product) obj, holder);
        }
    }

    private void populateData(Cart cart, ViewHolder holder) {
        Glide.with(context).load(cart.getThumbnail()).into(holder.imageView);
        holder.tvName.setText(cart.getName());
        holder.tvQty.setText(context.getString(R.string.items_place, cart.getQuantity()));
        holder.tvPrice.setText(String.format("%s %s", cart.getPrice(), cart.getCurrency()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        CartVariantAdapter adapter = new CartVariantAdapter(context, cart.getAttributes());
        holder.recyclerView.setAdapter(adapter);

        if (cart.getAttributes().size() > 0) {
            holder.variantVu.setVisibility(View.VISIBLE);
        }
        else {
            holder.variantVu.setVisibility(View.GONE);
        }
        if (!cart.getColor().equalsIgnoreCase("")) {
            holder.colorVu.setVisibility(View.VISIBLE);
            holder.colorBgVu.setCardBackgroundColor(Color.parseColor(cart.getColor()));
        }
        else {
            holder.colorVu.setVisibility(View.INVISIBLE);
        }
    }

    private void populateData(Product product, ViewHolder holder) {
        Glide.with(context).load(product.getThumbnail()).into(holder.imageView);
        holder.tvName.setText(product.getName());
        holder.tvQty.setText(context.getString(R.string.items_place, product.getQty()));
        holder.tvPrice.setText(String.format("%s %s", product.getSalePrice(), product.getCurrency()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        CartVariantAdapter adapter = new CartVariantAdapter(context, product.getAttributes());
        holder.recyclerView.setAdapter(adapter);

        if (product.getAttributes().size() > 0) {
            holder.variantVu.setVisibility(View.VISIBLE);
        }
        else {
            holder.variantVu.setVisibility(View.GONE);
        }
        if (!product.getColorCode().equalsIgnoreCase("")) {
            holder.colorVu.setVisibility(View.VISIBLE);
            holder.colorBgVu.setCardBackgroundColor(Color.parseColor(product.getColorCode()));
        }
        else {
            holder.colorVu.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvPrice, tvQty;
        CardView layout, colorBgVu;
        LinearLayout colorVu;
        RelativeLayout variantVu;
        ImageView imageView;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img_vu);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQty = itemView.findViewById(R.id.tv_items);
            layout = itemView.findViewById(R.id.main_layout);
            colorBgVu = itemView.findViewById(R.id.color_bg_vu);
            colorVu = itemView.findViewById(R.id.color_vu);
            variantVu = itemView.findViewById(R.id.variant_vu);
            recyclerView = itemView.findViewById(R.id.recycler_vu);
        }
    }
}