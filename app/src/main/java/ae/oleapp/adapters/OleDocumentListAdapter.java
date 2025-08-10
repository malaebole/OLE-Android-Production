package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.DocList;
import ae.oleapp.models.IncomeHistory;

public class OleDocumentListAdapter extends RecyclerView.Adapter<OleDocumentListAdapter.ViewHolder>{

    private final Context context;
    private final List<DocList> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "", type="";


    public OleDocumentListAdapter(Context context, List<DocList> list, String type) {
        this.context = context;
        this.list = list;
        this.type = type;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.universal_doc_vu, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

                holder.title.setText(list.get(position).getName());
                holder.tvIssueDate.setText(list.get(position).getIssueDate());
                holder.tvExpiryDate.setText(list.get(position).getExpiryDate());
                holder.tvAddedDate.setText(list.get(position).getAddedDate());


                holder.relMainData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v,holder.getAdapterPosition());

            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,tvIssueDate, tvExpiryDate,tvAddedDate;
        CardView relMainData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            tvIssueDate = itemView.findViewById(R.id.tv_issue_date);
            tvExpiryDate = itemView.findViewById(R.id.tv_expiry_date);
            tvAddedDate = itemView.findViewById(R.id.tv_added_date);
            relMainData = itemView.findViewById(R.id.rel_main_data);


        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}