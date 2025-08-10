package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> {

    private final Context context;
    private final List<PlayerInfo> list;
    private ItemClickListener itemClickListener;
    private String chairUrl = "";
    private String isGameOn = "0";

    public PlayerListAdapter(Context context, List<PlayerInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setIsGameOn(String isGameOn, boolean isRefresh) {
        this.isGameOn = isGameOn;
        if (isRefresh) {
            notifyDataSetChanged();
        }
    }

    public void setChairUrl(String chairUrl) {
        this.chairUrl = chairUrl;
        notifyDataSetChanged();
    }

    public String getChairUrl() {
        return chairUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_vu, parent, false);
        ViewHolder holder = new ViewHolder(view);
        final View shape = holder.layout;
        holder.layout.setOnLongClickListener(v -> {
            final PlayerInfo item = list.get(holder.getAdapterPosition());
            if (item != null && item.getInGame().equalsIgnoreCase("1") && isGameOn!=null && isGameOn.equalsIgnoreCase("1")) {
                final DragData state = new DragData(item, holder.getAdapterPosition());
                final View.DragShadowBuilder shadow = new View.DragShadowBuilder(shape);
                ViewCompat.startDragAndDrop(shape, null, shadow, state, 0);
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(50);
                return true;
            }
            else {
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.gameCardVu.setVisibility(View.GONE);
        if (!chairUrl.isEmpty()) {
            Glide.with(context).load(chairUrl).into(holder.chairVu);
        }
        PlayerInfo info = list.get(position);
        if (info == null) {
            holder.nameVu.setVisibility(View.VISIBLE);
            //holder.arrowVu.setVisibility(View.INVISIBLE);
            holder.emojiVu.setVisibility(View.INVISIBLE);
            holder.tvPlus.setVisibility(View.VISIBLE);
            holder.tvName.setText(R.string.add);
            holder.nameVu.setStrokeColor(context.getResources().getColor(R.color.light_red));
            holder.gameCardVu.setVisibility(View.GONE);
        }
        else {
            holder.nameVu.setVisibility(View.VISIBLE);
            //holder.arrowVu.setVisibility(View.VISIBLE);
            holder.emojiVu.setVisibility(View.VISIBLE);
            holder.tvPlus.setVisibility(View.INVISIBLE);
            String[] arr = info.getNickName().split(" ");
            if (arr.length > 0) {
                String firstName = arr[0];
                String lastName = arr.length > 1 ? arr[1] : "";
                String fullName = firstName + " " + lastName;
                holder.tvName.setText(fullName);
                //tvName.setText(arr[0]);
                holder.tvName.setSelected(true);
            }
            else {
                holder.tvName.setText(info.getNickName());
                holder.tvName.setSelected(true);
            }

//            if (arr.length > 0) {
//                holder.tvName.setText(arr[0]);
//            }
//            else {
//                holder.tvName.setText(info.getNickName());
//            }
            Glide.with(context).load(info.getEmojiUrl()).into(holder.playerImgVu);
            Glide.with(context).load(info.getBibUrl()).into(holder.shirtImgVu);

            if (info.getCardType() !=null && !info.getCardType().equalsIgnoreCase("")){
                if (info.getCardType().equalsIgnoreCase("red")){
                    holder.gameCardVu.setVisibility(View.VISIBLE);
                    holder.colorCardVu.setBackgroundColor(Color.parseColor("#f02301"));
                }else {
                    holder.gameCardVu.setVisibility(View.VISIBLE);
                    holder.colorCardVu.setBackgroundColor(Color.parseColor("#ffe200"));
                }
            }
            if (info.getInGame().equalsIgnoreCase("1")) {
                 holder.circle.setBackground(context.getResources().getDrawable(R.drawable.activegreenl));
                 holder.nameVu.setStrokeColor(context.getResources().getColor(R.color.greenColor));
                //holder.nameVu.setStrokeColor(context.getResources().getColor(R.color.greenColor));
              //  holder.arrowVu.setImageResource(R.drawable.name_arrow_green);
            }
            else {
                holder.circle.setBackground(context.getResources().getDrawable(R.drawable.activegreenl));
                holder.nameVu.setStrokeColor(context.getResources().getColor(R.color.nonactiveblue));
                //holder.nameVu.setStrokeColor(context.getResources().getColor(R.color.yellowColor));
              // holder.arrowVu.setImageResource(R.drawable.name_arrow);
            }
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvPlus;
        ImageView arrowVu, chairVu, playerImgVu, shirtImgVu, circle;
        LinearLayout layout, emojiVu;
        MaterialCardView nameVu, gameCardVu;
        View colorCardVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPlus = itemView.findViewById(R.id.tv_plus);
            circle = itemView.findViewById(R.id.circle);
           // arrowVu = itemView.findViewById(R.id.arrow);
            chairVu = itemView.findViewById(R.id.chair);
            gameCardVu = itemView.findViewById(R.id.gamecard_vu);
            playerImgVu = itemView.findViewById(R.id.player_img_vu);
            shirtImgVu = itemView.findViewById(R.id.shirt);
            layout = itemView.findViewById(R.id.layout);
            emojiVu = itemView.findViewById(R.id.emoji_vu);
            nameVu = itemView.findViewById(R.id.name_vu);
            colorCardVu = itemView.findViewById(R.id.color_card_vu);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}