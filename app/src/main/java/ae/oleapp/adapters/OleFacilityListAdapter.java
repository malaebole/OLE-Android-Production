package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleClubFacility;

public class OleFacilityListAdapter extends RecyclerView.Adapter<OleFacilityListAdapter.ViewHolder> {

    private final Context context;
    private final List<OleClubFacility> list;
    private OnItemClickListener onItemClickListener;
    public List<OleClubFacility> selectedFacility = new ArrayList<>();

    public OleFacilityListAdapter(Context context, List<OleClubFacility> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSelectedFacility(OleClubFacility facility) {
        int index = isExistInSelected(facility.getId());
        if (index == -1) {
            /// enter qty if countable
            if (facility.getType().equalsIgnoreCase("countable")) {
                facility.setQty(1);
            }
            else {
                facility.setQty(0);
            }
            selectedFacility.add(facility);
            this.notifyDataSetChanged();
        }
        else {
            if (!facility.getType().equalsIgnoreCase("countable")) {
                selectedFacility.remove(index);
                this.notifyDataSetChanged();
            }
        }
    }

    public int isExistInSelected(String facId) {
        int index = -1;
        for (int i = 0; i < selectedFacility.size(); i++) {
            if (selectedFacility.get(i).getId().equalsIgnoreCase(facId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olefacility_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleClubFacility facility = list.get(position);
        holder.tvTitle.setText(facility.getName());
        holder.btnVu.setVisibility(View.INVISIBLE);
        int index = isExistInSelected(facility.getId());
        if (index == -1) {
            Glide.with(context).load(facility.getIcon()).into(holder.imgVu);
            holder.imgVuCheck.setImageResource(R.drawable.friendly_game_round);
        } else {
            Glide.with(context).load(facility.getActiveIcon()).into(holder.imgVu);
            holder.imgVuCheck.setImageResource(R.drawable.blue_check);
        }
        if (facility.getPrice().equalsIgnoreCase("")) {
            holder.tvPrice.setText(context.getResources().getString(R.string.free));
        } else {
            if (facility.getType().equalsIgnoreCase("countable") && facility.getQty() > 0) {
                holder.tvPrice.setText(String.format("%s %s", facility.getPrice(), facility.getCurrency()));
                holder.tvQty.setText(String.valueOf(facility.getQty()));
                holder.btnVu.setVisibility(View.VISIBLE);
            } else {
                holder.tvPrice.setText(String.format("%s %s", facility.getPrice(), facility.getCurrency()));
            }
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
                }
            }
        });

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnPlusClick(v, holder.getAdapterPosition());
                }
            }
        });

        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnMinusClick(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgVu, imgVuCheck;
        TextView tvTitle, tvPrice, tvQty;
        CardView btnPlus, btnMinus, layout;
        LinearLayout btnVu;

        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            imgVuCheck = itemView.findViewById(R.id.img_vu_check);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQty = itemView.findViewById(R.id.tv_qty);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            layout = itemView.findViewById(R.id.main);
            btnVu = itemView.findViewById(R.id.btn_vu);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
        void OnPlusClick(View v, int pos);
        void OnMinusClick(View v, int pos);
    }
}