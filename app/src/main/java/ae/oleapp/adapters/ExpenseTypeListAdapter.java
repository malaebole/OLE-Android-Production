package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.ExpenseTypeModel;

public class ExpenseTypeListAdapter extends RecyclerView.Adapter<ExpenseTypeListAdapter.ViewHolder> {


    private final Context context;
    private ItemClickListener onItemClickListener;
    private List<ExpenseTypeModel> list;
    private final List<ExpenseTypeModel> clubBankLists = new ArrayList<>();

    public void setOnItemClickListener(ItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ExpenseTypeListAdapter(Context context, List<ExpenseTypeModel> list) {
        this.context = context;
        this.list = list;
    }

    public void setDatasource(List<ExpenseTypeModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<ExpenseTypeModel> getBankSelectedList() {
        return clubBankLists;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_selection_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ExpenseTypeModel listItem = list.get(position);
        holder.textView.setText(listItem.getValue());
        Glide.with(context).load(R.drawable.payment_icon).into(holder.imgVu);


        holder.rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClickListener(v, holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imgVu;
        RelativeLayout rel;

        ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.text_vu);
            imgVu = itemView.findViewById(R.id.img_vu);
            rel = itemView.findViewById(R.id.rel);

        }
    }

    public interface ItemClickListener {
        void onItemClickListener(View view, int position);
    }
}