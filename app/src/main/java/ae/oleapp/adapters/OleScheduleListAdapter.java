package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleScheduleList;

public class OleScheduleListAdapter extends RecyclerView.Adapter<OleScheduleListAdapter.ViewHolder> {

    private final Context context;
    private final List<OleScheduleList> list;
    private ItemClickListener itemClickListener;

    public OleScheduleListAdapter(Context context, List<OleScheduleList> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleschedule_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleScheduleList booking = list.get(position);
        holder.tvSize.setText(booking.getFieldSize());
        holder.tvName.setText(booking.getName());
        holder.tvDate.setText(String.format("%s %s %s", booking.getFromDate(), context.getString(R.string.to), booking.getToDate()));
        holder.tvTime.setText(booking.getBookingTime());
        holder.tvDuration.setText(booking.getDuration());
        holder.tvDays.setText(context.getString(R.string.every_place, booking.getDays()));

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getAdapterPosition());
            }
        });
        holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.delClicked(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvDate, tvSize, tvTime, tvDuration, tvDays;
        CardView mainLayout;
        ImageButton btnDel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSize = itemView.findViewById(R.id.tv_size);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvDays = itemView.findViewById(R.id.tv_days);
            mainLayout = itemView.findViewById(R.id.rel_main);
            btnDel = itemView.findViewById(R.id.btn_del);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void delClicked(View view, int pos);
    }
}