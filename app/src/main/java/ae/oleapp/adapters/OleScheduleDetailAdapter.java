package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleScheduleList;

public class OleScheduleDetailAdapter extends RecyclerView.Adapter<OleScheduleDetailAdapter.ViewHolder> {

    private final Context context;
    private final List<OleScheduleList> list;
    private final List<OleScheduleList> selectedList = new ArrayList<>();
    private ItemClickListener itemClickListener;
    private boolean isUpdate = false;

    public OleScheduleDetailAdapter(Context context, List<OleScheduleList> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setUpdate(boolean update) {
        if (!update) {
            selectedList.clear();
        }
        isUpdate = update;
        notifyDataSetChanged();
    }

    public List<OleScheduleList> getSelectedList() {
        return selectedList;
    }

    public void setSelection(OleScheduleList data) {
        int index = checkIfExist(data.getId());
        if (index == -1) {
            selectedList.add(data);
        }
        else {
            selectedList.remove(index);
        }
        notifyDataSetChanged();
    }

    private int checkIfExist(String id) {
        int index = -1;
        for (int i = 0; i < selectedList.size(); i++) {
            if (selectedList.get(i).getId().equalsIgnoreCase(id)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleschedule_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleScheduleList booking = list.get(position);
        holder.tvDay.setText(booking.getDays());
        holder.tvDate.setText(booking.getFromDate());
        holder.tvTime.setText(booking.getBookingTime());
        holder.tvPrice.setText(context.getString(R.string.currency_place, booking.getPrice(), booking.getCurrency()));
        if (isUpdate) {
            holder.imgVuCheck.setVisibility(View.VISIBLE);
            int index = checkIfExist(booking.getId());
            if (index != -1) {
                holder.imgVuCheck.setImageResource(R.drawable.check);
            }
            else {
                holder.imgVuCheck.setImageResource(R.drawable.uncheck);
            }
        }
        else {
            holder.imgVuCheck.setVisibility(View.GONE);
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvDay, tvDate, tvTime, tvPrice;
        CardView mainLayout;
        ImageView imgVuCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDay = itemView.findViewById(R.id.tv_day);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPrice = itemView.findViewById(R.id.tv_price);
            mainLayout = itemView.findViewById(R.id.rel_main);
            imgVuCheck = itemView.findViewById(R.id.img_check);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}