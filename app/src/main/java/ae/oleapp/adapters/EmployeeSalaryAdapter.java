package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.EmployeeSalary;
import ae.oleapp.models.PartnerEarning;

public class EmployeeSalaryAdapter extends RecyclerView.Adapter<EmployeeSalaryAdapter.ViewHolder>{
    private final Context context;
    private final List<EmployeeSalary> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";
    private int selectedIndex;
    private Boolean salary = false;




//    public EmployeeSalaryAdapter(Context context, List<EmployeeSalary> list) {
//        this.context = context;
//        this.list = list;
//    }
    public EmployeeSalaryAdapter(Context context, List<EmployeeSalary> list, Boolean salary) {
        this.context = context;
        this.list = list;
        this.salary = salary;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_salary_data_vu, parent, false);
        return new ViewHolder(v);

    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


            holder.tvIncome.setText(list.get(position).getSalary());
            holder.tvName.setText(list.get(position).getName());
            holder.tvIncomeCur.setText(list.get(position).getCurrency());


            if (salary){
                holder.salaryStatusBorder.setVisibility(View.GONE);
                holder.icon.setVisibility(View.VISIBLE);
                if (selectedIndex == position){
                    holder.icon.setImageResource(R.drawable.selected_emp_icon);
                }else{
                    holder.icon.setImageResource(R.drawable.deselect_emp_icon);
                }

            }else{
                holder.salaryStatusBorder.setVisibility(View.VISIBLE);
                holder.icon.setVisibility(View.VISIBLE);
                if (list.get(position).getSalaryStatus().equalsIgnoreCase("paid")){
                    holder.salaryStatusBorder.setStrokeColor(ContextCompat.getColor(context, R.color.greenColor));
                    holder.tvSalaryStatus.setTextColor(ContextCompat.getColor(context, R.color.greenColor));
                    holder.tvSalaryStatus.setText(context.getString(R.string.paid));
                }else{
                    holder.salaryStatusBorder.setStrokeColor(ContextCompat.getColor(context, R.color.redColor));
                    holder.tvSalaryStatus.setTextColor(ContextCompat.getColor(context, R.color.redColor));
                    holder.tvSalaryStatus.setText(context.getString(R.string.unpaid));

                }
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

        TextView tvIncome,tvName, tvSalaryStatus,tvIncomeCur;
        ImageView bankImg,icon;

        MaterialCardView salaryStatusBorder;
        CardView mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvIncome = itemView.findViewById(R.id.income_tv);
            bankImg = itemView.findViewById(R.id.bank_img_vu);
            salaryStatusBorder = itemView.findViewById(R.id.salary_status_vu);
            tvSalaryStatus = itemView.findViewById(R.id.tv_salary_status);
            mainLayout = itemView.findViewById(R.id.rel_main_data);
            icon = itemView.findViewById(R.id.icon);
            tvIncomeCur = itemView.findViewById(R.id.tv_income_cur);



        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}
