package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.OleProfileView;

public class OleProfilePlayersAdapter extends RecyclerView.Adapter<OleProfilePlayersAdapter.ViewHolder> {

    private final Context context;
    private OnItemClickListener onItemClickListener;
    private final List<OlePlayerInfo> list;

    public OleProfilePlayersAdapter(Context context, List<OlePlayerInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleprofile_players, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerInfo info = list.get(position);
        holder.oleProfileView.populateData(info.getNickName(), info.getPhotoUrl(), info.getLevel(), true);

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        OleProfileView oleProfileView;
        RelativeLayout relMain;

        ViewHolder(View itemView) {
            super(itemView);

            oleProfileView = itemView.findViewById(R.id.profile_vu);
            relMain = itemView.findViewById(R.id.rl_main);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}