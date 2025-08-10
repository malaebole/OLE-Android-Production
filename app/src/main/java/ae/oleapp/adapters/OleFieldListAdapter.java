package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Field;

public class OleFieldListAdapter extends RecyclerView.Adapter<OleFieldListAdapter.ViewHolder> {

    private final Context context;
    private final List<Field> fieldList;
    private ItemClickListener itemClickListener;

    public OleFieldListAdapter(Context context, List<Field> fieldList) {
        this.context = context;
        this.fieldList = fieldList;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olefield_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Field field = fieldList.get(position);
        if (field.getImage() != null && !field.getImage().getPhotoPath().isEmpty()) {
            Glide.with(context).load(field.getImage().getPhotoPath()).into(holder.imgVu);
        }
        holder.tvName.setText(field.getName());
        if (field.getFieldSize() != null && !field.getFieldSize().isEmpty()) {
            holder.tvSize.setText(field.getFieldSize().getName());
            holder.padelImgVu.setVisibility(View.INVISIBLE);
            holder.sizeVu.setVisibility(View.VISIBLE);
        }
        else {
            holder.padelImgVu.setVisibility(View.VISIBLE);
            holder.sizeVu.setVisibility(View.INVISIBLE);
        }
        holder.tvFieldType.setText(field.getFieldType().getName());
        if (field.getGrassType() != null && !field.getGrassType().isEmpty()) {
            holder.tvGrassType.setText(field.getGrassType().getName());
            holder.sepVu.setVisibility(View.VISIBLE);
        }
        else {
            holder.sepVu.setVisibility(View.INVISIBLE);
            holder.tvGrassType.setText("");
        }
        holder.tvOneHour.setText(String.format("%s %s", field.getOneHour(), field.getCurrency()));
        holder.tvOneHalfHour.setText(String.format("%s %s", field.getOneHalfHours(), field.getCurrency()));
        holder.tvTwoHour.setText(String.format("%s %s", field.getTwoHours(), field.getCurrency()));

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            }
        });

        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.menuClicked(holder.btnMenu, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return fieldList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgVu;
        TextView tvName, tvFieldType, tvGrassType, tvOneHour, tvOneHalfHour, tvTwoHour, tvSize;
        ImageButton btnMenu;
        CardView layout;
        View sepVu;
        MaterialCardView padelImgVu;
        RelativeLayout sizeVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            tvName = itemView.findViewById(R.id.tv_name);
            tvFieldType = itemView.findViewById(R.id.tv_field_type);
            tvGrassType = itemView.findViewById(R.id.tv_grass_type);
            tvOneHalfHour = itemView.findViewById(R.id.tv_one_half_hour);
            tvOneHour = itemView.findViewById(R.id.tv_one_hour);
            tvTwoHour = itemView.findViewById(R.id.tv_two_hour);
            btnMenu = itemView.findViewById(R.id.btn_menu);
            layout = itemView.findViewById(R.id.rel_main);
            sepVu = itemView.findViewById(R.id.sep_vu);
            sizeVu = itemView.findViewById(R.id.size_vu);
            padelImgVu = itemView.findViewById(R.id.padel_image);
            tvSize = itemView.findViewById(R.id.tv_size);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void menuClicked(View view, int pos);
    }
}