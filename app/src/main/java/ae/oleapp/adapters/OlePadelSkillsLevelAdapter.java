package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePadelSkillLevel;
import ae.oleapp.owner.OleRankActivity;

public class OlePadelSkillsLevelAdapter extends RecyclerView.Adapter<OlePadelSkillsLevelAdapter.ViewHolder> {

    private final Context context;
    private final List<OlePadelSkillLevel> list;
    private String selectedLevelId = "";
    private OnItemClickListener onItemClickListener;

    public OlePadelSkillsLevelAdapter(Context context, List<OlePadelSkillLevel> list) {
        this.context = context;
        this.list = list;
    }

    public void setSelectedLevelId(String selectedLevelId) {
        this.selectedLevelId = selectedLevelId;
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
        holder.tvName.setText(list.get(position).getName());
        if (list.get(position).getId().equalsIgnoreCase(selectedLevelId)) {
            if (context instanceof OleRankActivity) {
                holder.tvName.setTextColor(context.getResources().getColor(R.color.yellowColor));
                holder.mainLayout.setCardBackgroundColor(Color.parseColor("#0A4B7F"));
            }
            else {
                holder.mainLayout.setCardBackgroundColor(context.getResources().getColor(R.color.blueColorNew));
                holder.tvName.setTextColor(context.getResources().getColor(R.color.whiteColor));
            }
        }
        else {
            if (context instanceof OleRankActivity) {
                holder.tvName.setTextColor(context.getResources().getColor(R.color.whiteColor));
                holder.mainLayout.setCardBackgroundColor(Color.parseColor("#0A4B7F"));
            }
            else {
                holder.mainLayout.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));
                holder.tvName.setTextColor(context.getResources().getColor(R.color.darkTextColor));
            }
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
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

        MaterialCardView mainLayout;
        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);

            mainLayout = itemView.findViewById(R.id.card_vu);
            tvName = itemView.findViewById(R.id.tv_date);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}