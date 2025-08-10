package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerInfo;

public class OleFriendlyGameShareAdapter extends RecyclerView.Adapter<OleFriendlyGameShareAdapter.ViewHolder> {

    private final Context context;
    private final List<OlePlayerInfo> list;
    private int requiredPlayers = 0;

    public OleFriendlyGameShareAdapter(Context context, List<OlePlayerInfo> list, int requiredPlayers) {
        this.context = context;
        this.list = list;
        this.requiredPlayers = requiredPlayers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olefriendly_game_share, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < list.size()) {
            holder.imgVu.setVisibility(View.VISIBLE);
            holder.tvQMark.setVisibility(View.INVISIBLE);
            OlePlayerInfo info = list.get(position);
            Glide.with(context).load(info.getPhotoUrl()).placeholder(R.drawable.player_active).into(holder.imgVu);
        }
        else {
            holder.imgVu.setVisibility(View.INVISIBLE);
            holder.tvQMark.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return requiredPlayers;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgVu;
        TextView tvQMark;

        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            tvQMark = itemView.findViewById(R.id.tv_q_mark);
        }
    }
}