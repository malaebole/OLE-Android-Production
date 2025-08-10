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
import ae.oleapp.models.UpcomingExpense;

public class OleUpcomingExpenseHistoryAdapter extends RecyclerView.Adapter<OleUpcomingExpenseHistoryAdapter.ViewHolder> {

        private final Context context;
        private final List<UpcomingExpense> list;
        private ItemClickListener itemClickListener;
        private String selectedId = "";


    public OleUpcomingExpenseHistoryAdapter(Context context, List<UpcomingExpense> list) {
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_expense_history_vu, parent, false);
            return new ViewHolder(v);

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//        if (list.get(position).getId().equalsIgnoreCase(selectedId)){
            holder.tvBankName.setText(list.get(position).getBankName());
            holder.tvUpcomingExpense.setText(list.get(position).getTitle());
            holder.tvUpcomingDate.setText(list.get(position).getRecurringDate());
            holder.tvCurrency.setText(list.get(position).getCurrency());
            holder.tvAmount.setText(list.get(position).getAmount());

            holder.relUpcomingExpense.setOnClickListener(new View.OnClickListener() {
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

            TextView tvBankName, tvUpcomingExpense, tvUpcomingDate, tvCurrency, tvAmount;
            CardView relUpcomingExpense;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvBankName = itemView.findViewById(R.id.bank_name_tv);
                tvUpcomingExpense = itemView.findViewById(R.id.upcoming_expense_tv);
                tvUpcomingDate = itemView.findViewById(R.id.upcoming_expense_date_tv);
                tvCurrency = itemView.findViewById(R.id.currency_tv);
                tvAmount = itemView.findViewById(R.id.amount_tv);
                relUpcomingExpense = itemView.findViewById(R.id.rel_upcoming_expense);


            }
        }

        public interface ItemClickListener {
            void itemClicked(View view, int pos);
        }
}
