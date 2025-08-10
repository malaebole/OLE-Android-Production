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
import ae.oleapp.models.FieldImage;

public class FieldImageListAdapter extends RecyclerView.Adapter<FieldImageListAdapter.ViewHolder> {

    private final Context context;
    private final List<FieldImage> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";

    public FieldImageListAdapter(Context context, List<FieldImage> list) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.field_image_vu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getBgImg()).into(holder.bgImgVu);
        Glide.with(context).load(list.get(position).getFieldImg()).into(holder.imgVu);
        if (list.get(position).getId().equalsIgnoreCase(selectedId)) {
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.yellowColor));
        }
        else {
            holder.cardView.setStrokeColor(Color.parseColor("#42FFFFFF"));
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

        ImageView imgVu, bgImgVu;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgVu = itemView.findViewById(R.id.img_vu);
            bgImgVu = itemView.findViewById(R.id.bg_img_vu);
            cardView = itemView.findViewById(R.id.card_vu);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}