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

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePadelMatchResults;
import ae.oleapp.util.OleProfileView;

public class OleProfilePadelMatchHistoryAdapter extends RecyclerView.Adapter<OleProfilePadelMatchHistoryAdapter.MatchViewHolder> {

    private final Context context;
    private final List<OlePadelMatchResults> list;
    private ItemClickListener itemClickListener;

    public OleProfilePadelMatchHistoryAdapter(Context context, List<OlePadelMatchResults> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleprofile_padel_match_history, parent, false);
        return new MatchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        OlePadelMatchResults match = list.get(position);
        holder.myOleProfileView.populateData(match.getCreatedBy().getNickName(), match.getCreatedBy().getPhotoUrl(), match.getCreatedBy().getLevel(), true);
        holder.myPartnerOleProfileView.populateData(match.getCreatorPartner().getNickName(), match.getCreatorPartner().getPhotoUrl(), match.getCreatorPartner().getLevel(), true);
        holder.tvDate.setText(match.getMatchDate());
//        holder.tvTeamASet1.setText(String.valueOf(match.getCreatorScore().getSetOne()));
//        holder.tvTeamASet2.setText(String.valueOf(match.getCreatorScore().getSetTwo()));
//        holder.tvTeamASet3.setText(String.valueOf(match.getCreatorScore().getSetThree()));
//        holder.tvTeamBSet1.setText(String.valueOf(match.getPlayerTwoScore().getSetOne()));
//        holder.tvTeamBSet2.setText(String.valueOf(match.getPlayerTwoScore().getSetTwo()));
//        holder.tvTeamBSet3.setText(String.valueOf(match.getPlayerTwoScore().getSetThree()));

//        if (match.getCreatorScore().getSetOne() > match.getPlayerTwoScore().getSetOne()) {
//            holder.tvTeamASet1.setTextColor(context.getResources().getColor(R.color.darkTextColor));
//            holder.tvTeamBSet1.setTextColor(context.getResources().getColor(R.color.separatorColor));
//        }
//        else {
//            holder.tvTeamASet1.setTextColor(context.getResources().getColor(R.color.separatorColor));
//            holder.tvTeamBSet1.setTextColor(context.getResources().getColor(R.color.darkTextColor));
//        }
//        if (match.getCreatorScore().getSetTwo() > match.getPlayerTwoScore().getSetTwo()) {
//            holder.tvTeamASet2.setTextColor(context.getResources().getColor(R.color.darkTextColor));
//            holder.tvTeamBSet2.setTextColor(context.getResources().getColor(R.color.separatorColor));
//        }
//        else {
//            holder.tvTeamASet2.setTextColor(context.getResources().getColor(R.color.separatorColor));
//            holder.tvTeamBSet2.setTextColor(context.getResources().getColor(R.color.darkTextColor));
//        }
//        if (match.getCreatorScore().getSetThree() > match.getPlayerTwoScore().getSetThree()) {
//            holder.tvTeamASet3.setTextColor(context.getResources().getColor(R.color.darkTextColor));
//            holder.tvTeamBSet3.setTextColor(context.getResources().getColor(R.color.separatorColor));
//        }
//        else {
//            holder.tvTeamASet3.setTextColor(context.getResources().getColor(R.color.separatorColor));
//            holder.tvTeamBSet3.setTextColor(context.getResources().getColor(R.color.darkTextColor));
//        }

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

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MatchViewHolder extends RecyclerView.ViewHolder{

        OleProfileView myOleProfileView, myPartnerOleProfileView, opponentOleProfileView, opponentPartnerOleProfileView;
        TextView tvDate; //tvTeamASet1, tvTeamASet2, tvTeamASet3, tvTeamBSet1, tvTeamBSet2, tvTeamBSet3;
        ImageView winnerBadge1, winnerBadge2, imgTeamASet1, imgTeamASet2, imgTeamASet3, imgTeamBSet1, imgTeamBSet2, imgTeamBSet3;
        CardView layout;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);

            winnerBadge1 = itemView.findViewById(R.id.winner_badge_1);
            winnerBadge2 = itemView.findViewById(R.id.winner_badge_2);
            tvDate = itemView.findViewById(R.id.tv_date);
//            tvTeamASet1 = itemView.findViewById(R.id.tv_team_a_set_1);
//            tvTeamASet2 = itemView.findViewById(R.id.tv_team_a_set_2);
//            tvTeamASet3 = itemView.findViewById(R.id.tv_team_a_set_3);
//            tvTeamBSet1 = itemView.findViewById(R.id.tv_team_b_set_1);
//            tvTeamBSet2 = itemView.findViewById(R.id.tv_team_b_set_2);
//            tvTeamBSet3 = itemView.findViewById(R.id.tv_team_b_set_3);
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

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}