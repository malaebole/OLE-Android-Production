package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.OleLevelsTarget;
import ae.oleapp.util.Constants;

public class OleProfileLevelDetailAdapter extends RecyclerView.Adapter<OleProfileLevelDetailAdapter.ViewHolder> {

    private final Context context;
    private final List<OleLevelsTarget> list;
    private String module = "";

    public OleProfileLevelDetailAdapter(Context context, List<OleLevelsTarget> list, String module) {
        this.context = context;
        this.list = list;
        this.module = module;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleprofile_level_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleLevelsTarget target = list.get(position);
        holder.tvTitle.setText(String.format(Locale.ENGLISH, "%s (%d %s)", target.getTitle(), target.getTotal(), context.getResources().getString(R.string.times)));
        if (target.getDates().equalsIgnoreCase("")) {
            holder.tvDates.setVisibility(View.GONE);
        }
        else {
            holder.tvDates.setVisibility(View.VISIBLE);
            holder.tvDates.setText(target.getDates());
        }
        if (target.getRemaining() == 0) {
            Glide.with(context).load(target.getActiveIcon()).into(holder.imgVuIcon);
            holder.tvRemaining.setText(R.string.completed);
        }
        else {
            Glide.with(context).load(target.getIcon()).into(holder.imgVuIcon);
            holder.tvRemaining.setText(String.format(Locale.ENGLISH, "%d %s", target.getRemaining(), context.getResources().getString(R.string.remaining)));
        }

        if (module.equalsIgnoreCase(Constants.kPadelModule)) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#1A5A35"));
        }
        else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#00305E"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle, tvDates, tvRemaining;
        ImageView imgVuIcon;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDates = itemView.findViewById(R.id.tv_dates);
            tvRemaining = itemView.findViewById(R.id.tv_remaining);
            imgVuIcon = itemView.findViewById(R.id.img_vu_icon);
            cardView = itemView.findViewById(R.id.card);
        }
    }
}