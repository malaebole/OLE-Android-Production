package ae.oleapp.adapters;

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.BankEarning;
import ae.oleapp.models.PartnerEarning;

public class OleShareHolderEarningAdapter extends RecyclerView.Adapter<OleShareHolderEarningAdapter.ViewHolder>{
    private final Context context;
    private final List<PartnerEarning> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";




    public OleShareHolderEarningAdapter(Context context, List<PartnerEarning> list) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shareholder_earning_data_vu, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        String text =  list.get(position).getShares();
        String formattedText = text + "%";

        SpannableString spannableString = new SpannableString(formattedText);
        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(0.5f);
        spannableString.setSpan(relativeSizeSpan, text.length(), formattedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        AlignmentSpan.Standard alignmentSpan = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL);
        spannableString.setSpan(alignmentSpan, text.length(), formattedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.tvSharePerc.setText(spannableString);
            holder.tvIncome.setText(list.get(position).getSharesAmount());
            holder.tvName.setText(list.get(position).getName());
            holder.tvIncomeCur.setText(list.get(position).getCurrency());

        if (list.get(position).getPhotoUrl().isEmpty()){
                holder.bankImg.setImageResource(R.drawable.partner_temp_img);
            }else{
                Glide.with(context).load(list.get(position).getPhotoUrl()).into(holder.bankImg);
            }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getAdapterPosition());
            }
        });
        //}
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvIncome,tvName,tvSharePerc,tvIncomeCur;
        ImageView bankImg;
        CardView mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSharePerc = itemView.findViewById(R.id.tv_share_percentage);
            tvIncome = itemView.findViewById(R.id.income_tv);
            tvName = itemView.findViewById(R.id.tv_name);
            bankImg = itemView.findViewById(R.id.bank_img_vu);
            mainLayout = itemView.findViewById(R.id.rel_main_data);
            tvIncomeCur = itemView.findViewById(R.id.tv_income_cur);



        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}
