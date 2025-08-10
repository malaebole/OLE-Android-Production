package ae.oleapp.adapters;

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.IncomeHistory;
import ae.oleapp.models.PartnerHistoryModel;

public class PartnerHistoryAdapter extends RecyclerView.Adapter<PartnerHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<PartnerHistoryModel> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";


    public PartnerHistoryAdapter(Context context, List<PartnerHistoryModel> list) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_history_vu, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//        if (list.get(position).getId().equalsIgnoreCase(selectedId)){

        holder.tvBankName.setText(list.get(position).getBankName());
        holder.tvSource.setText(list.get(position).getDate());
        holder.tvDate.setText(list.get(position).getAddedDate());
        holder.tvCurrency.setText(list.get(position).getCurrency());
        holder.tvAmount.setText(list.get(position).getAmount());

        holder.relIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getAdapterPosition());

            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvBankName, tvSource, tvDate, tvCurrency, tvAmount;
        CardView relIncome;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBankName = itemView.findViewById(R.id.bank_name_tv);
            tvSource = itemView.findViewById(R.id.source_tv);
            tvDate = itemView.findViewById(R.id.source_date_tv);
            tvCurrency = itemView.findViewById(R.id.currency_tv);
            tvAmount = itemView.findViewById(R.id.amount_tv);
            relIncome = itemView.findViewById(R.id.rel_income);


        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}