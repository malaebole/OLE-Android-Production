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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.OleMatchResults;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.player.OleProfileMatchHistoryDetailsActivity;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleProfileView;

public class OleProfileMatchHistoryAdapter extends RecyclerView.Adapter<OleProfileMatchHistoryAdapter.MatchViewHolder> {

    private final Context context;
    private final List<OleMatchResults> list;
    private ItemClickListener itemClickListener;

    public OleProfileMatchHistoryAdapter(Context context, List<OleMatchResults> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleprofile_match_history, parent, false);
        return new MatchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        OleMatchResults match = list.get(position);
        holder.oleProfileView1.populateData(match.getPlayerOne().getNickName(), match.getPlayerOne().getPhotoUrl(), match.getPlayerOne().getLevel(), true);
        holder.tvPoints.setText(String.format("%s:%s", match.getPlayerOne().getGoals(), match.getPlayerTwo().getGoals()));
        holder.tvClub.setText(match.getClubName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse(match.getMatchDate());
            dateFormat.applyPattern("dd/MM/yyyy");
            holder.tvDate.setText(dateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.tvDate.setText("");
        }

        if (match.getPlayerOne().getMatchStatus().equalsIgnoreCase("win")) {
            holder.winnerBadge1.setVisibility(View.VISIBLE);
        }
        else {
            holder.winnerBadge1.setVisibility(View.GONE);
        }

        OlePlayerInfo player2 = match.getPlayerTwo();
        // check object null or not
        if (!player2.isEmpty()) {
            holder.oleProfileView2.populateData(player2.getNickName(), player2.getPhotoUrl(), player2.getLevel(), true);
            if (match.getPlayerTwo().getMatchStatus().equalsIgnoreCase("win")) {
                holder.winnerBadge2.setVisibility(View.VISIBLE);
            }
            else {
                holder.winnerBadge2.setVisibility(View.GONE);
            }
        }
        else {
            holder.oleProfileView2.populateData("", "", null, false);
        }

        if (context instanceof OleProfileMatchHistoryDetailsActivity) {
            if (Functions.getAppLangStr(context).equalsIgnoreCase("ar")) {
                holder.tvPoints.setText(String.format("%s:%s", match.getPlayerTwo().getGoals(), match.getPlayerOne().getGoals()));
            }
            else {
                holder.tvPoints.setText(String.format("%s:%s", match.getPlayerOne().getGoals(), match.getPlayerTwo().getGoals()));
            }
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

    class MatchViewHolder extends RecyclerView.ViewHolder{

        OleProfileView oleProfileView1, oleProfileView2;
        TextView tvDate, tvPoints, tvClub;
        ImageView winnerBadge1, winnerBadge2;
        CardView layout;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);

            winnerBadge1 = itemView.findViewById(R.id.winner_badge_1);
            winnerBadge2 = itemView.findViewById(R.id.winner_badge_2);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvPoints = itemView.findViewById(R.id.tv_points);
            tvClub = itemView.findViewById(R.id.tv_club);
            oleProfileView1 = itemView.findViewById(R.id.profile_vu_1);
            oleProfileView2 = itemView.findViewById(R.id.profile_vu_2);
            layout = itemView.findViewById(R.id.rel_main);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}