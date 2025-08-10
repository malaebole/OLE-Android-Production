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
import ae.oleapp.models.OleProductReview;

public class OleProductReviewsAdapter extends RecyclerView.Adapter<OleProductReviewsAdapter.ViewHolder> {

    private final Context context;
    private final List<OleProductReview> list;

    public OleProductReviewsAdapter(Context context, List<OleProductReview> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleproduct_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleProductReview oleProductReview = list.get(position);
        holder.tvName.setText(oleProductReview.getUser().getName());
        holder.tvRate.setText(oleProductReview.getRating());
        holder.tvReview.setText(oleProductReview.getComment());
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