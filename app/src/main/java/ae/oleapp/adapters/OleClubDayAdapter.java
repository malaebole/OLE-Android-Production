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

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Day;
import ae.oleapp.models.OleKeyValuePair;

public class OleClubDayAdapter extends RecyclerView.Adapter<OleClubDayAdapter.ViewHolder> {

    private final Context context;
    private final List<OleKeyValuePair> list;
    private OnItemClickListener onItemClickListener;
    public List<Day> selectedDays = new ArrayList<>();
    private String currentDayId = "";

    public OleClubDayAdapter(Context context, List<OleKeyValuePair> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setCurrentDayId(String currentDayId) {
        this.currentDayId = currentDayId;
    }

    public String getCurrentDayId() {
        return currentDayId;
    }

    public int checkDayExist(String dayId) {
        int index = -1;
        for (int i = 0; i < selectedDays.size(); i++) {
            if (selectedDays.get(i).getDayId().equalsIgnoreCase(dayId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleclub_day, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleKeyValuePair day = list.get(position);
        holder.tvDayName.setText(day.getValue().substring(0, 3));
        if (currentDayId.equalsIgnoreCase(day.getKey())) {
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