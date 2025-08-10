package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerInfo;

public class OleManualListAdapter extends RecyclerView.Adapter<OleManualListAdapter.ViewHolder> {

    private final Context context;
    private OnItemClickListener onItemClickListener;
    private List<OlePlayerInfo> list;
    public List<OlePlayerInfo> selectedList = new ArrayList<>();
    public final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public OleManualListAdapter(Context context, List<OlePlayerInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setDatasource(List<OlePlayerInfo> list) {
        this.list = list;
        notifyDataSetChanged();
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olemanual_player_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerInfo info = list.get(position);
        holder.tvName.setText(info.getName());
        Glide.with(context).load(info.getPhotoUrl()).placeholder(R.drawable.player_active).into(holder.imgVu);
        if (info.getPlayerPosition() != null && info.getPlayerPosition().getName() != null) {
            holder.tvPosition.setText(info.getPlayerPosition().getName());
            holder.tvPosition.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvPosition.setVisibility(View.GONE);
            holder.tvPosition.setText("");
        }

        if (isExist(info.getId()) == -1) {
            holder.imgVuCheck.setImageResource(R.drawable.p_uncheck);
        } else {
            holder.imgVuCheck.setImageResource(R.drawable.p_check);
        }

        binderHelper.bind(holder.swipeLayout, String.valueOf(position));

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
                }
            }
        });

        holder.deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = isExist(list.get(holder.getAdapterPosition()).getId());
                if (index != -1) {
                    selectedList.remove(index);
                    notifyDataSetChanged();
                }
                onItemClickListener.OnDeleteClick(v, holder.getAdapterPosition());
            }
        });

        holder.editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = isExist(list.get(holder.getAdapterPosition()).getId());
                if (index != -1) {
                    selectedList.remove(index);
                    notifyDataSetChanged();
                }
                onItemClickListener.OnEditClick(v, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgVuCheck, imgVu;
        TextView tvPosition, tvName;
        RelativeLayout relMain;
        FrameLayout deleteLayout, editLayout;
        SwipeRevealLayout swipeLayout;

        ViewHolder(View itemView) {
            super(itemView);

            imgVuCheck = itemView.findViewById(R.id.img_vu_check);
            imgVu = itemView.findViewById(R.id.player_image);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPosition = itemView.findViewById(R.id.tv_position);
            relMain = itemView.findViewById(R.id.rl_main);
            deleteLayout = itemView.findViewById(R.id.delete_layout);
            editLayout = itemView.findViewById(R.id.edit_layout);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
        void OnDeleteClick(View v, int pos);
        void OnEditClick(View v, int pos);
    }
}