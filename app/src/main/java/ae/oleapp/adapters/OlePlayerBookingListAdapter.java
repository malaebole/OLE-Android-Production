package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerBookingList;
import ae.oleapp.models.UserInfo;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleNestedHorizontalRecyclerView;
import ae.oleapp.util.OlePadelProfileView;
import ae.oleapp.util.OleProfileView;

public class OlePlayerBookingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<OlePlayerBookingList> bookingList;
    private ItemClickListener itemClickListener;
    private boolean isFromBookingList = false;

    private static final int NORMAL_BOOKING = 1;
    private static final int FRIENDLY_GAME = 2;
    private static final int PUBLIC_CHALLENGE = 3;
    private static final int PRIVATE_CHALLENGE = 4;

    public OlePlayerBookingListAdapter(Context context, List<OlePlayerBookingList> bookingList, boolean isFromBookingList) {
        this.context = context;
        this.bookingList = bookingList;
        this.isFromBookingList = isFromBookingList;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == FRIENDLY_GAME) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olepublic_match, parent, false);
            return new GameViewHolder(v);
        }
        else if (viewType == PUBLIC_CHALLENGE || viewType == PRIVATE_CHALLENGE) {
            if (Functions.getPrefValue(context, Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olepadel_match, parent, false);
                return new PadelMatchViewHolder(v);
            }
            else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleprivate_match, parent, false);
                return new MatchViewHolder(v);
            }
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleplayer_booking_list, parent, false);
            return new BookingViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        OlePlayerBookingList booking = bookingList.get(position);
        if (getItemViewType(position) == FRIENDLY_GAME) {
            GameViewHolder holder = (GameViewHolder)viewHolder;
            holder.tvFieldName.setText(String.format("%s (%s)", booking.getFieldName(), booking.getFieldSize()));
            holder.oleProfileView.populateData(booking.getCreatedBy().getNickName(), booking.getCreatedBy().getPhotoUrl(), booking.getCreatedBy().getLevel(), true);
            holder.tvClubName.setText(booking.getClubName());
            holder.tvAge.setText(booking.getAgeLimit());
            holder.tvPrice.setText(String.format("%s %s", booking.getBookingPrice(), booking.getCurrency()));
            holder.tvLoc.setText(booking.getCity());
            holder.tvTime.setText(String.format("%s (%s)", booking.getBookingTime().split("-")[0], booking.getDuration()));
            holder.tvRemain.setText(context.getResources().getString(R.string.remains_place, booking.getRemainingPlayers()));
            holder.tvAge.setText(String.format("%s (%s)", context.getString(R.string.age), booking.getAgeLimit()));
            int totalPlayers = 0;
            if (booking.getTotalPlayers() == null || booking.getTotalPlayers().isEmpty()) {
                totalPlayers = 0;
            }
            else {
                totalPlayers = Integer.parseInt(booking.getTotalPlayers());
            }

            holder.seekBar.setEnabled(false);
            if (!booking.getRemainingPlayers().equalsIgnoreCase("") && Integer.parseInt(booking.getRemainingPlayers()) == 0) {
                holder.seekBar.setProgress(100);
            }
            else {
                int perc = (booking.getJoinedPlayers().size() * 100) / totalPlayers;
                holder.seekBar.setProgress(perc);
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                Date date = dateFormat.parse(booking.getBookingDate());
                dateFormat.applyPattern("EEE, dd/MM/yyyy");
                holder.tvDate.setText(dateFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
                holder.tvDate.setText("");
            }
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true);
            layoutManager.setStackFromEnd(true);
            holder.recyclerView.setLayoutManager(layoutManager);
            if (holder.recyclerView.getItemDecorationCount() == 0) {
                holder.recyclerView.addItemDecoration(new OleOverlapDecoration((int) context.getResources().getDimension(R.dimen._minus10sdp)));
            }
            holder.recyclerView.setHasFixedSize(true);
            OleGamePictureAdapter adapter = new OleGamePictureAdapter(context, booking.getJoinedPlayers());
            holder.recyclerView.setAdapter(adapter);

            if (isFromBookingList) {
                holder.btnJoin.setVisibility(View.GONE);
            }
            else {
                holder.btnJoin.setVisibility(View.VISIBLE);
            }
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            });
        }
        else if (getItemViewType(position) == PUBLIC_CHALLENGE || getItemViewType(position) == PRIVATE_CHALLENGE) {
            if (Functions.getPrefValue(context, Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                PadelMatchViewHolder holder = (PadelMatchViewHolder) viewHolder;
                holder.myProfileVu.populateData(booking.getCreatedBy().getNickName(), booking.getCreatedBy().getPhotoUrl(), booking.getCreatedBy().getLevel(), true);
                holder.myPartnerProfileVu.populateData(booking.getCreatorPartner().getNickName(), booking.getCreatorPartner().getPhotoUrl(), booking.getCreatorPartner().getLevel(), true);
                holder.tvFieldName.setText(booking.getFieldName());
                holder.tvClubName.setText(booking.getClubName());
                holder.tvPrice.setText(String.format("%s %s", booking.getBookingPrice(), booking.getCurrency()));
                holder.tvLoc.setText(booking.getCity());
                holder.tvTime.setText(String.format("%s (%s)", booking.getBookingTime().split("-")[0], booking.getDuration()));
                holder.tvAge.setText(String.format("%s (%s)", context.getString(R.string.age), booking.getAgeLimit()));
                holder.tvPower.setText(String.format("%s: %s", context.getString(R.string.power), booking.getSkillLevel()));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = dateFormat.parse(booking.getBookingDate());
                    dateFormat.applyPattern("EEE, dd/MM/yyyy");
                    holder.tvDate.setText(dateFormat.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                    holder.tvDate.setText("");
                }
                if (booking.getPlayerTwo() != null && !booking.getPlayerTwo().isEmpty()) {
                    holder.opponentProfileVu.populateData(booking.getPlayerTwo().getNickName(), booking.getPlayerTwo().getPhotoUrl(), booking.getPlayerTwo().getLevel(), true);
                }
                else {
                    holder.opponentProfileVu.populateData("?", "", null, false);
                }
                if (booking.getPlayerTwoPartner() != null && !booking.getPlayerTwoPartner().isEmpty()) {
                    holder.opponentPartnerProfileVu.populateData(booking.getPlayerTwoPartner().getNickName(), booking.getPlayerTwoPartner().getPhotoUrl(), booking.getPlayerTwoPartner().getLevel(), true);
                }
                else {
                    holder.opponentPartnerProfileVu.populateData("?", "", null, false);
                }

                if (isFromBookingList) {
                    holder.btnChallenge.setVisibility(View.INVISIBLE);
                } else {
                    holder.btnChallenge.setVisibility(View.VISIBLE);
                }

                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.itemClicked(v, holder.getAdapterPosition());
                    }
                });
            }
            else {
                MatchViewHolder holder = (MatchViewHolder) viewHolder;
                holder.p1OleProfileView.populateData(booking.getCreatedBy().getNickName(), booking.getCreatedBy().getPhotoUrl(), booking.getCreatedBy().getLevel(), true);
                holder.tvFieldSize.setText(booking.getFieldSize());
                holder.tvFieldName.setText(booking.getFieldName());
                holder.tvClubName.setText(booking.getClubName());
                holder.tvPrice.setText(String.format("%s %s", booking.getBookingPrice(), booking.getCurrency()));
                holder.tvLoc.setText(booking.getCity());
                holder.tvTime.setText(String.format("%s (%s)", booking.getBookingTime().split("-")[0], booking.getDuration()));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = dateFormat.parse(booking.getBookingDate());
                    dateFormat.applyPattern("EEE, dd/MM/yyyy");
                    holder.tvDate.setText(dateFormat.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                    holder.tvDate.setText("");
                }

                if (booking.getBookingType().equalsIgnoreCase(Constants.kPrivateChallenge)) {
                    holder.tvAge.setVisibility(View.INVISIBLE);
                } else {
                    holder.tvAge.setVisibility(View.VISIBLE);
                    holder.tvAge.setText(String.format("%s (%s)", context.getString(R.string.age), booking.getAgeLimit()));
                }

                UserInfo player2 = booking.getPlayerTwo();
                // check object null otr not
                if (!player2.isEmpty()) {
                    holder.p2OleProfileView.populateData(player2.getNickName(), player2.getPhotoUrl(), player2.getLevel(), true);
                } else {
                    holder.p2OleProfileView.populateData("?", "", null, false);
                }

                if (isFromBookingList) {
                    holder.btnAccept.setVisibility(View.INVISIBLE);
                } else {
                    holder.btnAccept.setVisibility(View.VISIBLE);
                }

                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.itemClicked(v, holder.getAdapterPosition());
                    }
                });
            }
        }
        else {
            BookingViewHolder holder = (BookingViewHolder)viewHolder;
            holder.tvClubName.setText(booking.getClubName());
            holder.tvType.setText(booking.getFieldType());
            holder.tvFieldName.setText(booking.getFieldName());
            holder.tvPrice.setText(String.format("%s %s", booking.getBookingPrice(), booking.getCurrency()));
            holder.tvTime.setText(booking.getBookingTime());

            if (booking.getBookingStatus().equalsIgnoreCase(Constants.kFinishedBooking)) {
                holder.btnCreate.setVisibility(View.INVISIBLE);
            }
            else {
                holder.btnCreate.setVisibility(View.VISIBLE);
            }

            if (Functions.getPrefValue(context, Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                holder.tvSize.setVisibility(View.GONE);
                holder.fieldImgVu.setVisibility(View.VISIBLE);
                Glide.with(context).load(booking.getFieldPhoto()).into(holder.fieldImgVu);
                holder.btnBg.setImageResource(R.drawable.padel_green_btn);
            }
            else {
                holder.tvSize.setVisibility(View.VISIBLE);
                holder.fieldImgVu.setVisibility(View.GONE);
                holder.tvSize.setText(booking.getFieldSize());
                holder.btnBg.setImageResource(R.drawable.green_btn_bg);
            }

            holder.btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.createMatchClicked(v, holder.getAdapterPosition());
                }
            });

            holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    @Override
    public int getItemViewType(int position) {
        OlePlayerBookingList booking = bookingList.get(position);
        if (booking.getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
            return FRIENDLY_GAME;
        }
        else if (booking.getBookingType().equalsIgnoreCase(Constants.kPublicChallenge)) {
            return PUBLIC_CHALLENGE;
        }
        else if (booking.getBookingType().equalsIgnoreCase(Constants.kPrivateChallenge)) {
            return PRIVATE_CHALLENGE;
        }
        else {
            return NORMAL_BOOKING;
        }
    }

    class BookingViewHolder extends RecyclerView.ViewHolder{

        TextView tvFieldName, tvClubName, tvSize, tvType, tvPrice, tvTime;
        CardView mainLayout, btnCreate;
        ImageView fieldImgVu, btnBg;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSize = itemView.findViewById(R.id.tv_size);
            tvType = itemView.findViewById(R.id.tv_type);
            tvFieldName = itemView.findViewById(R.id.tv_field_name);
            tvClubName = itemView.findViewById(R.id.tv_club_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvTime = itemView.findViewById(R.id.tv_time);
            mainLayout = itemView.findViewById(R.id.rel_main);
            btnCreate = itemView.findViewById(R.id.btn_create);
            fieldImgVu = itemView.findViewById(R.id.field_img_vu);
            btnBg = itemView.findViewById(R.id.btn_bg);
        }
    }

    class MatchViewHolder extends RecyclerView.ViewHolder{

        OleProfileView p1OleProfileView, p2OleProfileView;
        TextView tvFieldName, tvFieldSize, tvClubName, tvLoc, tvPrice, tvDate, tvTime, tvAccept, tvAge;
        CardView btnAccept, layout;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFieldSize = itemView.findViewById(R.id.tv_field_size);
            tvFieldName = itemView.findViewById(R.id.tv_field_name);
            tvClubName = itemView.findViewById(R.id.tv_club_name);
            tvPrice = itemView.findViewById(R.id.tv_fee);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvLoc = itemView.findViewById(R.id.tv_address);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAge = itemView.findViewById(R.id.tv_age);
            p1OleProfileView = itemView.findViewById(R.id.profile_vu_1);
            p2OleProfileView = itemView.findViewById(R.id.profile_vu_2);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            layout = itemView.findViewById(R.id.rel_main);
            tvAccept = itemView.findViewById(R.id.tv_accept);

        }
    }

    class PadelMatchViewHolder extends RecyclerView.ViewHolder{

        OlePadelProfileView myProfileVu, myPartnerProfileVu, opponentProfileVu, opponentPartnerProfileVu;
        TextView tvFieldName, tvClubName, tvLoc, tvPrice, tvDate, tvTime, tvChallenge, tvAge, tvPower;
        CardView btnChallenge, layout;

        public PadelMatchViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFieldName = itemView.findViewById(R.id.tv_field_name);
            tvClubName = itemView.findViewById(R.id.tv_club_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvLoc = itemView.findViewById(R.id.tv_address);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAge = itemView.findViewById(R.id.tv_age);
            tvPower = itemView.findViewById(R.id.tv_power);
            myProfileVu = itemView.findViewById(R.id.my_profile_vu);
            myPartnerProfileVu = itemView.findViewById(R.id.my_partner_profile_vu);
            opponentProfileVu = itemView.findViewById(R.id.opponent_profile_vu);
            opponentPartnerProfileVu = itemView.findViewById(R.id.opponent_partner_profile_vu);
            layout = itemView.findViewById(R.id.rel_main);
            tvChallenge = itemView.findViewById(R.id.tv_challenge);
            btnChallenge = itemView.findViewById(R.id.btn_challenge);

        }
    }

    class GameViewHolder extends RecyclerView.ViewHolder{

        TextView tvLoc, tvClubName, tvFieldName, tvPrice, tvDate, tvTime, tvRemain, tvJoin, tvAge;
        CardView layout;
        CardView btnJoin;
        OleNestedHorizontalRecyclerView recyclerView;
        AppCompatSeekBar seekBar;
        OleProfileView oleProfileView;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFieldName = itemView.findViewById(R.id.tv_field_name);
            oleProfileView = itemView.findViewById(R.id.profile_vu);
            tvLoc = itemView.findViewById(R.id.tv_loc);
            tvClubName = itemView.findViewById(R.id.tv_club_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvRemain = itemView.findViewById(R.id.tv_remaining);
            layout = itemView.findViewById(R.id.rel_main);
            btnJoin = itemView.findViewById(R.id.btn_join);
            recyclerView = itemView.findViewById(R.id.recycler_vu);
            tvJoin = itemView.findViewById(R.id.tv_join);
            seekBar = itemView.findViewById(R.id.seek_bar);
            tvAge = itemView.findViewById(R.id.tv_age);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void createMatchClicked(View view, int pos);
    }
}