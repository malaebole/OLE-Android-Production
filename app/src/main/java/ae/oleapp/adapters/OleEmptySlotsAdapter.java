package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.OleBookingSlot;
import ae.oleapp.util.Functions;

public class OleEmptySlotsAdapter extends RecyclerView.Adapter<OleEmptySlotsAdapter.ViewHolder> {

    private final Context context;
    private final List<OleBookingSlot> list;
    private final List<OleBookingSlot> selectedSlots = new ArrayList<>();
    private String selectedDate = "";
    private OnItemClickListener onItemClickListener;
    private boolean isPadel = false;

    public OleEmptySlotsAdapter(Context context, List<OleBookingSlot> list, boolean isPadel) {
        this.context = context;
        this.list = list;
        this.isPadel = isPadel;
    }

    public List<OleBookingSlot> getSelectedSlots() {
        return selectedSlots;
    }

    public void setPadel(boolean padel) {
        isPadel = padel;
    }

    public void clearSelectionAndReload(String date) {
        selectedDate = date;
        selectedSlots.clear();
        notifyDataSetChanged();
    }

    public void selectSlot(OleBookingSlot slot, String selectedDate) {
        if (!selectedDate.equalsIgnoreCase("")) {
            this.selectedDate = selectedDate;
        }
        int index = isExist(slot.getSlotId());
        if (index == -1) {
            selectedSlots.add(slot);
        }
        else {
            selectedSlots.remove(index);
        }
        notifyDataSetChanged();
    }

    private int isExist(String slotId) {
        int res = -1;
        for (int i = 0; i < selectedSlots.size(); i++) {
            if (selectedSlots.get(i).getSlotId().equalsIgnoreCase(slotId)) {
                res = i;
                break;
            }
        }
        return res;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleempty_slot, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleBookingSlot slot = list.get(position);
        populateData(slot, holder);

        if (isExist(slot.getSlotId()) != -1) {
            holder.imgVuBg.setVisibility(View.VISIBLE);
            holder.relBorder.setBackgroundResource(R.drawable.slot_bg_border);
            holder.tvDuration.setTextColor(Color.WHITE);
            holder.tvSlot.setText(slot.getSlot());
            holder.tvSlot.setTextColor(Color.WHITE);
            holder.tvDate.setTextColor(Color.WHITE);
            if (isPadel) {
                if (slot.getLadySlot() != null && slot.getLadySlot().equalsIgnoreCase("1")) {
                    holder.imgVu.setImageResource(R.drawable.padel_slot_pink);
                }
                else {
                    holder.imgVu.setImageResource(R.drawable.padel_slot_white);
                }
            }
            else {
                holder.imgVu.setImageResource(R.drawable.slot_white);
            }
        }
        else {
            holder.imgVuBg.setVisibility(View.INVISIBLE);
            holder.relBorder.setBackgroundResource(R.drawable.slot_bg_border);
            holder.tvDuration.setTextColor(context.getResources().getColor(R.color.darkTextColor));
            if (isPadel) {
                if (slot.getLadySlot() != null && slot.getLadySlot().equalsIgnoreCase("1")) {
                    holder.imgVu.setImageResource(R.drawable.padel_slot_pink);
                }
                else {
                    holder.imgVu.setImageResource(R.drawable.padel_slot_gray);
                }
            }
            else {
                holder.imgVu.setImageResource(R.drawable.slot_gray);
            }
        }

        holder.relBorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(holder, holder.getAdapterPosition());
            }
        });
    }

    private void populateData(OleBookingSlot slot, ViewHolder holder) {
        holder.tvSlot.setText(slot.getSlot());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            holder.tvDate.setVisibility(View.VISIBLE);
            Date startDate = df.parse(slot.getStart().split(" ")[0]);
            Date date = df.parse(selectedDate);
            if (startDate != null && date != null) {
                if (startDate.compareTo(date) == 0) {
                    holder.tvDate.setVisibility(View.GONE);
                } else {
                    df.applyPattern("dd/MM/yyyy");
                    holder.tvDate.setText(df.format(startDate));
                }
            }
            else {
                holder.tvDate.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.tvDate.setVisibility(View.GONE);
        }

        if (isPadel) {
            if (slot.getLadySlot() != null && slot.getLadySlot().equalsIgnoreCase("1")) {
                holder.imgVu.setImageResource(R.drawable.padel_slot_pink);
            }
            else {
                holder.imgVu.setImageResource(R.drawable.padel_slot_gray);
            }
        }
        else {
            holder.imgVu.setImageResource(R.drawable.slot_gray);
        }
        holder.relBorder.setBackgroundResource(R.drawable.slot_bg_border);
        holder.tvDuration.setTextColor(context.getResources().getColor(R.color.darkTextColor));
        holder.tvSlot.setTextColor(context.getResources().getColor(R.color.darkTextColor));
        holder.tvDate.setTextColor(context.getResources().getColor(R.color.darkTextColor));
        holder.imgVuBg.setVisibility(View.INVISIBLE);

        String green = "AM";
        String red = "PM";
        String htmlText = slot.getSlot().replace(green,"<font color='#49D483'>"+green +"</font>");
        htmlText = htmlText.replace(red,"<font color='#F02301'>"+red +"</font>");
        holder.tvSlot.setText(Html.fromHtml(htmlText));

        String[] arr = slot.getSlot().split("-");
        if (arr.length == 2) {
            String dur = "";
            try {
                dur = Functions.getTimeDifference(arr[0], arr[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvDuration.setText(String.format("%s %s", dur, context.getString(R.string.min)));

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mma", Locale.ENGLISH);
            try {
                Date date = dateFormat.parse(arr[0]);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                if (hour > 6 && hour < 17) {
                    holder.imgVuNight.setImageResource(R.drawable.sun_ic);
                }
                else {
                    holder.imgVuNight.setImageResource(R.drawable.moon_ic);
                }

            } catch (ParseException e) {
                e.printStackTrace();
                holder.imgVuNight.setImageResource(0);
            }

        }
        else {
            holder.tvDuration.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgVuBg, imgVu, imgVuNight;
        public RelativeLayout relBorder;
        TextView tvDuration, tvSlot, tvDate;

        ViewHolder(View itemView) {
            super(itemView);

            imgVuBg = itemView.findViewById(R.id.img_vu_bg);
            imgVu = itemView.findViewById(R.id.img_vu);
            imgVuNight = itemView.findViewById(R.id.img_vu_night);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvSlot = itemView.findViewById(R.id.tv_slot);
            tvDate = itemView.findViewById(R.id.tv_date);
            relBorder = itemView.findViewById(R.id.rel_border);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(ViewHolder v, int pos);
    }
}