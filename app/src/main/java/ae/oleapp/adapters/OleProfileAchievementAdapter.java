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
import ae.oleapp.models.OleProfileAchievement;

public class OleProfileAchievementAdapter extends RecyclerView.Adapter<OleProfileAchievementAdapter.ViewHolder> {

    private final Context context;
    private final List<OleProfileAchievement> list;
    private ItemClickListener itemClickListener;

    public OleProfileAchievementAdapter(Context context, List<OleProfileAchievement> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleprofile_achievement, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleProfileAchievement achievement = list.get(position);
        Glide.with(context).load(achievement.getIcon()).into(holder.imgVu);
        holder.tvTitle.setText(achievement.getTitle());

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

        TextView tvTitle;
        ImageView imgVu;
        CardView layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            tvTitle = itemView.findViewById(R.id.tv_title);
            layout = itemView.findViewById(R.id.rel_main);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}