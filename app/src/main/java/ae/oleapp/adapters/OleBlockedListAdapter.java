package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerInfo;

public class OleBlockedListAdapter extends RecyclerView.Adapter<OleBlockedListAdapter.ViewHolder> {

    private final Context context;
    private OnItemClickListener onItemClickListener;
    private List<OlePlayerInfo> list;
    public List<OlePlayerInfo> selectedList = new ArrayList<>();
    private final boolean isSelection;

    public OleBlockedListAdapter(Context context, List<OlePlayerInfo> list, boolean isSelection) {
        this.context = context;
        this.list = list;
        this.isSelection = isSelection;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setDatasource(List<OlePlayerInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void selectItem(OlePlayerInfo item) {
        int index = isExist(item.getPhone());
        if (index == -1) {
            selectedList.add(item);
        }
        else {
            selectedList.remove(index);
        }
        notifyDataSetChanged();
    }

    private int isExist(String phone) {
        for (int i = 0; i < selectedList.size(); i++) {
            if (selectedList.get(i).getPhone().equalsIgnoreCase(phone)) {
                return i;
            }
        }
        return  -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleplayer_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerInfo info = list.get(position);
        Glide.with(context).load(info.getPhotoUrl()).placeholder(R.drawable.player_active).into(holder.imgVu);
        holder.tvName.setText(info.getName());
        holder.tvPoints.setText(context.getString(R.string.blocked_place, info.getBlockedDate()));
        holder.tvPoints.setTextColor(context.getResources().getColor(R.color.subTextColor));
        holder.tvStatus.setText("");
        if (info.getLevel() != null && !info.getLevel().isEmpty() && !info.getLevel().getValue().equalsIgnoreCase("")) {
            holder.tvRank.setVisibility(View.VISIBLE);
            holder.tvRank.setText(String.format("LV: %s", info.getLevel().getValue()));
        }
        else {
            holder.tvRank.setVisibility(View.INVISIBLE);
        }

        if (info.getWinPercentage().isEmpty()) {
            holder.tvPerc.setText("0%");
        }
        else {
            holder.tvPerc.setText(String.format("%s%%", info.getWinPercentage()));
        }

        if (isSelection) {
            holder.imgVuCheck.setVisibility(View.VISIBLE);
            if (isExist(info.getPhone()) == -1) {
                holder.imgVuCheck.setImageResource(R.drawable.uncheck);
            } else {
                holder.imgVuCheck.setImageResource(R.drawable.blue_check);
            }
        }
        else {
            holder.imgVuCheck.setVisibility(View.INVISIBLE);
        }

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
                }
            }
        });

        holder.cardVu.setOnClickListener(new View.OnClickListener() {
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
        MaterialCardView cardVu;

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
            cardVu = itemView.findViewById(R.id.cardvu);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
        void OnImageClick(View v, int pos);
    }
}