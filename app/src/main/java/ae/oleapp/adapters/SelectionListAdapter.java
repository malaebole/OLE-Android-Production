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
import ae.oleapp.models.SelectionList;

public class SelectionListAdapter extends RecyclerView.Adapter<SelectionListAdapter.ViewHolder> {

    private final Context context;
    private ItemClickListener onItemClickListener;
    private List<SelectionList> list;
    private final List<SelectionList> selectedList = new ArrayList<>();

    public void setOnItemClickListener(ItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public SelectionListAdapter(Context context, List<SelectionList> list) {
        this.context = context;
        this.list = list;
    }

    public void setDatasource(List<SelectionList> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<SelectionList> getSelectedList() {
        return selectedList;
    }

    public void selectItem(SelectionList item) {
        int index = isExist(item.getId());
        if (index == -1) {
            selectedList.add(item);
        }
        else {
            selectedList.remove(index);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.selection_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SelectionList listItem = list.get(position);
        holder.textView.setText(listItem.getValue());

        if (isExist(listItem.getId()) != -1) {
            holder.imgCheck.setVisibility(View.VISIBLE);
        }
        else {
            holder.imgCheck.setVisibility(View.GONE);
        }

        if (listItem.getImgUrl() != null && !listItem.getImgUrl().equalsIgnoreCase("")) {
            holder.imgVu.setVisibility(View.VISIBLE);
            Glide.with(context).load(listItem.getImgUrl()).into(holder.imgVu);
        }
        else {
            holder.imgVu.setVisibility(View.GONE);
        }

        holder.rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClickListener(v, holder.getAdapterPosition());
            }
        });
    }

    private int isExist(String id) {
        for (int i = 0; i < selectedList.size(); i++) {
            if (selectedList.get(i).getId().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return  -1;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageView imgCheck, imgVu;
        RelativeLayout rel;

        ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.text_vu);
            imgCheck = itemView.findViewById(R.id.img_check);
            imgVu = itemView.findViewById(R.id.img_vu);
            rel = itemView.findViewById(R.id.rel);

        }
    }

    public interface ItemClickListener {
        void onItemClickListener(View view, int position);
    }
}