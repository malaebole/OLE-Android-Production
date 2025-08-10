package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerInfo;

public class OleJoinedListAdapter extends RecyclerView.Adapter<OleJoinedListAdapter.ViewHolder> {

    private final Context context;
    private OnItemClickListener onItemClickListener;
    private final List<OlePlayerInfo> list;
    private final boolean isFromDetail;
    private final boolean isStackVuVisible;
    private String bookingStatus = "";
    private String bookingType = "";
    public final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public OleJoinedListAdapter(Context context, List<OlePlayerInfo> list, boolean isFromDetail, boolean isStackVuVisible) {
        this.context = context;
        this.list = list;
        this.isFromDetail = isFromDetail;
        this.isStackVuVisible = isStackVuVisible;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olejoined_player_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerInfo info = list.get(position);
        Glide.with(context).load(info.getPhotoUrl()).placeholder(R.drawable.player_active).into(holder.imgVu);
        holder.tvName.setText(info.getNickName());
        if (info.getPlayerPosition() != null && info.getPlayerPosition().getName() != null) {
            holder.tvPosition.setText(info.getPlayerPosition().getName());
            holder.tvPosition.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvPosition.setVisibility(View.GONE);
            holder.tvPosition.setText("");
        }
        if (info.getPlayingLevel() != null && !info.getPlayingLevel().isEmpty()) {
            holder.tvRate.setText(String.format("%s%%", info.getPlayingLevel()));
        }
        else {
            holder.tvRate.setText("0%");
        }
        if (info.getLevel() != null && !info.getLevel().isEmpty() && !info.getLevel().getValue().equalsIgnoreCase("")) {
            holder.tvRank.setVisibility(View.VISIBLE);
            holder.tvRank.setText(String.format("LV: %s", info.getLevel().getValue()));
        }
        else {
            holder.tvRank.setVisibility(View.INVISIBLE);
        }

        holder.tvPayment.setVisibility(View.VISIBLE);
        if (info.getPaymentMethod() == null || info.getPaymentMethod().isEmpty()) {
            holder.tvPayment.setVisibility(View.GONE);
        }
        else if (info.getPaymentMethod().equalsIgnoreCase("cash")){
            holder.tvPayment.setText(context.getString(R.string.cash));
            holder.tvPayment.setTextColor(context.getResources().getColor(R.color.redColor));
        }
        else {
            holder.tvPayment.setText(context.getString(R.string.paid));
            holder.tvPayment.setTextColor(context.getResources().getColor(R.color.greenColor));
        }

        binderHelper.bind(holder.swipeLayout, String.valueOf(position));
        if (isFromDetail) {
            binderHelper.unlockSwipe(String.valueOf(position));
            holder.btnAccept.setVisibility(View.GONE);
            holder.rateVu.setVisibility(View.GONE);
        }
        else {
            binderHelper.lockSwipe(String.valueOf(position));
            if (isStackVuVisible) {
                holder.btnStack.setVisibility(View.VISIBLE);
            }
            else {
                holder.btnStack.setVisibility(View.GONE);
            }
        }

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
            }
        });

        holder.deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnDeleteClick(v, holder.getAdapterPosition());
            }
        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnAcceptClick(v, holder.getAdapterPosition());
            }
        });

        holder.btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnRateClick(v, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgVu;
        TextView tvRank, tvName, tvPerc, tvPosition, tvPayment, tvRate, tvRateTitle;
        RelativeLayout relMain;
        LinearLayout btnStack, rateVu;
        CardView btnAccept, btnRate;
        SwipeRevealLayout swipeLayout;
        FrameLayout deleteLayout;


        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.player_image);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPerc = itemView.findViewById(R.id.tv_perc);
            tvPosition = itemView.findViewById(R.id.tv_position);
            tvRate = itemView.findViewById(R.id.tv_rate);
            tvRateTitle = itemView.findViewById(R.id.tv_rate_title);
            tvPayment = itemView.findViewById(R.id.tv_payment);
            relMain = itemView.findViewById(R.id.rl_main);
            btnStack = itemView.findViewById(R.id.ln_btns);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnRate = itemView.findViewById(R.id.btn_rate);
            rateVu = itemView.findViewById(R.id.rate_vu);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            deleteLayout = itemView.findViewById(R.id.delete_layout);
            rateVu.setVisibility(View.GONE);
            btnRate.setVisibility(View.GONE);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
        void OnDeleteClick(View v, int pos);
        void OnAcceptClick(View v, int pos);
        void OnRateClick(View v, int pos);
    }
}