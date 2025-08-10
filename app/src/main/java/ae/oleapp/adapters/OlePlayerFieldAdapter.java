package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Field;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;

public class OlePlayerFieldAdapter extends RecyclerView.Adapter<OlePlayerFieldAdapter.ViewHolder> {

    private final Context context;
    private final List<Field> list;
    private OnItemClickListener onItemClick;

    public OlePlayerFieldAdapter(Context context, List<Field> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClick(OnItemClickListener onItemClick) {
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleplayer_field_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Field field = list.get(position);
        holder.tvName.setText(field.getName());
        holder.tvPrice.setText(String.format(context.getString(R.string.currency_place), field.getOneHour(), field.getCurrency()));

        if (field.getIsOffer().equalsIgnoreCase("1")) {
            holder.offerTag.setVisibility(View.VISIBLE);
        }
        else {
            holder.offerTag.setVisibility(View.GONE);
        }

        if (Functions.getPrefValue(context, Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
            holder.btnBg.setImageResource(R.drawable.padel_small_btn_bg);
            holder.sizeVu.setVisibility(View.INVISIBLE);
            holder.sepVu.setVisibility(View.INVISIBLE);
            holder.imgVuField.setVisibility(View.VISIBLE);
            holder.tvGrassType.setText(field.getFieldType().getName());
            if (field.getImage() != null && !field.getImage().getPhotoPath().isEmpty()) {
                Glide.with(context).load(field.getImage().getPhotoPath()).into(holder.imgVuField);
            }
        }
        else {
            holder.btnBg.setImageResource(R.drawable.small_btn_bg);
            holder.sizeVu.setVisibility(View.VISIBLE);
            holder.sepVu.setVisibility(View.VISIBLE);
            holder.imgVuField.setVisibility(View.INVISIBLE);
            holder.tvSize.setText(field.getFieldSize().getName());
            holder.tvGrassType.setText(field.getGrassType().getName());
            holder.tvFieldType.setText(field.getFieldType().getName());
        }

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClick != null) {
                    onItemClick.itemClicked(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout relMain;
        TextView tvSize, tvFieldType, tvGrassType, tvName, tvPrice;
        ImageView offerTag, imgVuField, btnBg;
        LinearLayout sizeVu;
        View sepVu;

        ViewHolder(View itemView) {
            super(itemView);

            relMain = itemView.findViewById(R.id.main);
            tvSize = itemView.findViewById(R.id.tv_size);
            tvFieldType = itemView.findViewById(R.id.tv_field_type);
            tvGrassType = itemView.findViewById(R.id.tv_grass_type);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            offerTag = itemView.findViewById(R.id.img_offer);
            imgVuField = itemView.findViewById(R.id.img_vu);
            sizeVu = itemView.findViewById(R.id.linear);
            sepVu = itemView.findViewById(R.id.sep);
            btnBg = itemView.findViewById(R.id.btn_bg);
        }
    }

    public interface OnItemClickListener {
        void itemClicked(View view, int position);
    }
}