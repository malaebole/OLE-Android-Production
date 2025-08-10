package ae.oleapp.adapters;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.DragData;
import ae.oleapp.models.PlayerInfo;

public class BestPlayerAdapter  extends RecyclerView.Adapter<BestPlayerAdapter.ViewHolder> {

    private final Context context;
    private final List<PlayerInfo> list;
    private ItemClickListener itemClickListener;
    private final String chairUrl = "";
    private final String isGameOn = "0";
    int selected_position = -1;

    public BestPlayerAdapter(Context context, List<PlayerInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.best_player_vu, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

       // Here I am just highlighting the background
        holder.bestPlayerMainll.setBackgroundResource(selected_position == position ? R.drawable.best_player_card_bg_active : R.drawable.best_player_card_bg_inactive);
        PlayerInfo info = list.get(position);
        if (info != null) {

            holder.nameVu.setVisibility(View.VISIBLE);
            holder.emojiVu.setVisibility(View.VISIBLE);
            String[] arr = info.getNickName().split(" ");
            if (arr.length > 0) {
                holder.tvName.setText(arr[0]);
            }
            else {
                holder.tvName.setText(info.getNickName());
            }
            Glide.with(context).load(info.getEmojiUrl()).into(holder.playerImgVu);
            Glide.with(context).load(info.getBibUrl()).into(holder.shirtImgVu);
            if (info.getIsCaptain().equalsIgnoreCase("1")){
                holder.captainIcon.setVisibility(View.VISIBLE);
            }else{
                holder.captainIcon.setVisibility(View.INVISIBLE);
            }
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (holder.getAdapterPosition() == RecyclerView.NO_POSITION){
//                    return;
//                }
                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = holder.getAdapterPosition();
                notifyItemChanged(selected_position);
                itemClickListener.itemClicked(v, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        ImageView chairVu, playerImgVu, shirtImgVu, captainIcon;
        LinearLayout layout, emojiVu;
        MaterialCardView nameVu;
        RelativeLayout bestPlayerMainll;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            chairVu = itemView.findViewById(R.id.chair);
            playerImgVu = itemView.findViewById(R.id.player_img_vu);
            shirtImgVu = itemView.findViewById(R.id.shirt);
            layout = itemView.findViewById(R.id.layout);
            emojiVu = itemView.findViewById(R.id.emoji_vu);
            nameVu = itemView.findViewById(R.id.name_vu);
            captainIcon = itemView.findViewById(R.id.captain_ic);
            bestPlayerMainll = itemView.findViewById(R.id.best_player_mainll);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}