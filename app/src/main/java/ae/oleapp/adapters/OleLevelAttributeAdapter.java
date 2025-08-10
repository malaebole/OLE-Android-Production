package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class OleLevelAttributeAdapter extends RecyclerView.Adapter<OleLevelAttributeAdapter.ViewHolder> {

    private final Context context;
    private final List<OleLevelsTarget> list;
    private final String module;
    private OnItemClickListener onItemClickListener;
    private boolean isShowTitle = false;

    public OleLevelAttributeAdapter(Context context, List<OleLevelsTarget> list, String module, boolean isShowTitle) {
        this.context = context;
        this.list = list;
        this.module = module;
        this.isShowTitle = isShowTitle;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olelevel_attribute, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OleLevelsTarget target = list.get(position);
            if (target.getRemaining() == 0) {
                Glide.with(context).load(target.getActiveIcon()).into(holder.imgVuIcon);
                holder.tvTitle.setText(R.string.completed);
            }
            else {
                Glide.with(context).load(target.getIcon()).into(holder.imgVuIcon);
                holder.tvTitle.setText(String.format(Locale.ENGLISH, "%d %s", target.getRemaining(), context.getResources().getString(R.string.remaining)));
            }

            if (position == 0) {
                holder.leftVu.setVisibility(View.INVISIBLE);
            }
            if (position == list.size() - 1) {
                holder.rightVu.setVisibility(View.INVISIBLE);
            }
            if (module.equalsIgnoreCase(Constants.kPadelModule)) {
                holder.leftVu.setBackgroundColor(Color.parseColor("#1A5A35"));
                holder.cardView.setCardBackgroundColor(Color.parseColor("#1A5A35"));
                holder.rightVu.setBackgroundColor(Color.parseColor("#1A5A35"));
            }
            else {
                holder.leftVu.setBackgroundColor(Color.parseColor("#00305E"));
                holder.cardView.setCardBackgroundColor(Color.parseColor("#00305E"));
                holder.rightVu.setBackgroundColor(Color.parseColor("#00305E"));
            }

            if (isShowTitle) {
                holder.tvTitle.setVisibility(View.VISIBLE);
            }
            else {
                holder.tvTitle.setVisibility(View.GONE);
            }

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.OnItemClick(view, holder.getAdapterPosition());
                    }
                }
            });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        View leftVu, rightVu;
        ImageView imgVuIcon;
        CardView cardView;
        RelativeLayout layout;

        ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            leftVu = itemView.findViewById(R.id.left_vu);
            rightVu = itemView.findViewById(R.id.right_vu);
            imgVuIcon = itemView.findViewById(R.id.img_vu_icon);
            cardView = itemView.findViewById(R.id.card);
            layout = itemView.findViewById(R.id.main);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}