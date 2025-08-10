package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ae.oleapp.models.OleBookingList;
import ae.oleapp.util.Constants;

public class OleBookingCountDetailAdapter extends RecyclerView.Adapter<OleBookingCountDetailAdapter.ViewHolder> {

    private final Context context;
    private final List<OleBookingList> list;
    private OnItemClickListener onItemClick;

    public OleBookingCountDetailAdapter(Context context, List<OleBookingList> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClick(OnItemClickListener onItemClick) {
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olebooking_count_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleBookingList booking = list.get(position);
        holder.tvFieldName.setText(booking.getFieldName());
        holder.tvDate.setText(String.format("%s | %s", booking.getBookingDate(), booking.getBookingTime()));
        if (booking.getStatus().equalsIgnoreCase(Constants.kCancelledByOwnerBooking) ||
                booking.getStatus().equalsIgnoreCase(Constants.kCancelledByPlayerBooking) ||
                booking.getStatus().equalsIgnoreCase(Constants.kBlockedBooking)) {
            holder.tvCancel.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvCancel.setVisibility(View.GONE);
        }

        if (booking.getBookingFieldType().equalsIgnoreCase("padel")) {
            holder.sizeVu.setVisibility(View.INVISIBLE);
            holder.padelImgVu.setVisibility(View.VISIBLE);
            Glide.with(context).load(booking.getFieldPhoto()).into(holder.imgVu);
        }
        else {
            holder.sizeVu.setVisibility(View.VISIBLE);
            holder.padelImgVu.setVisibility(View.INVISIBLE);
            holder.tvSize.setText(booking.getFieldSize());
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

        CardView relMain;
        TextView tvSize, tvFieldName, tvDate, tvCancel;
        MaterialCardView padelImgVu;
        RelativeLayout sizeVu;
        ImageView imgVu;

        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            relMain = itemView.findViewById(R.id.rel_main);
            tvSize = itemView.findViewById(R.id.tv_size);
            tvFieldName = itemView.findViewById(R.id.tv_field_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvCancel = itemView.findViewById(R.id.tv_cancel);
            sizeVu = itemView.findViewById(R.id.size_vu);
            padelImgVu = itemView.findViewById(R.id.padel_image);

        }
    }

    public interface OnItemClickListener {
        void itemClicked(View view, int position);
    }
}