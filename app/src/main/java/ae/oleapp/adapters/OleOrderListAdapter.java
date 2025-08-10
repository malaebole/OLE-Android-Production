package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Order;

public class OleOrderListAdapter extends RecyclerView.Adapter<OleOrderListAdapter.ViewHolder> {

    private final Context context;
    private final List<Order> list;
    private ItemClickListener itemClickListener;

    public OleOrderListAdapter(Context context, List<Order> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleorder_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = list.get(position);
        holder.tvOrderNo.setText(context.getString(R.string.order_no_place, order.getOrderNumber()));
        holder.tvDate.setText(context.getString(R.string.placed_on_place, order.getOrderDate()));
        if (order.getDeliveryStatus().equalsIgnoreCase("delivered") && order.getIsReviewed().equalsIgnoreCase("0")) {
            holder.btnReview.setVisibility(View.VISIBLE);
        }
        else {
            holder.btnReview.setVisibility(View.GONE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        OleOrderProductAdapter adapter = new OleOrderProductAdapter(context, order.getProducts());
        holder.recyclerView.setAdapter(adapter);

        holder.btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.reviewClicked(v, holder.getAdapterPosition());
            }
        });
        holder.vuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.viewClicked(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvOrderNo, tvDate;
        CardView btnReview;
        RecyclerView recyclerView;
        RelativeLayout vuView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderNo = itemView.findViewById(R.id.tv_order_no);
            tvDate = itemView.findViewById(R.id.tv_date);
            btnReview = itemView.findViewById(R.id.btn_review);
            recyclerView = itemView.findViewById(R.id.recycler_vu);
            vuView = itemView.findViewById(R.id.vu_view);
        }
    }

    public interface ItemClickListener {
        void viewClicked(View view, int pos);
        void reviewClicked(View view, int pos);
    }
}