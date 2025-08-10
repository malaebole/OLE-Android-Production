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

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerPosition;

public class OlePositionListAdapter extends RecyclerView.Adapter<OlePositionListAdapter.ViewHolder> {

    private final Context context;
    private final List<OlePlayerPosition> list;
    private int selectedIndex;
    private ItemClickListener itemClickListener;
    private final int layoutId;

    public OlePositionListAdapter(Context context, int layoutId, List<OlePlayerPosition> list, int selectedIndex) {
        this.context = context;
        this.list = list;
        this.selectedIndex = selectedIndex;
        this.layoutId = layoutId;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        this.notifyDataSetChanged();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerPosition olePlayerPosition = list.get(position);

        if (layoutId == R.layout.oleposition_list) {
            holder.tvName.setText(olePlayerPosition.getName());
            if (selectedIndex == position) {
                holder.imgVuCheck.setImageResource(R.drawable.check);
            }
            else {
                holder.imgVuCheck.setImageResource(R.drawable.friendly_game_round);
            }

            holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(v, holder.getAdapterPosition());
                }
            });
        }
        else {
            holder.tvPosition.setText(olePlayerPosition.getName());
            if (selectedIndex == position) {
                holder.tvPosition.setTextColor(context.getResources().getColor(R.color.whiteColor));
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.blueColorNew));
            }
            else {
                holder.tvPosition.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));
            }

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(v, holder.getAdapterPosition());
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvPosition;
        ImageView imgVuCheck;
        CardView mainLayout;
        MaterialCardView cardView;

        ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            imgVuCheck = itemView.findViewById(R.id.img_vu_check);
            mainLayout = itemView.findViewById(R.id.main);
            cardView = itemView.findViewById(R.id.card_vu);
            tvPosition = itemView.findViewById(R.id.tv_date);
        }
    }

    public interface ItemClickListener {
        void onItemClicked(View view, int pos);
    }
}