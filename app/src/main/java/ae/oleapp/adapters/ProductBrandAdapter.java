package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;


import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.ProductBrand;

public class ProductBrandAdapter extends RecyclerView.Adapter<ProductBrandAdapter.ViewHolder> {

    private final Context context;
    private final List<ProductBrand> list;
    private ItemClickListener itemClickListener;

    public ProductBrandAdapter(Context context, List<ProductBrand> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_brand, parent, false);
        setItemWidth(v, parent);
        return new ViewHolder(v);
    }

    private void setItemWidth(View view, ViewGroup parent) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.width = (int)((parent.getMeasuredWidth() / 2) * 0.9);
        view.setLayoutParams(params);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductBrand brand = list.get(position);
        Glide.with(context).load(brand.getBanner()).into(holder.imgVuBanner);
        Glide.with(context).load(brand.getLogo()).into(holder.imgVuLogo);
        holder.tvName.setText(brand.getName());

        holder.layout.setOnClickListener(new View.OnClickListener() {
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
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        CardView layout;
        RoundedImageView imgVuBanner;
        ImageView imgVuLogo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVuBanner = itemView.findViewById(R.id.img_vu_banner);
            imgVuLogo = itemView.findViewById(R.id.img_vu_logo);
            tvName = itemView.findViewById(R.id.tv_name);
            layout = itemView.findViewById(R.id.main_layout);

            imgVuBanner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    imgVuBanner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    imgVuBanner.setCornerRadius((float)imgVuBanner.getWidth() / 2);
                }
            });
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}