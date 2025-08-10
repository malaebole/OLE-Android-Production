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
import ae.oleapp.models.OleTipPayment;

public class OleTipPaymentAdapter extends RecyclerView.Adapter<OleTipPaymentAdapter.ViewHolder> {

    private final Context context;
    private final List<OleTipPayment> list;
    private ItemClickListener itemClickListener;

    public OleTipPaymentAdapter(Context context, List<OleTipPayment> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oletip_payment, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleTipPayment oleTipPayment = list.get(position);
        holder.tvTitle.setText(oleTipPayment.getTitle());
        holder.tvDate.setText(oleTipPayment.getDate());
        holder.tvAmount.setText(String.format("%s %s", oleTipPayment.getAmount(), oleTipPayment.getCurrency()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle, tvDate, tvAmount;
        CardView layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            layout = itemView.findViewById(R.id.rel_main);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}