package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleHomeNotification;
import ae.oleapp.util.OleProfileView;

public class OleRequestSliderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<OleHomeNotification> list;
    private ItemClickListener itemClickListener;

    public OleRequestSliderAdapter(Context context, List<OleHomeNotification> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olehome_request, parent, false);
        return new RequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        OleHomeNotification notification = list.get(position);
        RequestViewHolder holder = (RequestViewHolder)viewHolder;
        holder.oleProfileView.populateData(notification.getBy().getNickName(), notification.getBy().getPhotoUrl(), notification.getBy().getLevel(), true);
        holder.tvDate.setText(notification.getBookingDate());
        holder.tvTime.setText(notification.getBookingTime().split("-")[0]);
        if (notification.getType().equalsIgnoreCase("new_challenge")) {
            holder.tvType.setText(context.getString(R.string.challenged_you));
            holder.tvType.setTextColor(context.getResources().getColor(R.color.redColor));
        }
        else if (notification.getType().equalsIgnoreCase("friendly_game_request")) {
            holder.tvType.setText(context.getString(R.string.request_friendly_game));
            holder.tvType.setTextColor(context.getResources().getColor(R.color.greenColor));
        }
        else {
            holder.tvType.setText("");
        }
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.acceptClicked(v, holder.getAdapterPosition());
            }
        });
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

    class RequestViewHolder extends RecyclerView.ViewHolder{

        TextView tvDate, tvType, tvTime;
        OleProfileView oleProfileView;
        CardView layout, btnAccept;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvType = itemView.findViewById(R.id.tv_type);
            oleProfileView = itemView.findViewById(R.id.profile_vu);
            layout = itemView.findViewById(R.id.rel_main);
            btnAccept = itemView.findViewById(R.id.btn_accept);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void acceptClicked(View view, int pos);
    }
}