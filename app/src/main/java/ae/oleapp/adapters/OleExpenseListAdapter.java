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

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleClubExpense;

public class OleExpenseListAdapter extends RecyclerView.Adapter<OleExpenseListAdapter.ViewHolder> {

    private final Context context;
    private final List<OleClubExpense> list;
    private OnItemClickListener onItemClickListener;

    public OleExpenseListAdapter(Context context, List<OleClubExpense> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleexpense_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleClubExpense expense = list.get(position);
        holder.tvTitle.setText(expense.getName());
        holder.tvAddedBy.setText(String.format("%s: %s", context.getString(R.string.added_by), expense.getAddedBy()));
        holder.tvDate.setText(String.format("%s: %s", context.getString(R.string.added_date), expense.getAddedDate()));
        holder.tvAmount.setText(String.format("%s %s", expense.getAmount(), expense.getCurrency()));
        if (expense.getReceiptPhoto().equalsIgnoreCase("")) {
            holder.attachIc.setVisibility(View.GONE);
        }
        else {
            holder.attachIc.setVisibility(View.VISIBLE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
            }
        });
        holder.attachIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnAttachmentClick(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout layout;
        TextView tvTitle, tvAddedBy, tvDate, tvAmount;
        ImageView attachIc;

        ViewHolder(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.rl_main);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAddedBy = itemView.findViewById(R.id.tv_added_by);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            attachIc = itemView.findViewById(R.id.btn_attach);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
        void OnAttachmentClick(View v, int pos);
    }
}