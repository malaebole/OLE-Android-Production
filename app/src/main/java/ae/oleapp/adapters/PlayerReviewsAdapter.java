package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.PlayerRate;
import ae.oleapp.util.FlexibleThumbSeekbar;

public class PlayerReviewsAdapter extends RecyclerView.Adapter<PlayerReviewsAdapter.ViewHolder> {

    private final Context context;
    private final List<PlayerRate> list;

    public PlayerReviewsAdapter(Context context, List<PlayerRate> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerRate playerRate = list.get(position);
        Glide.with(context).load(playerRate.getBibUrl()).into(holder.shirtImgVu);
        Glide.with(context).load(playerRate.getEmojiUrl()).into(holder.emojiImgVu);
        holder.tvName.setText(playerRate.getNickName());
        holder.tvDate.setText(playerRate.getRateAdded());
        holder.tvReview.setText(playerRate.getFeedback());
        if (playerRate.getReachTime().equalsIgnoreCase("1")) {
            holder.tvReachTime.setText(R.string.before_the_time);
        }
        else if (playerRate.getReachTime().equalsIgnoreCase("2")) {
            holder.tvReachTime.setText(R.string.on_time);
        }
        else if (playerRate.getReachTime().equalsIgnoreCase("3")) {
            holder.tvReachTime.setText(R.string.late);
        }
        else if (playerRate.getReachTime().equalsIgnoreCase("4")) {
            holder.tvReachTime.setText(R.string.not_come);
        }
        else {
            holder.tvReachTime.setText("");
        }

        if (playerRate.getPlayingLevel().equalsIgnoreCase("0")) {
            holder.skillsProgressbar.setProgress(0);
            holder.tvSkills.setText("0%");
        }
        else {
            holder.skillsProgressbar.setProgress(Integer.parseInt(playerRate.getPlayingLevel()));
            holder.tvSkills.setText(String.format("%s%%", playerRate.getPlayingLevel()));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvDate, tvReachTime, tvReview, tvSkills;
        ImageView shirtImgVu, emojiImgVu;
        FlexibleThumbSeekbar skillsProgressbar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvReachTime = itemView.findViewById(R.id.tv_reach_time);
            tvReview = itemView.findViewById(R.id.tv_review);
            tvSkills = itemView.findViewById(R.id.tv_playing_skills);
            shirtImgVu = itemView.findViewById(R.id.shirt_img_vu);
            emojiImgVu = itemView.findViewById(R.id.emoji_img_vu);
            skillsProgressbar = itemView.findViewById(R.id.skills_progressbar);

            skillsProgressbar.setThumbImage(R.drawable.profile_skills_thumbl, 40);
            skillsProgressbar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
        }
    }
}