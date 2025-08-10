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

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.ClubGifts;

public class GiftsAdapter extends RecyclerView.Adapter<GiftsAdapter.ViewHolder>{

    private final Context context;
    private final List<ClubGifts> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";

    private int selectedTab = 0;


    public GiftsAdapter(Context context, List<ClubGifts> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }
    public void setSelectedTab(int tab) {
        selectedTab = tab;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {

        if (selectedTab == 0) {
            return getActiveItemCount();
        } else if (selectedTab == 1) {
            return getExpiredItemCount();
        } else {
            return 0;
        }
    }

    private int getActiveItemCount() {
        int count = 0;
        for (ClubGifts item : list) {
            if (item.getStatus().equals("Active")) {
                count++;
            }
        }
        return count;
    }

    private int getExpiredItemCount() {
        int count = 0;
        for (ClubGifts item : list) {
            if (item.getStatus().equals("Expired")) {
                count++;
            }
        }
        return count;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gifts_adapter, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        int actualPosition = getActualPosition(position);

        // Update the views based on the item at the actual position
        holder.tvGiftType.setText(list.get(actualPosition).getTargetType());
        holder.tvtTitle.setText(list.get(actualPosition).getName());
        holder.tvDate.setText(list.get(actualPosition).getStartDate());

        if (!list.get(actualPosition).getPhotoUrl().isEmpty()) {
            Glide.with(context).load(list.get(actualPosition).getPhotoUrl()).into(holder.giftImg);
        }else{
            Glide.with(context).load(R.drawable.attachment_img).into(holder.giftImg);
        }



        holder.relGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, getActualPosition(position));
            }
        });

    }

    private int getActualPosition(int position) {
        if (selectedTab == 0) {
            int count = 0;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getStatus().equals("Active")) {
                    if (count == position) {
                        return i;
                    }
                    count++;
                }
            }
        } else if (selectedTab == 1) {
            int count = 0;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getStatus().equals("Expired")) {
                    if (count == position) {
                        return i;
                    }
                    count++;
                }
            }
        }

        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvGiftType,tvtTitle,tvDate;
        ImageView giftImg;
        CardView relGift;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvGiftType = itemView.findViewById(R.id.tv_gift_type);
            tvtTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            relGift = itemView.findViewById(R.id.rel_gift);
            giftImg = itemView.findViewById(R.id.img_vu);


        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}