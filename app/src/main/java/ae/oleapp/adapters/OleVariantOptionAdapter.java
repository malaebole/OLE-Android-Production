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

public class OleVariantOptionAdapter extends RecyclerView.Adapter<OleVariantOptionAdapter.ViewHolder> {

    private final Context context;
    private final List<String> list;
    private ItemClickListener itemClickListener;
    private int selectedIndex = -1;

    public OleVariantOptionAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olevariant_option, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvValue.setText(list.get(position));
        if (selectedIndex == position) {
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.blueColorNew));
            holder.tvValue.setTextColor(context.getResources().getColor(R.color.blueColorNew));
        }
        else {
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.separatorColor));
            holder.tvValue.setTextColor(context.getResources().getColor(R.color.darkTextColor));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
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

        MaterialCardView cardView;
        TextView tvValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_vu);
            tvValue = itemView.findViewById(R.id.tv_value);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}