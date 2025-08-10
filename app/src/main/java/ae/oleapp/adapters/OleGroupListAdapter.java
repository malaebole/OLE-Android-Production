package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayersGroup;

public class OleGroupListAdapter extends RecyclerView.Adapter<OleGroupListAdapter.ViewHolder> {

    private final Context context;
    private final List<OlePlayersGroup> list;
    private OnItemClickListener onItemClickListener;

    public OleGroupListAdapter(Context context, List<OlePlayersGroup> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olegroup_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(list.get(position).getName());
        holder.tvPlayers.setText(String.format(Locale.ENGLISH, "%d %s", list.get(position).getPlayersCount(), context.getString(R.string.players)));

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnDeleteClick(v, holder.getAdapterPosition());
            }
        });
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

        RelativeLayout mainLayout;
        TextView tvName, tvPlayers;
        ImageButton btnDelete;

        ViewHolder(View itemView) {
            super(itemView);

            mainLayout = itemView.findViewById(R.id.rl_main);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPlayers = itemView.findViewById(R.id.tv_players);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
        void OnDeleteClick(View v, int pos);
    }
}