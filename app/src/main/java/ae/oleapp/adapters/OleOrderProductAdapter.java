package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Product;

public class OleOrderProductAdapter extends RecyclerView.Adapter<OleOrderProductAdapter.ViewHolder> {

    private final Context context;
    private final List<Product> list;

    public OleOrderProductAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleorder_product, parent, false);
        setItemWidth(v, parent);
        return new ViewHolder(v);
    }

    private void setItemWidth(View view, ViewGroup parent) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.width = (int)(parent.getMeasuredWidth() * 0.8);
        view.setLayoutParams(params);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = list.get(position);
        holder.tvName.setText(product.getName());
        Glide.with(context).load(product.getThumbnail()).into(holder.imageView);
        if (product.getDeliveryStatus().equalsIgnoreCase("pending")) {
            holder.tvStatus.setText(R.string.pending);
        }
        else if (product.getDeliveryStatus().equalsIgnoreCase("confirmed")) {
            holder.tvStatus.setText(R.string.confirmed);
        }
        else if (product.getDeliveryStatus().equalsIgnoreCase("shipped")) {
            holder.tvStatus.setText(R.string.shipped);
        }
        else if (product.getDeliveryStatus().equalsIgnoreCase("delivered")) {
            holder.tvStatus.setText(R.string.delivered);
        }
        else if (product.getDeliveryStatus().equalsIgnoreCase("canceled")) {
            holder.tvStatus.setText(R.string.cancelled);
        }
        else {
            holder.tvStatus.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvStatus;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img_vu);
            tvName = itemView.findViewById(R.id.tv_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}