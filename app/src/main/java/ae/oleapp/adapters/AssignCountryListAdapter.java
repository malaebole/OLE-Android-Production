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
import ae.oleapp.models.Country;
import ae.oleapp.models.Employee;

public class AssignCountryListAdapter extends RecyclerView.Adapter<AssignCountryListAdapter.ViewHolder> {

    private final Context context;
    private final List<Country> list;
    private ItemClickListener itemClickListener;
    private String assignCountries;

    public AssignCountryListAdapter(Context context, List<Country> list, String assignCountries) {
        this.context = context;
        this.list = list;
        this.assignCountries = assignCountries;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setAssignCountries(String assignCountries) {
        this.assignCountries = assignCountries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ole_assign_country_list_vu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Country country = list.get(position);
        holder.tvName.setText(country.getName());
        if (assignCountries.contains(country.getId())){
            Glide.with(context).load(R.drawable.friend_checkl).into(holder.selectVu);
        }else{
            Glide.with(context).load(R.drawable.friend_uncheckl).into(holder.selectVu);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
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

        TextView tvName;
        ImageView selectVu;
        CardView layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tvName = itemView.findViewById(R.id.tv_name);
            selectVu = itemView.findViewById(R.id.select_vu);
            layout = itemView.findViewById(R.id.rel_main);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}