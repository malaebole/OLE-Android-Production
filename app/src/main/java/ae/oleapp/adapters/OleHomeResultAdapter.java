package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import ae.oleapp.util.OlePadelProfileView;
import ae.oleapp.util.OleProfileView;

public class OleHomeResultAdapter extends RecyclerView.Adapter<OleHomeResultAdapter.ViewHolder> {

    private final Context context;
    private final List<Object> list;
    private ItemClickListener itemClickListener;

    public OleHomeResultAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olehome_result, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object object = list.get(position);
        if (object instanceof OlePadelMatchResults) {
            holder.footballVu.setVisibility(View.GONE);
            holder.padelVu.setVisibility(View.VISIBLE);
            OlePadelMatchResults results = (OlePadelMatchResults) object;
            holder.tvPadelDate.setText(results.getMatchDate());
            holder.myProfileVu.populateData(results.getCreatedBy().getNickName(), results.getCreatedBy().getPhotoUrl(), results.getCreatedBy().getLevel(), true);
            holder.myPartnerProfileVu.populateData(results.getCreatorPartner().getNickName(), results.getCreatorPartner().getPhotoUrl(), results.getCreatorPartner().getLevel(), true);
            if (results.getCreatorWin().equalsIgnoreCase("1")) {
                holder.p1Win.setImageResource(R.drawable.match_winner_badge);
            }
            else {
                holder.p1Win.setImageResource(R.drawable.match_loser_badge);
            }
            holder.opponentProfileVu.populateData(results.getPlayerTwo().getNickName(), results.getPlayerTwo().getPhotoUrl(), results.getPlayerTwo().getLevel(), true);
            holder.opponentPartnerProfileVu.populateData(results.getPlayerTwoPartner().getNickName(), results.getPlayerTwoPartner().getPhotoUrl(), results.getPlayerTwoPartner().getLevel(), true);
            if (results.getPlayerTwoWin().equalsIgnoreCase("1")) {
                holder.p2Win.setImageResource(R.drawable.match_winner_badge);
            }
            else {
                holder.p2Win.setImageResource(R.drawable.match_loser_badge);
            }
        }
        else {
            holder.footballVu.setVisibility(View.VISIBLE);
            holder.padelVu.setVisibility(View.GONE);
            OleMatchResults results = (OleMatchResults) object;
            holder.p1ProfileVu.populateData(results.getPlayerOne().getNickName(), results.getPlayerOne().getPhotoUrl(), results.getPlayerOne().getLevel(), true);
            holder.p2ProfileVu.populateData(results.getPlayerTwo().getNickName(), results.getPlayerTwo().getPhotoUrl(), results.getPlayerTwo().getLevel(), true);
            holder.tvScore.setText(String.format("%s : %s", results.getPlayerOne().getGoals(), results.getPlayerTwo().getGoals()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                Date date = dateFormat.parse(results.getMatchDate());
                dateFormat.applyPattern("dd/MM/yyyy");
                holder.tvDate.setText(dateFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
                holder.tvDate.setText("");
            }
            if (results.getPlayerOne().getMatchStatus().equalsIgnoreCase("win")) {
                holder.p1Trophy.setVisibility(View.VISIBLE);
                holder.p2Trophy.setVisibility(View.INVISIBLE);
            }
            else if (results.getPlayerOne().getMatchStatus().equalsIgnoreCase("lost")) {
                holder.p1Trophy.setVisibility(View.INVISIBLE);
                holder.p2Trophy.setVisibility(View.VISIBLE);
            }
            else {
                holder.p1Trophy.setVisibility(View.INVISIBLE);
                holder.p2Trophy.setVisibility(View.INVISIBLE);
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

    class ViewHolder extends RecyclerView.ViewHolder{


        OleProfileView p1ProfileVu, p2ProfileVu;
        TextView tvScore, tvDate, tvPadelDate;
        CardView layout;
        ImageView p1Trophy, p2Trophy, p1Win, p2Win;
        RelativeLayout footballVu, padelVu;
        OlePadelProfileView myProfileVu, myPartnerProfileVu, opponentProfileVu, opponentPartnerProfileVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            p1ProfileVu = itemView.findViewById(R.id.profile_vu_1);
            p2ProfileVu = itemView.findViewById(R.id.profile_vu_2);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvDate = itemView.findViewById(R.id.tv_date);
            layout = itemView.findViewById(R.id.rel_main);
            p1Trophy = itemView.findViewById(R.id.trophy_p1);
            p2Trophy = itemView.findViewById(R.id.trophy_p2);
            tvPadelDate = itemView.findViewById(R.id.tv_padel_date);
            p1Win = itemView.findViewById(R.id.p1_win);
            p2Win = itemView.findViewById(R.id.p2_win);
            footballVu = itemView.findViewById(R.id.football_vu);
            padelVu = itemView.findViewById(R.id.padel_vu);
            myProfileVu = itemView.findViewById(R.id.my_profile_vu);
            myPartnerProfileVu = itemView.findViewById(R.id.my_partner_profile_vu);
            opponentProfileVu = itemView.findViewById(R.id.opponent_profile_vu);
            opponentPartnerProfileVu = itemView.findViewById(R.id.opponent_partner_profile_vu);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}