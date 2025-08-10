package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.BankEarning;
import ae.oleapp.models.Club;
import ae.oleapp.models.FinanceHome;
import ae.oleapp.models.Team;

public class OleBanksEarningAdapter extends RecyclerView.Adapter<OleBanksEarningAdapter.ViewHolder>{


    private final Context context;
    private final List<BankEarning> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";


    public OleBanksEarningAdapter(Context context, List<BankEarning> list) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_earning_data_vu, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.tvIncome.setText(list.get(position).getAmount());
            holder.tvIncomeCur.setText(list.get(position).getCurrency());
            if (list.get(position).getPhotoUrl().isEmpty()){
                holder.bankImg.setImageResource(R.drawable.finance_temp);
            }else{
                Glide.with(context).load(list.get(position).getPhotoUrl()).into(holder.bankImg);
            }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
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

        TextView tvIncome,tvIncomeCur;
        ImageView bankImg;
        CardView mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvIncome = itemView.findViewById(R.id.income_tv);
            bankImg = itemView.findViewById(R.id.bank_img_vu);
            mainLayout = itemView.findViewById(R.id.rel_main_data);
            tvIncomeCur = itemView.findViewById(R.id.tv_income_cur);


        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}