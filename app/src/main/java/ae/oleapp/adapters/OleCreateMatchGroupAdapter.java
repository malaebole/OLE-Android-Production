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
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayersGroup;

public class OleCreateMatchGroupAdapter extends RecyclerView.Adapter<OleCreateMatchGroupAdapter.ViewHolder> {

    private final Context context;
    private final List<OlePlayersGroup> list;
    private int selectedIndex = -1;
    private OnItemClickListener onItemClickListener;

    public OleCreateMatchGroupAdapter(Context context, List<OlePlayersGroup> list) {
        this.context = context;
        this.list = list;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
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
        holder.tvName.setText(String.format(Locale.ENGLISH, "%s(%d %s)", list.get(position).getName(), list.get(position).getPlayersCount(), context.getString(R.string.players)));
        if (selectedIndex == position) {
            holder.mainLayout.setCardBackgroundColor(context.getResources().getColor(R.color.blueColorNew));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.whiteColor));
        }
        else {
            holder.mainLayout.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.darkTextColor));
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