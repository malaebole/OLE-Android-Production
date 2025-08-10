package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.player.OleGroupPlayersActivity;

public class OlePlayerListAdapter extends RecyclerView.Adapter<OlePlayerListAdapter.ViewHolder> {

    private final Context context;
    private OnItemClickListener onItemClickListener;
    private final List<OlePlayerInfo> list;
    public List<OlePlayerInfo> selectedList = new ArrayList<>();
    private final boolean isSelection;
    private boolean isFromFav = false;

    public OlePlayerListAdapter(Context context, List<OlePlayerInfo> list, boolean isSelection) {
        this.context = context;
        this.list = list;
        this.isSelection = isSelection;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setDatasource(List<OlePlayerInfo> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void setFromFav(boolean fromFav) {
        isFromFav = fromFav;
    }

    public void selectItem(OlePlayerInfo item) {
        int index = isExist(item.getId());
        if (index == -1) {
            selectedList.add(item);
        }
        else {
            selectedList.remove(index);
        }
        notifyDataSetChanged();
    }

    private int isExist(String id) {
        for (int i = 0; i < selectedList.size(); i++) {
            if (selectedList.get(i).getId().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return  -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleplayer_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerInfo info = list.get(position);
        Glide.with(context).load(info.getPhotoUrl()).placeholder(R.drawable.player_active).into(holder.imgVu);
        holder.tvName.setText(info.getNickName());
        holder.tvPoints.setText(String.format("%s: %s", context.getString(R.string.age), info.getAge()));
        if (info.getLevel() != null && !info.getLevel().isEmpty() && !info.getLevel().getValue().equalsIgnoreCase("")) {
            holder.tvRank.setVisibility(View.VISIBLE);
            holder.tvRank.setText(String.format("LV: %s", info.getLevel().getValue()));
        }
        else {
            holder.tvRank.setVisibility(View.INVISIBLE);
        }

        if (info.getWinPercentage() == null || info.getWinPercentage().isEmpty()) {
            holder.tvPerc.setText("0%");
        }
        else {
            holder.tvPerc.setText(String.format("%s%%", info.getWinPercentage()));
        }

        if (!isFromFav && info.getStatus() != null) {
            if (info.getStatus().equalsIgnoreCase("0")) {
                holder.tvStatus.setText(R.string.pending);
                holder.tvStatus.setTextColor(Color.parseColor("#FF7F00"));
            } else if (info.getStatus().equalsIgnoreCase("1")) {
                holder.tvStatus.setText(R.string.accepted);
                holder.tvStatus.setTextColor(Color.parseColor("#3BA016"));
            } else if (info.getStatus().equalsIgnoreCase("2")) {
                holder.tvStatus.setText(R.string.rejected);
                holder.tvStatus.setTextColor(Color.parseColor("#FF0000"));
            } else if (info.getStatus().equalsIgnoreCase("3")) {
                holder.tvStatus.setText(R.string.cancelled);
                holder.tvStatus.setTextColor(Color.parseColor("#FF0000"));
            } else {
                holder.tvStatus.setText("");
            }
        }
        else {
            holder.tvStatus.setText("");
        }

        if (isSelection) {
            holder.imgVuCheck.setVisibility(View.VISIBLE);
            if (isExist(info.getId()) == -1) {
                holder.imgVuCheck.setImageResource(R.drawable.p_uncheck);
            } else {
                holder.imgVuCheck.setImageResource(R.drawable.p_check);
            }
        }
        else {
            holder.imgVuCheck.setVisibility(View.INVISIBLE);
        }

        if (context instanceof OleGroupPlayersActivity) {
            holder.tvPerc.setText("");
        }

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
                }
            }
        });

        holder.imgVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnImageClick(v, holder.getAdapterPosition());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgVu, imgVuCheck;
        TextView tvRank, tvName, tvPerc, tvStatus, tvPoints;
        RelativeLayout relMain;

        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.player_image);
            imgVuCheck = itemView.findViewById(R.id.img_vu_check);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPoints = itemView.findViewById(R.id.tv_points);
            tvPerc = itemView.findViewById(R.id.tv_perc);
            tvStatus = itemView.findViewById(R.id.tv_status);
            relMain = itemView.findViewById(R.id.rl_main);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
        void OnImageClick(View v, int pos);
    }
}