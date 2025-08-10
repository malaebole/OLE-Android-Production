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
import ae.oleapp.models.Shirt;

public class ShirtListAdapter extends RecyclerView.Adapter<ShirtListAdapter.ViewHolder> {

    private final Context context;
    private final List<Shirt> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";
    private Boolean shirtIsPremium = true;

    public ShirtListAdapter(Context context, List<Shirt> list) {
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
    public void setPremiumShirt(Boolean shirtIsPremium) {
        this.shirtIsPremium = shirtIsPremium;
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

        if (shirtIsPremium){
            if (list.get(position).getType() != null && list.get(position).getType().equalsIgnoreCase("paid")){
                holder.vipImgVu.setVisibility(View.VISIBLE);
            }else{
                holder.vipImgVu.setVisibility(View.GONE);
            }
        }else{
            holder.vipImgVu.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgVu, vipImgVu;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            cardView = itemView.findViewById(R.id.card_vu);
            vipImgVu = itemView.findViewById(R.id.vip_img_vu);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}