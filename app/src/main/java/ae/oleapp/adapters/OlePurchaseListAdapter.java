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

public class OlePurchaseListAdapter extends RecyclerView.Adapter<OlePurchaseListAdapter.ViewHolder> {

    private final Context context;
    private final List<OleInventoryItem> list;
    private ItemClickListener itemClickListener;

    public OlePurchaseListAdapter(Context context, List<OleInventoryItem> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olepurchase_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleInventoryItem item = list.get(position);
        Glide.with(context).load(item.getPhoto()).into(holder.imageView);
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(String.format("%s %s", item.getPurchasedPrice(), item.getCurrency()));
        holder.tvStock.setText(String.format("%s: %s", context.getString(R.string.stock), item.getCurrentStock()));

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.editClicked(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvStock, tvPrice;
        ImageView imageView;
        CardView layout;
        MaterialCardView btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStock = itemView.findViewById(R.id.tv_stock);
            imageView = itemView.findViewById(R.id.img_vu);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            layout = itemView.findViewById(R.id.rel_main);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void editClicked(View view, int pos);
    }
}