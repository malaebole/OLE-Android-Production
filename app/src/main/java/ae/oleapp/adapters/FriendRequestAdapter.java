package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.EmployeeDocModel;
import ae.oleapp.models.FriendRequestModel;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>{

    private final Context context;
    private final List<FriendRequestModel> list;
    private ItemClickListener itemClickListener;


    public FriendRequestAdapter(Context context, List<FriendRequestModel> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_vu, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tvName.setText(list.get(position).getName());
        holder.tvPhone.setText(list.get(position).getPhone());
        holder.tvMsg.setText(list.get(position).getMessage());

        if (!list.get(position).getEmojiUrl().isEmpty()){
            Glide.with(context).load(list.get(position).getEmojiUrl()).into(holder.emoji);
        }

        if (!list.get(position).getBibUrl().isEmpty()){
            Glide.with(context).load(list.get(position).getBibUrl()).into(holder.shirt);
        }

        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v,holder.getAdapterPosition(),true);
            }
        });

        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v,holder.getAdapterPosition(),false);
            }
        });


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName,tvPhone, tvMsg;
        CardView relIncome;
        ImageView emoji,shirt,rejectBtn,acceptBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvMsg = itemView.findViewById(R.id.tv_msg);
            emoji = itemView.findViewById(R.id.emoji_img_vu);
            shirt = itemView.findViewById(R.id.shirt_img_vu);
            rejectBtn = itemView.findViewById(R.id.reject_btn);
            acceptBtn = itemView.findViewById(R.id.accept_btn);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos, Boolean decision);
    }
}