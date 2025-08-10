package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerBalance;

public class OleOutstandingBalanceListAdapter extends RecyclerView.Adapter<OleOutstandingBalanceListAdapter.ViewHolder> {

    private final Context context;
    private final List<OlePlayerBalance> list;
    private ItemClickListener itemClickListener;

    public OleOutstandingBalanceListAdapter(Context context, List<OlePlayerBalance> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleoutstanding_balance, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerBalance balance = list.get(position);
        holder.tvClub.setText(balance.getClubName());
        holder.tvDate.setText(String.format("%s: %s", context.getString(R.string.date), balance.getDate()));
        holder.tvAmount.setText(String.format("%s %s",balance.getAmount(), balance.getCurrency()));

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvClub, tvDate, tvAmount;
        RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClub = itemView.findViewById(R.id.tv_club_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            layout = itemView.findViewById(R.id.rl_main);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}