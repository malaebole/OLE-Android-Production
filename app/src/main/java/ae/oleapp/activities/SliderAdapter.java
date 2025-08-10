package ae.oleapp.activities;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;

public class SliderAdapter extends PagerAdapter {

    private final Context mContext;
    private final List<SliderModelClass> theSlideItemsModelClassList;
    TextView captionTitle;
    MaterialCardView materialCardView;
    String friendId = "", groupName="";
    private OnItemClickListener onItemClickListener;
    private final GestureDetector gestureDetector;

    public SliderAdapter(Context mContext, List<SliderModelClass> theSlideItemsModelClassList, ViewPager viewPager) {
        this.mContext = mContext;
        this.theSlideItemsModelClassList = theSlideItemsModelClassList;

        // Initialize GestureDetector
        gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // Handle single tap event
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(friendId,groupName);
                }
                return true;
            }
        });

        // Attach OnTouchListener to ViewPager to intercept touch events
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sliderLayout = inflater.inflate(R.layout.items_layout, null);

        captionTitle = sliderLayout.findViewById(R.id.my_caption_title);
        captionTitle.setText(theSlideItemsModelClassList.get(position).getGroupName());
        captionTitle.setSelected(true);

        materialCardView = sliderLayout.findViewById(R.id.name_vuu);
        String ingame = theSlideItemsModelClassList.get(position).getInGame();
        String gameId = theSlideItemsModelClassList.get(position).getGameId();

        if (gameId.isEmpty() && ingame.equalsIgnoreCase("0")) {
            materialCardView.setStrokeColor(ContextCompat.getColor(sliderLayout.getContext(), R.color.light_red));
        } else if (!gameId.isEmpty() || ingame.equalsIgnoreCase("1")) {
            materialCardView.setStrokeColor(ContextCompat.getColor(sliderLayout.getContext(), R.color.greenColor));
        }

        container.addView(sliderLayout);

        sliderLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                friendId = theSlideItemsModelClassList.get(position).getFriendId();
                groupName = theSlideItemsModelClassList.get(position).getGroupName();

                return false;
            }
        });

        return sliderLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return theSlideItemsModelClassList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    public interface OnItemClickListener {
        void onItemClick(String friendId, String groupName);
    }
}
