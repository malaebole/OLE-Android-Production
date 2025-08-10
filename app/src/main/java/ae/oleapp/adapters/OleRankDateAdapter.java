package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;

public class OleRankDateAdapter extends RecyclerView.Adapter<OleRankDateAdapter.ViewHolder> {

    private final Context context;
    private final List<Date> list;
    private int selectedDateIndex = -1;
    private int minIndex = -1;
    private int maxIndex = -1;
    private OnItemClickListener onItemClickListener;

    public OleRankDateAdapter(Context context, List<Date> list, int selectedIndex) {
        this.context = context;
        this.list = list;
        this.selectedDateIndex = selectedIndex;
        this.minIndex = selectedIndex;
    }

    public void setSelectedDateIndex(int selectedIndex) {
        this.selectedDateIndex = selectedIndex;
        this.notifyDataSetChanged();
    }

    public void setSelectedDateIndex(int minIndex, int maxIndex) {
        this.minIndex = minIndex;
        this.maxIndex = maxIndex;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olerank_date, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Date date = list.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        if (date != null) {
            String y = dateFormat.format(date);
            dateFormat.applyPattern("MMM");
            holder.tvDate.setText(String.format("%s %s", dateFormat.format(date), y));
        }
        else  {
            holder.tvDate.setText(R.string.all);
        }

        boolean isSelect = false;
        if (minIndex != -1 && maxIndex != -1 && minIndex <= position && position <= maxIndex) {
            isSelect = true;
        }
        else if (minIndex != -1 &&  minIndex == position) {
            isSelect = true;
        }
        else if (maxIndex != -1 &&  maxIndex == position) {
            isSelect = true;
        }

        if (isSelect) {
            holder.tvDate.setTextColor(context.getResources().getColor(R.color.whiteColor));
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.blueColorNew));
        }
        else {
            holder.tvDate.setTextColor(context.getResources().getColor(R.color.darkTextColor));
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));
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
        TextView tvDate;

        ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_vu);
            tvDate = itemView.findViewById(R.id.tv_date);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}