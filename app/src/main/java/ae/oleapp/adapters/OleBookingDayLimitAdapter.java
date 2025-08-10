package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleKeyValuePair;

public class OleBookingDayLimitAdapter extends RecyclerView.Adapter<OleBookingDayLimitAdapter.ViewHolder> {

    private final Context context;
    private final List<OleKeyValuePair> list;
    private String selectedDay = "";
    private OnItemClickListener onItemClickListener;

    public OleBookingDayLimitAdapter(Context context, List<OleKeyValuePair> list, String selectedDay) {
        this.context = context;
        this.list = list;
        this.selectedDay = selectedDay;
    }

    public void setSelectedDay(String selectedDay) {
        this.selectedDay = selectedDay;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olerank_date, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(list.get(position).getValue());
        if (list.get(position).getKey().equalsIgnoreCase(selectedDay)) {
            holder.tvName.setTextColor(context.getResources().getColor(R.color.whiteColor));
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.blueColorNew));
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.transparent));
        }
        else {
            holder.tvName.setTextColor(context.getResources().getColor(R.color.darkTextColor));
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.separatorColor));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MaterialCardView cardView;
        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_vu);
            tvName = itemView.findViewById(R.id.tv_date);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}