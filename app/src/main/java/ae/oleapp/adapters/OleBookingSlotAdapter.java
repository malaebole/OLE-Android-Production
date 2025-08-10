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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.OleBookingSlot;
import ae.oleapp.owner.OleBookingActivity;
import ae.oleapp.owner.OleFastBookingActivity;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import pl.droidsonroids.gif.GifImageView;

public class OleBookingSlotAdapter extends RecyclerView.Adapter<OleBookingSlotAdapter.ViewHolder> {

    private final Context context;
    private final List<OleBookingSlot> list;
    private int selectedSlotIndex = -1;
    private String selectedDate = "";
    private OnItemClickListener onItemClickListener;
    private boolean isPadel = false;
    private int fieldPos = -1;
    private boolean setManualWidth = false;

    public OleBookingSlotAdapter(Context context, List<OleBookingSlot> list, boolean isPadel) {
        this.context = context;
        this.list = list;
        this.isPadel = isPadel;
    }

    public void setFieldPos(int fieldPos) {
        this.fieldPos = fieldPos;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public void setSetManualWidth(boolean setManualWidth) {
        this.setManualWidth = setManualWidth;
    }

    public void setSelectedSlotIndex(int selectedIndex, String selectedDate) {
        this.selectedSlotIndex = selectedIndex;
        if (!selectedDate.equalsIgnoreCase("")) {
            this.selectedDate = selectedDate;
        }
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olebooking_slot, parent, false);
        if (setManualWidth) {
            setItemWidth(v, parent);
        }
        return new ViewHolder(v);
    }

    private void setItemWidth(View view, ViewGroup parent) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.width = (parent.getMeasuredWidth() / 3);
        view.setLayoutParams(params);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleBookingSlot slot = list.get(position);
        populateData(slot, holder);

        if (!slot.getStatus().equalsIgnoreCase("booked") && !slot.getStatus().equalsIgnoreCase("hidden")) {
            if (selectedSlotIndex == position) {
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
        }

        if (context instanceof OleBookingActivity || context instanceof OleFastBookingActivity) {
            try {
                double discount = 0;
                if (context instanceof OleBookingActivity) {
                    discount = ((OleBookingActivity) context).checkOfferForSlot(slot.getStart(), slot.getEnd());
                }
                else {
                    discount = ((OleFastBookingActivity) context).checkOfferForSlot(fieldPos, slot.getStart(), slot.getEnd());
                }
                if (discount > 0) {
                    holder.imgVuOffer.setVisibility(View.VISIBLE);
                }
                else {
                    holder.imgVuOffer.setVisibility(View.INVISIBLE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                holder.imgVuOffer.setVisibility(View.INVISIBLE);
            }
        }
        else {
            holder.imgVuOffer.setVisibility(View.INVISIBLE);
        }

        holder.relBorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(holder, holder.getAdapterPosition());
            }
        });

        holder.relBorder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.OnItemLongClick(holder, holder.getAdapterPosition());
                return true;
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
        if (Functions.getPrefValue(context, Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
            holder.tvPlayerName.setText(slot.getUserName());
            holder.nameVu.setVisibility(View.VISIBLE);
            if (slot.getSchedule() != null && slot.getSchedule().equalsIgnoreCase("1")) {
                holder.imgVuSchedule.setVisibility(View.VISIBLE);
            }
            else {
                holder.imgVuSchedule.setVisibility(View.GONE);
            }
        }
        else {
            holder.nameVu.setVisibility(View.INVISIBLE);
        }
        if (slot.getStatus().equalsIgnoreCase("booked") || slot.getStatus().equalsIgnoreCase("hidden")) {
            if (isPadel) {
                holder.imgVu.setImageResource(R.drawable.padel_booked_slots);
            }
            else {
                holder.imgVu.setImageResource(R.drawable.booked_slots);
            }
            holder.relBorder.setBackgroundResource(R.drawable.slot_bg_border_red);
            holder.tvDuration.setTextColor(context.getResources().getColor(R.color.redColor));
            holder.tvSlot.setTextColor(context.getResources().getColor(R.color.redColor));
            holder.tvDate.setTextColor(context.getResources().getColor(R.color.redColor));
            holder.imgVuBg.setVisibility(View.INVISIBLE);
            if (slot.getStatus().equalsIgnoreCase("booked") && !slot.getWaitingList().isEmpty()) {
                holder.gif.setVisibility(View.VISIBLE);
            }else{
                holder.gif.setVisibility(View.GONE);
            }
        }
        else {
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
            if (slot.getStatus().equalsIgnoreCase("available") && !slot.getWaitingList().isEmpty()) {
                holder.gif.setVisibility(View.VISIBLE);
            }else{
                holder.gif.setVisibility(View.GONE);
            }


            String green = "AM";
            String red = "PM";
            String htmlText = slot.getSlot().replace(green,"<font color='#49D483'>"+green +"</font>");
            htmlText = htmlText.replace(red,"<font color='#F02301'>"+red +"</font>");
            holder.tvSlot.setText(Html.fromHtml(htmlText));
        }

        String[] arr = slot.getSlot().split("-");
        if (arr.length == 2) {
            String dur = "";
            try {
                dur = Functions.getTimeDifference(arr[0], arr[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvDuration.setText(String.format("%s %s", dur, context.getString(R.string.min)));
            if (dur.equalsIgnoreCase("60")) {
                holder.slotDuration = "1";
            }
            else if (dur.equalsIgnoreCase("90")) {
                holder.slotDuration = "1.5";
            }
            else if (dur.equalsIgnoreCase("120")) {
                holder.slotDuration = "2";
            }

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

        ImageView imgVuBg, imgVu, imgVuNight, imgVuOffer, imgVuSchedule;
        public RelativeLayout relBorder, nameVu;
        TextView tvDuration, tvSlot, tvPlayerName, tvDate;
        public String slotDuration = "";

        GifImageView gif;

        ViewHolder(View itemView) {
            super(itemView);

            imgVuBg = itemView.findViewById(R.id.img_vu_bg);
            imgVu = itemView.findViewById(R.id.img_vu);
            imgVuNight = itemView.findViewById(R.id.img_vu_night);
            imgVuOffer = itemView.findViewById(R.id.img_vu_offer);
            imgVuSchedule = itemView.findViewById(R.id.img_schedule);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvPlayerName = itemView.findViewById(R.id.tv_player_name);
            tvSlot = itemView.findViewById(R.id.tv_slot);
            tvDate = itemView.findViewById(R.id.tv_date);
            relBorder = itemView.findViewById(R.id.rel_border);
            nameVu = itemView.findViewById(R.id.rel_name);
            gif = itemView.findViewById(R.id.attention_gif);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(ViewHolder v, int pos);
        void OnItemLongClick(ViewHolder v, int pos);
    }
}