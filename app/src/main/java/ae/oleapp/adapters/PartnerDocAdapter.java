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
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.IncomeHistory;
import ae.oleapp.models.PartnerDocModel;

public class PartnerDocAdapter extends RecyclerView.Adapter<PartnerDocAdapter.ViewHolder>{

        private final Context context;
        private final List<PartnerDocModel> list;
        private ItemClickListener itemClickListener;
        private String selectedId = "";


        public PartnerDocAdapter(Context context, List<PartnerDocModel> list) {
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
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.universal_layout_vu, parent, false);
                return new ViewHolder(v);

                }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

                holder.tvName.setText(list.get(position).getName());
                holder.tvCount.setText(list.get(position).getFilesCount());

                if (!list.get(position).getPhotoUrl().isEmpty()){
                    Glide.with(context).load(list.get(position).getPhotoUrl()).into(holder.imageView);
                }

        holder.relIncome.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

                    itemClickListener.itemClicked(v,holder.getAdapterPosition());
                 }
            });

        }


        @Override
        public int getItemCount() {
        return list.size();
        }

class ViewHolder extends RecyclerView.ViewHolder{

    TextView tvName,tvCount;
    CardView relIncome;
    ImageView imageView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        tvName = itemView.findViewById(R.id.tv_name);
        tvCount = itemView.findViewById(R.id.tv_count);
        imageView = itemView.findViewById(R.id.img_vu);
        relIncome = itemView.findViewById(R.id.rel_main_data);


    }
}

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
}
}