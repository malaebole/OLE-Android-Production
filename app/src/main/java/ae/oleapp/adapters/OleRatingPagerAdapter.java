package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerInfo;
import ae.oleapp.util.Functions;
import io.feeeei.circleseekbar.CircleSeekBar;

public class OleRatingPagerAdapter extends RecyclerView.Adapter<OleRatingPagerAdapter.ViewHolder> {

    private final Context context;
    private final List<OlePlayerInfo> list;
    private OnItemClickListener onItemClickListener;

    public OleRatingPagerAdapter(Context context, List<OlePlayerInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olerating_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerInfo olePlayerInfo = list.get(position);
        holder.tvName.setText(olePlayerInfo.getNickName());
        holder.tvPoints.setText(context.getString(R.string.points_place, olePlayerInfo.getPoints()));
        Glide.with(context).load(olePlayerInfo.getPhotoUrl()).placeholder(R.drawable.player_active).into(holder.imageView);
        if (olePlayerInfo.getWinPercentage().isEmpty()) {
            holder.tvPerc.setText("0%");
        }
        else {
            holder.tvPerc.setText(String.format("%s%%", olePlayerInfo.getWinPercentage()));
        }
        if (olePlayerInfo.getLevel() != null && !olePlayerInfo.getLevel().isEmpty() && !olePlayerInfo.getLevel().getValue().equalsIgnoreCase("")) {
            holder.tvRank.setVisibility(View.VISIBLE);
            holder.tvRank.setText(String.format("LV: %s", olePlayerInfo.getLevel().getValue()));
        }
        else {
            holder.tvRank.setVisibility(View.INVISIBLE);
        }

        holder.circleSeekBar.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar seekbar, int curValue) {
                holder.tvLevel.setText(String.format("%s%%", curValue));
            }
        });

        holder.circleSeekBar.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action)
            {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            // Handle Seek bar touch events.
            v.onTouchEvent(event);
            return true;
        });

        holder.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.circleSeekBar.getCurProcess() == 0) {
                    Functions.showToast(context, context.getString(R.string.playing_level_not_zero), FancyToast.ERROR);
                    return;
                }
                if (holder.reachTime.isEmpty()) {
                    Functions.showToast(context, context.getString(R.string.select_player_time), FancyToast.ERROR);
                    return;
                }
                onItemClickListener.OnSubmitClick(v, holder.getAdapterPosition(), holder.circleSeekBar.getCurProcess(), holder.reachTime, holder.etMsg.getText().toString());
            }
        });
        holder.relBeforeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.reachTime = "1";
                holder.imgBeforeTime.setImageResource(R.drawable.check);
                holder.imgOnTime.setImageResource(R.drawable.uncheck);
                holder.imgLate.setImageResource(R.drawable.uncheck);
                holder.imgNotCome.setImageResource(R.drawable.uncheck);
            }
        });
        holder.relOnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.reachTime = "2";
                holder.imgBeforeTime.setImageResource(R.drawable.uncheck);
                holder.imgOnTime.setImageResource(R.drawable.check);
                holder.imgLate.setImageResource(R.drawable.uncheck);
                holder.imgNotCome.setImageResource(R.drawable.uncheck);
            }
        });
        holder.relLate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.reachTime = "3";
                holder.imgBeforeTime.setImageResource(R.drawable.uncheck);
                holder.imgOnTime.setImageResource(R.drawable.uncheck);
                holder.imgLate.setImageResource(R.drawable.check);
                holder.imgNotCome.setImageResource(R.drawable.uncheck);
            }
        });
        holder.relNotCome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.reachTime = "4";
                holder.imgBeforeTime.setImageResource(R.drawable.uncheck);
                holder.imgOnTime.setImageResource(R.drawable.uncheck);
                holder.imgLate.setImageResource(R.drawable.uncheck);
                holder.imgNotCome.setImageResource(R.drawable.check);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView, imgBeforeTime, imgOnTime, imgLate, imgNotCome;
        TextView tvName, tvPerc, tvLevel, tvPoints, tvRank;
        CircleSeekBar circleSeekBar;
        EditText etMsg;
        CardView btnSubmit;
        RelativeLayout relBeforeTime ,relOnTime, relLate, relNotCome;
        String reachTime = "";

        ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.player_image);
            imgBeforeTime = itemView.findViewById(R.id.img_before_time);
            imgOnTime = itemView.findViewById(R.id.img_on_time);
            imgLate = itemView.findViewById(R.id.img_late);
            imgNotCome = itemView.findViewById(R.id.img_not_come);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPerc = itemView.findViewById(R.id.tv_perc);
            tvLevel = itemView.findViewById(R.id.tv_level);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvPoints = itemView.findViewById(R.id.tv_points);
            circleSeekBar = itemView.findViewById(R.id.circular);
            etMsg = itemView.findViewById(R.id.et_msg);
            btnSubmit = itemView.findViewById(R.id.btn_submit);
            relBeforeTime = itemView.findViewById(R.id.rel_before_time);
            relOnTime = itemView.findViewById(R.id.rel_on_time);
            relLate = itemView.findViewById(R.id.rel_late);
            relNotCome = itemView.findViewById(R.id.rel_not_come);

            reachTime = "";
            circleSeekBar.setCurProcess(0);
            tvLevel.setText("0%");
            etMsg.setText("");
            imgBeforeTime.setImageResource(R.drawable.uncheck);
            imgOnTime.setImageResource(R.drawable.uncheck);
            imgLate.setImageResource(R.drawable.uncheck);
            imgNotCome.setImageResource(R.drawable.uncheck);
        }
    }

    public interface OnItemClickListener {
        void OnSubmitClick(View v, int pos, int rating, String reachTime, String feedback);
    }
}