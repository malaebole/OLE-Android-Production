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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.IncomeHistory;

public class BanksAdapter extends RecyclerView.Adapter<BanksAdapter.ViewHolder>{

    private final Context context;
    private final List<ClubBankLists> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";


    public BanksAdapter(Context context, List<ClubBankLists> list) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.banks, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvBankName.setText(list.get(position).getName());
            holder.tvBranch.setText(list.get(position).getBranchName());

            if (!list.get(position).getPhoto().isEmpty()){
                 Glide.with(context).load(list.get(position).getPhoto()).into(holder.bankImg);

            }

            holder.relBank.setOnClickListener(new View.OnClickListener() {
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

        TextView tvBankName,tvBranch;
        ImageView bankImg;
        CardView relBank;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBankName = itemView.findViewById(R.id.tv_bank_name);
            tvBranch = itemView.findViewById(R.id.tv_branch);
            relBank = itemView.findViewById(R.id.rel_bank);
            bankImg = itemView.findViewById(R.id.img_vu);


        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}