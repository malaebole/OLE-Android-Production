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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.Earning;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;

public class OlePEarningListAdapter extends RecyclerView.Adapter<OlePEarningListAdapter.ViewHolder> {

    private final Context context;
    private final List<Earning> list;
    private OnItemClickListener itemClickListener;

    public OlePEarningListAdapter(Context context, List<Earning> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleearning_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Earning earning = list.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date = dateFormat.parse(earning.getDate());
            dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy", new Locale(Functions.getAppLangStr(context)));
            holder.tvDay.setText(dateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.tvDay.setText("");
        }

        if (Functions.getPrefValue(context, Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
            if (earning.getPaymentType().equalsIgnoreCase("balance")) {
                holder.tvPrice.setText(String.format("%s %s", earning.getAmount(), earning.getCurrency()));
                holder.tvType.setText(context.getResources().getString(R.string.wallet));
                holder.imgVuLogo.setImageResource(R.drawable.cash_ic);
                if (date != null) {
                    dateFormat.applyPattern("MMM dd, yyyy");
                    String dateStr = dateFormat.format(date);
                    holder.tvTitle.setText(context.getResources().getString(R.string.amount_added_wallet_on_place, dateStr));
                }
                else {
                    holder.tvTitle.setText(context.getResources().getString(R.string.amount_added_wallet));
                }
            }
            else {
                holder.tvTitle.setText(String.format("%s(%s)", earning.getClubName(), earning.getFieldName()));
                holder.tvPrice.setText(String.format("%s %s", earning.getAmount(), earning.getCurrency()));
                holder.tvType.setText(context.getResources().getString(R.string.booking));
                holder.imgVuLogo.setImageResource(R.drawable.withdraw_ic);
            }
        }
        else {
            holder.tvTitle.setText(String.format("%s(%s)", earning.getClubName(), earning.getFieldName()));
            if (earning.getPaymentType().equalsIgnoreCase("balance")) {
                holder.tvType.setText(String.format("%s %s", earning.getPaymentMethod(), context.getString(R.string.payment)));
                holder.imgVuLogo.setImageResource(R.drawable.cash_ic);
                holder.tvPrice.setText(String.format("%s (%s %s %s)", earning.getAmount(), earning.getDiscount(), earning.getCurrency(), context.getString(R.string.discount)));
            }
            else {
                holder.tvType.setText(context.getString(R.string.withdraw));
                holder.imgVuLogo.setImageResource(R.drawable.withdraw_ic);
                holder.tvPrice.setText(String.format("%s %s", earning.getAmount(), earning.getCurrency()));
            }
        }


        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.OnItemClick(v, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle, tvPrice, tvType, tvDay;
        RelativeLayout main;
        ImageView imgVuLogo;

        ViewHolder(View itemView) {
            super(itemView);

            main = itemView.findViewById(R.id.main);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvType = itemView.findViewById(R.id.tv_type);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvDay = itemView.findViewById(R.id.tv_day);
            imgVuLogo = itemView.findViewById(R.id.img_vu_logo);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}