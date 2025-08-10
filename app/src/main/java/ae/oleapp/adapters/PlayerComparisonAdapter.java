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

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.DragData;
import ae.oleapp.models.PlayerInfo;

public class PlayerComparisonAdapter extends RecyclerView.Adapter<PlayerComparisonAdapter.ViewHolder> {

    private final Context context;
    private final List<PlayerInfo> list;
    private ItemClickListener itemClickListener;
    private Boolean hasTouchAccess;

    public PlayerComparisonAdapter(Context context, List<PlayerInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void user(Boolean hasTouchAccess){
        this.hasTouchAccess = hasTouchAccess;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_comparison, parent, false);
        ViewHolder holder = new ViewHolder(view);
        final View shape = holder.layout;
        if (hasTouchAccess){
            holder.layout.setOnLongClickListener(v -> {
                final PlayerInfo item = list.get(holder.getAdapterPosition());
                final DragData state = new DragData(item, holder.getAdapterPosition());
                final View.DragShadowBuilder shadow = new View.DragShadowBuilder(shape);
                ViewCompat.startDragAndDrop(shape, null, shadow, state, 0);
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(50);
                return true;
            });
        }

        return holder;
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
        Glide.with(context).load(info.getEmojiUrl()).into(holder.playerImgVu);
        Glide.with(context).load(info.getBibUrl()).into(holder.shirtImgVu);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        ImageView playerImgVu, shirtImgVu;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            playerImgVu = itemView.findViewById(R.id.player_img_vu);
            shirtImgVu = itemView.findViewById(R.id.shirt);
            layout = itemView.findViewById(R.id.layout);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}