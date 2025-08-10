package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.owner.OlePadelBookingDetailActivity;
import ae.oleapp.padel.OlePartnerListActivity;
import ae.oleapp.player.OlePBookingInfoActivity;

public class OlePadelPlayerListAdapter extends RecyclerView.Adapter<OlePadelPlayerListAdapter.ViewHolder> {

    private final Context context;
    private OnItemClickListener onItemClickListener;
    private List<OlePlayerInfo> list;
    public List<OlePlayerInfo> selectedList = new ArrayList<>();
    private String partnerPrice = "";
    private String currency = "";
    private boolean isPay = false;

    public OlePadelPlayerListAdapter(Context context, List<OlePlayerInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setPay(boolean pay) {
        isPay = pay;
        notifyDataSetChanged();
    }

    public void setDatasource(List<OlePlayerInfo> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void setDatasource(List<OlePlayerInfo> list, String partnerPrice, String currency) {
        this.partnerPrice = partnerPrice;
        this.currency = currency;
        this.list = list;
        notifyDataSetChanged();
    }

    public void setDatasource(List<OlePlayerInfo> list, boolean isPay) {
        this.isPay = isPay;
        this.list = list;
        notifyDataSetChanged();
    }

    public void selectItem(OlePlayerInfo item) {
        int index = isExist(item.getId());
        if (index == -1) {
            selectedList.add(item);
        }
        else {
            selectedList.remove(index);
        }
        notifyDataSetChanged();
    }

    private int isExist(String id) {
        for (int i = 0; i < selectedList.size(); i++) {
            if (selectedList.get(i).getId().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return  -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olepadel_player_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerInfo info = list.get(position);
        Glide.with(context).load(info.getPhotoUrl()).placeholder(R.drawable.player_active).into(holder.imgVu);
        holder.tvName.setText(info.getNickName());
        holder.tvAge.setText(String.format("%s %s", context.getString(R.string.age), info.getAge()));
        holder.tvPrice.setText(String.format("%s %s", info.getPaidAmount(), info.getCurrency()));
        if (info.getPaymentStatus() != null && info.getPaymentStatus().equalsIgnoreCase("1")) {
            holder.tvPaymentStatus.setText(R.string.paid);
            holder.tvPaymentStatus.setTextColor(context.getResources().getColor(R.color.greenColor));
        }
        else {
            holder.tvPaymentStatus.setText(R.string.unpaid);
            holder.tvPaymentStatus.setTextColor(context.getResources().getColor(R.color.redColor));
        }
        if (info.getLevel() != null && !info.getLevel().isEmpty() && !info.getLevel().getValue().equalsIgnoreCase("")) {
            holder.tvRank.setVisibility(View.VISIBLE);
            holder.tvRank.setText(String.format("LV: %s", info.getLevel().getValue()));
        }
        else {
            holder.tvRank.setVisibility(View.INVISIBLE);
        }

        if (context instanceof OlePadelBookingDetailActivity) {
            holder.btnDel.setVisibility(View.GONE);
            holder.imgVuCheck.setVisibility(View.GONE);
            holder.tvPaymentStatus.setVisibility(View.VISIBLE);
        }
        else if (context instanceof OlePBookingInfoActivity) {
            holder.tvPrice.setText(String.format("%s %s", partnerPrice, currency));
            int index = isExist(info.getId());
            if (index == -1) {
                holder.imgVuCheck.setImageResource(R.drawable.uncheck);
            }
            else {
                holder.imgVuCheck.setImageResource(R.drawable.p_check);
            }
        }
        else if (context instanceof OlePartnerListActivity) {
            if (isPay) {
                holder.btnDel.setVisibility(View.GONE);
                holder.imgVuCheck.setVisibility(View.VISIBLE);
                holder.tvPaymentStatus.setVisibility(View.VISIBLE);
                int index = isExist(info.getId());
                if (index == -1) {
                    holder.imgVuCheck.setImageResource(R.drawable.uncheck);
                }
                else {
                    holder.imgVuCheck.setImageResource(R.drawable.p_check);
                }
            }
            else {
                holder.btnDel.setVisibility(View.GONE);
                holder.imgVuCheck.setVisibility(View.GONE);
                holder.tvPaymentStatus.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.btnDel.setVisibility(View.GONE);
            holder.imgVuCheck.setVisibility(View.GONE);
            holder.tvPaymentStatus.setVisibility(View.GONE);
            holder.tvPrice.setVisibility(View.GONE);
        }

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
                }
            }
        });
        holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnDeleteClick(v, holder.getAdapterPosition());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgVu, imgVuCheck;
        TextView tvAge, tvName, tvPrice, tvPaymentStatus, tvRank;
        RelativeLayout relMain;
        ImageButton btnDel;

        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.player_image);
            imgVuCheck = itemView.findViewById(R.id.img_vu_check);
            tvAge = itemView.findViewById(R.id.tv_age);
            tvName = itemView.findViewById(R.id.tv_name);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvPaymentStatus = itemView.findViewById(R.id.tv_payment_status);
            relMain = itemView.findViewById(R.id.rl_main);
            btnDel = itemView.findViewById(R.id.btn_del);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
        void OnDeleteClick(View v, int pos);
    }
}