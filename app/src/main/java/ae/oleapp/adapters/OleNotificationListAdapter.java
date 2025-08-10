package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleNotificationList;

public class OleNotificationListAdapter extends RecyclerView.Adapter<OleNotificationListAdapter.ViewHolder> {

    private final Context context;
    private final List<OleNotificationList> list;
    private OnItemClickListener itemClickListener;
    public final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public OleNotificationListAdapter(Context context, List<OleNotificationList> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olenotification_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleNotificationList notification = list.get(position);
        holder.tvDesc.setText(notification.getMessage());
        holder.tvDate.setText(notification.getTime());
        if (notification.getIsRead().equalsIgnoreCase("1")) {
            holder.indicatorVu.setVisibility(View.GONE);
        }
        else {
            holder.indicatorVu.setVisibility(View.VISIBLE);
        }

        binderHelper.bind(holder.swipeLayout, String.valueOf(position));

        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.OnItemClick(v, holder.getAdapterPosition());
            }
        });

        holder.deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.OnDeleteClick(v, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvDesc, tvDate;
        RelativeLayout indicatorVu, main;
        SwipeRevealLayout swipeLayout;
        FrameLayout deleteLayout;

        ViewHolder(View itemView) {
            super(itemView);

            indicatorVu = itemView.findViewById(R.id.indicator_vu);
            main = itemView.findViewById(R.id.main);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvDate = itemView.findViewById(R.id.tv_date);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            deleteLayout = itemView.findViewById(R.id.delete_layout);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
        void OnDeleteClick(View v, int pos);
    }
}