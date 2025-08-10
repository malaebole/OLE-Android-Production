package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerLevel;
import ae.oleapp.util.Constants;
import ae.oleapp.util.OleNestedHorizontalRecyclerView;

public class OleProfileLevelAdapter extends RecyclerView.Adapter<OleProfileLevelAdapter.ViewHolder> {

    private final Context context;
    private final List<OlePlayerLevel> list;
    private OnItemClickListener onItemClickListener;
    private final RecyclerView recyclerView;
    private String module = "";

    public OleProfileLevelAdapter(Context context, List<OlePlayerLevel> list, RecyclerView recyclerView, String module) {
        this.context = context;
        this.list = list;
        this.recyclerView = recyclerView;
        this.module = module;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setModule(String module) {
        this.module = module;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleprofile_level, parent, false);
        if (list.size() > 1) {
            if (recyclerView.getWidth() == 0) {
                recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        ViewGroup.LayoutParams params = v.getLayoutParams();
                        params.width = (int) (recyclerView.getWidth() * 0.9);
                        v.setLayoutParams(params);
                    }
                });
            } else {
                ViewGroup.LayoutParams params = v.getLayoutParams();
                params.width = (int) (recyclerView.getWidth() * 0.9);
                v.setLayoutParams(params);
            }
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerLevel olePlayerLevel = list.get(position);
        holder.tvLevelNumber.setText(olePlayerLevel.getValue());
        holder.tvTitle.setText(olePlayerLevel.getTitle());

        if (module.equalsIgnoreCase(Constants.kPadelModule)) {
            holder.bgVu.setImageResource(R.drawable.padel_level_bg);
            holder.numberBgVu.setImageResource(R.drawable.football_level_number_bg_green);
        }
        else {
            holder.bgVu.setImageResource(R.drawable.level_bg);
            holder.numberBgVu.setImageResource(R.drawable.football_level_number_bg_blue);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        OleLevelAttributeAdapter adapter = new OleLevelAttributeAdapter(context, olePlayerLevel.getTargets(), module, true);
        adapter.setOnItemClickListener(new OleLevelAttributeAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int pos) {
                onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
            }
        });
        holder.recyclerView.setAdapter(adapter);

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CardView mainLayout;
        TextView tvTitle, tvLevelNumber;
        ImageView bgVu, numberBgVu;
        OleNestedHorizontalRecyclerView recyclerView;

        ViewHolder(View itemView) {
            super(itemView);

            mainLayout = itemView.findViewById(R.id.levels_vu);
            tvTitle = itemView.findViewById(R.id.tv_level_player);
            tvLevelNumber = itemView.findViewById(R.id.tv_level_number);
            bgVu = itemView.findViewById(R.id.bg_vu);
            numberBgVu = itemView.findViewById(R.id.level_number_bg);
            recyclerView = itemView.findViewById(R.id.recycler_vu);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}