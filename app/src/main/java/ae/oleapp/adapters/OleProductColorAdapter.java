package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleProductColor;

public class OleProductColorAdapter extends RecyclerView.Adapter<OleProductColorAdapter.ViewHolder> {

    private final Context context;
    private final List<OleProductColor> list;
    private ItemClickListener itemClickListener;
    private int selectedColorIndex = -1;

    public OleProductColorAdapter(Context context, List<OleProductColor> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedColorIndex(int selectedColorIndex) {
        this.selectedColorIndex = selectedColorIndex;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleproduct_color, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleProductColor oleProductColor = list.get(position);
        holder.bgVu.setCardBackgroundColor(ColorUtils.setAlphaComponent(Color.parseColor(oleProductColor.getCode()), 52));
        holder.colorVu.setCardBackgroundColor(Color.parseColor(oleProductColor.getCode()));
        if (selectedColorIndex == position) {
            holder.imgVuTick.setVisibility(View.VISIBLE);
            holder.bgVu.setScaleX(1.2f);
            holder.bgVu.setScaleY(1.2f);
        }
        else {
            holder.imgVuTick.setVisibility(View.INVISIBLE);
            holder.bgVu.setScaleX(1.0f);
            holder.bgVu.setScaleY(1.0f);
        }

        holder.bgVu.setOnClickListener(new View.OnClickListener() {
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

        CardView bgVu, colorVu;
        ImageView imgVuTick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVuTick = itemView.findViewById(R.id.img_vu_tick);
            bgVu = itemView.findViewById(R.id.bg_vu);
            colorVu = itemView.findViewById(R.id.color_vu);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}