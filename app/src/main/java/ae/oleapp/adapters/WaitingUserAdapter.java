package ae.oleapp.adapters;

import static android.content.Context.VIBRATOR_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.BookingWaitingList;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.OleBookingSlot;

public class WaitingUserAdapter extends RecyclerView.Adapter<WaitingUserAdapter.ViewHolder>{

    private final Context context;
    private final List<BookingWaitingList> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";
    private final OleBookingSlot oleBookingSlot;
    private Vibrator vibrator;

    public WaitingUserAdapter(Context context, List<BookingWaitingList> list, OleBookingSlot oleBookingSlot) {
        this.context = context;
        this.list = list;
        this.oleBookingSlot = oleBookingSlot;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.waiting_user, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if (oleBookingSlot.getStatus().equalsIgnoreCase("booked") || oleBookingSlot.getStatus().equalsIgnoreCase("hidden")) {
          holder.book.setVisibility(View.GONE);
        }
        else{
            holder.book.setVisibility(View.VISIBLE);
        }
        if (!list.isEmpty()){
            holder.tvName.setText(list.get(position).getUserName());
            holder.tvPhone.setText(list.get(position).getUserPhone());
            holder.tvWaitingTime.setText(list.get(position).getWaitingTime());
        }
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrationEffect effect = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    effect = VibrationEffect.createOneShot(5, VibrationEffect.EFFECT_TICK);
                    vibrator.vibrate(effect);
                }
                itemClickListener.phoneClicked(v, holder.getAdapterPosition());
            }
        });
        holder.book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrationEffect effect = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    effect = VibrationEffect.createOneShot(5, VibrationEffect.EFFECT_TICK);
                    vibrator.vibrate(effect);
                }
                itemClickListener.bookingClicked(v, holder.getAdapterPosition());
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrationEffect effect = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    effect = VibrationEffect.createOneShot(5, VibrationEffect.EFFECT_TICK);
                    vibrator.vibrate(effect);
                }
                itemClickListener.deleteClicked(v, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName,tvPhone, tvWaitingTime;
        ImageView call, book, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvWaitingTime = itemView.findViewById(R.id.tv_waiting_time);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            call = itemView.findViewById(R.id.call);
            book = itemView.findViewById(R.id.book);
            delete = itemView.findViewById(R.id.delete);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void phoneClicked(View view, int pos);
        void bookingClicked(View view, int pos);
        void deleteClicked(View view, int pos);
    }
}