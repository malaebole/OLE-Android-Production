package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.ProductSpecs;

public class ProductSpecsAdapter extends RecyclerView.Adapter<ProductSpecsAdapter.ViewHolder> {

    private final Context context;
    private final List<ProductSpecs> list;

    public ProductSpecsAdapter(Context context, List<ProductSpecs> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_specs, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductSpecs productSpecs = list.get(position);
        holder.tvTitle.setText(productSpecs.getTitle());
        holder.tvValue.setText(productSpecs.getValue());
        if (position % 2 == 0) {
            holder.bgVu.setBackgroundColor(context.getResources().getColor(R.color.whiteColor));
        }
        else {
            holder.bgVu.setBackgroundColor(context.getResources().getColor(R.color.bgVuColor));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout bgVu;
        TextView tvTitle, tvValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            bgVu = itemView.findViewById(R.id.main);
            tvValue = itemView.findViewById(R.id.tv_value);
        }
    }
}