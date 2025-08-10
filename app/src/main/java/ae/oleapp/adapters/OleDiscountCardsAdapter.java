package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.OleDiscountCard;
import ae.oleapp.util.Functions;

public class OleDiscountCardsAdapter extends RecyclerView.Adapter<OleDiscountCardsAdapter.ViewHolder> {

    private final Context context;
    private final List<OleDiscountCard> list;
    private ItemClickListener itemClickListener;
    private final boolean isShare;
    public final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public OleDiscountCardsAdapter(Context context, List<OleDiscountCard> list, boolean isShare) {
        this.context = context;
        this.list = list;
        this.isShare = isShare;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olediscount_card_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleDiscountCard oleDiscountCard = list.get(position);
        holder.tvTitle.setText(oleDiscountCard.getTitle());
        holder.tvClub.setText(oleDiscountCard.getClubName());
        holder.tvDate.setText(String.format("%s-%s", oleDiscountCard.getFromDate(), oleDiscountCard.getToDate()));
        holder.tvReqBooking.setText(context.getString(R.string.booking_required_place, oleDiscountCard.getTargetBooking()));
        Glide.with(context).load(oleDiscountCard.getClubLogo()).into(holder.imageView);
        if (oleDiscountCard.getDiscountType().equalsIgnoreCase("amount")) {
            holder.tvDiscount.setText(String.format("%s %s", oleDiscountCard.getDiscountValue(), oleDiscountCard.getCurrency()));
        }
        else {
            holder.tvDiscount.setText(String.format("%s%%", oleDiscountCard.getDiscountValue()));
        }

        binderHelper.bind(holder.swipeLayout, String.valueOf(position));
        if (isShare) {
            binderHelper.lockSwipe(String.valueOf(position));
        }
        else {
            binderHelper.unlockSwipe(String.valueOf(position));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            Date expiryDate = dateFormat.parse(oleDiscountCard.getToDate());
            int days = Functions.getDateDifferenceInDays(new Date(), expiryDate);
            days = days + 1;
            if (days <= 0) {
                holder.expiredCard.setVisibility(View.VISIBLE);
            }
            else {
                holder.expiredCard.setVisibility(View.GONE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            holder.expiredCard.setVisibility(View.GONE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            }
        });

        holder.deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.deleteClicked(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle, tvDate, tvReqBooking, tvDiscount, tvClub;
        SwipeRevealLayout swipeLayout;
        FrameLayout deleteLayout, layout;
        ImageView imageView;
        CardView expiredCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClub = itemView.findViewById(R.id.tv_club);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvReqBooking = itemView.findViewById(R.id.tv_req_booking);
            tvDiscount = itemView.findViewById(R.id.tv_discount);
            layout = itemView.findViewById(R.id.rel_main);
            imageView = itemView.findViewById(R.id.img_vu_logo);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            deleteLayout = itemView.findViewById(R.id.delete_layout);
            expiredCard = itemView.findViewById(R.id.expired_card);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void deleteClicked(View view, int pos);
    }
}