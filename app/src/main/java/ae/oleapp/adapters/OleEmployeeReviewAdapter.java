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

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleEmployeeReview;

public class OleEmployeeReviewAdapter extends RecyclerView.Adapter<OleEmployeeReviewAdapter.ViewHolder> {

    private final Context context;
    private final List<OleEmployeeReview> list;
    private ItemClickListener itemClickListener;
    private boolean isEmployee = false;

    public OleEmployeeReviewAdapter(Context context, List<OleEmployeeReview> list) {
        this.context = context;
        this.list = list;
        this.isEmployee = false;
    }

    public OleEmployeeReviewAdapter(Context context, List<OleEmployeeReview> list, boolean isEmployee) {
        this.context = context;
        this.list = list;
        this.isEmployee = isEmployee;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleemployee_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleEmployeeReview review = list.get(position);
        if (isEmployee) {
            holder.imageView.setImageResource(R.drawable.player_active);
            holder.tvName.setText(R.string.anonymous);
        }
        else {
            Glide.with(context).load(review.getPlayerPhoto()).placeholder(R.drawable.player_active).into(holder.imageView);
            holder.tvName.setText(review.getPlayerName());
        }
        holder.tvTime.setText(review.getTimesAgo());
        holder.tvRate.setText(review.getStars());
        holder.tvTip.setText(context.getString(R.string.tip_place, review.getTipAmount(), review.getCurrency()));
        if (review.getComment().isEmpty()) {
            holder.tvReview.setVisibility(View.GONE);
        }
        else {
            holder.tvReview.setVisibility(View.VISIBLE);
            holder.tvReview.setText(review.getComment());
        }
        float ratingCount = Float.parseFloat(review.getStars());
        if (ratingCount == 1) {
            holder.tvRateDesc.setTextColor(context.getResources().getColor(R.color.redColor));
            holder.tvRateDesc.setText(R.string.bad_service);
            holder.imgVuEmoji.setImageResource(R.drawable.bad_service);
        }
        else if (ratingCount == 2) {
            holder.tvRateDesc.setTextColor(context.getResources().getColor(R.color.orangeColor));
            holder.tvRateDesc.setText(R.string.average);
            holder.imgVuEmoji.setImageResource(R.drawable.average);
        }
        else if (ratingCount == 3) {
            holder.tvRateDesc.setTextColor(context.getResources().getColor(R.color.blueColorNew));
            holder.tvRateDesc.setText(R.string.good);
            holder.imgVuEmoji.setImageResource(R.drawable.good);
        }
        else if (ratingCount == 4) {
            holder.tvRateDesc.setTextColor(context.getResources().getColor(R.color.blueColorNew));
            holder.tvRateDesc.setText(R.string.very_good);
            holder.imgVuEmoji.setImageResource(R.drawable.very_good);
        }
        else if (ratingCount == 5) {
            holder.tvRateDesc.setTextColor(context.getResources().getColor(R.color.greenColor));
            holder.tvRateDesc.setText(R.string.excellent);
            holder.imgVuEmoji.setImageResource(R.drawable.excellent);
        }
        else {
            holder.tvRateDesc.setText("");
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvTime, tvRate, tvReview, tvTip, tvRateDesc;
        ImageView imageView, imgVuEmoji;
        CardView layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTime = itemView.findViewById(R.id.tv_time);
            tvName = itemView.findViewById(R.id.tv_name);
            tvRate = itemView.findViewById(R.id.tv_rate);
            tvReview = itemView.findViewById(R.id.tv_review);
            tvTip = itemView.findViewById(R.id.tv_tip);
            imageView = itemView.findViewById(R.id.img_vu);
            imgVuEmoji = itemView.findViewById(R.id.img_vu_emoji);
            tvRateDesc = itemView.findViewById(R.id.tv_rate_desc);
            layout = itemView.findViewById(R.id.rel_main);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}