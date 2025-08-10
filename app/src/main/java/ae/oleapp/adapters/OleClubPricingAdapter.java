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

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleMembershipPlan;

public class OleClubPricingAdapter extends RecyclerView.Adapter<OleClubPricingAdapter.ViewHolder> {

    private final Context context;
    private final List<OleMembershipPlan> list;
    private ItemClickListener itemClickListener;
    private boolean canChoose = false;
    private int selectedPos = -1;

    public OleClubPricingAdapter(Context context, List<OleMembershipPlan> list, boolean canChoose) {
        this.context = context;
        this.list = list;
        this.canChoose = canChoose;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public int getSelectedPos() {
        return selectedPos;
    }

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleclub_pricing, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleMembershipPlan plan = list.get(position);
        holder.tvTitle.setText(plan.getTitle());
        holder.tvPrice.setText(String.format("%s %s", plan.getPrice(), plan.getCurrency()));
        if (plan.getSaving().equalsIgnoreCase("")) {
            holder.saveVu.setVisibility(View.GONE);
        }
        else {
            holder.saveVu.setVisibility(View.VISIBLE);
            holder.tvSave.setText(context.getString(R.string.place_save, plan.getSaving()));
        }
        if (canChoose) {
            if (selectedPos == position) {
                holder.imgCheck.setVisibility(View.VISIBLE);
                holder.main.setStrokeColor(context.getResources().getColor(R.color.blueColorNew));
            }
            else {
                holder.imgCheck.setVisibility(View.INVISIBLE);
                holder.main.setStrokeColor(context.getResources().getColor(R.color.separatorColor));
            }

            holder.main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.choosePlanClicked(v, holder.getAdapterPosition());
                    }
                }
            });
        }
        else {
            holder.imgCheck.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle, tvPrice, tvSave;
        ImageView imgCheck;
        MaterialCardView main;
        RelativeLayout saveVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            main = itemView.findViewById(R.id.main);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSave = itemView.findViewById(R.id.tv_save);
            tvPrice = itemView.findViewById(R.id.tv_price);
            imgCheck = itemView.findViewById(R.id.img_check);
            saveVu = itemView.findViewById(R.id.save_vu);
        }
    }

    public interface ItemClickListener {
        void choosePlanClicked(View view, int pos);
    }
}