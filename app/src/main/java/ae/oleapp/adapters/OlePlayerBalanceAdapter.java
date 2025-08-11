package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ae.oleapp.R;
import ae.oleapp.models.OlePlayerBalance;
import ae.oleapp.models.OlePlayerBalanceDetail;
import ae.oleapp.util.expandablerecyclerviewadapter.BaseExpandableRecyclerViewAdapter;

public class OlePlayerBalanceAdapter extends BaseExpandableRecyclerViewAdapter<OlePlayerBalance, OlePlayerBalanceDetail, OlePlayerBalanceAdapter.GroupViewHolder, OlePlayerBalanceAdapter.ChildViewHolder> {

    private final Context context;
    private final List<OlePlayerBalance> list;
    private OnItemClickListener itemClickListener;

    public OlePlayerBalanceAdapter(Context context, List<OlePlayerBalance> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public OlePlayerBalance getGroupItem(int groupIndex) {
        return list.get(groupIndex);
    }

    @Override
    public GroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int groupViewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleplayer_balance, parent, false);
        return new GroupViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(GroupViewHolder holder, OlePlayerBalance balance, boolean isExpand) {
        holder.tvAmount.setText(String.format("%s %s", balance.getAmount(), balance.getCurrency()));
        holder.tvDate.setText(String.format("%s: %s", context.getString(R.string.date), balance.getDate()));
        holder.tvAddedBy.setText(String.format("%s: %s", context.getString(R.string.added_by), balance.getAddedBy()));
        if (balance.isExpandable()) {
            holder.btnArrow.setVisibility(View.VISIBLE);
            holder.btnArrow.setImageResource(isExpand ? R.drawable.double_arrow_up : R.drawable.double_arrow_down);
        } else {
            holder.btnArrow.setVisibility(View.GONE);
        }
        if (balance.getAmount().isEmpty() || balance.getAmount().equalsIgnoreCase("0")) {
            holder.btnPay.setVisibility(View.GONE);
        } else {
            holder.btnPay.setVisibility(View.VISIBLE);
        }

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClickListener(balance);
            }
        });
        holder.btnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGroupExpanding(balance)) {
                    foldGroup(balance);
                    holder.btnArrow.setImageResource(R.drawable.double_arrow_down);
                }
                else {
                    expandGroup(balance);
                    holder.btnArrow.setImageResource(R.drawable.double_arrow_up);
                }
            }
        });
        holder.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.payClickListener(balance);
            }
        });
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup parent, int childViewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleplayer_balance_detail, parent, false);
        return new ChildViewHolder(v);
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder childViewHolder, OlePlayerBalance groupBean, OlePlayerBalanceDetail olePlayerBalanceDetail) {
        childViewHolder.tvAmount.setText(String.format("%s %s", olePlayerBalanceDetail.getPaidAmount(), olePlayerBalanceDetail.getCurrency()));
        childViewHolder.tvDate.setText(String.format("%s: %s", context.getString(R.string.date), olePlayerBalanceDetail.getReceivedDate()));
        childViewHolder.tvReceivedBy.setText(String.format("%s: %s", context.getString(R.string.received_by), olePlayerBalanceDetail.getReceivedBy()));
        if (olePlayerBalanceDetail.getIsDiscount().equalsIgnoreCase("1")) {
            childViewHolder.tvDiscount.setVisibility(View.VISIBLE);
        } else {
            childViewHolder.tvDiscount.setVisibility(View.GONE);
        }
        if (olePlayerBalanceDetail.getReceipt().equalsIgnoreCase("")) {
            childViewHolder.attachIcon.setVisibility(View.GONE);
        }
        else {
            childViewHolder.attachIcon.setVisibility(View.VISIBLE);
        }

        childViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.childItemClickListener(olePlayerBalanceDetail);
            }
        });
    }

    class GroupViewHolder extends BaseExpandableRecyclerViewAdapter.BaseGroupViewHolder {

        TextView tvDate, tvAmount, tvAddedBy;
        RelativeLayout relMain, btnPay;
        ImageButton btnArrow;

        GroupViewHolder(View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvAddedBy = itemView.findViewById(R.id.tv_added_by);
            relMain = itemView.findViewById(R.id.rl_main);
            btnPay = itemView.findViewById(R.id.btn_pay);
            btnArrow = itemView.findViewById(R.id.btn_arrow);
        }

        @Override
        protected void onExpandStatusChanged(RecyclerView.Adapter adapter, boolean isExpanding) {
            btnArrow.setImageResource(isExpanding ? R.drawable.double_arrow_up : R.drawable.double_arrow_down);
        }
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvAmount, tvReceivedBy, tvDiscount;
        ImageView attachIcon;
        RelativeLayout layout;

        ChildViewHolder(View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvReceivedBy = itemView.findViewById(R.id.tv_received_by);
            tvDiscount = itemView.findViewById(R.id.tv_discount);
            attachIcon = itemView.findViewById(R.id.attach_ic);
            layout = itemView.findViewById(R.id.rl_main);
        }
    }

    public interface OnItemClickListener {
        void itemClickListener(OlePlayerBalance balance);
        void childItemClickListener(OlePlayerBalanceDetail detail);
        void payClickListener(OlePlayerBalance balance);
    }
}