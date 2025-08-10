package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerInfo;

public class OleBookingDateLimitPlayerAdapter extends RecyclerView.Adapter<OleBookingDateLimitPlayerAdapter.ViewHolder> {

    private final Context context;
    private OnItemClickListener onItemClickListener;
    private final List<OlePlayerInfo> list;

    public OleBookingDateLimitPlayerAdapter(Context context, List<OlePlayerInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olebooking_date_limit_player_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerInfo info = list.get(position);
        Glide.with(context).load(info.getPhotoUrl()).placeholder(R.drawable.player_active).into(holder.imgVu);
        holder.tvName.setText(info.getNickName());
        holder.tvAge.setText(String.format("%s %s", context.getString(R.string.age), info.getAge()));
        if (info.getPaymentMethod().equalsIgnoreCase("cash")) {
            holder.tvPaymentMethod.setText(R.string.cash);
        }
        else if (info.getPaymentMethod().equalsIgnoreCase("card")) {
            holder.tvPaymentMethod.setText(R.string.card);
        }
        else {
            holder.tvPaymentMethod.setText(String.format("%s/%s", context.getString(R.string.cash), context.getString(R.string.card)));
        }
        if (info.getLevel() != null && !info.getLevel().isEmpty() && !info.getLevel().getValue().equalsIgnoreCase("")) {
            holder.tvRank.setVisibility(View.VISIBLE);
            holder.tvRank.setText(String.format("LV: %s", info.getLevel().getValue()));
        }
        else {
            holder.tvRank.setVisibility(View.INVISIBLE);
        }

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgVu;
        TextView tvAge, tvName, tvPaymentMethod, tvRank;
        RelativeLayout relMain;

        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.player_image);
            tvAge = itemView.findViewById(R.id.tv_age);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);
            relMain = itemView.findViewById(R.id.rl_main);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}