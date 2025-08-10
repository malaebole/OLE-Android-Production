package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleKeyValuePair;

public class OleEmpRatingFilterAdapter extends RecyclerView.Adapter<OleEmpRatingFilterAdapter.ViewHolder> {

    private final Context context;
    private final List<OleKeyValuePair> list;
    private int selectedIndex = -1;
    private OnItemClickListener onItemClickListener;

    public OleEmpRatingFilterAdapter(Context context, List<OleKeyValuePair> list, int selectedIndex) {
        this.context = context;
        this.list = list;
        this.selectedIndex = selectedIndex;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleemp_rating_filter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleKeyValuePair pair = list.get(position);
        holder.tvTitle.setText(pair.getValue());
        holder.imgVuEmoji.setVisibility(View.VISIBLE);
        if (pair.getKey().equalsIgnoreCase("1.0")) {
            holder.imgVuEmoji.setImageResource(R.drawable.bad_service);
        }
        else if (pair.getKey().equalsIgnoreCase("2.0")) {
            holder.imgVuEmoji.setImageResource(R.drawable.average);
        }
        else if (pair.getKey().equalsIgnoreCase("3.0")) {
            holder.imgVuEmoji.setImageResource(R.drawable.good);
        }
        else if (pair.getKey().equalsIgnoreCase("4.0")) {
            holder.imgVuEmoji.setImageResource(R.drawable.very_good);
        }
        else if (pair.getKey().equalsIgnoreCase("5.0")) {
            holder.imgVuEmoji.setImageResource(R.drawable.excellent);
        }
        else {
            holder.imgVuEmoji.setVisibility(View.GONE);
        }
        if (selectedIndex == position) {
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.whiteColor));
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.blueColorNew));
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.transparent));
        }
        else {
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.darkTextColor));
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.separatorColor));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MaterialCardView cardView;
        ImageView imgVuEmoji;
        TextView tvTitle;

        ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_vu);
            tvTitle = itemView.findViewById(R.id.tv_title);
            imgVuEmoji = itemView.findViewById(R.id.img_vu_emoji);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}