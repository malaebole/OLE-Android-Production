package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleColorModel;

public class OleColorListAdapter extends RecyclerView.Adapter<OleColorListAdapter.ViewHolder> {

    private final Context context;
    private final List<OleColorModel> list;
    private OnItemClickListener onItemClickListener;

    public OleColorListAdapter(Context context, List<OleColorModel> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olecolor_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleColorModel oleColorModel = list.get(position);
        holder.bgVu.setCardBackgroundColor(Color.parseColor(oleColorModel.getColor()));
        holder.bgVu.setAlpha(0.25f);
        holder.colorVu.setCardBackgroundColor(Color.parseColor(oleColorModel.getColor()));

        holder.bgVu.setOnClickListener(new View.OnClickListener() {
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

        MaterialCardView bgVu, colorVu;

        ViewHolder(View itemView) {
            super(itemView);
            bgVu = itemView.findViewById(R.id.bg_vu);
            colorVu = itemView.findViewById(R.id.color_vu);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}