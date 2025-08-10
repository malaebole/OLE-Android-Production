package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.OleHistoryDetail;

public class OleHistoryDetailAdapter extends RecyclerView.Adapter<OleHistoryDetailAdapter.ViewHolder> {

    private final Context context;
    private final List<OleHistoryDetail> list;

    public OleHistoryDetailAdapter(Context context, List<OleHistoryDetail> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olehistory_detail_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleHistoryDetail history = list.get(position);
        holder.tvClub.setText(history.getClubName());
        if (history.getPlayerOne().getMatchStatus().equalsIgnoreCase("won")) {
            holder.tvP1Status.setText(context.getString(R.string.win));
            holder.tvP1Status.setTextColor(Color.parseColor("#49D483"));
        }
        else if (history.getPlayerOne().getMatchStatus().equalsIgnoreCase("lost")) {
            holder.tvP1Status.setText(context.getString(R.string.lost));
            holder.tvP1Status.setTextColor(Color.parseColor("#FE5517"));
        }
        else {
            holder.tvP1Status.setText(context.getString(R.string.draw));
            holder.tvP1Status.setTextColor(Color.parseColor("#FE5517"));
        }

        if (history.getPlayerTwo().getMatchStatus().equalsIgnoreCase("won")) {
            holder.tvP2Status.setText(context.getString(R.string.win));
            holder.tvP2Status.setTextColor(Color.parseColor("#49D483"));
        }
        else if (history.getPlayerTwo().getMatchStatus().equalsIgnoreCase("lost")) {
            holder.tvP2Status.setText(context.getString(R.string.lost));
            holder.tvP2Status.setTextColor(Color.parseColor("#FE5517"));
        }
        else {
            holder.tvP2Status.setText(context.getString(R.string.draw));
            holder.tvP2Status.setTextColor(Color.parseColor("#FE5517"));
        }

        holder.tvPoints.setText(String.format("%s : %s", history.getPlayerOne().getGoals(), history.getPlayerTwo().getGoals()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse(history.getBookingDate());
            dateFormat.applyPattern("dd/MM/yyyy");
            holder.tvDate.setText(dateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.tvDate.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvDate, tvClub, tvP1Status, tvP2Status, tvPoints;

        ViewHolder(View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_date);
            tvClub = itemView.findViewById(R.id.tv_club);
            tvP1Status = itemView.findViewById(R.id.tv_p1_status);
            tvP2Status = itemView.findViewById(R.id.tv_p2_status);
            tvPoints = itemView.findViewById(R.id.tv_points);
        }
    }
}