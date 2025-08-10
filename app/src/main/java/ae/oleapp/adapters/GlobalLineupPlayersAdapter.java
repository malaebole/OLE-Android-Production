package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.AssignedCountries;
import ae.oleapp.models.LineupGlobalPlayers;

public class GlobalLineupPlayersAdapter extends RecyclerView.Adapter<GlobalLineupPlayersAdapter.ViewHolder> {

    private final Context context;
    private final List<LineupGlobalPlayers> list;
    private ItemClickListener itemClickListener;
    private final boolean setManualWidth = false;
    private final int playersLimit = 0;
    private final boolean isEmployee = false;
    private final String selectedId = "";

    public GlobalLineupPlayersAdapter(Context context, List<LineupGlobalPlayers> list) {
        this.context = context;
        this.list = list;
    }



    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.global_lineup_player_vu, parent, false);
        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LineupGlobalPlayers info = list.get(position);
        holder.tvName.setText(info.getNickName());

        if (!info.getEmojiUrl().isEmpty()){
            Glide.with(context).load(info.getEmojiUrl()).into(holder.emojiVu);
        }
        if (!info.getBibUrl().isEmpty()){
            Glide.with(context).load(info.getBibUrl()).into(holder.shirtVu);
        }

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

        TextView tvName;
        CardView cardVu;
        ImageView emojiVu, shirtVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            cardVu = itemView.findViewById(R.id.card_vu);
            emojiVu = itemView.findViewById(R.id.emoji_img_vu);
            shirtVu = itemView.findViewById(R.id.shirt);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}