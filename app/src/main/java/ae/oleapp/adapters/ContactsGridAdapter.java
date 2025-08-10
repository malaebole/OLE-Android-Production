package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.ContactList;
import ae.oleapp.models.ContactPlayers;
import ae.oleapp.models.PlayerInfo;

public class ContactsGridAdapter extends RecyclerView.Adapter<ContactsGridAdapter.ViewHolder> {

    private final Context context;

    private final List<ContactPlayers> list;
    private final List<ContactPlayers> filteredPlayers;
    private ItemClickListener itemClickListener;
    private boolean setManualWidth = false, isFilter = false;
    private int playersLimit = 0;



    public ContactsGridAdapter(Context context, List<ContactPlayers> list, boolean setManualWidth, int playersLimit) {
        this.context = context;
        this.list = list;
        this.setManualWidth = setManualWidth;
        this.playersLimit = playersLimit;
        this.filteredPlayers = new ArrayList<>(list);
    }

    public ContactsGridAdapter(Context context, List<ContactPlayers> list, boolean setManualWidth) {
        this(context, list, setManualWidth, 1000);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public void setFilteredList(List<ContactPlayers> filteredList,Boolean isFilter) {
        filteredPlayers.clear();
        this.isFilter = isFilter;
        filteredPlayers.addAll(filteredList);
        notifyDataSetChanged();
    }





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_player_vu, parent, false);
        if (setManualWidth) {
            setItemWidth(v, parent);
        }
        return new ViewHolder(v);
    }

    private void setItemWidth(View view, ViewGroup parent) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.width = (int)((parent.getMeasuredWidth() / 3) * 0.9);
        view.setLayoutParams(params);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactPlayers info;
        if (isFilter){
             info = filteredPlayers.get(position);
        }else{
             info = list.get(position);
        }
        String[] arr = info.getNickName().split(" ");
        if (arr.length > 0) {
            holder.tvName.setText(arr[0]);
        }
        else {
            holder.tvName.setText(info.getNickName());
        }

        holder.tvPhone.setText(info.getPhone());
        holder.emojiVu.setVisibility(View.VISIBLE);
        Glide.with(context).load(info.getEmojiUrl()).into(holder.emojiVu);
        Glide.with(context).load(info.getBibUrl()).placeholder(R.drawable.shirtl).into(holder.shirt);

        holder.addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        if (isFilter){
            return filteredPlayers.size();
        }else{
            return list.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvPhone;
        ImageView emojiVu, shirt;
        ImageButton addPlayer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            emojiVu = itemView.findViewById(R.id.emoji_img_vu);
            shirt = itemView.findViewById(R.id.shirt);
            addPlayer = itemView.findViewById(R.id.add_player);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}