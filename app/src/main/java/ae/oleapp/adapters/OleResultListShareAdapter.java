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
import ae.oleapp.models.OlePadelMatchResults;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleProfileView;

public class OleResultListShareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Object> list;
    private ItemClickListener itemClickListener;

    private static final int PADEL_RESULT = 1;
    private static final int FOOTBALL_RESULT = 2;

    public OleResultListShareAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == PADEL_RESULT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleprofile_padel_match_history, parent, false);
            return new PadelViewHolder(v);
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleresult_list, parent, false);
            return new FootballViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == PADEL_RESULT) {
            OlePadelMatchResults match = (OlePadelMatchResults) list.get(position);
            PadelViewHolder holder = (PadelViewHolder) viewHolder;
            holder.myOleProfileView.populateData(match.getCreatedBy().getNickName(), match.getCreatedBy().getPhotoUrl(), match.getCreatedBy().getLevel(), true);
            holder.myPartnerOleProfileView.populateData(match.getCreatorPartner().getNickName(), match.getCreatorPartner().getPhotoUrl(), match.getCreatorPartner().getLevel(), true);
            holder.tvDate.setText(match.getMatchDate());
            if (match.getCreatorScore().getSetOne() == 1) {
                holder.imgTeamASet1.setImageResource(R.drawable.set_one_green);
            }
            else {
                holder.imgTeamASet1.setImageResource(R.drawable.set_one_red);
            }
            if (match.getCreatorScore().getSetTwo() == 1) {
                holder.imgTeamASet2.setImageResource(R.drawable.set_two_green);
            }
            else {
                holder.imgTeamASet2.setImageResource(R.drawable.set_two_red);
            }
            if (match.getCreatorScore().getSetThree() == 1) {
                holder.imgTeamASet3.setImageResource(R.drawable.set_three_green);
            }
            else {
                holder.imgTeamASet3.setImageResource(R.drawable.set_three_red);
            }

            if (match.getPlayerTwoScore().getSetOne() == 1) {
                holder.imgTeamBSet1.setImageResource(R.drawable.set_one_green);
            }
            else {
                holder.imgTeamBSet1.setImageResource(R.drawable.set_one_red);
            }
            if (match.getPlayerTwoScore().getSetTwo() == 1) {
                holder.imgTeamBSet2.setImageResource(R.drawable.set_two_green);
            }
            else {
                holder.imgTeamBSet2.setImageResource(R.drawable.set_two_red);
            }
            if (match.getPlayerTwoScore().getSetThree() == 1) {
                holder.imgTeamBSet3.setImageResource(R.drawable.set_three_green);
            }
            else {
                holder.imgTeamBSet3.setImageResource(R.drawable.set_three_red);
            }

            if (match.getCreatorWin().equalsIgnoreCase("1")) {
                holder.winnerBadge1.setImageResource(R.drawable.match_winner_badge);
            }
            else {
                holder.winnerBadge1.setImageResource(R.drawable.match_loser_badge);
            }

            holder.opponentOleProfileView.populateData(match.getPlayerTwo().getNickName(), match.getPlayerTwo().getPhotoUrl(), match.getPlayerTwo().getLevel(), true);
            holder.opponentPartnerOleProfileView.populateData(match.getPlayerTwoPartner().getNickName(), match.getPlayerTwoPartner().getPhotoUrl(), match.getPlayerTwoPartner().getLevel(), true);
            if (match.getPlayerTwoWin().equalsIgnoreCase("1")) {
                holder.winnerBadge2.setImageResource(R.drawable.match_winner_badge);
            }
            else {
                holder.winnerBadge2.setImageResource(R.drawable.match_loser_badge);
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
        else {
            OleMatchResults match = (OleMatchResults) list.get(position);
            FootballViewHolder holder = (FootballViewHolder) viewHolder;
            holder.oleProfileView1.populateData(match.getPlayerOne().getNickName(), match.getPlayerOne().getPhotoUrl(), match.getPlayerOne().getLevel(), true);
            if (Functions.getAppLangStr(context).equalsIgnoreCase("ar")) {
                holder.tvPoints.setText(String.format("%s:%s", match.getPlayerTwo().getGoals(), match.getPlayerOne().getGoals()));
            }
            else {
                holder.tvPoints.setText(String.format("%s:%s", match.getPlayerOne().getGoals(), match.getPlayerTwo().getGoals()));
            }
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
                holder.tvP1Status.setText(R.string.win);
            }
            else if (match.getPlayerOne().getMatchStatus().equalsIgnoreCase("lost")) {
                holder.tvP1Status.setText(R.string.lost);
            }
            else {
                holder.tvP1Status.setText(R.string.draw);
            }

            OlePlayerInfo player2 = match.getPlayerTwo();
            // check object null or not
            if (!player2.isEmpty()) {
                holder.oleProfileView2.populateData(player2.getNickName(), player2.getPhotoUrl(), player2.getLevel(), true);
                if (match.getPlayerTwo().getMatchStatus().equalsIgnoreCase("win")) {
                    holder.tvP2Status.setText(R.string.win);
                }
                else if (match.getPlayerTwo().getMatchStatus().equalsIgnoreCase("lost")) {
                    holder.tvP2Status.setText(R.string.lost);
                }
                else {
                    holder.tvP2Status.setText(R.string.draw);
                }
            }
            else {
                holder.oleProfileView2.populateData("", "", null, false);
            }

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof OlePadelMatchResults) {
            return PADEL_RESULT;
        }
        else {
            return FOOTBALL_RESULT;
        }
    }

    class PadelViewHolder extends RecyclerView.ViewHolder{

        OleProfileView myOleProfileView, myPartnerOleProfileView, opponentOleProfileView, opponentPartnerOleProfileView;
        TextView tvDate;
        ImageView winnerBadge1, winnerBadge2, imgTeamASet1, imgTeamASet2, imgTeamASet3, imgTeamBSet1, imgTeamBSet2, imgTeamBSet3;
        CardView layout;

        public PadelViewHolder(@NonNull View itemView) {
            super(itemView);

            winnerBadge1 = itemView.findViewById(R.id.winner_badge_1);
            winnerBadge2 = itemView.findViewById(R.id.winner_badge_2);
            tvDate = itemView.findViewById(R.id.tv_date);
            imgTeamASet1 = itemView.findViewById(R.id.img_team_a_set_1);
            imgTeamASet2 = itemView.findViewById(R.id.img_team_a_set_2);
            imgTeamASet3 = itemView.findViewById(R.id.img_team_a_set_3);
            imgTeamBSet1 = itemView.findViewById(R.id.img_team_b_set_1);
            imgTeamBSet2 = itemView.findViewById(R.id.img_team_b_set_2);
            imgTeamBSet3 = itemView.findViewById(R.id.img_team_b_set_3);
            myOleProfileView = itemView.findViewById(R.id.my_profile_vu);
            myPartnerOleProfileView = itemView.findViewById(R.id.my_partner_profile_vu);
            opponentOleProfileView = itemView.findViewById(R.id.opponent_profile_vu);
            opponentPartnerOleProfileView = itemView.findViewById(R.id.opponent_partner_profile_vu);
            layout = itemView.findViewById(R.id.rel_main);

        }
    }

    class FootballViewHolder extends RecyclerView.ViewHolder{

        OleProfileView oleProfileView1, oleProfileView2;
        TextView tvDate, tvPoints, tvP1Status, tvP2Status;
        CardView layout;

        public FootballViewHolder(@NonNull View itemView) {
            super(itemView);

            tvP1Status = itemView.findViewById(R.id.tv_p1_status);
            tvP2Status = itemView.findViewById(R.id.tv_p2_status);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvPoints = itemView.findViewById(R.id.tv_points);
            oleProfileView1 = itemView.findViewById(R.id.profile_vu_1);
            oleProfileView2 = itemView.findViewById(R.id.profile_vu_2);
            layout = itemView.findViewById(R.id.rel_main);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}