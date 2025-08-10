package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class OleCartListAdapter extends RecyclerView.Adapter<OleCartListAdapter.ViewHolder> {

    private final Context context;
    private final List<Cart> list;
    private ItemClickListener itemClickListener;
    private int selectedQty = 1;
    private int currentStock = 0;

    public OleCartListAdapter(Context context, List<Cart> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olecart_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cart cart = list.get(position);
        Glide.with(context).load(cart.getThumbnail()).into(holder.imageView);
        holder.tvName.setText(cart.getName());
        holder.tvQty.setText(cart.getQuantity());
        holder.tvPrice.setText(String.format("%s %s", cart.getPrice(), cart.getCurrency()));
        selectedQty = Integer.parseInt(cart.getQuantity());
        currentStock = Integer.parseInt(cart.getCurrentStock());
        if (selectedQty <= currentStock) {
            holder.tvStock.setVisibility(View.GONE);
        }
        else {
            holder.tvStock.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        OleCartVariantAdapter adapter = new OleCartVariantAdapter(context, cart.getAttributes());
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
            holder.colorVu.setVisibility(View.GONE);
        }

        holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.deleteClicked(v, holder.getAdapterPosition());
                }
            }
        });
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.plusClicked(v, holder.getAdapterPosition());
            }
        });
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.minusClicked(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvPrice, tvQty, tvStock;
        CardView layout, btnMinus, btnPlus, colorBgVu;
        LinearLayout colorVu;
        RelativeLayout variantVu;
        ImageView imageView;
        ImageButton btnDel;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img_vu);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQty = itemView.findViewById(R.id.tv_qty);
            tvStock = itemView.findViewById(R.id.tv_stock);
            layout = itemView.findViewById(R.id.main_layout);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnDel = itemView.findViewById(R.id.btn_del);
            colorBgVu = itemView.findViewById(R.id.color_bg_vu);
            colorVu = itemView.findViewById(R.id.color_vu);
            variantVu = itemView.findViewById(R.id.variant_vu);
            recyclerView = itemView.findViewById(R.id.recycler_vu);
        }
    }

    public interface ItemClickListener {
        void deleteClicked(View view, int pos);
        void plusClicked(View view, int pos);
        void minusClicked(View view, int pos);
    }
}