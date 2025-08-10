package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerMatch;
import ae.oleapp.models.UserInfo;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleNestedHorizontalRecyclerView;
import ae.oleapp.util.OlePadelProfileView;
import ae.oleapp.util.OleProfileView;

public class OleMatchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private List<OlePlayerMatch> list;
    private ItemClickListener itemClickListener;
    private RecyclerView recyclerView;

    private static final int TYPE_MATCH = 1;
    private static final int TYPE_GAME = 2;

    public OleMatchListAdapter(Context context, List<OlePlayerMatch> list) {
        this.context = context;
        this.list = list;
    }

    public OleMatchListAdapter(Context context, List<OlePlayerMatch> list, RecyclerView recyclerView) {
        this.context = context;
        this.list = list;
        this.recyclerView = recyclerView;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setDataSource(List<OlePlayerMatch> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_MATCH) {
            if (Functions.getPrefValue(context, Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olepadel_match, parent, false);
                setPadding(v);
                return new PadelMatchViewHolder(v);
            }
            else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleprivate_match, parent, false);
                setPadding(v);
                return new MatchViewHolder(v);
            }
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olepublic_match, parent, false);
            setPadding(v);
            return new GameViewHolder(v);
        }
    }

    private void setPadding(View v) {
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
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        OlePlayerMatch match = list.get(position);
        if (getItemViewType(position) == TYPE_MATCH) {
            if (Functions.getPrefValue(context, Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                PadelMatchViewHolder holder = (PadelMatchViewHolder) viewHolder;
                holder.myProfileVu.populateData(match.getCreatedBy().getNickName(), match.getCreatedBy().getPhotoUrl(), match.getCreatedBy().getLevel(), true);
                holder.myPartnerProfileVu.populateData(match.getCreatorPartner().getNickName(), match.getCreatorPartner().getPhotoUrl(), match.getCreatorPartner().getLevel(), true);
                holder.tvFieldName.setText(match.getFieldName());
                holder.tvClubName.setText(match.getClubName());
                holder.tvPrice.setText(String.format("%s %s", match.getJoiningFee(), match.getCurrency()));
                holder.tvLoc.setText(match.getCity());
                holder.tvTime.setText(String.format("%s (%s)", match.getBookingTime().split("-")[0], match.getDuration()));
                holder.tvAge.setText(String.format("%s (%s)", context.getString(R.string.age), match.getAgeLimit()));
                holder.tvPower.setText(String.format("%s: %s", context.getString(R.string.power), match.getSkillLevel()));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = dateFormat.parse(match.getBookingDate());
                    dateFormat.applyPattern("EEE, dd/MM/yyyy");
                    holder.tvDate.setText(dateFormat.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                    holder.tvDate.setText("");
                }
                if (match.getPlayerTwo() != null && !match.getPlayerTwo().isEmpty()) {
                    holder.opponentProfileVu.populateData(match.getPlayerTwo().getNickName(), match.getPlayerTwo().getPhotoUrl(), match.getPlayerTwo().getLevel(), true);
                }
                else {
                    holder.opponentProfileVu.populateData("?", "", null, false);
                }
                if (match.getPlayerTwoPartner() != null && !match.getPlayerTwoPartner().isEmpty()) {
                    holder.opponentPartnerProfileVu.populateData(match.getPlayerTwoPartner().getNickName(), match.getPlayerTwoPartner().getPhotoUrl(), match.getPlayerTwoPartner().getLevel(), true);
                }
                else {
                    holder.opponentPartnerProfileVu.populateData("?", "", null, false);
                }

                holder.btnChallenge.setVisibility(View.VISIBLE);
                if (match.getMyStatus().equalsIgnoreCase("no_request")) {
                    holder.btnChallenge.setEnabled(true);
                    holder.tvChallenge.setText(R.string.challenge);
                } else {
                    holder.btnChallenge.setEnabled(false);
                    if (match.getMyStatus().equalsIgnoreCase("pending")) {
                        holder.tvChallenge.setText(R.string.pending);
                    } else if (match.getMyStatus().equalsIgnoreCase("accepted")) {
                        holder.tvChallenge.setText(R.string.you_are_in);
                    } else if (match.getMyStatus().equalsIgnoreCase("rejected")) {
                        holder.tvChallenge.setText(R.string.rejected);
                    } else if (match.getMyStatus().equalsIgnoreCase("match_started")) {
                        holder.btnChallenge.setVisibility(View.INVISIBLE);
                    }
                }

                if (match.getCreatedBy().getId().equalsIgnoreCase(Functions.getPrefValue(context, Constants.kUserID))) {
                    holder.btnChallenge.setVisibility(View.INVISIBLE);
                }

                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.itemClicked(v, holder.getAdapterPosition());
                    }
                });

                holder.btnChallenge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.challengeClicked(v, holder.getAdapterPosition());
                    }
                });
            }
            else {
                MatchViewHolder holder = (MatchViewHolder) viewHolder;
                holder.p1OleProfileView.populateData(match.getCreatedBy().getNickName(), match.getCreatedBy().getPhotoUrl(), match.getCreatedBy().getLevel(), true);
                holder.tvFieldSize.setText(match.getFieldSize());
                holder.tvFieldName.setText(match.getFieldName());
                holder.tvClubName.setText(match.getClubName());
                holder.tvPrice.setText(String.format("%s %s", match.getJoiningFee(), match.getCurrency()));
                holder.tvLoc.setText(String.format("%s - %s", match.getDistance(), match.getCity()));
                holder.tvTime.setText(String.format("%s (%s)", match.getBookingTime().split("-")[0], match.getDuration()));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = dateFormat.parse(match.getBookingDate());
                    dateFormat.applyPattern("EEE, dd/MM/yyyy");
                    holder.tvDate.setText(dateFormat.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                    holder.tvDate.setText("");
                }

                if (match.getBookingType().equalsIgnoreCase(Constants.kPrivateChallenge)) {
                    holder.tvAge.setVisibility(View.INVISIBLE);
                } else {
                    holder.tvAge.setVisibility(View.VISIBLE);
                    holder.tvAge.setText(String.format("%s (%s)", context.getString(R.string.age), match.getAgeLimit()));
                }

                holder.btnAccept.setVisibility(View.VISIBLE);
                if (match.getBookingType().equalsIgnoreCase(Constants.kPrivateChallenge)) {
                    if (match.getMyStatus().equalsIgnoreCase("pending")) {
                        holder.btnAccept.setEnabled(true);
                        holder.tvAccept.setText(R.string.accept);
                    } else {
                        holder.btnAccept.setEnabled(false);
                        if (match.getMyStatus().equalsIgnoreCase("accepted")) {
                            holder.tvAccept.setText(R.string.you_are_in);
                        } else if (match.getMyStatus().equalsIgnoreCase("rejected")) {
                            holder.tvAccept.setText(R.string.rejected);
                        } else if (match.getMyStatus().equalsIgnoreCase("match_started")) {
                            holder.btnAccept.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    if (match.getMyStatus().equalsIgnoreCase("no_request")) {
                        holder.btnAccept.setEnabled(true);
                        holder.tvAccept.setText(R.string.challenge);
                    } else {
                        holder.btnAccept.setEnabled(false);
                        if (match.getMyStatus().equalsIgnoreCase("pending")) {
                            holder.tvAccept.setText(R.string.pending);
                        } else if (match.getMyStatus().equalsIgnoreCase("accepted")) {
                            holder.tvAccept.setText(R.string.you_are_in);
                        } else if (match.getMyStatus().equalsIgnoreCase("rejected")) {
                            holder.tvAccept.setText(R.string.rejected);
                        } else if (match.getMyStatus().equalsIgnoreCase("match_started")) {
                            holder.btnAccept.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                if (match.getCreatedBy().getId().equalsIgnoreCase(Functions.getPrefValue(context, Constants.kUserID))) {
                    holder.btnAccept.setVisibility(View.INVISIBLE);
                }

                UserInfo player2 = match.getPlayerTwo();
                // check object null or not
                if (!player2.isEmpty()) {
                    holder.p2OleProfileView.populateData(player2.getNickName(), player2.getPhotoUrl(), player2.getLevel(), true);
                } else {
                    holder.p2OleProfileView.populateData("?", "", null, false);
                }

                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.itemClicked(v, holder.getAdapterPosition());
                    }
                });

                holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.acceptClicked(v, holder.getAdapterPosition());
                    }
                });

                setMargin(holder.layout);
            }
        }
        else {
            GameViewHolder holder = (GameViewHolder)viewHolder;
            holder.tvFieldName.setText(String.format("%s (%s)", match.getFieldName(), match.getFieldSize()));
            holder.oleProfileView.populateData(match.getCreatedBy().getNickName(), match.getCreatedBy().getPhotoUrl(), match.getCreatedBy().getLevel(), true);
            holder.tvClubName.setText(match.getClubName());
            holder.tvPrice.setText(String.format("%s %s", match.getJoiningFee(), match.getCurrency()));
            holder.tvLoc.setText(String.format("%s - %s", match.getDistance(), match.getCity()));
            holder.tvTime.setText(String.format("%s (%s)", match.getBookingTime().split("-")[0], match.getDuration()));
            holder.tvRemain.setText(context.getResources().getString(R.string.remains_place, match.getRemainingPlayers()));
            holder.tvAge.setText(String.format("%s (%s)", context.getString(R.string.age), match.getAgeLimit()));
            int totalPlayers = 0;
            if (match.getTotalPlayers() == null || match.getTotalPlayers().isEmpty()) {
                totalPlayers = 0;
            }
            else {
                totalPlayers = Integer.parseInt(match.getTotalPlayers());
            }

            holder.seekBar.setEnabled(false);
            if (!match.getRemainingPlayers().equalsIgnoreCase("") && Integer.parseInt(match.getRemainingPlayers()) == 0) {
                holder.seekBar.setProgress(100);
            }
            else {
                int perc = (match.getJoinedPlayers().size() * 100) / totalPlayers;
                holder.seekBar.setProgress(perc);
            }

            if (match.getMyStatus().equalsIgnoreCase("no_request")) {
                holder.btnJoin.setEnabled(true);
                holder.tvJoin.setText(R.string.join_game);
            }
            else {
                holder.btnJoin.setEnabled(false);
                if (match.getMyStatus().equalsIgnoreCase("pending")) {
                    holder.tvJoin.setText(R.string.pending);
                }
                else if (match.getMyStatus().equalsIgnoreCase("accepted")) {
                    holder.tvJoin.setText(R.string.you_are_in);
                }
                else if (match.getMyStatus().equalsIgnoreCase("rejected")) {
                    holder.tvJoin.setText(R.string.rejected);
                }
            }

            if (match.getCreatedBy().getId().equalsIgnoreCase(Functions.getPrefValue(context, Constants.kUserID))) {
                holder.btnJoin.setVisibility(View.GONE);
            }
            else {
                holder.btnJoin.setVisibility(View.VISIBLE);
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                Date date = dateFormat.parse(match.getBookingDate());
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
            OleGamePictureAdapter adapter = new OleGamePictureAdapter(context, match.getJoinedPlayers());
            holder.recyclerView.setAdapter(adapter);

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            });

            holder.btnJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.joinClicked(v, holder.getAdapterPosition());
                }
            });

            setMargin(holder.layout);
        }
    }

    private void setMargin(CardView cardVu) {
        if (recyclerView != null) {
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) cardVu.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            layoutParams.setMarginStart(0);
            layoutParams.setMarginEnd(0);
            cardVu.setLayoutParams(layoutParams);
            cardVu.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
            return TYPE_GAME;
        }
        else {
            return TYPE_MATCH;
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

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void joinClicked(View view, int pos);
        void acceptClicked(View view, int pos);
        void challengeClicked(View view, int pos);
    }
}