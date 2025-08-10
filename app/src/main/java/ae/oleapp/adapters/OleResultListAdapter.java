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
import ae.oleapp.models.OleMatchResults;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleProfileView;

public class OleResultListAdapter extends RecyclerView.Adapter<OleResultListAdapter.MatchViewHolder> {

    private final Context context;
    private final List<OleMatchResults> list;
    private ItemClickListener itemClickListener;

    public OleResultListAdapter(Context context, List<OleMatchResults> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleresult_list, parent, false);
        return new MatchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        OleMatchResults match = list.get(position);
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MatchViewHolder extends RecyclerView.ViewHolder{

        OleProfileView oleProfileView1, oleProfileView2;
        TextView tvDate, tvPoints, tvP1Status, tvP2Status;
        CardView layout;

        public MatchViewHolder(@NonNull View itemView) {
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