package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.BankHistory;
import ae.oleapp.models.IncomeHistory;

public class OleBankIncomeHistoryAdapter extends RecyclerView.Adapter<OleBankIncomeHistoryAdapter.ViewHolder>{

    private final Context context;
    private final List<BankHistory> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";


    public OleBankIncomeHistoryAdapter(Context context, List<BankHistory> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_history_vu, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //if (list.get(position).getId().equalsIgnoreCase(selectedId)){


        holder.tvDate.setText(list.get(position).getAddedDate());
        holder.tvSource.setText(list.get(position).getSource());
        holder.tvEarningAmt.setText(list.get(position).getEarningAmount());
        holder.tvIcnomeCurrency.setText(list.get(position).getCurrency());
        holder.tvCurrency.setText(list.get(position).getCurrency());
        holder.tvAmount.setText(list.get(position).getAmount());

        if (!list.get(position).getDepositType().isEmpty()){
            if (list.get(position).getDepositType().equalsIgnoreCase("extra")){
                holder.tvExtraOrRemaining.setText("(Extra +"+list.get(position).getExtraAmount()+" "+list.get(position).getCurrency()+")");
                holder.tvExtraOrRemaining.setTextColor(context.getResources().getColor(R.color.greenColor));
            }else{
                holder.tvExtraOrRemaining.setText("(Remaining "+list.get(position).getExtraAmount()+" "+list.get(position).getCurrency()+")");
                holder.tvExtraOrRemaining.setTextColor(context.getResources().getColor(R.color.redColor));

            }
        }

        holder.relIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v,holder.getAdapterPosition());

            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvSource, tvDate,tvCurrency,tvAmount, tvEarningAmt, tvIcnomeCurrency, tvExtraOrRemaining;
        CardView relIncome;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSource = itemView.findViewById(R.id.source_tv);
            tvDate = itemView.findViewById(R.id.source_date_tv);
            tvCurrency = itemView.findViewById(R.id.currency_tv);
            tvAmount = itemView.findViewById(R.id.amount_tv);
            relIncome = itemView.findViewById(R.id.rel_income);
            tvEarningAmt = itemView.findViewById(R.id.earning_amt);
            tvIcnomeCurrency = itemView.findViewById(R.id.income_currency);
            tvExtraOrRemaining = itemView.findViewById(R.id.extra_or_remain);


        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}