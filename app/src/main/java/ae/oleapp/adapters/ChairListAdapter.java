package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Chair;

public class ChairListAdapter extends RecyclerView.Adapter<ChairListAdapter.ViewHolder> {

    private final Context context;
    private final List<Chair> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";

    public ChairListAdapter(Context context, List<Chair> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shirts_vu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getPhotoUrl()).into(holder.imgVu);
        holder.vipImageView.setVisibility(View.GONE);
        if (list.get(position).getId().equalsIgnoreCase(selectedId)) {
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.yellowColor));
            holder.cardView.setCardBackgroundColor(Color.parseColor("#7A000000"));
        }
        else {
            holder.cardView.setStrokeColor(Color.TRANSPARENT);
            holder.cardView.setCardBackgroundColor(Color.TRANSPARENT);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
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

        ImageView imgVu,vipImageView;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            cardView = itemView.findViewById(R.id.card_vu);
            vipImageView = itemView.findViewById(R.id.vip_img_vu);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}