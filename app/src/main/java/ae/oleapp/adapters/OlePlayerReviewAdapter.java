package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerReview;
import ae.oleapp.util.OleProfileView;
import io.feeeei.circleseekbar.CircleSeekBar;

public class OlePlayerReviewAdapter extends RecyclerView.Adapter<OlePlayerReviewAdapter.ViewHolder> {

    private final Context context;
    private final List<OlePlayerReview> list;
    private OnItemClickListener onItemClickListener;

    public OlePlayerReviewAdapter(Context context, List<OlePlayerReview> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleplayer_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerReview review = list.get(position);
        if (review.getNickName().isEmpty()) {
            holder.oleProfileView.populateData(review.getName(), review.getPhotoUrl(), review.getLevel(), true);
        }
        else {
            holder.oleProfileView.populateData(review.getNickName(), review.getPhotoUrl(), review.getLevel(), true);
        }
        if (review.getReachTime().equalsIgnoreCase("1")) {
            holder.tvReachTime.setText(R.string.before_the_time);
        }
        else if (review.getReachTime().equalsIgnoreCase("2")) {
            holder.tvReachTime.setText(R.string.on_time);
        }
        else if (review.getReachTime().equalsIgnoreCase("3")) {
            holder.tvReachTime.setText(R.string.late);
        }
        else if (review.getReachTime().equalsIgnoreCase("4")) {
            holder.tvReachTime.setText(R.string.not_come);
        }

        holder.tvFeedback.setText(review.getFeedback());

        if (review.getPlayingLevel().isEmpty()) {
            holder.circleSeekBar.setCurProcess(0);
            holder.tvPerc.setText("0%");
        }
        else {
            holder.circleSeekBar.setCurProcess((int) Double.parseDouble(review.getPlayingLevel()));
            holder.tvPerc.setText(String.format("%s%%", review.getPlayingLevel()));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse(review.getDate());
            dateFormat.applyPattern("dd/MM/yyyy");
            holder.tvDate.setText(dateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.tvDate.setText("");
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvDate, tvReachTime, tvFeedback, tvPerc;
        CircleSeekBar circleSeekBar;
        OleProfileView oleProfileView;
        CardView mainLayout;

        ViewHolder(View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_date);
            tvFeedback = itemView.findViewById(R.id.tv_feedback);
            tvReachTime = itemView.findViewById(R.id.tv_reach_time);
            tvPerc = itemView.findViewById(R.id.tv_perc);
            circleSeekBar = itemView.findViewById(R.id.circular);
            oleProfileView = itemView.findViewById(R.id.profile_vu);
            mainLayout = itemView.findViewById(R.id.main);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}