package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleSmsData;

public class OleSmsServiceAdapter extends RecyclerView.Adapter<OleSmsServiceAdapter.ViewHolder> {

    private final Context context;
    private final List<OleSmsData> list;
    private OnItemClickListener onItemClick;

    public OleSmsServiceAdapter(Context context, List<OleSmsData> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClick(OnItemClickListener onItemClick) {
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olesms_service, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleSmsData data = list.get(position);
        holder.tvAmount.setText(String.format("%s: %s %s", context.getString(R.string.paid_amount), data.getPaidPrice(),data.getCurrency()));
        if (data.getStatus().equalsIgnoreCase("pending")) {
            holder.tvStatus.setText(R.string.pending);
            holder.tvStatus.setTextColor(Color.parseColor("#FE5517"));
        }
        else if (data.getStatus().equalsIgnoreCase("approved")) {
            holder.tvStatus.setText(R.string.approved);
            holder.tvStatus.setTextColor(Color.parseColor("#49D483"));
        }
        else if (data.getStatus().equalsIgnoreCase("sent")) {
            holder.tvStatus.setText(R.string.sent);
            holder.tvStatus.setTextColor(Color.parseColor("#49D483"));
        }
        else if (data.getStatus().equalsIgnoreCase("rejected")) {
            holder.tvStatus.setText(R.string.rejected);
            holder.tvStatus.setTextColor(Color.parseColor("#FE5517"));
        }
        else {
            holder.tvStatus.setText("");
        }
        if (data.getSmsFor().equalsIgnoreCase("top_ten")) {
            holder.tvTitle.setText(context.getString(R.string.top_10_players));
        }
        else if (data.getSmsFor().equalsIgnoreCase("call")) {
            holder.tvTitle.setText(context.getString(R.string.call_booking_players));
        }
        else if (data.getSmsFor().equalsIgnoreCase("app")) {
            holder.tvTitle.setText(context.getString(R.string.online_booking_players));
        }
        else {
            holder.tvTitle.setText(context.getString(R.string.all_players));
        }

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.itemClicked(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CardView relMain;
        TextView tvTitle, tvAmount, tvStatus;

        ViewHolder(View itemView) {
            super(itemView);

            relMain = itemView.findViewById(R.id.rel_main);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }

    public interface OnItemClickListener {
        void itemClicked(View view, int position);
    }
}