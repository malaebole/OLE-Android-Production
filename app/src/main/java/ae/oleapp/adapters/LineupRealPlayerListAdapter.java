package ae.oleapp.adapters;

import android.content.Context;
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
import ae.oleapp.models.LineupGlobalPlayers;
import ae.oleapp.models.LineupRealDragData;
import ae.oleapp.models.LineupRealPlayerInfo;


public class LineupRealPlayerListAdapter extends RecyclerView.Adapter<LineupRealPlayerListAdapter.ViewHolder> {

    private final Context context;
    private final List<LineupGlobalPlayers> list;
    private ItemClickListener itemClickListener;
    private String chairUrl = "";
    private String isGameOn = "0";

    public LineupRealPlayerListAdapter(Context context, List<LineupGlobalPlayers> list) {
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
            final LineupGlobalPlayers item = list.get(holder.getAdapterPosition());
            if (item != null) {
                final LineupRealDragData state = new LineupRealDragData(item, holder.getAdapterPosition());
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
        LineupGlobalPlayers info = list.get(position);
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

            Glide.with(context).load(info.getEmojiUrl()).into(holder.playerImgVu);
            Glide.with(context).load(info.getBibUrl()).into(holder.shirtImgVu);
            holder.circle.setBackground(context.getResources().getDrawable(R.drawable.activegreenl));
            holder.nameVu.setStrokeColor(context.getResources().getColor(R.color.greenColor));

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