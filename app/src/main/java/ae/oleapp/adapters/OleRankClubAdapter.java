package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Club;
import ae.oleapp.owner.OleAddEmployeeActivity;
import ae.oleapp.owner.OleAddFieldActivity;
import ae.oleapp.owner.OleBookingCountDetailActivity;

public class OleRankClubAdapter extends RecyclerView.Adapter<OleRankClubAdapter.ViewHolder> {

    private final Context context;
    private final List<Club> list;
    private int selectedIndex = -1;
    //private String selectedId="";
    private OnItemClickListener onItemClickListener;
    private boolean isPadel = false;

    public OleRankClubAdapter(Context context, List<Club> list, int selectedIndex, boolean isPadel) {
        this.context = context;
        this.list = list;
        this.selectedIndex = selectedIndex;
        this.isPadel = isPadel;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }
//    public void setSelectedId(String selectedId) {
//        this.selectedId = selectedId;
//        notifyDataSetChanged();
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olerank_date, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvDate.setText(list.get(position).getName());


          if (selectedIndex == position) {
              if (isPadel) {
                  holder.tvDate.setTextColor(context.getResources().getColor(R.color.yellowColor));
                  holder.cardView.setCardBackgroundColor(Color.parseColor("#0A4B7F"));
              }
              else {
                  holder.tvDate.setTextColor(context.getResources().getColor(R.color.whiteColor));
                  holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.blueColorNew));
              }
              if (context instanceof OleAddFieldActivity || context instanceof OleBookingCountDetailActivity || context instanceof OleAddEmployeeActivity) {
                  holder.cardView.setStrokeColor(context.getResources().getColor(R.color.transparent));
              }
          }
          else {
              if (isPadel) {
                  holder.tvDate.setTextColor(context.getResources().getColor(R.color.whiteColor));
                  holder.cardView.setCardBackgroundColor(Color.parseColor("#0A4B7F"));
              }
              else {
                  holder.tvDate.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                  holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));
              }
              if (context instanceof OleAddFieldActivity || context instanceof OleBookingCountDetailActivity || context instanceof OleAddEmployeeActivity) {
                  holder.cardView.setStrokeColor(context.getResources().getColor(R.color.separatorColor));
              }
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
        TextView tvDate;

        ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_vu);
            tvDate = itemView.findViewById(R.id.tv_date);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}