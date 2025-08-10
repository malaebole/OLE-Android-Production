package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.OleBookingListDate;
import ae.oleapp.util.Functions;

public class OleBookingDateAdapter extends RecyclerView.Adapter<OleBookingDateAdapter.ViewHolder> {

    private final Context context;
    private Object[] list;
    private int selectedDateIndex = -1;
    private OnItemClickListener onItemClickListener;

    public OleBookingDateAdapter(Context context, Object[] list, int selectedIndex) {
        this.context = context;
        this.list = list;
        this.selectedDateIndex = selectedIndex;
    }

    public void setSelectedDateIndex(int selectedIndex) {
        this.selectedDateIndex = selectedIndex;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setDataSource(Object[] list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olebooking_date, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (list[position] instanceof Date) {
            Date date = (Date) list[position];
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE", new Locale(Functions.getAppLangStr(context)));
            holder.tvDayName.setText(dateFormat.format(date));
            dateFormat.applyPattern("MMM");
            holder.tvMonth.setText(dateFormat.format(date));
            dateFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
            holder.tvDate.setText(dateFormat.format(date));

            if (position == selectedDateIndex) {
                holder.imgVuBg.setVisibility(View.VISIBLE);
                holder.relBorder.setBackgroundResource(0);
                holder.tvDate.setTextColor(context.getResources().getColor(R.color.whiteColor));
                holder.tvDayName.setTextColor(context.getResources().getColor(R.color.blueColorNew));
                holder.tvMonth.setTextColor(context.getResources().getColor(R.color.blueColorNew));
            }
            else {
                holder.imgVuBg.setVisibility(View.INVISIBLE);
                holder.relBorder.setBackgroundResource(R.drawable.date_bg_border);
                holder.tvDate.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                holder.tvDayName.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                holder.tvMonth.setTextColor(context.getResources().getColor(R.color.darkTextColor));
            }
        }
        else {
            OleBookingListDate oleBookingListDate = (OleBookingListDate) list[position];
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                Date date = dateFormat.parse(oleBookingListDate.getDate());
                dateFormat = new SimpleDateFormat("EEE", new Locale(Functions.getAppLangStr(context)));
                holder.tvDayName.setText(dateFormat.format(date));
                dateFormat.applyPattern("MMM");
                holder.tvMonth.setText(dateFormat.format(date));
                dateFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
                holder.tvDate.setText(dateFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (position == selectedDateIndex) {
                holder.imgVuBg.setVisibility(View.VISIBLE);
                holder.relBorder.setBackgroundResource(0);
                holder.tvDate.setTextColor(context.getResources().getColor(R.color.whiteColor));
                holder.tvDayName.setTextColor(context.getResources().getColor(R.color.blueColorNew));
                holder.tvMonth.setTextColor(context.getResources().getColor(R.color.blueColorNew));
            }
            else {
                if (oleBookingListDate.getBookingAvailable() != null && oleBookingListDate.getBookingAvailable().equalsIgnoreCase("1")) {
                    holder.imgVuBg.setVisibility(View.INVISIBLE);
                    holder.tvDate.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                    holder.relBorder.setBackgroundResource(R.drawable.green_date_bg_border);
                    holder.tvDayName.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                    holder.tvMonth.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                    // lady slots color === 1 for only lady; 2 for both; 0 for only boy
                    if (oleBookingListDate.getLadySlot() != null) {
                        if (oleBookingListDate.getLadySlot().equalsIgnoreCase("1") || oleBookingListDate.getLadySlot().equalsIgnoreCase("2")) { // both
                            holder.relBorder.setBackgroundResource(R.drawable.pinkgreen_date_bg_border);
                        }
                        else {
                            holder.relBorder.setBackgroundResource(R.drawable.green_date_bg_border);
                        }
                    }
                }
                else if (oleBookingListDate.getHiddenSlots() != null && oleBookingListDate.getHiddenSlots().equalsIgnoreCase("1")) {
                    holder.imgVuBg.setVisibility(View.INVISIBLE);
                    holder.relBorder.setBackgroundResource(R.drawable.red_date_bg_border);
                    holder.tvDate.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                    holder.tvDayName.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                    holder.tvMonth.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                }
                else {
                    holder.imgVuBg.setVisibility(View.INVISIBLE);
                    holder.relBorder.setBackgroundResource(R.drawable.date_bg_border);
                    holder.tvDate.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                    holder.tvDayName.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                    holder.tvMonth.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                    // lady slots color === 1 for only lady; 2 for both; 0 for only boy
                    if (oleBookingListDate.getLadySlot() != null) {
                        if (oleBookingListDate.getLadySlot().equalsIgnoreCase("2")) { // both
                            holder.relBorder.setBackgroundResource(R.drawable.pinkwhite_date_bg_border);
                        }
                        else if (oleBookingListDate.getLadySlot().equalsIgnoreCase("1")) { // only lady
                            holder.relBorder.setBackgroundResource(R.drawable.pink_date_bg_border);
                        }
                        else { //only boy
                            holder.relBorder.setBackgroundResource(R.drawable.date_bg_border);
                        }
                    }
                }
            }

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
        return list.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgVuBg;
        RelativeLayout relBorder;
        LinearLayout mainLayout;
        TextView tvDayName, tvMonth, tvDate;

        ViewHolder(View itemView) {
            super(itemView);

            imgVuBg = itemView.findViewById(R.id.img_vu);
            mainLayout = itemView.findViewById(R.id.main);
            tvDayName = itemView.findViewById(R.id.tv_day_name);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvDate = itemView.findViewById(R.id.tv_date);
            relBorder = itemView.findViewById(R.id.rel_border);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}