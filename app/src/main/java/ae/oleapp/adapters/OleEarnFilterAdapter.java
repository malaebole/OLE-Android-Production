package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ae.oleapp.R;

public class OleEarnFilterAdapter extends RecyclerView.Adapter<OleEarnFilterAdapter.ViewHolder> {

    private final Context context;
    private final String[] list;
    private OnItemClickListener itemClickListener;
    private int selectedIndex = -1;

    public OleEarnFilterAdapter(Context context, String[] list, int selectedIndex) {
        this.context = context;
        this.list = list;
        this.selectedIndex = selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleearn_filter_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String str = list[position];
        holder.tvTitle.setText(str);
        if (selectedIndex == position) {
            holder.imgCheck.setImageResource(R.drawable.check);
        }
        else {
            holder.imgCheck.setImageResource(R.drawable.uncheck);
        }

        if (position == list.length - 1) {
            holder.vuSep.setVisibility(View.GONE);
        }
        else {
            holder.vuSep.setVisibility(View.VISIBLE);
        }

        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.OnItemClick(v, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        ImageView imgCheck;
        RelativeLayout main;
        View vuSep;

        ViewHolder(View itemView) {
            super(itemView);

            imgCheck = itemView.findViewById(R.id.img_check);
            main = itemView.findViewById(R.id.main);
            tvTitle = itemView.findViewById(R.id.tv_title);
            vuSep = itemView.findViewById(R.id.sep);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}