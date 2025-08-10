package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.owner.OleBookingDetailActivity;
import ae.oleapp.owner.OlePadelBookingDetailActivity;

public class OleClubDetailFacAdapter extends RecyclerView.Adapter<OleClubDetailFacAdapter.ViewHolder> {

    private final Context context;
    private final List<OleClubFacility> list;
    private boolean isShowQty = false;

    public OleClubDetailFacAdapter(Context context, List<OleClubFacility> list) {
        this(context, list, false);
    }

    public OleClubDetailFacAdapter(Context context, List<OleClubFacility> list, boolean isShowQty) {
        this.context = context;
        this.list = list;
        this.isShowQty = isShowQty;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleclub_detail_fac, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleClubFacility facility = list.get(position);
        holder.tvTitle.setText(facility.getName());
        Glide.with(context).load(facility.getActiveIcon()).into(holder.imgVu);
        if (facility.getPrice().equalsIgnoreCase("")) {
            holder.tvPrice.setText(String.format("%s: %s", context.getString(R.string.price), context.getString(R.string.free)));
        }
        else {
            if (context instanceof OleBookingDetailActivity || context instanceof OlePadelBookingDetailActivity) {
                if (facility.getType().equalsIgnoreCase("countable")) {
                    int price = Integer.parseInt(facility.getPrice());
                    int qty = Integer.parseInt(facility.getMaxQuantity());
                    holder.tvPrice.setText(String.format("%s: %s %s", context.getString(R.string.price), price * qty, facility.getCurrency()));
                }
                else {
                    holder.tvPrice.setText(String.format("%s: %s %s", context.getString(R.string.price), facility.getPrice(), facility.getCurrency()));
                }
            }
            else {
                if (isShowQty) {
                    holder.tvPrice.setText(String.format("%s: %s %s (%s)", context.getString(R.string.price), facility.getPrice(), facility.getCurrency(), facility.getMaxQuantity()));
                }
                else {
                    holder.tvPrice.setText(String.format("%s: %s %s", context.getString(R.string.price), facility.getPrice(), facility.getCurrency()));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgVu;
        TextView tvTitle, tvPrice;

        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}