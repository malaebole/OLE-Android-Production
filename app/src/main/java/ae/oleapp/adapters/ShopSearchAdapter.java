package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.SearchResult;

public class ShopSearchAdapter extends RecyclerView.Adapter<ShopSearchAdapter.ViewHolder> {

    private final Context context;
    private final List<SearchResult> list;
    private ItemClickListener itemClickListener;

    public ShopSearchAdapter(Context context, List<SearchResult> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_search, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchResult searchResult = list.get(position);
        holder.tvName.setText(searchResult.getName());
        if (searchResult.getItemNo().equalsIgnoreCase("")) {
            holder.tvItemNo.setVisibility(View.GONE);
        }
        else {
            holder.tvItemNo.setVisibility(View.VISIBLE);
            holder.tvItemNo.setText(context.getString(R.string.item_no_place, searchResult.getItemNo()));
        }
        if (searchResult.getTargetName().equalsIgnoreCase("")) {
            holder.tvCat.setVisibility(View.GONE);
        }
        else {
            holder.tvCat.setVisibility(View.VISIBLE);
            holder.tvCat.setText(searchResult.getTargetName());
        }

        holder.btnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.arrowClicked(v, holder.getAdapterPosition());
            }
        });

        holder.relMain.setOnClickListener(new View.OnClickListener() {
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

        TextView tvName, tvItemNo, tvCat;
        ImageButton btnArrow;
        RelativeLayout relMain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvItemNo = itemView.findViewById(R.id.tv_item_no);
            tvCat = itemView.findViewById(R.id.tv_category);
            btnArrow = itemView.findViewById(R.id.btn_arrow);
            relMain = itemView.findViewById(R.id.main);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void arrowClicked(View view, int pos);
    }
}