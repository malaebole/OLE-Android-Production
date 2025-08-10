package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerBalance;

public class OleEarningBalanceDetailAdapter extends RecyclerView.Adapter<OleEarningBalanceDetailAdapter.ViewHolder> {

    private final Context context;
    private List<OlePlayerBalance> list;
    private ItemClickListener itemClickListener;
    private boolean isPaid = false;

    public OleEarningBalanceDetailAdapter(Context context, List<OlePlayerBalance> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setDatasource(List<OlePlayerBalance> list, boolean isPaid) {
        this.list = list;
        this.isPaid = isPaid;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleearning_balance_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerBalance balance = list.get(position);
        holder.tvAmount.setText(String.format("%s %s", balance.getAmount(), balance.getCurrency()));
        holder.tvName.setText(balance.getPlayerName());
        holder.tvDate.setText(String.format("%s: %s", context.getString(R.string.date), balance.getDate()));
        if (isPaid) {
            holder.tvAddedBy.setText(String.format("%s: %s", context.getString(R.string.received_by), balance.getAddedBy()));
            if (balance.getIsDiscount().equalsIgnoreCase("1")) {
                holder.tvDiscount.setVisibility(View.VISIBLE);
            }
            else {
                holder.tvDiscount.setVisibility(View.GONE);
            }
            if (balance.getReceipt().equalsIgnoreCase("")) {
                holder.btnReceipt.setVisibility(View.GONE);
            }
            else {
                holder.btnReceipt.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.tvDiscount.setVisibility(View.GONE);
            holder.btnReceipt.setVisibility(View.GONE);
            holder.tvAddedBy.setText(String.format("%s: %s", context.getString(R.string.added_by), balance.getAddedBy()));
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getAdapterPosition());
            }
        });
        holder.btnReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.receiptClicked(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvDate, tvAddedBy, tvAmount, tvDiscount;
        Button btnReceipt;
        RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAddedBy = itemView.findViewById(R.id.tv_added_by);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvDiscount = itemView.findViewById(R.id.tv_discount);
            btnReceipt = itemView.findViewById(R.id.btn_receipt);
            layout = itemView.findViewById(R.id.rl_main);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void receiptClicked(View view, int pos);
    }
}