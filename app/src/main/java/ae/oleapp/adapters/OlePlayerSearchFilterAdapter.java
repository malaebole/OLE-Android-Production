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

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleKeyValuePair;

public class OlePlayerSearchFilterAdapter extends RecyclerView.Adapter<OlePlayerSearchFilterAdapter.ViewHolder> {

    private final Context context;
    private final List<OleKeyValuePair> list;
    private OnItemClickListener itemClickListener;
    private String selectedValue = "";

    public OlePlayerSearchFilterAdapter(Context context, List<OleKeyValuePair> list, String selectedValue) {
        this.context = context;
        this.list = list;
        this.selectedValue = selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleearn_filter_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleKeyValuePair pair = list.get(position);
        holder.tvTitle.setText(pair.getValue());
        if (selectedValue.equalsIgnoreCase(pair.getKey())) {
            holder.imgCheck.setImageResource(R.drawable.check);
        }
        else {
            holder.imgCheck.setImageResource(R.drawable.uncheck);
        }

        if (position == list.size() - 1) {
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
        return list.size();
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