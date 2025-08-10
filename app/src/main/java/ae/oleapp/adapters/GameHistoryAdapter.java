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
import ae.oleapp.models.GameHistory;

public class GameHistoryAdapter extends RecyclerView.Adapter<GameHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<GameHistory> list;
    private ItemClickListener itemClickListener;

    public GameHistoryAdapter(Context context, List<GameHistory> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameHistory game = list.get(position);
        holder.tvGroupName.setText(game.getGroupName());
        holder.tvDate.setText(game.getGameDate());
        holder.tvTime.setText(game.getGameTime());
        holder.tvTeamA.setText(game.getTeamA().getTeamAName());
        holder.tvTeamB.setText(game.getTeamB().getTeamBName());
        holder.tvTeamAPlayer.setText(game.getTeamAPlayer().getNickName());
        holder.tvTeamBPlayer.setText(game.getTeamBPlayer().getNickName());
        Glide.with(context).load(game.getTeamAPlayer().getEmojiUrl()).into(holder.emojiImgVuP1);
        Glide.with(context).load(game.getTeamBPlayer().getEmojiUrl()).into(holder.emojiImgVuP2);
        Glide.with(context).load(game.getTeamAPlayer().getBibUrl()).into(holder.shirtImgVuP1);
        Glide.with(context).load(game.getTeamBPlayer().getBibUrl()).into(holder.shirtImgVuP2);
        if (game.getTeamAPlayer().getIsCaptain().equalsIgnoreCase("1")) {
            holder.teamACaptain.setVisibility(View.VISIBLE);
        }
        else {
            holder.teamACaptain.setVisibility(View.INVISIBLE);
        }
        if (game.getTeamBPlayer().getIsCaptain().equalsIgnoreCase("1")) {
            holder.teamBCaptain.setVisibility(View.VISIBLE);
        }
        else {
            holder.teamBCaptain.setVisibility(View.INVISIBLE);
        }

        if (game.getTeamA().getStatus().equalsIgnoreCase("WON")) {
            holder.teamAWin.setVisibility(View.VISIBLE);
            holder.teamAWinCup.setVisibility(View.VISIBLE);
            holder.teamAVu.setAlpha(1.0f);
        }
        else if (game.getTeamA().getStatus().equalsIgnoreCase("LOST")) {
            holder.teamAWin.setVisibility(View.GONE);
            holder.teamAWinCup.setVisibility(View.INVISIBLE);
            holder.teamAVu.setAlpha(0.5f);
        }
        else if (game.getTeamA().getStatus().equalsIgnoreCase("DRAW")) {
            holder.teamAWin.setVisibility(View.GONE);
            holder.teamAWinCup.setVisibility(View.INVISIBLE);
            holder.teamAVu.setAlpha(1.0f);
        }

        if (game.getTeamB().getStatus().equalsIgnoreCase("WON")) {
            holder.teamBWin.setVisibility(View.VISIBLE);
            holder.teamBWinCup.setVisibility(View.VISIBLE);
            holder.teamBVu.setAlpha(1.0f);
        }
        else if (game.getTeamB().getStatus().equalsIgnoreCase("LOST")) {
            holder.teamBWin.setVisibility(View.GONE);
            holder.teamBWinCup.setVisibility(View.INVISIBLE);
            holder.teamBVu.setAlpha(0.5f);
        }
        else if (game.getTeamB().getStatus().equalsIgnoreCase("DRAW")) {
            holder.teamBWin.setVisibility(View.GONE);
            holder.teamBWinCup.setVisibility(View.INVISIBLE);
            holder.teamBVu.setAlpha(1.0f);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.itemClicked(view, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvGroupName, tvDate, tvTime, tvTeamA, tvTeamB, tvTeamAPlayer, tvTeamBPlayer;
        ImageView teamAWin, teamBWin, shirtImgVuP1, shirtImgVuP2, emojiImgVuP1, emojiImgVuP2, teamACaptain, teamBCaptain, teamAWinCup, teamBWinCup;
        CardView layout, teamAVu, teamBVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvGroupName = itemView.findViewById(R.id.tv_group_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvTeamA = itemView.findViewById(R.id.tv_team_a);
            tvTeamB = itemView.findViewById(R.id.tv_team_b);
            tvTeamAPlayer = itemView.findViewById(R.id.tv_team_a_p);
            tvTeamBPlayer = itemView.findViewById(R.id.tv_team_b_p);
            teamAWin = itemView.findViewById(R.id.team_a_win);
            teamBWin = itemView.findViewById(R.id.team_b_win);
            shirtImgVuP1 = itemView.findViewById(R.id.shirt_img_vu_p1);
            shirtImgVuP2 = itemView.findViewById(R.id.shirt_img_vu_p2);
            emojiImgVuP1 = itemView.findViewById(R.id.emoji_img_vu_p1);
            emojiImgVuP2 = itemView.findViewById(R.id.emoji_img_vu_p2);
            teamACaptain = itemView.findViewById(R.id.team_a_captain);
            teamBCaptain = itemView.findViewById(R.id.team_b_captain);
            teamAWinCup = itemView.findViewById(R.id.team_a_win_cup);
            teamBWinCup = itemView.findViewById(R.id.team_b_win_cup);
            teamAVu = itemView.findViewById(R.id.team_a_vu);
            teamBVu = itemView.findViewById(R.id.team_b_vu);
            layout = itemView.findViewById(R.id.main_layout);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}