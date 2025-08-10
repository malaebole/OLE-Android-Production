package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;

public class OleBuySmsAdapter extends RecyclerView.Adapter<OleBuySmsAdapter.ViewHolder> {

    private final Context context;
    private final List<Integer> list;
    private OnItemClickListener onItemClick;
    private int selectedQty;

    public OleBuySmsAdapter(Context context, List<Integer> list, int selectedQty) {
        this.context = context;
        this.list = list;
        this.selectedQty = selectedQty;
    }

    public void setOnItemClick(OnItemClickListener onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setSelectedQty(int selectedQty) {
        this.selectedQty = selectedQty;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olebuy_sms, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvCount.setText(String.valueOf(list.get(position)));
        if (list.get(position) == selectedQty) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.blueColorNew));
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.transparent));
            holder.tvCount.setTextColor(context.getResources().getColor(R.color.whiteColor));
            holder.tvSms.setTextColor(context.getResources().getColor(R.color.whiteColor));
        }
        else {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.separatorColor));
            holder.tvCount.setTextColor(context.getResources().getColor(R.color.darkTextColor));
            holder.tvSms.setTextColor(context.getResources().getColor(R.color.subTextColor));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.itemClicked(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MaterialCardView cardView;
        TextView tvCount, tvSms;

        ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardvu);
            tvCount = itemView.findViewById(R.id.tv_qty);
            tvSms = itemView.findViewById(R.id.tv_sms);
        }
    }

    public interface OnItemClickListener {
        void itemClicked(View view, int position);
    }
}