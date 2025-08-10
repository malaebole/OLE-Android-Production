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

public class OleRankAgeAdapter extends RecyclerView.Adapter<OleRankAgeAdapter.ViewHolder> {

    private final Context context;
    private final List<String> list;
    private int selectedIndex = -1;
    private OnItemClickListener onItemClickListener;

    public OleRankAgeAdapter(Context context, List<String> list, int selectedIndex) {
        this.context = context;
        this.list = list;
        this.selectedIndex = selectedIndex;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
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
        String str = list.get(position);
        holder.tvDate.setText(str);

        if (selectedIndex == position) {
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