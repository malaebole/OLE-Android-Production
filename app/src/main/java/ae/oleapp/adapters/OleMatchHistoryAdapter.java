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
import ae.oleapp.models.OleMatchHistory;
import ae.oleapp.util.OleProfileView;

public class OleMatchHistoryAdapter extends RecyclerView.Adapter<OleMatchHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<OleMatchHistory> list;
    private OnItemClickListener onItemClickListener;

    public OleMatchHistoryAdapter(Context context, List<OleMatchHistory> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olematch_history_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleMatchHistory match = list.get(position);
        holder.p1Profile.populateData(match.getUserData().getNickName(), match.getUserData().getPhotoUrl(), match.getUserData().getLevel(), true);
        holder.tvP1Win.setText(match.getUserData().getMatchWon());
        holder.tvDraw.setText(match.getMatchDraw());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse(match.getLastPlayed());
            dateFormat.applyPattern("EEE, dd/MM/yyyy");
            holder.tvDate.setText(dateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.tvDate.setText("");
        }
        holder.p2Profile.populateData(match.getPlayerTwo().getNickName(), match.getPlayerTwo().getPhotoUrl(), match.getPlayerTwo().getLevel(), true);
        holder.tvP2Win.setText(match.getPlayerTwo().getMatchWon());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        OleProfileView p1Profile, p2Profile;
        TextView tvP1Win, tvP2Win, tvDraw, tvDate;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);

            p1Profile = itemView.findViewById(R.id.profile_vu_1);
            p2Profile = itemView.findViewById(R.id.profile_vu_2);
            tvP1Win = itemView.findViewById(R.id.tv_p1_win);
            tvP2Win = itemView.findViewById(R.id.tv_p2_win);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDraw = itemView.findViewById(R.id.tv_draw);
            cardView = itemView.findViewById(R.id.main);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}