package ae.oleapp.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.github.infinitebanner.AbsBannerAdapter;
import com.github.infinitebanner.InfiniteBannerView;
import com.makeramen.roundedimageview.RoundedImageView;


import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.ShopBanner;


public class ShopBannerAdapter extends AbsBannerAdapter {

    private final List<ShopBanner> list;
    private final Context context;
    private ItemClickListener itemClickListener;

    public ShopBannerAdapter(Context context, List<ShopBanner> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    protected int getCount() {
        return list.size();
    }

    @Override
    protected View makeView(InfiniteBannerView parent) {
        RoundedImageView imageView = new RoundedImageView(parent.getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setCornerRadius(context.getResources().getDimension(R.dimen._5sdp));
        return imageView;
    }

    @Override
    protected void bind(View view, int position) {
        Glide.with(context).load(list.get(position).getPhoto()).into(((RoundedImageView) view));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(view, position);
            }
        });
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}
