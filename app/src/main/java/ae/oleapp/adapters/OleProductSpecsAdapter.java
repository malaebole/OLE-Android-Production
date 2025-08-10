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
import ae.oleapp.models.OleProductSpecs;

public class OleProductSpecsAdapter extends RecyclerView.Adapter<OleProductSpecsAdapter.ViewHolder> {

    private final Context context;
    private final List<OleProductSpecs> list;

    public OleProductSpecsAdapter(Context context, List<OleProductSpecs> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleproduct_specs, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleProductSpecs oleProductSpecs = list.get(position);
        holder.tvTitle.setText(oleProductSpecs.getTitle());
        holder.tvValue.setText(oleProductSpecs.getValue());
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