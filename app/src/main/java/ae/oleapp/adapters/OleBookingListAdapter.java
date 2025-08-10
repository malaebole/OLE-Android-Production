package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import com.google.android.material.card.MaterialCardView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleBookingList;
import ae.oleapp.util.Constants;

public class OleBookingListAdapter extends RecyclerView.Adapter<OleBookingListAdapter.ViewHolder> {

    private final Context context;
    private List<OleBookingList> oleBookingList = new ArrayList<>();
    private ItemClickListener itemClickListener;

    public OleBookingListAdapter(Context context, List<OleBookingList> oleBookingList) {
        this.context = context;
        this.oleBookingList = oleBookingList;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setDataSource(List<OleBookingList> list) {
        this.oleBookingList = list;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olebooking_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleBookingList booking = oleBookingList.get(position);
        holder.tvClubName.setText(booking.getClubName());
        holder.tvPlayerName.setText(booking.getUser().getName());
        holder.tvFieldName.setText(booking.getFieldName());
        holder.tvDuration.setText(booking.getDuration());
        holder.tvTime.setText(booking.getBookingTime());
        int balance = Integer.parseInt(booking.getBalanceAmount());

        if (balance > 0){

            holder.balanceLayout.setVisibility(View.VISIBLE);
            holder.balanceAmount.setText(booking.getBalanceAmount());
            holder.amountText.setText(context.getResources().getString(R.string.balance));
            holder.balanceAmount.setTextColor(context.getResources().getColor(R.color.redColor));
            holder.currencyAmount.setTextColor(context.getResources().getColor(R.color.redColor));

        }
        else{
            holder.balanceLayout.setVisibility(View.GONE);
        }

        if (!booking.getFieldColor().equalsIgnoreCase("")) {
            holder.mainLayout.setStrokeColor(Color.parseColor(booking.getFieldColor()));
            holder.durationVu.setCardBackgroundColor(Color.parseColor(booking.getFieldColor()));
            GradientDrawable shapeDrawable = (GradientDrawable) holder.sizeVu.getBackground().getCurrent();
            shapeDrawable.setColor(Color.parseColor(booking.getFieldColor()));
        }
        else {
            holder.mainLayout.setStrokeColor(Color.TRANSPARENT);
            holder.durationVu.setCardBackgroundColor(context.getResources().getColor(R.color.blueColorNew));
            GradientDrawable shapeDrawable = (GradientDrawable) holder.sizeVu.getBackground().getCurrent();
            shapeDrawable.setColor(context.getResources().getColor(R.color.blueColorNew));
        }

        if (booking.getStatus().equalsIgnoreCase(Constants.kConfirmedByOwnerBooking) || booking.getStatus().equalsIgnoreCase(Constants.kConfirmedByPlayerBooking)) {   //on 6 booking price
            holder.confirmTag.setVisibility(View.VISIBLE);
            holder.confirmTag.setImageResource(R.drawable.confirm_tag);
        }
        else if (booking.getStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
            holder.confirmTag.setVisibility(View.VISIBLE);
            holder.confirmTag.setImageResource(R.drawable.thumbs_up_ic);

//            holder.balanceLayout.setVisibility(View.VISIBLE);
//            holder.balanceAmount.setText(booking.getBookingPrice());
//            holder.amountText.setText(context.getResources().getString(R.string.paid_amount));
//            holder.balanceAmount.setTextColor(context.getResources().getColor(R.color.blueColorNew));
//            holder.currencyAmount.setTextColor(context.getResources().getColor(R.color.blueColorNew));

        }
        else {
            holder.confirmTag.setVisibility(View.GONE);
        }

        if (booking.getCallBooking().equalsIgnoreCase("1")) {
            holder.tvCall.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvCall.setVisibility(View.GONE);
        }

        if (booking.getStatus().equalsIgnoreCase(Constants.kCancelledByOwnerBooking) ||
                booking.getStatus().equalsIgnoreCase(Constants.kCancelledByPlayerBooking) ||
                booking.getStatus().equalsIgnoreCase(Constants.kBlockedBooking)) {
            holder.tvType.setVisibility(View.VISIBLE);
            holder.tvType.setText(booking.getTimeAgo());
            holder.tvType.setTextColor(Color.parseColor("#F02301"));
        }
        else {
            if (booking.getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
                holder.tvType.setVisibility(View.VISIBLE);
                holder.tvType.setText(context.getString(R.string.friendly_game));
                holder.tvType.setTextColor(Color.parseColor("#49D483"));
            }
            else if (booking.getBookingType().equalsIgnoreCase(Constants.kPublicChallenge) || booking.getBookingType().equalsIgnoreCase(Constants.kPrivateChallenge)) {
                holder.tvType.setVisibility(View.VISIBLE);
                holder.tvType.setText(context.getString(R.string.challenge_match));
                holder.tvType.setTextColor(Color.parseColor("#F02301"));
            }
            else {
                holder.tvType.setVisibility(View.GONE);
            }
        }

        if (booking.getLadySlot() != null && booking.getLadySlot().equalsIgnoreCase("1")) {
            holder.ladyIcon.setVisibility(View.VISIBLE);
        }
        else {
            holder.ladyIcon.setVisibility(View.GONE);
        }

        if (booking.getIsBlocked() != null && booking.getIsBlocked().equalsIgnoreCase("1")) {
            holder.tvBlocked.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvBlocked.setVisibility(View.GONE);
        }

        if (booking.getBookingFieldType().equalsIgnoreCase("padel")) {
            holder.sizeVu.setVisibility(View.INVISIBLE);
            holder.fieldImageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(booking.getFieldPhoto()).into(holder.fieldImageView);
        }
        else {
            holder.sizeVu.setVisibility(View.VISIBLE);
            holder.fieldImageView.setVisibility(View.INVISIBLE);
            holder.tvSize.setText(booking.getFieldSize());
        }


        holder.tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.callClicked(v, holder.getAdapterPosition());
            }
        });

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getAdapterPosition());
            }
        });

        holder.mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemClickListener.OnItemLongClick(v, holder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return oleBookingList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvFieldName, tvClubName, tvSize, tvPlayerName, tvDuration, tvTime, tvType, tvCall, tvBlocked, balanceAmount,amountText, currencyAmount;
        ImageView confirmTag, ladyIcon;
        RoundedImageView fieldImageView;
        MaterialCardView mainLayout, durationVu;
        RelativeLayout sizeVu;
        LinearLayout balanceLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSize = itemView.findViewById(R.id.tv_size);
            tvFieldName = itemView.findViewById(R.id.tv_field_name);
            tvClubName = itemView.findViewById(R.id.tv_club_name);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvBlocked = itemView.findViewById(R.id.tv_blocked);
            tvPlayerName = itemView.findViewById(R.id.tv_name);
            tvType = itemView.findViewById(R.id.tv_type);
            tvCall = itemView.findViewById(R.id.tv_call);
            mainLayout = itemView.findViewById(R.id.rel_main);
            confirmTag = itemView.findViewById(R.id.confirm_tag);
            fieldImageView = itemView.findViewById(R.id.field_img_vu);
            ladyIcon = itemView.findViewById(R.id.lady_icon);
            durationVu = itemView.findViewById(R.id.duration_vu);
            sizeVu = itemView.findViewById(R.id.size_vu);
            balanceAmount = itemView.findViewById(R.id.amount);
            balanceLayout = itemView.findViewById(R.id.balance_layout);
            amountText = itemView.findViewById(R.id.amount_text);
            currencyAmount = itemView.findViewById(R.id.amount_currency);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void OnItemLongClick(View v, int pos);
        void callClicked(View view, int pos);
    }
}