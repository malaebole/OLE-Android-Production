package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Day;

public class OleClubDetailDayAdapter extends RecyclerView.Adapter<OleClubDetailDayAdapter.ViewHolder> {

    private final Context context;
    private final List<Day> list;
    private OnItemClickListener onItemClickListener;
    private String currentDayId = "";

    public OleClubDetailDayAdapter(Context context, List<Day> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setCurrentDayId(String currentDayId) {
        this.currentDayId = currentDayId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleclub_day, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Day day = list.get(position);
        holder.tvDayName.setText(day.getDayName().substring(0, 3));
        if (currentDayId.equalsIgnoreCase(day.getDayId())) {
            holder.cardView.setStrokeWidth(0);
            holder.bgImg.setVisibility(View.VISIBLE);
            holder.tvDayName.setTextColor(Color.WHITE);
            holder.cardView.setStrokeColor(Color.WHITE);
        }
        else {
            holder.cardView.setStrokeWidth(2);
            holder.bgImg.setVisibility(View.GONE);
            holder.tvDayName.setTextColor(Color.parseColor("#004890"));
            holder.cardView.setStrokeColor(Color.parseColor("#004890"));
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
        TextView tvDayName;
        ImageView bgImg;

        ViewHolder(View itemView) {
            super(itemView);

            tvDayName = itemView.findViewById(R.id.tv_day);
            cardView = itemView.findViewById(R.id.card_vu);
            bgImg = itemView.findViewById(R.id.bg_img);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}