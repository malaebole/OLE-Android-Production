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

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleInventoryItem;

public class OleNewSaleAdapter extends RecyclerView.Adapter<OleNewSaleAdapter.ViewHolder> {

    private final Context context;
    private final List<OleInventoryItem> list;
    private ItemClickListener itemClickListener;

    public OleNewSaleAdapter(Context context, List<OleInventoryItem> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olenew_sale, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleInventoryItem item = list.get(position);
        Glide.with(context).load(item.getPhoto()).into(holder.imageView);
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(String.format("%s %s", item.getSalePrice(), item.getCurrency()));
        holder.tvStock.setText(String.format("%s: %s", context.getString(R.string.stock), item.getCurrentStock()));
        holder.tvQty.setText(String.valueOf(item.getQty()));

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.plusClicked(v, holder.getAdapterPosition());
                }
            }
        });
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.minusClicked(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvStock, tvPrice, tvQty;
        ImageView imageView;
        CardView layout;
        MaterialCardView btnPlus, btnMinus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStock = itemView.findViewById(R.id.tv_stock);
            tvQty = itemView.findViewById(R.id.tv_qty);
            imageView = itemView.findViewById(R.id.img_vu);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            layout = itemView.findViewById(R.id.rel_main);
        }
    }

    public interface ItemClickListener {
        void plusClicked(View view, int pos);
        void minusClicked(View view, int pos);
    }
}