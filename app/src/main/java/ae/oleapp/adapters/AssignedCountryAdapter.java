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
import java.util.List;
import ae.oleapp.R;
import ae.oleapp.models.AssignedCountries;

public class AssignedCountryAdapter extends RecyclerView.Adapter<AssignedCountryAdapter.ViewHolder> {

    private final Context context;
    private final List<AssignedCountries> list;
    private ItemClickListener itemClickListener;
    private final boolean setManualWidth = false;
    private final int playersLimit = 0;
    private final boolean isEmployee = false;
    private final String selectedId = "";

    public AssignedCountryAdapter(Context context, List<AssignedCountries> list) {
        this.context = context;
        this.list = list;
    }



    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.assigned_country_vu, parent, false);
        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AssignedCountries info = list.get(position);
        holder.tvName.setText(info.getName());
        holder.tvCount.setText(info.getTeamsCount());
        if (!info.getFlag().isEmpty()){
            Glide.with(context).load(info.getFlag()).into(holder.flagImageVu);
        }



        holder.cardVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvCount;
        CardView cardVu;
        ImageView flagImageVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            cardVu = itemView.findViewById(R.id.rel_main_data);
            tvCount = itemView.findViewById(R.id.tv_count);
            flagImageVu = itemView.findViewById(R.id.flag_img_vu);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}