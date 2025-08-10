package ae.oleapp.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.util.FlexibleThumbSeekbar;
import ae.oleapp.util.Functions;

public class RatingPagerAdapter extends RecyclerView.Adapter<RatingPagerAdapter.ViewHolder> {

    private final Context context;
    private final List<PlayerInfo> list;
    private OnItemClickListener onItemClickListener;

    public RatingPagerAdapter(Context context, List<PlayerInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(true); //checkx false
        PlayerInfo playerInfo = list.get(position);
        holder.tvName.setText(playerInfo.getNickName());
        Glide.with(context).load(playerInfo.getBibUrl()).into(holder.shirtImgVu);
        Glide.with(context).load(playerInfo.getEmojiUrl()).into(holder.emojiImgVu);

        if (playerInfo.getBestPlayer().equalsIgnoreCase("0")){
            holder.crownVu.setVisibility(View.GONE);
            holder.bisht.setVisibility(View.GONE);

        }else if (playerInfo.getBestPlayer().equalsIgnoreCase("1")){
            holder.crownVu.setVisibility(View.VISIBLE);
            holder.bisht.setVisibility(View.VISIBLE);
        }
            // holder.crownVu.setVisibility(View.GONE);

        holder.seekbarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                holder.tvSpeedPerc.setText(String.format("%s%%", i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        holder.seekbarShooting.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                holder.tvShootingPerc.setText(String.format("%s%%", i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        holder.seekbarDribble.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                holder.tvDribblePerc.setText(String.format("%s%%", i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        holder.seekbarIq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                holder.tvIqPerc.setText(String.format("%s%%", i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        holder.seekbarFitness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                holder.tvFitnessPerc.setText(String.format("%s%%", i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        holder.seekbarDefence.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                holder.tvDefencePerc.setText(String.format("%s%%", i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        holder.seekbarSpeed.setOnTouchListener((v, event) -> {
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
        holder.seekbarShooting.setOnTouchListener((v, event) -> {
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
        holder.seekbarDribble.setOnTouchListener((v, event) -> {
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
        holder.seekbarIq.setOnTouchListener((v, event) -> {
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
        holder.seekbarFitness.setOnTouchListener((v, event) -> {
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
        holder.seekbarDefence.setOnTouchListener((v, event) -> {
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
                if (holder.seekbarSpeed.getProgress() == 0) {
                    Functions.showToast(context, context.getString(R.string.playing_skills_not_zero), FancyToast.ERROR);
                    return;
                }
                if (holder.seekbarShooting.getProgress() == 0) {
                    Functions.showToast(context, context.getString(R.string.playing_skills_not_zero), FancyToast.ERROR);
                    return;
                }
                if (holder.seekbarDribble.getProgress() == 0) {
                    Functions.showToast(context, context.getString(R.string.playing_skills_not_zero), FancyToast.ERROR);
                    return;
                }
                if (holder.seekbarIq.getProgress() == 0) {
                    Functions.showToast(context, context.getString(R.string.playing_skills_not_zero), FancyToast.ERROR);
                    return;
                }
                if (holder.seekbarFitness.getProgress() == 0) {
                    Functions.showToast(context, context.getString(R.string.playing_skills_not_zero), FancyToast.ERROR);
                    return;
                }
                if (holder.seekbarDefence.getProgress() == 0) {
                    Functions.showToast(context, context.getString(R.string.playing_skills_not_zero), FancyToast.ERROR);
                    return;
                }

//                if (holder.reachTime.isEmpty()) {
//                    Functions.showToast(context, context.getString(R.string.select_reach_time), FancyToast.ERROR);
//                    return;
//                }
                onItemClickListener.OnSubmitClick(v, holder.getAdapterPosition(), holder.seekbarSpeed.getProgress(), holder.seekbarShooting.getProgress(), holder.seekbarDribble.getProgress(), holder.seekbarIq.getProgress(), holder.seekbarFitness.getProgress(), holder.seekbarDefence.getProgress(), holder.reachTime, holder.etMsg.getText().toString(), holder.gotCard);
            }
        });
        holder.relBeforeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.reachTime = "1";
                holder.imgBeforeTime.setImageResource(R.drawable.rating_checkl);
                holder.imgOnTime.setImageResource(R.drawable.rating_uncheck_new);
                holder.imgLate.setImageResource(R.drawable.rating_uncheck_new);
                holder.imgNotCome.setImageResource(R.drawable.rating_uncheck_new);
            }
        });
        holder.relOnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.reachTime = "2";
                holder.imgBeforeTime.setImageResource(R.drawable.rating_uncheck_new);
                holder.imgOnTime.setImageResource(R.drawable.rating_checkl);
                holder.imgLate.setImageResource(R.drawable.rating_uncheck_new);
                holder.imgNotCome.setImageResource(R.drawable.rating_uncheck_new);
            }
        });
        holder.relLate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.reachTime = "3";
                holder.imgBeforeTime.setImageResource(R.drawable.rating_uncheck_new);
                holder.imgOnTime.setImageResource(R.drawable.rating_uncheck_new);
                holder.imgLate.setImageResource(R.drawable.rating_checkl);
                holder.imgNotCome.setImageResource(R.drawable.rating_uncheck_new);
            }
        });
        holder.relNotCome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.reachTime = "4";
                holder.imgBeforeTime.setImageResource(R.drawable.rating_uncheck_new);
                holder.imgOnTime.setImageResource(R.drawable.rating_uncheck_new);
                holder.imgLate.setImageResource(R.drawable.rating_uncheck_new);
                holder.imgNotCome.setImageResource(R.drawable.rating_checkl);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action)
                    {
                        case MotionEvent.ACTION_SCROLL:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                    }
                    v.onTouchEvent(event);
                    return true;
                }

            });
        }

        holder.redCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.clickRed == 0){
                    holder.redCard.setImageResource(R.drawable.red_card_active);
                    holder.yellowCard.setImageResource(R.drawable.yellow_card_inactive);
                    holder.gotCard = "red";
                    holder.clickRed = 1;
                }else{
                    holder.redCard.setImageResource(R.drawable.red_card_inactive);
                    holder.clickRed = 0;
                    holder.gotCard = "";
                }

            }
        });

        holder.yellowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.clickYellow == 0){
                    holder.yellowCard.setImageResource(R.drawable.yellow_card_active);
                    holder.redCard.setImageResource(R.drawable.red_card_inactive);
                    holder.gotCard = "yellow";
                    holder.clickYellow = 1;
                }else{
                    holder.yellowCard.setImageResource(R.drawable.yellow_card_inactive);
                    holder.clickYellow = 0;
                    holder.gotCard = "";
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView shirtImgVu, emojiImgVu, imgBeforeTime, imgOnTime, imgLate, imgNotCome, redCard, yellowCard, crownVu, bisht;
        TextView tvName, tvSpeedPerc, tvShootingPerc, tvDribblePerc, tvIqPerc, tvFitnessPerc, tvDefencePerc,reasonText;
        FlexibleThumbSeekbar seekbarSpeed, seekbarShooting, seekbarDribble, seekbarIq, seekbarFitness, seekbarDefence;
        EditText etMsg;
        CardView btnSubmit;
        RelativeLayout relBeforeTime, relOnTime, relLate, relNotCome, etMsgLayout;
        String reachTime = "";
        HorizontalScrollView horizontalScrollView;
        String gotCard = "";
        int clickRed = 0;
        int clickYellow = 0;

        ViewHolder(View itemView) {
            super(itemView);

            shirtImgVu = itemView.findViewById(R.id.shirt_img_vu);
            emojiImgVu = itemView.findViewById(R.id.emoji_img_vu);
            imgBeforeTime = itemView.findViewById(R.id.img_before_time);
            imgOnTime = itemView.findViewById(R.id.img_on_time);
            imgLate = itemView.findViewById(R.id.img_late);
            imgNotCome = itemView.findViewById(R.id.img_not_come);
            tvName = itemView.findViewById(R.id.tv_name);
            redCard = itemView.findViewById(R.id.redCard);
            yellowCard = itemView.findViewById(R.id.yellowCard);
            etMsgLayout = itemView.findViewById(R.id.et_msg_layout);
            reasonText = itemView.findViewById(R.id.reasonText);
            crownVu = itemView.findViewById(R.id.crown_vu);
            bisht = itemView.findViewById(R.id.bisht);

            tvSpeedPerc = itemView.findViewById(R.id.tv_speed_skills);
            tvShootingPerc = itemView.findViewById(R.id.tv_shooting_perc);
            tvDribblePerc = itemView.findViewById(R.id.tv_dribble_perc);
            tvIqPerc = itemView.findViewById(R.id.tv_iq_perc);
            tvFitnessPerc = itemView.findViewById(R.id.tv_fitness_perc);
            tvDefencePerc = itemView.findViewById(R.id.tv_defence_perc);

            seekbarSpeed = itemView.findViewById(R.id.speed_progressbar);
            seekbarShooting = itemView.findViewById(R.id.shooting_progressbar);
            seekbarDribble = itemView.findViewById(R.id.dribble_progressbar);
            seekbarIq = itemView.findViewById(R.id.iq_progressbar);
            seekbarFitness = itemView.findViewById(R.id.fitness_progressbar);
            seekbarDefence = itemView.findViewById(R.id.defence_progressbar);

            etMsg = itemView.findViewById(R.id.et_msg);
            btnSubmit = itemView.findViewById(R.id.btn_submit);
            relBeforeTime = itemView.findViewById(R.id.rel_before_time);
            relOnTime = itemView.findViewById(R.id.rel_on_time);
            relLate = itemView.findViewById(R.id.rel_late);
            relNotCome = itemView.findViewById(R.id.rel_not_come);
            horizontalScrollView = itemView.findViewById(R.id.horizontalScrollView);

            reachTime = "";

            seekbarSpeed.setProgress(0);
            tvSpeedPerc.setText("0%");

            seekbarShooting.setProgress(0);
            tvShootingPerc.setText("0%");

            seekbarDribble.setProgress(0);
            tvDribblePerc.setText("0%");

            seekbarIq.setProgress(0);
            tvIqPerc.setText("0%");

            seekbarFitness.setProgress(0);
            tvFitnessPerc.setText("0%");

            seekbarDefence.setProgress(0);
            tvDefencePerc.setText("0%");

            etMsg.setText("");

            imgBeforeTime.setImageResource(R.drawable.rating_uncheck_new);
            imgOnTime.setImageResource(R.drawable.rating_uncheck_new);
            imgLate.setImageResource(R.drawable.rating_uncheck_new);
            imgNotCome.setImageResource(R.drawable.rating_uncheck_new);

            seekbarSpeed.setThumbImage(R.drawable.speed_icon, 120);
            seekbarShooting.setThumbImage(R.drawable.shooting_icon, 120);
            seekbarDribble.setThumbImage(R.drawable.dribble_icon, 120);
            seekbarIq.setThumbImage(R.drawable.iq_icon, 120);
            seekbarFitness.setThumbImage(R.drawable.fitness_icon, 120);
            seekbarDefence.setThumbImage(R.drawable.defence_icon, 120);

        }
    }

    public interface OnItemClickListener {
        void OnSubmitClick(View v, int pos, int Speed, int Shooting, int Dribble, int Iq, int Fitness, int Defence, String reachTime, String feedback, String gotCard);
        }
}