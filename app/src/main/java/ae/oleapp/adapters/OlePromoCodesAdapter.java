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
import ae.oleapp.models.OlePromoCode;
import ae.oleapp.util.Functions;

public class OlePromoCodesAdapter extends RecyclerView.Adapter<OlePromoCodesAdapter.ViewHolder> {

    private final Context context;
    private final List<OlePromoCode> list;
    private ItemClickListener itemClickListener;
    private boolean isShare = false;
    public final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public OlePromoCodesAdapter(Context context, List<OlePromoCode> list, boolean isShare) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olepromo_code_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePromoCode olePromoCode = list.get(position);
        holder.tvTitle.setText(olePromoCode.getCouponTitle());
        holder.tvClub.setText(olePromoCode.getClubName());
        holder.tvDate.setText(String.format("%s - %s", olePromoCode.getStartFrom(), olePromoCode.getExpiry()));
        holder.tvTimes.setText(context.getString(R.string.place_times_applied, olePromoCode.getUsedTimes()));
        if (!olePromoCode.getClubLogo().isEmpty()) {
            Glide.with(context).load(olePromoCode.getClubLogo()).into(holder.imageView);
        }
        if (olePromoCode.getDiscountType().equalsIgnoreCase("amount")) {
            holder.tvDiscount.setText(String.format("%s %s", olePromoCode.getDiscount(), olePromoCode.getCurrency()));
        }
        else {
            holder.tvDiscount.setText(String.format("%s%%", olePromoCode.getDiscount()));
        }

        holder.tvPending.setText(olePromoCode.getPendingBookings());
        holder.tvCompleted.setText(olePromoCode.getCompletedBookings());
        holder.tvPayment.setText(String.format("%s %s", olePromoCode.getPlayerPayment(), olePromoCode.getCurrency()));
        holder.tvPlayers.setText(olePromoCode.getPlayersCount());
        holder.tvNewPlayers.setText(olePromoCode.getNewPlayers());

        binderHelper.bind(holder.swipeLayout, String.valueOf(position));
        if (isShare) {
            binderHelper.lockSwipe(String.valueOf(position));
        }
        else {
            binderHelper.unlockSwipe(String.valueOf(position));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            Date expiryDate = dateFormat.parse(olePromoCode.getExpiry());
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

        TextView tvTitle, tvDate, tvTimes, tvDiscount, tvClub, tvPending, tvCompleted, tvPayment, tvPlayers, tvNewPlayers;
        SwipeRevealLayout swipeLayout;
        FrameLayout deleteLayout, layout;
        ImageView imageView;
        CardView expiredCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClub = itemView.findViewById(R.id.tv_club);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTimes = itemView.findViewById(R.id.tv_times);
            tvDiscount = itemView.findViewById(R.id.tv_discount);
            layout = itemView.findViewById(R.id.rel_main);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            deleteLayout = itemView.findViewById(R.id.delete_layout);
            imageView = itemView.findViewById(R.id.img_vu_logo);
            tvPending = itemView.findViewById(R.id.tv_pending);
            tvCompleted = itemView.findViewById(R.id.tv_completed);
            tvPayment = itemView.findViewById(R.id.tv_payment);
            tvPlayers = itemView.findViewById(R.id.tv_players);
            tvNewPlayers = itemView.findViewById(R.id.tv_new_players);
            expiredCard = itemView.findViewById(R.id.expired_card);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void deleteClicked(View view, int pos);
    }
}