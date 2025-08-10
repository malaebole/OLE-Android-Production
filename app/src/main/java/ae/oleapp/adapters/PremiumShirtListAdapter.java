package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Shirt;

public class PremiumShirtListAdapter extends RecyclerView.Adapter<PremiumShirtListAdapter.ViewHolder> {

    private final Context context;
    private final List<Shirt> list;
    private PremiumShirtListAdapter.ItemClickListener itemClickListener;
    private String selectedId = "";

    public PremiumShirtListAdapter(Context context, List<Shirt> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(PremiumShirtListAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PremiumShirtListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.premium_shirts_vu, parent, false);
        return new PremiumShirtListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PremiumShirtListAdapter.ViewHolder holder, int position) {

        Glide.with(context).load(list.get(position).getPhotoUrl()).into(holder.imgVu);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgVu;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            cardView = itemView.findViewById(R.id.card_vu);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}