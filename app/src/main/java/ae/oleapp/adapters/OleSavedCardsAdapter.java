package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleUserCard;

public class OleSavedCardsAdapter extends RecyclerView.Adapter<OleSavedCardsAdapter.ViewHolder> {

    private final Context context;
    private final List<OleUserCard> list;
    private ItemClickListener itemClickListener;

    public OleSavedCardsAdapter(Context context, List<OleUserCard> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olecard_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleUserCard oleUserCard = list.get(position);
        holder.tvCard.setText(oleUserCard.getCardNumber());
        holder.tvExpiry.setText(oleUserCard.getCardExpiry());
        holder.tvCVV.setText("***");

        int prefix = Integer.parseInt(oleUserCard.getCardNumber().substring(0, 2));
        if (oleUserCard.getCardNumber().startsWith("4")) {
            holder.imgVuType.setImageResource(R.drawable.visa_ic);
        }
        else if (prefix >= 51 && prefix <= 55) {
            holder.imgVuType.setImageResource(R.drawable.mastercard_ic);
        }
        else {
            holder.imgVuType.setImageResource(0);
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(v, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvCard, tvExpiry, tvCVV;
        ImageView imgVuType;
        CardView mainLayout;

        ViewHolder(View itemView) {
            super(itemView);

            tvCard = itemView.findViewById(R.id.tv_card);
            tvExpiry = itemView.findViewById(R.id.tv_expiry);
            tvCVV = itemView.findViewById(R.id.tv_cvv);
            imgVuType = itemView.findViewById(R.id.img_vu_type);
            mainLayout = itemView.findViewById(R.id.main);
        }
    }

    public interface ItemClickListener {
        void onItemClicked(View view, int pos);
    }
}