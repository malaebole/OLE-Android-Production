package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Employee;

public class OleEmployeeRateAdapter extends RecyclerView.Adapter<OleEmployeeRateAdapter.ViewHolder> {

    private final Context context;
    private final List<Employee> list;
    private final List<Employee> selectedList = new ArrayList<>();
    private ItemClickListener itemClickListener;

    public OleEmployeeRateAdapter(Context context, List<Employee> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public List<Employee> getSelectedList() {
        return selectedList;
    }

    public void selectItem(Employee employee) {
        int index = isExist(employee.getId());
        if (index == -1) {
            selectedList.add(employee);
        }
        else {
            selectedList.remove(index);
        }
        notifyDataSetChanged();
    }

    private int isExist(String empId) {
        int index = -1;
        for (int i = 0; i < selectedList.size(); i++) {
            if (selectedList.get(i).getId().equalsIgnoreCase(empId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleemployee_rate, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee employee = list.get(position);
        holder.tvName.setText(employee.getName());
        //Glide.with(context).load(employee.getEmployeePhoto()).placeholder(R.drawable.emp_ic).into(holder.imageView);
        Glide.with(context).load(employee.getEmployeePhoto()).apply(RequestOptions.circleCropTransform()).into(holder.imageView);
        if (isExist(employee.getId()) == -1) {
            holder.imgVuTick.setImageResource(R.drawable.deselect_football_icon);
        }
        else {
            holder.imgVuTick.setImageResource(R.drawable.selected_football_icon);
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
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        ImageView imageView, imgVuTick;
        RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.name);
            imageView = itemView.findViewById(R.id.img);
            imgVuTick = itemView.findViewById(R.id.img_vu_tick);
            layout = itemView.findViewById(R.id.rel_main);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}