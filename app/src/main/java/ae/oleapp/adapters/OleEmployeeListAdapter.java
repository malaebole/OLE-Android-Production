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

import com.bumptech.glide.Glide;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Employee;

public class OleEmployeeListAdapter extends RecyclerView.Adapter<OleEmployeeListAdapter.ViewHolder> {

    private final Context context;
    private final List<Employee> list;
    private ItemClickListener itemClickListener;

    public OleEmployeeListAdapter(Context context, List<Employee> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleemployee_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee employee = list.get(position);
        holder.tvClubName.setText(String.format("%s: %s", context.getString(R.string.club_name), employee.getClubName()));
        holder.tvName.setText(employee.getName());
        holder.tvPost.setText(String.format("%s: %s", context.getString(R.string.role), employee.getRoleName()));
        holder.tvRate.setText(employee.getRating());
        Glide.with(context).load(employee.getEmployeePhoto()).placeholder(R.drawable.emp_ic).into(holder.imageView);

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
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvClubName, tvName, tvPost, tvRate;
        ImageView imageView;
        CardView layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClubName = itemView.findViewById(R.id.tv_club_name);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPost = itemView.findViewById(R.id.tv_post);
            tvRate = itemView.findViewById(R.id.tv_rate);
            imageView = itemView.findViewById(R.id.img_vu);
            layout = itemView.findViewById(R.id.rel_main);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}