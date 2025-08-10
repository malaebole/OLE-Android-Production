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
import ae.oleapp.models.BankEarning;
import ae.oleapp.models.ClubBankLists;

public class ClubsBanksAdapter extends RecyclerView.Adapter<ClubsBanksAdapter.ViewHolder> {

        private final Context context;
        private final List<ClubBankLists> list;
        private ItemClickListener itemClickListener;
        private String selectedId = "";

        public ClubsBanksAdapter(Context context, List<ClubBankLists> list) {
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
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.clubs_name_vu, parent, false);
                return new ViewHolder(v);
                }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                holder.tvName.setText(list.get(position).getName());
                if (list.get(position).getId().equalsIgnoreCase(selectedId)) {
                    holder.cardView.setStrokeColor(context.getResources().getColor(R.color.blueColorNew));
                    holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.transparent));
                    holder.tvName.setTextColor(context.getResources().getColor(R.color.blueColorNew));
                }
                else {
                    holder.cardView.setStrokeColor(context.getResources().getColor(R.color.black20Color));
                    holder.cardView.setCardBackgroundColor(Color.TRANSPARENT);
                    holder.tvName.setTextColor(context.getResources().getColor(R.color.black20Color));
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
                return list.size();
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