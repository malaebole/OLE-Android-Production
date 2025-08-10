package ae.oleapp.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.OleBookingSlot;
import ae.oleapp.util.Functions;

public class OleEmptySlotShareAdapter extends RecyclerView.Adapter<OleEmptySlotShareAdapter.ViewHolder> {

    private final Context context;
    private final List<OleBookingSlot> list;
    private boolean isPadel = false;

    public OleEmptySlotShareAdapter(Context context, List<OleBookingSlot> list, boolean isPadel) {
        this.context = context;
        this.list = list;
        this.isPadel = isPadel;
    }

    public void setPadel(boolean padel) {
        isPadel = padel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleempty_slot_share, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleBookingSlot slot = list.get(position);
        populateData(slot, holder);
    }

    private void populateData(OleBookingSlot slot, ViewHolder holder) {
        holder.tvTime.setText(slot.getSlot());
        if (isPadel) {
            if (slot.getLadySlot() != null && slot.getLadySlot().equalsIgnoreCase("1")) {
                holder.imgVu.setImageResource(R.drawable.padel_lady_slot_share);
            }
            else {
                holder.imgVu.setImageResource(R.drawable.padel_slot_share);
            }
        }
        else {
            holder.imgVu.setImageResource(R.drawable.football_slot_share);
        }

        String green = "AM";
        String red = "PM";
        String htmlText = slot.getSlot().replace(green,"<font color='#49D483'>"+green +"</font>");
        htmlText = htmlText.replace(red,"<font color='#F02301'>"+red +"</font>");
        holder.tvTime.setText(Html.fromHtml(htmlText));

        String[] arr = slot.getSlot().split("-");
        if (arr.length == 2) {
            String dur = "";
            try {
                dur = Functions.getTimeDifference(arr[0], arr[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvDuration.setText(dur);

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

        ImageView imgVu, imgVuNight;
        TextView tvDuration, tvTime;

        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            imgVuNight = itemView.findViewById(R.id.img_vu_night);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvTime = itemView.findViewById(R.id.tv_slot);

        }
    }
}