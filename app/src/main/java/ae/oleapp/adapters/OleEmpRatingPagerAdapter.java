package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Employee;

public class OleEmpRatingPagerAdapter extends RecyclerView.Adapter<OleEmpRatingPagerAdapter.ViewHolder> {

    private final Context context;
    private final List<Employee> list;
    private OnItemClickListener onItemClickListener;
    private int ratingScor;

    public OleEmpRatingPagerAdapter(Context context, List<Employee> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleemp_rating_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee employee = list.get(position);
        // Glide.with(context).load(employee.getEmployeePhoto()).placeholder(R.drawable.emp_ic).into(holder.imageView);
        Glide.with(context).load(employee.getEmployeePhoto()).apply(RequestOptions.circleCropTransform()).into(holder.imageView);
        holder.tvName.setText(employee.getName());

        holder.imgVuEmoji.setImageResource(R.drawable.excellent_emoji);
        holder.tvRateDesc.setTextColor(context.getResources().getColor(R.color.greenColor));
        holder.tvRateDesc.setText(R.string.excellent);

        holder.ratingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float ratings, boolean fromUser) {
                holder.rating = ratings;
                ratingScor = (int) ratings;
                switch (ratingScor) {
                    case 1:
                        holder.ratingBar.setFilledDrawable(context.getResources().getDrawable(R.drawable.red_star));
                        holder.ratingBar.setRating(1);
                        holder.imgVuEmoji.setImageResource(R.drawable.angry_emoji);
                        holder.tvRateDesc.setTextColor(context.getResources().getColor(R.color.redColor));
                        holder.tvRateDesc.setText(context.getString(R.string.angry));
                        holder.ratingBar.setFilledDrawable(context.getResources().getDrawable(R.drawable.red_star));
                        break;
                    case 2:
                        holder.ratingBar.setFilledDrawable(context.getResources().getDrawable(R.drawable.red_star));
                        holder.ratingBar.setRating(2);
                        holder.imgVuEmoji.setImageResource(R.drawable.sad_emoji);
                        holder.tvRateDesc.setTextColor(context.getResources().getColor(R.color.redColor));
                        holder.tvRateDesc.setText(context.getString(R.string.sad));
                        holder.ratingBar.setFilledDrawable(context.getResources().getDrawable(R.drawable.red_star));
                        break;
                    case 3:
                        holder.ratingBar.setFilledDrawable(context.getResources().getDrawable(R.drawable.green_star));
                        holder.ratingBar.setRating(3);
                        holder.imgVuEmoji.setImageResource(R.drawable.wow_emoji);
                        holder.tvRateDesc.setTextColor(context.getResources().getColor(R.color.greenColor));
                        holder.tvRateDesc.setText(context.getString(R.string.wow));
                        holder.ratingBar.setFilledDrawable(context.getResources().getDrawable(R.drawable.green_star));
                        break;
                    case 4:
                        holder.ratingBar.setFilledDrawable(context.getResources().getDrawable(R.drawable.green_star));
                        holder.ratingBar.setRating(4);
                        holder.imgVuEmoji.setImageResource(R.drawable.awesome_emoji);
                        holder.tvRateDesc.setTextColor(context.getResources().getColor(R.color.greenColor));
                        holder.tvRateDesc.setText(context.getString(R.string.awesome));
                        holder.ratingBar.setFilledDrawable(context.getResources().getDrawable(R.drawable.green_star));
                        break;
                    case 5:
                        holder.ratingBar.setFilledDrawable(context.getResources().getDrawable(R.drawable.green_star));
                        holder.ratingBar.setRating(5);
                        holder.imgVuEmoji.setImageResource(R.drawable.excellent_emoji);
                        holder.tvRateDesc.setTextColor(context.getResources().getColor(R.color.greenColor));
                        holder.tvRateDesc.setText(R.string.excellent);
                        holder.ratingBar.setFilledDrawable(context.getResources().getDrawable(R.drawable.green_star));

                        break;
                    default:
                        holder.ratingBar.setFilledDrawable(context.getResources().getDrawable(R.drawable.empty_star));
                        holder.ratingBar.setRating(0);
                        holder.ratingBar.setFilledDrawable(context.getResources().getDrawable(R.drawable.empty_star));
                        break;
                }
                holder.ratingBar.refreshDrawableState();
            }
        });


        holder.fiveVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.etTip.getText().toString().equalsIgnoreCase("5")) {
                    holder.etTip.setText("");
                    holder.tvFive.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                    holder.fiveVu.setCardBackgroundColor(Color.WHITE);
                    holder.tvTen.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                    holder.tenVu.setCardBackgroundColor(Color.WHITE);
                }
                else {
                    holder.etTip.setText("5");
                    holder.tvFive.setTextColor(Color.BLACK);
                    holder.fiveVu.setCardBackgroundColor(context.getResources().getColor(R.color.greenColor));
                    holder.tvTen.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                    holder.tenVu.setCardBackgroundColor(Color.WHITE);
                }
            }
        });
        holder.tenVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.etTip.getText().toString().equalsIgnoreCase("10")) {
                    holder.etTip.setText("");
                    holder.tvTen.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                    holder.tenVu.setCardBackgroundColor(Color.WHITE);
                    holder.tvFive.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                    holder.fiveVu.setCardBackgroundColor(Color.WHITE);
                }
                else {
                    holder.etTip.setText("10");
                    holder.tvTen.setTextColor(Color.BLACK);
                    holder.tenVu.setCardBackgroundColor(context.getResources().getColor(R.color.greenColor));
                    holder.tvFive.setTextColor(context.getResources().getColor(R.color.darkTextColor));
                    holder.fiveVu.setCardBackgroundColor(Color.WHITE);
                }
            }
        });
        holder.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tip = 0;
                if (!holder.etTip.getText().toString().equalsIgnoreCase("")) {
                    tip = Integer.parseInt(holder.etTip.getText().toString());
                }
                onItemClickListener.onSubmitClick(v, holder.getAdapterPosition(), holder.rating, holder.etMsg.getText().toString(), tip);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView, imgVuEmoji;
        TextView tvName, tvRateDesc, tvFive, tvTen;
        ScaleRatingBar ratingBar;
        EditText etMsg, etTip;
        CardView btnSubmit;
        MaterialCardView fiveVu, tenVu;
        float rating = 5;

        ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img);
            imgVuEmoji = itemView.findViewById(R.id.img_vu_emoji);
            tvName = itemView.findViewById(R.id.tv_name);
            tvRateDesc = itemView.findViewById(R.id.tv_rate_desc);
            tvFive = itemView.findViewById(R.id.tv_five);
            tvTen = itemView.findViewById(R.id.tv_ten);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            etMsg = itemView.findViewById(R.id.et_msg);
            etTip = itemView.findViewById(R.id.et_tip);
            btnSubmit = itemView.findViewById(R.id.btn_submit);
            fiveVu = itemView.findViewById(R.id.five_vu);
            tenVu = itemView.findViewById(R.id.ten_vu);

            etMsg.setText("");
            ratingBar.setRating(rating);
        }
    }

    public interface OnItemClickListener {
        void onSubmitClick(View v, int pos, float rating, String feedback, int tip);
    }
}