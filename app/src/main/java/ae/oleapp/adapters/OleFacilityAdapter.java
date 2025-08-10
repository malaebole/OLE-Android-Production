package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.OleClubFacility;

public class OleFacilityAdapter extends RecyclerView.Adapter<OleFacilityAdapter.ViewHolder> {

    private final Context context;
    private final List<OleClubFacility> list;
    private OnItemClickListener onItemClickListener;
    public List<OleClubFacility> selectedFacility = new ArrayList<>();
    private boolean isFromDetail = false;

    public OleFacilityAdapter(Context context, List<OleClubFacility> list, boolean isFromDetail) {
        this.context = context;
        this.list = list;
        this.isFromDetail = isFromDetail;
    }

    public void setDataSource(List<OleClubFacility> list) {
        this.list.clear();
        this.list.addAll(list);
        this.notifyDataSetChanged();
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olebooking_facility, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleClubFacility facility = list.get(position);
        holder.tvTitle.setText(facility.getName());
        holder.btnMinus.setVisibility(View.GONE);
        holder.btnPlus.setVisibility(View.GONE);
        if (isFromDetail) {
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.blueColorNew));
            holder.tvPrice.setTextColor(context.getResources().getColor(R.color.blueColorNew));
            Glide.with(context).load(facility.getActiveIcon()).into(holder.imgVu);
            if (facility.getPrice().equalsIgnoreCase("")) {
                holder.tvPrice.setText(context.getResources().getString(R.string.free));
            } else {
                if (facility.getType().equalsIgnoreCase("countable")) {
                    holder.tvPrice.setText(String.format(Locale.ENGLISH, "%s %s\n(%s %s)", facility.getPrice(), facility.getCurrency(), facility.getMaxQuantity(), facility.getUnit()));

                } else {
                    holder.tvPrice.setText(String.format("%s %s", facility.getPrice(), facility.getCurrency()));
                }
            }
        }
        else {
            int index = isExistInSelected(facility.getId());
            if (index == -1) {
                Glide.with(context).load(facility.getIcon()).into(holder.imgVu);
            } else {
                Glide.with(context).load(facility.getActiveIcon()).into(holder.imgVu);
            }
            if (facility.getPrice().equalsIgnoreCase("")) {
                holder.tvPrice.setText(context.getResources().getString(R.string.free));
            } else {
                if (facility.getType().equalsIgnoreCase("countable") && facility.getQty() > 0) {
                    holder.tvPrice.setText(String.format(Locale.ENGLISH, "%s %s (%d)", facility.getPrice(), facility.getCurrency(), facility.getQty()));
                    holder.btnMinus.setVisibility(View.VISIBLE);
                    holder.btnPlus.setVisibility(View.VISIBLE);
                } else {
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgVu;
        TextView tvTitle, tvPrice;
        Button btnPlus, btnMinus;
        RelativeLayout relMain;

        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            relMain = itemView.findViewById(R.id.rel_main);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
        void OnPlusClick(View v, int pos);
        void OnMinusClick(View v, int pos);
    }
}