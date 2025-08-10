package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleColorModel;

public class OleFieldColorListAdapter extends RecyclerView.Adapter<OleFieldColorListAdapter.ViewHolder> {

    private final Context context;
    private final List<OleColorModel> list;
    private OnItemClickListener onItemClickListener;
    private int selectedIndex = -1;

    public OleFieldColorListAdapter(Context context, List<OleColorModel> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olefield_color_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleColorModel oleColorModel = list.get(position);
        holder.colorVu.setCardBackgroundColor(Color.parseColor(oleColorModel.getColor()));

        if (selectedIndex == position) {
            holder.imgVuTick.setVisibility(View.VISIBLE);
        }
        else {
            holder.imgVuTick.setVisibility(View.INVISIBLE);
        }

        holder.colorVu.setOnClickListener(new View.OnClickListener() {
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

        CardView colorVu;
        ImageView imgVuTick;

        ViewHolder(View itemView) {
            super(itemView);
            imgVuTick = itemView.findViewById(R.id.img_vu_tick);
            colorVu = itemView.findViewById(R.id.color_vu);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}