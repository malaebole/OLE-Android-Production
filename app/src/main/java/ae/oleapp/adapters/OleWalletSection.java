package ae.oleapp.adapters;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleTransactionHistory;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class OleWalletSection extends Section {

    private final String title;
    private final List<OleTransactionHistory> list;
    private final ClickListener clickListener;

    public OleWalletSection(@NonNull final String title, @NonNull final List<OleTransactionHistory> list,
                            @NonNull final ClickListener clickListener) {

        super(SectionParameters.builder()
                .itemResourceId(R.layout.olewallet_list)
                .headerResourceId(R.layout.olewallet_header)
                .build());

        this.title = title;
        this.list = list;
        this.clickListener = clickListener;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ItemViewHolder viewHolder = (ItemViewHolder) holder;

        OleTransactionHistory history  = list.get(position);
        viewHolder.tvDesc.setText(history.getPaymentName());
        if (history.getPaymentType().equalsIgnoreCase("topup") || history.getPaymentType().equalsIgnoreCase("refund")) {
            viewHolder.tvAmount.setTextColor(Color.parseColor("#49D483"));
            viewHolder.tvAmount.setText(String.format("+%s %s", history.getAmount(), history.getCurrency()));
            viewHolder.imgVuLogo.setImageResource(R.drawable.credit_received_ic);
            if (history.getPaymentType().equalsIgnoreCase("refund")) {
                if (history.getClubName().equalsIgnoreCase("")) {
                    viewHolder.tvClubName.setVisibility(View.GONE);
                }
                else {
                    viewHolder.tvClubName.setVisibility(View.VISIBLE);
                    viewHolder.tvClubName.setText(history.getClubName());
                }
                if (history.getFieldName() == null || history.getFieldName().equalsIgnoreCase("") || history.getFieldSize().equalsIgnoreCase("")) {
                    viewHolder.tvFieldName.setVisibility(View.GONE);
                }
                else {
                    viewHolder.tvFieldName.setVisibility(View.VISIBLE);
                    viewHolder.tvFieldName.setText(String.format("%s (%s)", history.getFieldName(), history.getFieldSize()));
                }
            }
            else {
                viewHolder.tvClubName.setText(history.getCardNum());
                viewHolder.tvFieldName.setText(history.getCardName());
            }
        }
        else if (history.getPaymentType().equalsIgnoreCase("shopping")) {
            viewHolder.tvAmount.setTextColor(Color.parseColor("#F02301"));
            viewHolder.imgVuLogo.setImageResource(R.drawable.credit_spent_ic);
            viewHolder.tvClubName.setText(R.string.shop);
            viewHolder.tvFieldName.setVisibility(View.GONE);
            viewHolder.tvAmount.setText(String.format("-%s %s", history.getAmount(), history.getCurrency()));
        }
        else if (history.getPaymentType().equalsIgnoreCase("deduction")) {
            viewHolder.tvAmount.setTextColor(Color.parseColor("#F02301"));
            viewHolder.imgVuLogo.setImageResource(R.drawable.credit_spent_ic);
            viewHolder.tvClubName.setText(R.string.wallet_deduction);
            viewHolder.tvFieldName.setVisibility(View.GONE);
            viewHolder.tvAmount.setText(String.format("-%s %s", history.getAmount(), history.getCurrency()));
        }
        else {
            viewHolder.tvAmount.setTextColor(Color.parseColor("#F02301"));
            viewHolder.imgVuLogo.setImageResource(R.drawable.credit_spent_ic);
            viewHolder.tvClubName.setText(history.getClubName());
            viewHolder.tvFieldName.setText(String.format("%s (%s)", history.getFieldName(), history.getFieldSize()));
            viewHolder.tvAmount.setText(String.format("-%s %s", history.getAmount(), history.getCurrency()));
        }


        viewHolder.main.setOnClickListener(v ->
                clickListener.onItemRootViewClicked(this, list.get(position))
        );

        if (position == 0 && position == list.size() - 1) {
            viewHolder.main.setBackgroundResource(R.drawable.wallet_rounded_corner);
            viewHolder.sepVu.setVisibility(View.GONE);
        }
        else if (position == 0) {
            viewHolder.main.setBackgroundResource(R.drawable.wallet_top_rounded_corner);
            viewHolder.sepVu.setVisibility(View.VISIBLE);
        }
        else if (position == list.size() - 1) {
            viewHolder.main.setBackgroundResource(R.drawable.wallet_bottom_rounded_corner);
            viewHolder.sepVu.setVisibility(View.GONE);
        }
        else {
            viewHolder.main.setBackgroundResource(0);
            viewHolder.main.setBackgroundColor(Color.WHITE);
            viewHolder.sepVu.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final HeaderViewHolder viewHolder = (HeaderViewHolder) holder;

        viewHolder.tvDate.setText(title);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvDesc, tvClubName, tvFieldName, tvAmount;
        RelativeLayout main;
        ImageView imgVuLogo;
        View sepVu;

        ItemViewHolder(@NonNull View view) {
            super(view);

            main = itemView.findViewById(R.id.main);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvClubName = itemView.findViewById(R.id.tv_club_name);
            tvFieldName = itemView.findViewById(R.id.tv_field_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            imgVuLogo = itemView.findViewById(R.id.img_vu_logo);
            sepVu = itemView.findViewById(R.id.sep_vu);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        final TextView tvDate;

        HeaderViewHolder(@NonNull View view) {
            super(view);

            tvDate = view.findViewById(R.id.tv_date);
        }
    }

    public interface ClickListener {

        void onItemRootViewClicked(@NonNull final OleWalletSection section, final OleTransactionHistory history);
    }
}
