package ae.oleapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ae.oleapp.R;
import ae.oleapp.models.OleHomeNotification;
import ae.oleapp.models.OleMatchResults;
import ae.oleapp.models.OlePadelMatchResults;
import ae.oleapp.models.Product;
import ae.oleapp.player.OleFootballResultShareActivity;
import ae.oleapp.player.OlePadelResultShareActivity;
import ae.oleapp.util.Constants;
import ae.oleapp.util.OleProfileView;
import ae.oleapp.util.OleScrollingLinearLayoutManager;

public class OleHomeNotifAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<OleHomeNotification> list;
    private List<OleHomeNotification> challengeList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private List<Object> resultList = new ArrayList<>();
    private ItemClickListener itemClickListener;
    private Timer timer = new Timer();
    private Timer productTimer = new Timer();
    private Timer resultTimer = new Timer();

    private static final int TYPE_MSG = 1;
    private static final int TYPE_BOOKING = 2;
    private static final int TYPE_PRODUCT = 3;
    private static final int TYPE_CLUB_OFFER = 4;
    private static final int TYPE_CHALLENGE_REQ = 5;
    private static final int TYPE_OTHER = 6;
    private static final int TYPE_MATCH_RES = 7;

    public OleHomeNotifAdapter(Context context, List<OleHomeNotification> list, List<OleHomeNotification> challengeList) {
        this.context = context;
        this.list = list;
        this.challengeList = challengeList;
    }

    public void setChallengeList(List<OleHomeNotification> challengeList) {
        this.challengeList = challengeList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setResultList(List<Object> resultList) {
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_BOOKING) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olehome_booking, parent, false);
            setItemWidth(v, parent);
            return new BookingViewHolder(v);
        }
        else if (viewType == TYPE_CLUB_OFFER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olehome_club_offer, parent, false);
            setItemWidth(v, parent);
            return new ClubOfferViewHolder(v);
        }
        else if (viewType == TYPE_MSG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olehome_msg, parent, false);
            setItemWidth(v, parent);
            return new MsgViewHolder(v);
        }
        else if (viewType == TYPE_PRODUCT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olehome_shop, parent, false);
            setItemWidth(v, parent);
            return new ShopOfferViewHolder(v);
        }
        else if (viewType == TYPE_CHALLENGE_REQ) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olehome_request_slider, parent, false);
            setItemWidth(v, parent);
            return new RequestSliderViewHolder(v);
        }
        else if (viewType == TYPE_MATCH_RES) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olehome_request_slider, parent, false);
            setItemWidth(v, parent);
            return new RequestSliderViewHolder(v);
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olehome_request, parent, false);
            setItemWidth(v, parent);
            return new RequestViewHolder(v);
        }
    }

    private void setItemWidth(View view, ViewGroup parent) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.width = (parent.getMeasuredWidth() / 2);
        params.height = (parent.getMeasuredWidth() / 2);
        view.setLayoutParams(params);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        OleHomeNotification notification = list.get(position);
        if (getItemViewType(position) == TYPE_BOOKING) {
            BookingViewHolder holder = (BookingViewHolder)viewHolder;
            holder.oleProfileView.populateData(notification.getBy().getNickName(), notification.getBy().getPhotoUrl(), notification.getBy().getLevel(), true);
            holder.tvTime.setText(String.format("%s (%s)", notification.getBookingTime().split("-")[0], notification.getDuration()));
            holder.tvDate.setText(notification.getBookingDate());
            if (notification.getBookingType().equalsIgnoreCase(Constants.kFriendlyGame)) {
                holder.tvType.setText(context.getString(R.string.next_game));
                holder.tvType.setTextColor(context.getResources().getColor(R.color.greenColor));
            }
            else if (notification.getBookingType().equalsIgnoreCase(Constants.kNormalBooking)) {
                holder.tvType.setText(context.getString(R.string.next_booking));
                holder.tvType.setTextColor(context.getResources().getColor(R.color.greenColor));
            }
            else {
                holder.tvType.setText(context.getString(R.string.next_match));
                holder.tvType.setTextColor(context.getResources().getColor(R.color.redColor));
            }
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            });
        }
        else if (getItemViewType(position) == TYPE_CLUB_OFFER) {
            ClubOfferViewHolder holder = (ClubOfferViewHolder)viewHolder;
            Glide.with(context).load(notification.getClubLogo()).into(holder.imgVu);
            holder.tvTitle.setText(notification.getOfferType());
            holder.tvDay.setText(notification.getDay());
            holder.tvClubName.setText(notification.getClubName());
            holder.tvDiscount.setText(String.format("%s %s", notification.getDiscountValue(), notification.getCurrency()));
            holder.btnBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.bookClicked(v, holder.getAdapterPosition());
                }
            });
        }
        else if (getItemViewType(position) == TYPE_MSG) {
            MsgViewHolder holder = (MsgViewHolder)viewHolder;
            holder.oleProfileView.populateData(notification.getBy().getNickName(), notification.getBy().getPhotoUrl(), notification.getBy().getLevel(), true);
            holder.tvTime.setText(notification.getTime());
            holder.tvMsg.setText(notification.getMessage());
            holder.btnReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.replyClicked(v, holder.getAdapterPosition());
                }
            });
        }
        else if (getItemViewType(position) == TYPE_PRODUCT) {
            ShopOfferViewHolder holder = (ShopOfferViewHolder)viewHolder;
            OleScrollingLinearLayoutManager layoutManager = new OleScrollingLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false, 100);
            holder.recyclerView.setLayoutManager(layoutManager);
            OleHomeProductAdapter adapter = new OleHomeProductAdapter(context, productList);
            adapter.setItemClickListener(new OleHomeProductAdapter.ItemClickListener() {
                @Override
                public void itemClicked(View view, int pos) {
                    itemClickListener.productItemClicked(view, pos);
                }

                @Override
                public void favClicked(View view, int pos) {
                    itemClickListener.productFavClicked(view, pos);
                }
            });
            holder.recyclerView.setAdapter(adapter);

            productTimer.cancel();
            productTimer.purge();
            productTimer = new Timer();
            productTimer.schedule(new AutoScrollTask(holder.recyclerView, productList.toArray(), layoutManager), 2000, 3000);
        }
        else if (getItemViewType(position) == TYPE_CHALLENGE_REQ) {
            RequestSliderViewHolder holder = (RequestSliderViewHolder) viewHolder;
            OleScrollingLinearLayoutManager notifLayoutManager = new OleScrollingLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false, 100);
            holder.recyclerView.setLayoutManager(notifLayoutManager);
            OleRequestSliderAdapter sliderAdapter = new OleRequestSliderAdapter(context, challengeList);
            sliderAdapter.setItemClickListener(new OleRequestSliderAdapter.ItemClickListener() {
                @Override
                public void itemClicked(View view, int pos) {
                    itemClickListener.sliderItemClicked(view, pos);
                }

                @Override
                public void acceptClicked(View view, int pos) {
                    itemClickListener.sliderAcceptClicked(view, pos);
                }
            });
            holder.recyclerView.setAdapter(sliderAdapter);

            timer.cancel();
            timer.purge();
            timer = new Timer();
            timer.schedule(new AutoScrollTask(holder.recyclerView, challengeList.toArray(), notifLayoutManager), 2000, 3000);
        }
        else if (getItemViewType(position) == TYPE_MATCH_RES) {
            RequestSliderViewHolder holder = (RequestSliderViewHolder) viewHolder;
            OleScrollingLinearLayoutManager notifLayoutManager = new OleScrollingLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false, 100);
            holder.recyclerView.setLayoutManager(notifLayoutManager);
            OleHomeResultAdapter resultAdapter = new OleHomeResultAdapter(context, resultList);
            resultAdapter.setItemClickListener(new OleHomeResultAdapter.ItemClickListener() {
                @Override
                public void itemClicked(View view, int pos) {
                    if (resultList.get(pos) instanceof OlePadelMatchResults) {
                        Intent intent = new Intent(context, OlePadelResultShareActivity.class);
                        intent.putExtra("result", new Gson().toJson(resultList.get(pos)));
                        intent.putExtra("club_name", ((OlePadelMatchResults)resultList.get(pos)).getClubName());
                        context.startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(context, OleFootballResultShareActivity.class);
                        intent.putExtra("result", new Gson().toJson(resultList.get(pos)));
                        intent.putExtra("club_name", ((OleMatchResults)resultList.get(pos)).getClubName());
                        context.startActivity(intent);
                    }
                }
            });
            holder.recyclerView.setAdapter(resultAdapter);

            resultTimer.cancel();
            resultTimer.purge();
            resultTimer = new Timer();
            resultTimer.schedule(new AutoScrollTask(holder.recyclerView, resultList.toArray(), notifLayoutManager), 2000, 3000);
        }
        else {
            RequestViewHolder holder = (RequestViewHolder)viewHolder;
            holder.oleProfileView.populateData(notification.getBy().getNickName(), notification.getBy().getPhotoUrl(), notification.getBy().getLevel(), true);
            holder.tvDate.setText(notification.getBookingDate());
            holder.tvTime.setText(notification.getBookingTime().split("-")[0]);
            if (notification.getType().equalsIgnoreCase("new_challenge")) {
                holder.tvType.setText(context.getString(R.string.challenged_you));
                holder.tvType.setTextColor(context.getResources().getColor(R.color.redColor));
            }
            else if (notification.getType().equalsIgnoreCase("friendly_game_request")) {
                holder.tvType.setText(context.getString(R.string.request_friendly_game));
                holder.tvType.setTextColor(context.getResources().getColor(R.color.greenColor));
            }
            else if (notification.getType().equalsIgnoreCase("new_invitation")) {
                holder.tvType.setText(context.getString(R.string.invited_you));
                holder.tvType.setTextColor(context.getResources().getColor(R.color.redColor));
            }
            else {
                holder.tvType.setText("");
            }
            holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.acceptClicked(v, holder.getAdapterPosition());
                }
            });
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getType().equalsIgnoreCase("new_message")) {
            return TYPE_MSG;
        }
        else if (list.get(position).getType().equalsIgnoreCase("next_booking")) {
            return TYPE_BOOKING;
        }
        else if (list.get(position).getType().equalsIgnoreCase("club_offers")) {
            return TYPE_CLUB_OFFER;
        }
        else if (list.get(position).getType().equalsIgnoreCase("product")) {
            return TYPE_PRODUCT;
        }
        else if (list.get(position).getType().equalsIgnoreCase("challenge")) {
            return TYPE_CHALLENGE_REQ;
        }
        else if (list.get(position).getType().equalsIgnoreCase("match_result")) {
            return TYPE_MATCH_RES;
        }
        else {
            return TYPE_OTHER;
        }
    }

    class MsgViewHolder extends RecyclerView.ViewHolder{

        TextView tvMsg, tvTime;
        CardView layout, btnReply;
        OleProfileView oleProfileView;

        public MsgViewHolder(@NonNull View itemView) {
            super(itemView);

            oleProfileView = itemView.findViewById(R.id.profile_vu);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvMsg = itemView.findViewById(R.id.tv_msg);
            layout = itemView.findViewById(R.id.rel_main);
            btnReply = itemView.findViewById(R.id.btn_reply);
        }
    }

    class BookingViewHolder extends RecyclerView.ViewHolder{

        TextView tvType, tvTime, tvDate;
        CardView layout;
        OleProfileView oleProfileView;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            oleProfileView = itemView.findViewById(R.id.profile_vu);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvType = itemView.findViewById(R.id.tv_type);
            layout = itemView.findViewById(R.id.rel_main);
        }
    }

    class ShopOfferViewHolder extends RecyclerView.ViewHolder{

        RecyclerView recyclerView;

        public ShopOfferViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recycler_vu);
        }
    }

    class ClubOfferViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle, tvDiscount, tvClubName, tvDay;
        ImageView imgVu;
        CardView layout, btnBook;

        public ClubOfferViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDiscount = itemView.findViewById(R.id.tv_discount);
            tvClubName = itemView.findViewById(R.id.tv_club_name);
            tvDay = itemView.findViewById(R.id.tv_day);
            layout = itemView.findViewById(R.id.rel_main);
            btnBook = itemView.findViewById(R.id.btn_book);
        }
    }

    class RequestViewHolder extends RecyclerView.ViewHolder{

        TextView tvDate, tvType, tvTime;
        OleProfileView oleProfileView;
        CardView layout, btnAccept;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvType = itemView.findViewById(R.id.tv_type);
            oleProfileView = itemView.findViewById(R.id.profile_vu);
            layout = itemView.findViewById(R.id.rel_main);
            btnAccept = itemView.findViewById(R.id.btn_accept);
        }
    }

    class RequestSliderViewHolder extends RecyclerView.ViewHolder{

        RecyclerView recyclerView;

        public RequestSliderViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recycler_vu);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void replyClicked(View view, int pos);
        void bookClicked(View view, int pos);
        void acceptClicked(View view, int pos);
        void sliderAcceptClicked(View view, int pos);
        void sliderItemClicked(View view, int pos);
        void productFavClicked(View view, int pos);
        void productItemClicked(View view, int pos);
    }

    private class AutoScrollTask extends TimerTask {
        private final int position = 0;
        private final boolean end = false;
        private final RecyclerView recyclerVu;
        private final OleScrollingLinearLayoutManager layoutManager;
        private final Object[] list;

        public AutoScrollTask(RecyclerView recyclerVu, Object[] list, OleScrollingLinearLayoutManager layoutManager) {
            this.recyclerVu = recyclerVu;
            this.list = list;
            this.layoutManager = layoutManager;
        }

        @Override
        public void run() {
            recyclerVu.post(new Runnable() {
                @Override
                public void run() {
                    if (list.length > 0) {
                        int nextPage = (layoutManager.findLastVisibleItemPosition() + 1) % list.length;
                        recyclerVu.smoothScrollToPosition(nextPage);
                    }
                }
            });
//            if(position == list.length - 1){
//                end = true;
//            } else if (position == 0) {
//                end = false;
//            }
//            if(!end){
//                position++;
//            } else {
//                position--;
//            }
//            if (position >= 0) {
//                recyclerVu.smoothScrollToPosition(position);
//            }
        }
    }
}