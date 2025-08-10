package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.models.Club;
import ae.oleapp.models.Day;
import ae.oleapp.models.OleShiftTime;
import ae.oleapp.player.OlePlayerMainTabsActivity;
import ae.oleapp.util.Functions;

public class OlePlayerClubListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Club> clubList;
    private ItemClickListener itemClickListener;
    private RecyclerView recyclerView;

    public OlePlayerClubListAdapter(Context context, List<Club> clubList) {
        this.context = context;
        this.clubList = clubList;
    }

    public OlePlayerClubListAdapter(Context context, List<Club> clubList, RecyclerView recyclerView) {
        this.context = context;
        this.clubList = clubList;
        this.recyclerView = recyclerView;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olep_club_list, parent, false);
        if (recyclerView != null) {
            if (recyclerView.getWidth() == 0) {
                recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        ViewGroup.LayoutParams params = v.getLayoutParams();
                        params.width = (int) (recyclerView.getWidth() * 0.9);
                        v.setLayoutParams(params);
                    }
                });
            }
            else {
                ViewGroup.LayoutParams params = v.getLayoutParams();
                params.width = (int) (recyclerView.getWidth() * 0.9);
                v.setLayoutParams(params);
            }
        }
        return new ClubViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ClubViewHolder holder = (ClubViewHolder)viewHolder;
        Club club = clubList.get(position);
        if (!club.getCoverPath().isEmpty()) {
            Glide.with(context).load(club.getCoverPath()).into(holder.imgBanner);
        }
        holder.tvName.setText(club.getName());
        String distance;
        if (club.getDistance().isEmpty()) {
            distance = context.getResources().getString(R.string.km_place, "0");
        }
        else {
            distance = context.getResources().getString(R.string.km_place, club.getDistance());
        }
        holder.tvLoc.setText(String.format("%s - %s", distance, club.getCity().getName()));
        if (club.getRating().isEmpty()) {
            holder.tvRate.setText("0.0");
        }
        else {
            holder.tvRate.setText(club.getRating());
        }
        if (club.getStartPrice().isEmpty()) {
            holder.tvPrice.setText(String.format("0 %s", club.getCurrency()));
        }
        else {
            holder.tvPrice.setText(String.format("%s %s", club.getStartPrice(), club.getCurrency()));
        }
        if (club.getFavorite().equalsIgnoreCase("1")) {
            holder.btnFav.setImageResource(R.drawable.fav_green);
        }
        else {
            holder.btnFav.setImageResource(R.drawable.club_unfav);
        }
        if (club.getContact().isEmpty()) {
            holder.btnCall.setVisibility(View.GONE);
        }
        else {
            holder.btnCall.setVisibility(View.VISIBLE);
        }
        if (club.getIsOffer().equalsIgnoreCase("1")) {
            holder.offerTag.setVisibility(View.VISIBLE);
        }
        else {
            holder.offerTag.setVisibility(View.GONE);
        }
        String todayName = Functions.getDayName(new Date());
        Day day = checkDayExist(todayName, club);
        if (day != null) {
            if (day.getShifting().size() == 2) {
                holder.tvShift1.setVisibility(View.VISIBLE);
                OleShiftTime time1 = day.getShifting().get(0);
                holder.tvShift1.setText(time1.getOpening()+" - "+time1.getClosing());
                holder.tvShift2.setVisibility(View.VISIBLE);
                OleShiftTime time2 = day.getShifting().get(1);
                holder.tvShift2.setText(time2.getOpening()+" - "+time2.getClosing());
            }
            else if (day.getShifting().size() == 1) {
                holder.tvShift1.setVisibility(View.VISIBLE);
                OleShiftTime time1 = day.getShifting().get(0);
                holder.tvShift1.setText(time1.getOpening()+" - "+time1.getClosing());
                holder.tvShift2.setVisibility(View.GONE);
            }
        }
        else {
            holder.tvShift1.setVisibility(View.GONE);
            holder.tvShift2.setVisibility(View.GONE);
        }
        if (club.getIsFeatured().equalsIgnoreCase("1")) {
            holder.tvFeatured.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvFeatured.setVisibility(View.GONE);
        }
        if (club.getFavoriteCount().isEmpty()) {
            holder.tvFavCount.setText("0");
        }
        else {
            holder.tvFavCount.setText(club.getFavoriteCount());
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            }
        });

        holder.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.favClicked(v, holder.getAdapterPosition());
                }
            }
        });

        holder.rateVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.rateVuClicked(v, holder.getAdapterPosition());
                }
            }
        });

        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof OlePlayerMainTabsActivity) {
                    ((OlePlayerMainTabsActivity)context).makeCall(club.getContact());
                }
                else if (context instanceof BaseActivity) {
                    ((BaseActivity)context).makeCall(club.getContact());
                }
            }
        });

        if (recyclerView != null) {
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) holder.layout.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            layoutParams.setMarginStart(0);
            layoutParams.setMarginEnd(0);
            holder.layout.setLayoutParams(layoutParams);
            holder.layout.requestLayout();
        }
    }

    private Day checkDayExist(String dayName, Club club) {
        for (Day day: club.getTimings()) {
            if (day.getDayName().equalsIgnoreCase(dayName)) {
                return day;
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return clubList.size();
    }

    class ClubViewHolder extends RecyclerView.ViewHolder{

        ImageView imgBanner, offerTag;
        TextView tvName, tvLoc, tvFeatured, tvFavCount, tvRate, tvShift1, tvShift2, tvPrice;
        ImageButton btnCall, btnFav;
        CardView layout;
        LinearLayout rateVu;

        public ClubViewHolder(@NonNull View itemView) {
            super(itemView);

            imgBanner = itemView.findViewById(R.id.img_vu);
            offerTag = itemView.findViewById(R.id.img_offer);
            tvFeatured = itemView.findViewById(R.id.tv_feature);
            tvName = itemView.findViewById(R.id.tv_name);
            tvLoc = itemView.findViewById(R.id.tv_loc);
            tvFavCount = itemView.findViewById(R.id.tv_fav_count);
            tvRate = itemView.findViewById(R.id.tv_rate);
            tvShift1 = itemView.findViewById(R.id.tv_shift_1);
            tvShift2 = itemView.findViewById(R.id.tv_shift_2);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnCall = itemView.findViewById(R.id.btn_call);
            btnFav = itemView.findViewById(R.id.btn_fav);
            layout = itemView.findViewById(R.id.rel_main);
            rateVu = itemView.findViewById(R.id.rate_vu);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void favClicked(View view, int pos);
        void rateVuClicked(View view, int pos);
    }
}