package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Club;
import ae.oleapp.models.Team;


public class OleClubNameAdapter extends RecyclerView.Adapter<OleClubNameAdapter.ViewHolder> {

        private final Context context;
        private final List<Club> clubList;
        private ItemClickListener itemClickListener;
        private String selectedId = "";
        private int selectedIndex;

        public OleClubNameAdapter(Context context, List<Club> clubList) {
            this.context = context;
            this.clubList = clubList;
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public void setSelectedId(String selectedId) {
            this.selectedId = selectedId;
            notifyDataSetChanged();
        }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.clubs_name_vu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Club club = clubList.get(position);
        holder.tvName.setText(club.getName());
            //holder.tvName.setText(clubList.get(position).getName());
            //|| selectedIndex == position
      if (clubList.get(position).getId().equalsIgnoreCase(selectedId)) {
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.yellowColor) );
            holder.cardView.setCardBackgroundColor(Color.parseColor("#7A000000"));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.yellowColor));
        }
        else {
            holder.cardView.setStrokeColor(Color.parseColor("#1275C6"));
            holder.cardView.setCardBackgroundColor(Color.TRANSPARENT);
            holder.tvName.setTextColor(Color.parseColor("#FFFFFF"));
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getAdapterPosition());
            }
        });
    }


        @Override
        public int getItemCount() {
            return clubList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            TextView tvName;
            MaterialCardView cardView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_name);
                cardView = itemView.findViewById(R.id.card_vu);

            }
        }

        public interface ItemClickListener {
            void itemClicked(View view, int pos);
        }
    }