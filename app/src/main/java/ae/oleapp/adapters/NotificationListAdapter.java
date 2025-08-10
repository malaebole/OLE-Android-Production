package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
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
import ae.oleapp.models.NotificationList;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

    private final Context context;
    private final List<NotificationList> list;
    private ItemClickListener itemClickListener;

    public NotificationListAdapter(Context context, List<NotificationList> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_ist, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationList notification = list.get(position);
        Glide.with(context).load(notification.getSender().getEmojiUrl()).into(holder.emojiImgVu);
        Glide.with(context).load(notification.getSender().getBibUrl()).into(holder.shirtImgVu);
        holder.tvTime.setText(notification.getTime());
        holder.tvTitle.setText(notification.getMessage());
        if (notification.getIsRead().equalsIgnoreCase("1")) {
            holder.tvTitle.setTypeface(holder.tvTitle.getTypeface(), Typeface.NORMAL);
        }
        else {
            holder.tvTitle.setTypeface(holder.tvTitle.getTypeface(), Typeface.BOLD);
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

        TextView tvTitle, tvTime;
        ImageView shirtImgVu, emojiImgVu;
        CardView layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTime = itemView.findViewById(R.id.tv_time);
            shirtImgVu = itemView.findViewById(R.id.shirt_img_vu);
            emojiImgVu = itemView.findViewById(R.id.emoji_img_vu);
            layout = itemView.findViewById(R.id.main_layout);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}