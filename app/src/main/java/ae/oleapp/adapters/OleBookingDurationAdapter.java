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
import ae.oleapp.models.OleKeyValuePair;

public class OleBookingDurationAdapter extends RecyclerView.Adapter<OleBookingDurationAdapter.ViewHolder> {

    private final Context context;
    private final List<OleKeyValuePair> list;
    private String selectedDuration = "";
    private OnItemClickListener onItemClickListener;

    public OleBookingDurationAdapter(Context context, List<OleKeyValuePair> list, String duration) {
        this.context = context;
        this.list = list;
        this.selectedDuration = duration;
    }

    public void setSelectedDuration(String selectedDuration) {
        this.selectedDuration = selectedDuration;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olebooking_field, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(list.get(position).getValue());
        if (list.get(position).getKey().equalsIgnoreCase(selectedDuration)) {
            holder.mainLayout.setCardBackgroundColor(context.getResources().getColor(R.color.blueColorNew));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.whiteColor));
        }
        else {
            holder.mainLayout.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.darkTextColor));
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CardView mainLayout;
        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);

            mainLayout = itemView.findViewById(R.id.rel_main);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}