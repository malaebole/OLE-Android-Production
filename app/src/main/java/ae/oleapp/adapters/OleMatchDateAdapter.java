package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;

public class OleMatchDateAdapter extends RecyclerView.Adapter<OleMatchDateAdapter.ViewHolder> {

    private final Context context;
    private final List<Date> list;
    private int selectedDateIndex = -1;
    private int minIndex = -1;
    private int maxIndex = -1;
    private OnItemClickListener onItemClickListener;

    public OleMatchDateAdapter(Context context, List<Date> list, int selectedIndex) {
        this.context = context;
        this.list = list;
        this.selectedDateIndex = selectedIndex;
        this.minIndex = selectedIndex;
    }

    public void setSelectedDateIndex(int selectedIndex) {
        this.selectedDateIndex = selectedIndex;
        this.notifyDataSetChanged();
    }

    public void setSelectedDateIndex(int minIndex, int maxIndex) {
        this.minIndex = minIndex;
        this.maxIndex = maxIndex;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olematch_date, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Date date = list.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        if (date != null) {
            holder.tvYear.setText(dateFormat.format(date));
            dateFormat.applyPattern("MMM");
            holder.tvMonth.setText(dateFormat.format(date));
        }
        else  {
            holder.tvMonth.setText(R.string.all);
            holder.tvYear.setText(dateFormat.format(new Date()));
        }

        if (position == selectedDateIndex) {
            holder.imgVuBg.setVisibility(View.VISIBLE);
            holder.relBorder.setBackgroundResource(0);
            holder.tvMonth.setTextColor(context.getResources().getColor(R.color.whiteColor));
        } else {
            holder.imgVuBg.setVisibility(View.INVISIBLE);
            holder.relBorder.setBackgroundResource(R.drawable.date_bg_border);
            holder.tvMonth.setTextColor(Color.parseColor("#004890"));
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

        ImageView imgVuBg;
        RelativeLayout relBorder;
        LinearLayout mainLayout;
        TextView tvMonth, tvYear;

        ViewHolder(View itemView) {
            super(itemView);

            imgVuBg = itemView.findViewById(R.id.img_vu);
            mainLayout = itemView.findViewById(R.id.main);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvYear = itemView.findViewById(R.id.tv_year);
            relBorder = itemView.findViewById(R.id.rel_border);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}