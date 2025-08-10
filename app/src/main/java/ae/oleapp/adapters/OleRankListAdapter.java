package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerRank;

public class OleRankListAdapter extends RecyclerView.Adapter<OleRankListAdapter.ViewHolder> {

    private final Context context;
    private final List<OlePlayerRank> list;
    private boolean isMostBooking;
    private OnItemClickListener onItemClickListener;

    public OleRankListAdapter(Context context, List<OlePlayerRank> list, boolean isMostBooking) {
        this.context = context;
        this.list = list;
        this.isMostBooking = isMostBooking;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setMostBooking(boolean mostBooking) {
        isMostBooking = mostBooking;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olerank_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerRank rank = list.get(position);
        holder.tvRank.setText(String.valueOf(rank.getRank()));
        holder.tvName.setText(rank.getNickName());
        Glide.with(context).load(rank.getPhotoUrl()).placeholder(R.drawable.player_active).into(holder.imgVu);
        if (isMostBooking){
            holder.tvPoints.setText(rank.getTotalHours());
            holder.tvPerc.setVisibility(View.GONE);
        }
        else {
            holder.tvPoints.setText(rank.getPoints());
            holder.tvPerc.setText(rank.getWinPercentage());
            holder.tvPerc.setVisibility(View.VISIBLE);
        }

        if (rank.getRank() == 1) {
            holder.tvRank.setVisibility(View.INVISIBLE);
            holder.imgVuRank.setVisibility(View.VISIBLE);
            holder.imgVuRank.setImageResource(R.drawable.rank_badge_one);
        }
        else if (rank.getRank() == 2) {
            holder.tvRank.setVisibility(View.INVISIBLE);
            holder.imgVuRank.setVisibility(View.VISIBLE);
            holder.imgVuRank.setImageResource(R.drawable.rank_badge_two);
        }
        else if (rank.getRank() == 3) {
            holder.tvRank.setVisibility(View.INVISIBLE);
            holder.imgVuRank.setVisibility(View.VISIBLE);
            holder.imgVuRank.setImageResource(R.drawable.rank_badge_three);
        }
        else {
            holder.tvRank.setVisibility(View.VISIBLE);
            holder.imgVuRank.setVisibility(View.INVISIBLE);
        }

        holder.relMain.setOnClickListener(new View.OnClickListener() {
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

        ImageView imgVu, imgVuRank;
        TextView tvRank, tvName, tvPoints, tvPerc;
        RelativeLayout relMain;

        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            imgVuRank = itemView.findViewById(R.id.img_vu_rank);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPoints = itemView.findViewById(R.id.tv_points);
            tvPerc = itemView.findViewById(R.id.tv_perc);
            relMain = itemView.findViewById(R.id.rl);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}