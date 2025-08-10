package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;

public class OleJoinedGridAdapter extends RecyclerView.Adapter<OleJoinedGridAdapter.ViewHolder> {

    private final Context context;
    private OnItemClickListener onItemClickListener;
    private final List<OlePlayerInfo> list;
    private final boolean isFromDetail;
    private String bookingStatus = "";
    public final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public OleJoinedGridAdapter(Context context, List<OlePlayerInfo> list, boolean isFromDetail) {
        this.context = context;
        this.list = list;
        this.isFromDetail = isFromDetail;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olejoined_grid_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerInfo info = list.get(position);
        Glide.with(context).load(info.getPhotoUrl()).placeholder(R.drawable.player_active).into(holder.imgVu);
        holder.tvName.setText(info.getNickName());
        if (info.getLevel() != null && !info.getLevel().isEmpty() && !info.getLevel().getValue().equalsIgnoreCase("")) {
            holder.tvRank.setVisibility(View.VISIBLE);
            holder.tvRank.setText(String.format("LV: %s", info.getLevel().getValue()));
        }
        else {
            holder.tvRank.setVisibility(View.INVISIBLE);
        }
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

        if (info.getPlayerConfirmed() != null && info.getPlayerConfirmed().equalsIgnoreCase("1")) {
            holder.imgVuTag.setVisibility(View.VISIBLE);
        }
        else {
            holder.imgVuTag.setVisibility(View.GONE);
        }

        if (Functions.getPrefValue(context, Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
            if (isFromDetail) {
                if (bookingStatus.equalsIgnoreCase(Constants.kFinishedBooking)) {
                    holder.btnDel.setVisibility(View.GONE);
                    if (info.getIsRated() == null || info.getIsRated().equalsIgnoreCase("0")) {
                        holder.btnRate.setVisibility(View.VISIBLE);
                        holder.rateVu.setVisibility(View.GONE);
                        holder.tvRateTitle.setText(context.getString(R.string.his_last_rating));
                    } else {
                        holder.btnRate.setVisibility(View.GONE);
                        holder.rateVu.setVisibility(View.VISIBLE);
                        holder.tvRateTitle.setText(context.getString(R.string.you_give_rate));
                    }
                } else {
                    holder.btnDel.setVisibility(View.VISIBLE);
                    holder.btnRate.setVisibility(View.GONE);
                    holder.rateVu.setVisibility(View.VISIBLE);
                    holder.tvRateTitle.setText(context.getString(R.string.his_last_rating));
                }
            } else {
                holder.btnDel.setVisibility(View.GONE);
                if (bookingStatus.equalsIgnoreCase(Constants.kFinishedBooking)) {
                    if (info.getId().equalsIgnoreCase(Functions.getPrefValue(context, Constants.kUserID))) {
                        holder.btnRate.setVisibility(View.GONE);
                        holder.rateVu.setVisibility(View.GONE);
                    } else {
                        if (info.getIsRated() == null || info.getIsRated().equalsIgnoreCase("0")) {
                            holder.btnRate.setVisibility(View.VISIBLE);
                            holder.rateVu.setVisibility(View.GONE);
                            holder.tvRateTitle.setText(context.getString(R.string.his_last_rating));
                        } else {
                            holder.btnRate.setVisibility(View.GONE);
                            holder.rateVu.setVisibility(View.VISIBLE);
                            holder.tvRateTitle.setText(context.getString(R.string.you_give_rate));
                        }
                    }
                } else {
                    holder.btnRate.setVisibility(View.GONE);
                    holder.rateVu.setVisibility(View.VISIBLE);
                    holder.tvRateTitle.setText(context.getString(R.string.his_last_rating));
                }
            }
        }
        else {
            holder.btnRate.setVisibility(View.GONE);
            holder.rateVu.setVisibility(View.GONE);
            holder.btnDel.setVisibility(View.GONE);
        }

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
            }
        });

        holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnDeleteClick(v, holder.getAdapterPosition());
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

        ImageView imgVu, imgVuTag;
        TextView tvName, tvPosition, tvRate, tvRateTitle, tvRank;
        RelativeLayout relMain;
        LinearLayout rateVu;
        CardView btnRate;
        ImageButton btnDel;

        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.player_image);
            imgVuTag = itemView.findViewById(R.id.img_tag);
            tvName = itemView.findViewById(R.id.tv_name);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvPosition = itemView.findViewById(R.id.tv_position);
            tvRate = itemView.findViewById(R.id.tv_rate);
            tvRateTitle = itemView.findViewById(R.id.tv_rate_title);
            relMain = itemView.findViewById(R.id.rl_main);
            btnRate = itemView.findViewById(R.id.btn_rate);
            rateVu = itemView.findViewById(R.id.rate_vu);
            btnDel = itemView.findViewById(R.id.btn_del);
            rateVu.setVisibility(View.GONE);
            btnRate.setVisibility(View.GONE);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
        void OnDeleteClick(View v, int pos);
        void OnRateClick(View v, int pos);
    }
}