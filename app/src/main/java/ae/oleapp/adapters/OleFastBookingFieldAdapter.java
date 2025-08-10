package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Field;

public class OleFastBookingFieldAdapter extends RecyclerView.Adapter<OleFastBookingFieldAdapter.ViewHolder> {

    private final Context context;
    private final List<Field> list;
    private boolean isPadel = false;
    private OnItemClickListener onItemClickListener;
    private String selectedDate = "";

    public OleFastBookingFieldAdapter(Context context, List<Field> list, String selectedDate) {
        this.context = context;
        this.list = list;
        this.selectedDate = selectedDate;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setPadel(boolean padel) {
        isPadel = padel;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olefast_booking_field, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Field field = list.get(position);
        if (field.getFieldSize() != null && !field.getFieldSize().isEmpty()) {
            holder.tvName.setText(String.format("%s (%s)", field.getName(), field.getFieldSize().getName()));
        }
        else {
            holder.tvName.setText(field.getName());
        }

        LinearLayoutManager slotLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.recyclerView.setLayoutManager(slotLayoutManager);
        holder.slotAdapter = new OleBookingSlotAdapter(context, field.getSlotList(), isPadel);
        holder.slotAdapter.setFieldPos(position);
        holder.slotAdapter.setSelectedDate(selectedDate);
        holder.slotAdapter.setOnItemClickListener(new OleBookingSlotAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(OleBookingSlotAdapter.ViewHolder v, int pos) {
                onItemClickListener.OnItemClick(holder, v, pos, holder.getAdapterPosition());
            }

            @Override
            public void OnItemLongClick(OleBookingSlotAdapter.ViewHolder v, int pos) {
                onItemClickListener.OnItemLongClick(holder, v, pos, holder.getAdapterPosition());
            }
        });
        holder.recyclerView.setAdapter(holder.slotAdapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RecyclerView recyclerView;
        TextView tvName;
        public OleBookingSlotAdapter slotAdapter;

        ViewHolder(View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.slots_recycler_vu);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(ViewHolder fieldVu, OleBookingSlotAdapter.ViewHolder v, int slotPos, int fieldPos);
        void OnItemLongClick(ViewHolder fieldVu, OleBookingSlotAdapter.ViewHolder v, int slotPos, int fieldPos);
    }
}