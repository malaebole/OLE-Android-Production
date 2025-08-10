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
import ae.oleapp.activities.MainActivity;
import ae.oleapp.models.GroupData;

public class MyGroupsAdapter extends RecyclerView.Adapter<MyGroupsAdapter.ViewHolder> {

    private final Context context;
    private final List<GroupData> list;
    private ItemClickListener itemClickListener;

    public MyGroupsAdapter(Context context, List<GroupData> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_group, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupData groupData = list.get(position);
        holder.tvName.setText(groupData.getGroupName());
        Glide.with(context).load(groupData.getBibUrl()).into(holder.shirtImgVu);
        Glide.with(context).load(groupData.getEmojiUrl()).into(holder.emojiImgVu);
        holder.tvDate.setText(String.format("%s, %s", groupData.getGameDate(), groupData.getGameTime()));
        if (groupData.getUserCaptain().equalsIgnoreCase("1")) {
            holder.captainIcon.setVisibility(View.VISIBLE);
        }
        else {
            holder.captainIcon.setVisibility(View.INVISIBLE);
        }

        if (context instanceof MainActivity) {
            if (groupData.getInGame().equalsIgnoreCase("1")) {
                holder.dotVu.setCardBackgroundColor(context.getResources().getColor(R.color.greenColor));
            }
            else {
                holder.dotVu.setCardBackgroundColor(context.getResources().getColor(R.color.yellowColor));
            }
        }
        else {
            holder.dotVu.setVisibility(View.INVISIBLE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.itemClicked(view, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvDate;
        ImageView shirtImgVu, emojiImgVu, captainIcon;
        CardView layout, dotVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_group_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            shirtImgVu = itemView.findViewById(R.id.shirt_img_vu);
            emojiImgVu = itemView.findViewById(R.id.emoji_img_vu);
            captainIcon = itemView.findViewById(R.id.captain_ic);
            layout = itemView.findViewById(R.id.main_layout);
            dotVu = itemView.findViewById(R.id.dot_vu);
            dotVu.setVisibility(View.GONE);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}