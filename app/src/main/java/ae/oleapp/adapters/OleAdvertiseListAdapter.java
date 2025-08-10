package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
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
import ae.oleapp.models.OleAdvertiseClub;

public class OleAdvertiseListAdapter extends RecyclerView.Adapter<OleAdvertiseListAdapter.ViewHolder> {

    private final Context context;
    private final List<OleAdvertiseClub> list;
    private ItemClickListener itemClickListener;

    public OleAdvertiseListAdapter(Context context, List<OleAdvertiseClub> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleadvertise_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleAdvertiseClub club = list.get(position);
        holder.tvClub.setText(club.getName());
        holder.tvDate.setText(String.format("%s-%s", club.getFrom(), club.getTo()));
        holder.tvCity.setText(club.getCities());
        if (!club.getLogo().isEmpty()) {
            Glide.with(context).load(club.getLogo()).into(holder.imageView);
        }
        if (club.getStatus().equalsIgnoreCase("Active")) {
            holder.tvStatus.setText(R.string.active);
            holder.tvStatus.setTextColor(Color.parseColor("#49D483"));
        }
        else if (club.getStatus().equalsIgnoreCase("Expired soon")) {
            holder.tvStatus.setText(R.string.expired_soon);
            holder.tvStatus.setTextColor(Color.parseColor("#F02301"));
        }
        else if (club.getStatus().equalsIgnoreCase("Expired")) {
            holder.tvStatus.setText(R.string.expired);
            holder.tvStatus.setTextColor(Color.parseColor("#F02301"));
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
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

        TextView tvClub, tvDate, tvCity, tvStatus;
        CardView layout;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClub = itemView.findViewById(R.id.tv_club);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvCity = itemView.findViewById(R.id.tv_city);
            tvStatus = itemView.findViewById(R.id.tv_status);
            layout = itemView.findViewById(R.id.rel_main);
            imageView = itemView.findViewById(R.id.img_vu_logo);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}