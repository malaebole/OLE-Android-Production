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

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.PartnerData;

public class PartnerAdapter extends RecyclerView.Adapter<PartnerAdapter.ViewHolder>{

    private final Context context;
    private final List<PartnerData> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";


    public PartnerAdapter(Context context, List<PartnerData> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener (ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.partners, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text =  list.get(position).getShares();
//        String formattedText = text + "%";
//
//        SpannableString spannableString = new SpannableString(formattedText);
//        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(0.5f);
//        spannableString.setSpan(relativeSizeSpan, text.length(), formattedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        AlignmentSpan.Standard alignmentSpan = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL);
//        spannableString.setSpan(alignmentSpan, text.length(), formattedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.tvPercentagePartner.setText(text+"%");
        holder.tvName.setText(list.get(position).getName());
        if (list.get(position).getPhotoUrl().isEmpty()){
            holder.partnerImg.setImageResource(R.drawable.partner_temp_img);
        }else{
            Glide.with(context).load(list.get(position).getPhotoUrl()).into(holder.partnerImg);
        }


        holder.relMain.setOnClickListener(new View.OnClickListener() {
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

        TextView tvName,tvPercentagePartner;
        ImageView partnerImg;
        CardView relMain;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            relMain = itemView.findViewById(R.id.rel_main);
            partnerImg = itemView.findViewById(R.id.img_vu);
            tvPercentagePartner = itemView.findViewById(R.id.tv_share_percentage);


        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}