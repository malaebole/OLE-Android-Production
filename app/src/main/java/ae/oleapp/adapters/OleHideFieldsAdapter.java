package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleHideField;

public class OleHideFieldsAdapter extends RecyclerView.Adapter<OleHideFieldsAdapter.ViewHolder> {

    private final Context context;
    private final List<OleHideField> fieldList;
    private ItemClickListener itemClickListener;

    public OleHideFieldsAdapter(Context context, List<OleHideField> fieldList) {
        this.context = context;
        this.fieldList = fieldList;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olehide_field, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleHideField field = fieldList.get(position);
        holder.tvDesc.setText(field.getReason());
        holder.tvDate.setText(field.getDate());
        holder.tvTime.setText(field.getTimeSlots());
        if (field.getHiddenType().equalsIgnoreCase("tournament")) {
            holder.tvType.setText(R.string.tournament);
        }
        else if (field.getHiddenType().equalsIgnoreCase("maintenance")) {
            holder.tvType.setText(R.string.maintenance);
        }
        else if (field.getHiddenType().equalsIgnoreCase("holiday")) {
            holder.tvType.setText(R.string.holidays);
        }
        else {
            holder.tvType.setText(R.string.other);
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
        return fieldList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvType, tvDesc, tvDate, tvTime;
        CardView layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvType = itemView.findViewById(R.id.tv_type);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            layout = itemView.findViewById(R.id.rel_main);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}