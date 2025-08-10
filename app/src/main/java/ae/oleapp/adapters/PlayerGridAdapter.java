package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.ExpenseList;
import ae.oleapp.models.PlayerInfo;

public class PlayerGridAdapter extends RecyclerView.Adapter<PlayerGridAdapter.ViewHolder> {

    private final Context context;
    private final List<PlayerInfo> list;
    private ItemClickListener itemClickListener;
    private boolean setManualWidth = false;
    private final List<PlayerInfo> selectedList = new ArrayList<>();
    private int playersLimit = 0;
    private boolean isEmployee = false;
    private String selectedId = "";
    private Boolean vsMode = false;


    public PlayerGridAdapter(Context context, List<PlayerInfo> list, boolean setManualWidth, int playersLimit, boolean vsMode) {
        this.context = context;
        this.list = list;
        this.setManualWidth = setManualWidth;
        this.playersLimit = playersLimit;
        this.vsMode = vsMode;
    }

    public PlayerGridAdapter(Context context, List<PlayerInfo> list, boolean setManualWidth, boolean vsMode) {
        this(context, list, setManualWidth, 1000, vsMode);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public void updateData(List<PlayerInfo> newData) {
        list.clear();
        list.addAll(newData);
        notifyDataSetChanged();
    }


    public List<PlayerInfo> getSelectedList() {
        return selectedList;
    }

    public void selectPos(int pos) {
        int index = isExist(list.get(pos).getId());
        if (index == -1) {

            if (vsMode){
                selectedList.add(list.get(pos));

            }else{

                if (selectedList.size() < playersLimit) {
                    selectedList.add(list.get(pos));
                }
            }

        }
        else {
            selectedList.remove(index);
        }
        notifyDataSetChanged();
    }
    public void checkEmployee(Boolean isEmployee, String selectedId) {
       this.isEmployee = isEmployee;
       this.selectedId = selectedId;
        notifyDataSetChanged();
    }

    private int isExist(String id) {
        int index = -1;
        for (int i = 0; i < selectedList.size(); i++) {
            if (selectedList.get(i).getId().equalsIgnoreCase(id)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_grid, parent, false);
        if (setManualWidth) {
            setItemWidth(v, parent);
        }
        return new ViewHolder(v);
    }

    private void setItemWidth(View view, ViewGroup parent) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.width = (int)((parent.getMeasuredWidth() / 3) * 0.9);
        view.setLayoutParams(params);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerInfo info = list.get(position);
        String[] arr = info.getNickName().split(" ");
        if (arr.length > 0) {
            holder.tvName.setText(arr[0]);
        }
        else {
            holder.tvName.setText(info.getNickName());
        }
        holder.tvTotal.setText(info.getMatchPlayed());
        if (info.getWinPercentage() == null || info.getWinPercentage().equalsIgnoreCase("")) {
            holder.tvPerc.setText("0%");
        }
        else {
            holder.tvPerc.setText(String.format("%s%%", info.getWinPercentage()));
        }
        if (isEmployee){
            if (!info.getId().equalsIgnoreCase(selectedId)) {
                holder.checkVu.setImageResource(R.drawable.friend_uncheckl);
            }
            else {
                holder.checkVu.setImageResource(R.drawable.friend_checkl);
            }
        }else{
            if (isExist(info.getId()) == -1) {
                holder.checkVu.setImageResource(R.drawable.friend_uncheckl);
            }
            else {
                holder.checkVu.setImageResource(R.drawable.friend_checkl);
            }
        }

        holder.emojiVu.setVisibility(View.VISIBLE);
        holder.playerImgVu.setVisibility(View.INVISIBLE);
        Glide.with(context).load(info.getEmojiUrl()).into(holder.playerEmoji);
        Glide.with(context).load(info.getBibUrl()).placeholder(R.drawable.shirtl).into(holder.shirt);

        holder.cardVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvPerc, tvTotal;
        LinearLayout emojiVu;
        CardView cardVu;
        ImageView playerEmoji, playerImgVu, shirt, checkVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvPerc = itemView.findViewById(R.id.tv_perc);
            tvTotal = itemView.findViewById(R.id.tv_total);
            emojiVu = itemView.findViewById(R.id.emoji_vu);
            cardVu = itemView.findViewById(R.id.card_vu);
            playerEmoji = itemView.findViewById(R.id.emoji_img_vu);
            playerImgVu = itemView.findViewById(R.id.player_img_vu);
            shirt = itemView.findViewById(R.id.shirt);
            checkVu = itemView.findViewById(R.id.check_vu);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}