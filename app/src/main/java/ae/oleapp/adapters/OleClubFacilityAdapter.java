package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleClubFacility;

public class OleClubFacilityAdapter extends RecyclerView.Adapter<OleClubFacilityAdapter.ViewHolder> {

    private final Context context;
    private final List<OleClubFacility> list;
    private OnItemClickListener onItemClickListener;
    public List<OleClubFacility> selectedFacility = new ArrayList<>();

    public OleClubFacilityAdapter(Context context, List<OleClubFacility> list) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleclub_facility, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleClubFacility facility = list.get(position);
        holder.tvTitle.setText(facility.getName());
        if (facility.getPrice().equalsIgnoreCase("")) {
            holder.tvPrice.setText("");
        } else {
            holder.tvPrice.setText(String.format("%s %s", facility.getPrice(), facility.getCurrency()));
        }

        int index = isExistInSelected(facility.getId());
        if (index == -1) {
            Glide.with(context).load(facility.getIcon()).into(holder.imgVu);
            holder.tvPrice.setText("");
        }
        else {
            facility = selectedFacility.get(index);
            Glide.with(context).load(facility.getActiveIcon()).into(holder.imgVu);
            if (facility.getPrice().equalsIgnoreCase("")) {
                holder.tvPrice.setText(R.string.free);
            }
            else {
                holder.tvPrice.setText(String.format("%s %s", facility.getPrice(), facility.getCurrency()));
            }
        }

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgVu;
        TextView tvTitle, tvPrice;
        LinearLayout relMain;

        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPrice = itemView.findViewById(R.id.tv_price);
            relMain = itemView.findViewById(R.id.main);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}