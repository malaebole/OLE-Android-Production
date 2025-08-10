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
import ae.oleapp.models.OlePlayerInfo;

public class OleBookingCountAdapter extends RecyclerView.Adapter<OleBookingCountAdapter.ViewHolder> {

    private final Context context;
    private OnItemClickListener onItemClickListener;
    private List<OlePlayerInfo> list;
    private String filter = "";

    public OleBookingCountAdapter(Context context, List<OlePlayerInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setDatasource(List<OlePlayerInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olebooking_count, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerInfo info = list.get(position);
        Glide.with(context).load(info.getPhotoUrl()).placeholder(R.drawable.player_active).into(holder.imgVu);
        holder.tvName.setText(info.getName());
        holder.tvClubName.setVisibility(View.GONE);
        holder.tvLimit.setVisibility(View.VISIBLE);
        if (filter.equalsIgnoreCase("booking_dates_limit")) {
            holder.tvLimit.setText(String.format("%s: %s %s", context.getString(R.string.booking_dates_limit), info.getDays(), context.getString(R.string.days)));
        }
        else if (filter.equalsIgnoreCase("pending_balance")) {
            holder.tvLimit.setText(String.format("%s: %s %s", context.getString(R.string.pending_balance), info.getPendingBalance(), info.getCurrency()));
        }
        else if (filter.equalsIgnoreCase("most_app_bookings")) {
            holder.tvLimit.setText(String.format("%s: %s", context.getString(R.string.online_bookings), info.getTotalBookings()));
        }
        else if (filter.equalsIgnoreCase("most_call_bookings")) {
            holder.tvLimit.setText(String.format("%s: %s", context.getString(R.string.call_bookings), info.getTotalBookings()));
        }
        else if (filter.equalsIgnoreCase("most_cancellation")) {
            holder.tvLimit.setText(String.format("%s: %s", context.getString(R.string.cancelled_bookings), info.getTotalBookings()));
        }
        else if (filter.equalsIgnoreCase("loyalty_cards")) {
            holder.tvLimit.setText(String.format("%s: %s", context.getString(R.string.remaining_bookings), info.getRemainingBookings()));
            holder.tvClubName.setVisibility(View.VISIBLE);
            holder.tvClubName.setText(String.format("%s: %s", context.getString(R.string.club_name), info.getClubName()));
        }
        else {
            holder.tvLimit.setVisibility(View.GONE);
        }
        if (info.getLevel() != null && !info.getLevel().isEmpty() && !info.getLevel().getValue().equalsIgnoreCase("")) {
            holder.tvRank.setVisibility(View.VISIBLE);
            holder.tvRank.setText(String.format("LV: %s", info.getLevel().getValue()));
        }
        else {
            holder.tvRank.setVisibility(View.INVISIBLE);
        }

        holder.relMain.setOnClickListener(new View.OnClickListener() {
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

        ImageView imgVu;
        TextView tvRank, tvName, tvLimit, tvClubName;
        RelativeLayout relMain;

        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.player_image);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvName = itemView.findViewById(R.id.tv_name);
            tvClubName = itemView.findViewById(R.id.tv_club_name);
            tvLimit = itemView.findViewById(R.id.tv_limit);
            relMain = itemView.findViewById(R.id.rl_main);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}