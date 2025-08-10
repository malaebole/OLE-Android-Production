package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

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

public class OleHideSlotAdapter extends RecyclerView.Adapter<OleHideSlotAdapter.ViewHolder> {

    private final Context context;
    private final List<OleBookingSlot> slotList;
    private ItemClickListener itemClickListener;
    public List<OleBookingSlot> selectedSlots = new ArrayList<>();

    public OleHideSlotAdapter(Context context, List<OleBookingSlot> slotList) {
        this.context = context;
        this.slotList = slotList;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public int checkSlotExist(String slotId) {
        int index = -1;
        for (int i = 0; i < selectedSlots.size(); i++) {
            if (selectedSlots.get(i).getSlotId().equalsIgnoreCase(slotId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void selectItem(OleBookingSlot slot) {
        int index = checkSlotExist(slot.getSlotId());
        if (index == -1) {
            selectedSlots.add(slot);
        }
        else {
            selectedSlots.remove(index);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olehide_slot, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleBookingSlot slot = slotList.get(position);
        String green = "AM";
        String red = "PM";
        String htmlText = slot.getSlot().replace(green,"<font color='#00C301'>"+green +"</font>");
        htmlText = htmlText.replace(red,"<font color='#ff0000'>"+red +"</font>");
        holder.tvTime.setText(Html.fromHtml(htmlText));

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
                    holder.imgVu.setImageResource(R.drawable.sun_ic);
                    holder.cardView.setStrokeColor(Color.parseColor("#FDE22E"));
                }
                else {
                    holder.imgVu.setImageResource(R.drawable.moon_ic);
                    holder.cardView.setStrokeColor(Color.parseColor("#FF6E39"));
                }

            } catch (ParseException e) {
                e.printStackTrace();
                holder.imgVu.setImageResource(0);
                holder.cardView.setStrokeColor(Color.WHITE);
            }

        }
        else {
            holder.tvDuration.setText("");
        }

        int index = checkSlotExist(slot.getSlotId());
        if (index != -1) {
            holder.layout.setAlpha(1.0f);
            holder.imgVuCheck.setImageResource(R.drawable.check);
        }
        else {
            holder.layout.setAlpha(1.0f);
            holder.imgVuCheck.setImageResource(R.drawable.uncheck);
        }

        if (slot.getStatus() != null && slot.getStatus().equalsIgnoreCase("hidden")) {
            holder.layout.setAlpha(0.5f);
            holder.imgVuCheck.setImageResource(R.drawable.check);
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
        return slotList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTime, tvDuration;
        ImageView imgVu, imgVuCheck;
        CardView layout;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTime = itemView.findViewById(R.id.tv_time);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            imgVu = itemView.findViewById(R.id.img_vu);
            imgVuCheck = itemView.findViewById(R.id.img_vu_check);
            layout = itemView.findViewById(R.id.main);
            cardView = itemView.findViewById(R.id.card_vu);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}