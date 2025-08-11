package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Earning;
import ae.oleapp.models.OleEarningData;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class OleOwnerEarningAdapter extends SectionedRecyclerViewAdapter {

    private final List<OleEarningData> list;
    private final Context context;
    private ItemClickListener itemClickListener;

    public OleOwnerEarningAdapter(Context context, @NonNull List<OleEarningData> list) {

        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getSectionCount() {
        return list.size();
    }


    public int getItemCount(int section) {
        return list.get(section).getEarnings().size();
    }

    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int section) {
        HeaderViewHolder holder = (HeaderViewHolder)viewHolder;
        holder.tvDate.setText("");
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int section, int relativePosition, int absolutePosition) {
        ViewHolder holder = (ViewHolder)viewHolder;
        Earning earning = list.get(section).getEarnings().get(relativePosition);
        holder.tvClubName.setVisibility(View.VISIBLE);
        holder.tvFieldName.setVisibility(View.VISIBLE);
        holder.tvDesc.setText(earning.getPlayerName());
        holder.tvClubName.setText(earning.getClubName());
        if (earning.getFieldSize() != null && !earning.getFieldSize().isEmpty()) {
            holder.tvFieldName.setText(String.format("%s (%s)", earning.getFieldName(), earning.getFieldSize()));
        }
        else {
            holder.tvFieldName.setText(earning.getFieldName());
        }
        holder.tvAmount.setText(String.format("%s %s", earning.getAmount(), earning.getCurrency()));
        holder.tvDate.setText(earning.getDate());
        if ( earning.getType().equalsIgnoreCase("booking")) {
            holder.imgVuLogo.setImageResource(R.drawable.credit_received_ic);
            holder.tvAmount.setTextColor(Color.parseColor("#49D483"));
        }
        else {
            holder.imgVuLogo.setImageResource(R.drawable.credit_spent_ic);
            holder.tvAmount.setTextColor(Color.parseColor("#F02301"));
            if (earning.getType().equalsIgnoreCase("match_fee")) {
                holder.tvDesc.setText(R.string.ole_match_fee);
            }
            else {
                holder.tvDesc.setText(R.string.transfer_to_bank);
                holder.tvClubName.setVisibility(View.GONE);
                holder.tvFieldName.setVisibility(View.GONE);
            }
        }

        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, section, relativePosition);
            }
        });
        if (relativePosition == 0 && relativePosition == list.get(section).getEarnings().size() - 1) {
            holder.main.setBackgroundResource(R.drawable.wallet_rounded_corner);
            holder.sepVu.setVisibility(View.GONE);
        }
        else if (relativePosition == 0) {
            holder.main.setBackgroundResource(R.drawable.wallet_top_rounded_corner);
            holder.sepVu.setVisibility(View.VISIBLE);
        }
        else if (relativePosition == list.get(section).getEarnings().size() - 1) {
            holder.main.setBackgroundResource(R.drawable.wallet_bottom_rounded_corner);
            holder.sepVu.setVisibility(View.GONE);
        }
        else {
            holder.main.setBackgroundResource(0);
            holder.main.setBackgroundColor(Color.WHITE);
            holder.sepVu.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olewallet_header, parent, false);
            v.getLayoutParams().height = 25;
            v.requestLayout();
            return new HeaderViewHolder(v);
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleowner_earning, parent, false);
            return new ViewHolder(v);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDesc, tvClubName, tvFieldName, tvAmount, tvDate;
        RelativeLayout main;
        ImageView imgVuLogo;
        View sepVu;

        ViewHolder(@NonNull View view) {
            super(view);

            main = itemView.findViewById(R.id.main);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvClubName = itemView.findViewById(R.id.tv_club_name);
            tvFieldName = itemView.findViewById(R.id.tv_field_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvDate = itemView.findViewById(R.id.tv_date);
            imgVuLogo = itemView.findViewById(R.id.img_vu_logo);
            sepVu = itemView.findViewById(R.id.sep_vu);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        final TextView tvDate;

        HeaderViewHolder(@NonNull View view) {
            super(view);

            tvDate = view.findViewById(R.id.tv_date);
            tvDate.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int section, int pos);
    }
}
