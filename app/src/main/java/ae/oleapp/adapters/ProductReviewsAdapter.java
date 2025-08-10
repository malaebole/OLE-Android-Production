package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.ProductReview;

public class ProductReviewsAdapter extends RecyclerView.Adapter<ProductReviewsAdapter.ViewHolder> {

    private final Context context;
    private final List<ProductReview> list;

    public ProductReviewsAdapter(Context context, List<ProductReview> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductReview productReview = list.get(position);
        holder.tvName.setText(productReview.getUser().getName());
        holder.tvRate.setText(productReview.getRating());
        holder.tvReview.setText(productReview.getComment());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvRate, tvReview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvRate = itemView.findViewById(R.id.tv_rate);
            tvReview = itemView.findViewById(R.id.tv_review);
        }
    }
}