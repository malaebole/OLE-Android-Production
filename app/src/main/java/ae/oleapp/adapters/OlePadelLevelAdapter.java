package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePadelClassification;

public class OlePadelLevelAdapter extends RecyclerView.Adapter<OlePadelLevelAdapter.ViewHolder> {

    private final Context context;
    private final List<OlePadelClassification> list;
    private ItemClickListener itemClickListener;

    public OlePadelLevelAdapter(Context context, List<OlePadelClassification> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olepadel_level, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePadelClassification info = list.get(position);
        Glide.with(context).load(info.getPlayerData().getPhotoUrl()).placeholder(R.drawable.player_active).into(holder.imgVu);
        holder.tvName.setText(info.getPlayerData().getNickName());
        holder.tvAge.setText(String.format("%s %s", context.getString(R.string.age), info.getPlayerData().getAge()));
        holder.tvClass.setText(info.getClassRank());

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

        TextView tvName, tvAge, tvClass;
        RelativeLayout layout;
        ImageView imgVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.player_image);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAge = itemView.findViewById(R.id.tv_age);
            tvClass = itemView.findViewById(R.id.tv_class);
            layout = itemView.findViewById(R.id.main_layout);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}