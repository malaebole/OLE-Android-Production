package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.Club;
import ae.oleapp.util.Functions;

public class OleClubListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Club> clubList;
    private ItemClickListener itemClickListener;
//    private CustomItemClickListener customItemClickListener;
//    private ClubNameClickListner clubNameClicked;
    private boolean isFootballAvailable = false;
    private boolean isPadelAvailable = false;

    private static final int TYPE_VIEW = 1;
    private static final int TYPE_CLUB = 2;
    private static final int TYPE_Club_List = 3;

    private String selectedId = "";

    private int selectedIndex = -1;

    public OleClubListAdapter(Context context, List<Club> clubList, int selectedIndex) {
        this.context = context;
        this.clubList = clubList;
        this.selectedIndex = selectedIndex;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

//    public void setCustomItemClickListener(CustomItemClickListener customItemClickListener) {
//        this.customItemClickListener = customItemClickListener;
//    }
//
//    public void setClubNameClicked(ClubNameClickListner clubNameClickListner) {
//        this.clubNameClicked = clubNameClickListner;
//    }

    public void setAvailable(boolean footballAvailable, boolean padelAvailable) {
        isFootballAvailable = footballAvailable;
        isPadelAvailable = padelAvailable;
    }
    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }

    public void setSelectedIndex(String selectedIndex) {
        this.selectedIndex = Integer.parseInt(selectedIndex);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == TYPE_VIEW) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleclub_list_vu, parent, false);
//            RecyclerView.ViewHolder viewHolder = new CustomViewHolder(v);
//            return viewHolder;
//        }else if (viewType == TYPE_Club_List){
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.clubs_name_vu, parent, false);
//            RecyclerView.ViewHolder viewHolder = new CustomCLubNameHolder(v);
//            return viewHolder;
//        }
//        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleclub_list, parent, false);
            RecyclerView.ViewHolder viewHolder = new ClubViewHolder(v);
            return viewHolder;
//}
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
//        if (getItemViewType(position) == TYPE_VIEW) {
//            CustomViewHolder holder = (CustomViewHolder)viewHolder;
//
//        }else if (getItemViewType(position) == TYPE_Club_List){
//            CustomCLubNameHolder holder = (CustomCLubNameHolder)viewHolder;
//            Club club = clubList.get(position);
//            holder.tvName.setText(club.getName());
//            if (clubList.get(position).getId().equalsIgnoreCase("0")) {
//                holder.cardView.setStrokeColor(context.getResources().getColor(R.color.yellowColor));
//                holder.cardView.setCardBackgroundColor(Color.parseColor("#7A000000"));
//                holder.tvName.setTextColor(context.getResources().getColor(R.color.yellowColor));
//            }
//            else {
//                holder.cardView.setStrokeColor(Color.parseColor("#204334"));
//                holder.cardView.setCardBackgroundColor(Color.TRANSPARENT);
//                holder.tvName.setTextColor(Color.parseColor("#204334"));
//            }
//            holder.cardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    clubNameClicked.clubNameClick(v, holder.getAdapterPosition());
//                }
//            });
//        }
//        else {
        if (clubList.get(position).getId().equalsIgnoreCase(selectedId)) {
            ClubViewHolder holder = (ClubViewHolder)viewHolder;
            Club club = clubList.get(position);
            if (!club.getCoverPath().isEmpty()) {
                Glide.with(context).load(club.getCoverPath()).into(holder.imgBanner);
            }
            //holder.tvName.setText(club.getName());
            holder.tvLoc.setText(club.getCity().getName());
            if (club.getRating().isEmpty()) {
                holder.tvRate.setText("0.0");
            }
            else {
                holder.tvRate.setText(club.getRating());
            }
            if (club.getIsOffer().equalsIgnoreCase("1")) {
                holder.offerVu.setVisibility(View.VISIBLE);
                holder.tvOffer.setText(club.getOffer());
            }
            else {
                holder.offerVu.setVisibility(View.GONE);
            }
            if (club.getIsFeatured().equalsIgnoreCase("1")) {
                holder.featuredVu.setVisibility(View.VISIBLE);
            }
            else {
                holder.featuredVu.setVisibility(View.GONE);
            }
            if (club.getFavoriteCount() == null || club.getFavoriteCount().isEmpty()) {
                holder.tvFavCount.setText("0");
            }
            else {
                holder.tvFavCount.setText(club.getFavoriteCount());
            }

            if (isPadelAvailable) {
                holder.btnPadelCallBooking.setVisibility(View.VISIBLE);
            }
            else {
                holder.btnPadelCallBooking.setVisibility(View.GONE);
            }
            if (isFootballAvailable) {
                holder.btnFootballCallBooking.setVisibility(View.VISIBLE);
            }
            else {
                holder.btnFootballCallBooking.setVisibility(View.GONE);
            }

            holder.tvEarning.setText(String.format("%s %s", club.getTodayEarning(), club.getCurrency()));
            holder.tvBookings.setText(String.valueOf(club.getBookingCount()));
            holder.tvHours.setText(club.getTotalHours());
            holder.tvConfirmed.setText(club.getTotalConfirmed());
            holder.tvPending.setText(club.getWaitingUserCount());
            holder.tvNew.setText(club.getNewPlayersCount());

            holder.warningTag.setVisibility(View.GONE);
            holder.btnRenew.setVisibility(View.GONE);
            if (club.getIsExpired().equalsIgnoreCase("1")) {
                holder.warningTag.setVisibility(View.VISIBLE);
                holder.btnRenew.setVisibility(View.VISIBLE);
                holder.tvExpiry.setText(context.getString(R.string.membership_expired));
                holder.tvExpiry.setTextColor(context.getResources().getColor(R.color.redColor));
            }
            else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                try {
                    Date expiryDate = dateFormat.parse(club.getClubExpiryDate());
                    int days = Functions.getDateDifferenceInDays(new Date(), expiryDate);
                    days = days + 1;
                    if (days <= 0) {
                        holder.warningTag.setVisibility(View.VISIBLE);
                        holder.btnRenew.setVisibility(View.VISIBLE);
                        holder.tvExpiry.setText(context.getString(R.string.membership_expired));
                        holder.tvExpiry.setTextColor(context.getResources().getColor(R.color.redColor));
                    }
                    else if (days <= 10) {
                        holder.warningTag.setVisibility(View.VISIBLE);
                        holder.btnRenew.setVisibility(View.VISIBLE);
                        holder.tvExpiry.setText(context.getString(R.string.membership_expire_place_days, days));
                        holder.tvExpiry.setTextColor(context.getResources().getColor(R.color.redColor));
                    }
                    else {
                        holder.warningTag.setVisibility(View.GONE);
                        holder.btnRenew.setVisibility(View.GONE);
                        holder.tvExpiry.setText(context.getString(R.string.membership_expire_place_days, days));
                        holder.tvExpiry.setTextColor(context.getResources().getColor(R.color.subTextColor));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (club.getFieldsCount() > 0) {
                holder.infoVu.setVisibility(View.VISIBLE);
                holder.noFieldVu.setVisibility(View.GONE);
            }
            else {
                holder.infoVu.setVisibility(View.GONE);
                holder.noFieldVu.setVisibility(View.VISIBLE);
            }

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.itemClicked(v, holder.getAdapterPosition());
                    }
                }
            });

            holder.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.menuClicked(holder.btnMenu, holder.getAdapterPosition());
                }
            });

            holder.inventoryVu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.inventoryClicked(holder.inventoryVu, holder.getAdapterPosition());
                }
            });

            holder.btnAddField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.addFieldClicked(holder.btnAddField, holder.getAdapterPosition());
                }
            });

            holder.btnRenew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.renewClicked(holder.btnRenew, holder.getAdapterPosition());
                }
            });

            holder.btnPadelCallBooking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.padelBookingClicked(v, holder.getAdapterPosition());
                }
            });
            holder.btnFootballCallBooking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.footballBookingClicked(v, holder.getAdapterPosition());
                }
            });
        }
//            holder.btnPromotion.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    itemClickListener.promotionClicked(v, holder.getAdapterPosition());
//                }
//            });
       // }

    }

    @Override
    public int getItemCount() {
        return clubList.size();
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (clubList.size() > 0 && position == 1) {
//            return TYPE_VIEW;
//        }
////        else if (!clubList.get(position).getName().isEmpty()) {
////            return TYPE_Club_List;
////        }
//        else {
//            return TYPE_CLUB;
//        }
//    }

    class ClubViewHolder extends RecyclerView.ViewHolder{

        ImageView imgBanner, warningTag;
        TextView tvName, tvLoc, tvOffer, tvFavCount, tvRate, tvEarning, tvBookings, tvHours, tvConfirmed, tvPending, tvExpiry, tvNew;
        ImageButton btnMenu;
        CardView layout, offerVu, featuredVu, inventoryVu;
        Button btnRenew;
        RelativeLayout infoVu;
        LinearLayout noFieldVu;
        MaterialCardView btnAddField;

        ImageButton btnFootballCallBooking, btnPadelCallBooking, btnPromotion;

        public ClubViewHolder(@NonNull View itemView) {
            super(itemView);

            imgBanner = itemView.findViewById(R.id.img_vu);
            warningTag = itemView.findViewById(R.id.warning_tag);
            featuredVu = itemView.findViewById(R.id.feature_vu);
            offerVu = itemView.findViewById(R.id.offer_vu);
            tvName = itemView.findViewById(R.id.tv_name);
            tvLoc = itemView.findViewById(R.id.tv_loc);
            tvFavCount = itemView.findViewById(R.id.tv_fav_count);
            tvRate = itemView.findViewById(R.id.tv_rate);
            tvOffer = itemView.findViewById(R.id.tv_offer);
            tvNew = itemView.findViewById(R.id.tv_new);
            tvEarning = itemView.findViewById(R.id.tv_earning);
            tvBookings = itemView.findViewById(R.id.tv_bookings);
            tvHours = itemView.findViewById(R.id.tv_hours);
            tvConfirmed = itemView.findViewById(R.id.tv_confirmed);
            tvPending = itemView.findViewById(R.id.tv_pending);
            btnMenu = itemView.findViewById(R.id.btn_menu);
            layout = itemView.findViewById(R.id.rel_main);
            btnRenew = itemView.findViewById(R.id.btn_renew);
            tvExpiry = itemView.findViewById(R.id.tv_expire);
            noFieldVu = itemView.findViewById(R.id.no_field_vu);
            infoVu = itemView.findViewById(R.id.info_vu);
            btnAddField = itemView.findViewById(R.id.btn_add_field);
            inventoryVu = itemView.findViewById(R.id.inventory_vu);
            btnFootballCallBooking = itemView.findViewById(R.id.btn_football_booking);
            btnPadelCallBooking = itemView.findViewById(R.id.btn_padel_booking);
           // btnPromotion = itemView.findViewById(R.id.btn_promotion);
        }
    }

//    class CustomViewHolder extends RecyclerView.ViewHolder{
//
//        ImageButton btnFootballCallBooking, btnPadelCallBooking, btnPromotion;
//
//        public CustomViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            btnFootballCallBooking = itemView.findViewById(R.id.btn_football_booking);
//            btnPadelCallBooking = itemView.findViewById(R.id.btn_padel_booking);
//            btnPromotion = itemView.findViewById(R.id.btn_promotion);
//        }
//    }

//    class CustomCLubNameHolder extends RecyclerView.ViewHolder{
//
//        TextView tvName;
//        MaterialCardView cardView;
//
//        public CustomCLubNameHolder(@NonNull View itemView) {
//            super(itemView);
//
//            tvName = itemView.findViewById(R.id.tv_name);
//            cardView = itemView.findViewById(R.id.card_vu);
//        }
//    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void menuClicked(View view, int pos);
        void inventoryClicked(View view, int pos);
        void addFieldClicked(View view, int pos);
        void renewClicked(View view, int pos);

        void padelBookingClicked(View view, int pos);
        void footballBookingClicked(View view, int pos);
        void promotionClicked(View view, int pos);
    }

//    public interface CustomItemClickListener {
//        void padelBookingClicked(View view, int pos);
//        void footballBookingClicked(View view, int pos);
//        void promotionClicked(View view, int pos);
//    }
//
//    public interface ClubNameClickListner {
//        void clubNameClick(View view, int pos);
//    }
}