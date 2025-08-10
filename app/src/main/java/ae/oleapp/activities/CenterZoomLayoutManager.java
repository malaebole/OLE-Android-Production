package ae.oleapp.activities;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CenterZoomLayoutManager extends LinearLayoutManager {

    private final float mShrinkAmount = 0.55f;
    private final float mShrinkDistance = 0.9f;

    public CenterZoomLayoutManager(Context context) {
        super(context);
    }

    public CenterZoomLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int orientation = getOrientation();
        if (orientation == VERTICAL) {
            int scrolled = super.scrollVerticallyBy(dy, recycler, state);
            float midpoint = getHeight() / 2.f;
            float d0 = 0.f;
            float d1 = mShrinkDistance * midpoint;
            float s0 = 1.f;
            float s1 = 1.f - mShrinkAmount;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                float childMidpoint = (getDecoratedBottom(child) + getDecoratedTop(child)) / 2.f;
                float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
                float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
                child.setScaleX(scale);
                child.setScaleY(scale);
            }
            return scrolled;
        } else {
            return 0;
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int orientation = getOrientation();
        if (orientation == HORIZONTAL) {
            int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
            int contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
            int totalItemCount = state.getItemCount();
            int childWidth = getChildAt(0).getWidth();
            int scrollOffset = getPaddingLeft() - getChildAt(0).getLeft();

            if (scrolled == 0) {
                // No need to adjust the scroll position
                return 0;
            } else if (scrolled > 0) {
                // Scrolling to the right
                if (scrollOffset + scrolled >= childWidth) {

                    // Reached the end of the layout, wrap around
                    int newPosition = (getPosition(getChildAt(getChildCount() - 1)) + 1) % totalItemCount;
                    removeAndRecycleView(getChildAt(0), recycler);
                    View newView = recycler.getViewForPosition(newPosition);
                    addView(newView);
                    measureChildWithMargins(newView, 0, 0);
                    int left = getChildAt(getChildCount() - 2).getRight();

                    int top = getPaddingTop();
                    int right = left + childWidth;
                    int bottom = top + getDecoratedMeasuredHeight(newView);
                    layoutDecorated(newView, left, top, right, bottom);
                    scrolled -= (childWidth - scrollOffset);

                }
            } else {
                // Scrolling to the left
                if (scrollOffset + scrolled < 0) {

                    //Reached the beginning of the layout, wrap around
                    int newPosition = (getPosition(getChildAt(0)) - 1 + totalItemCount) % totalItemCount;
                    removeAndRecycleView(getChildAt(getChildCount() - 1), recycler);
                    View newView = recycler.getViewForPosition(newPosition);
                    addView(newView, 0);
                    measureChildWithMargins(newView, 0, 0);
                    int left = getChildAt(1).getLeft() - childWidth;

                    int top = getPaddingTop();
                    int right = left + childWidth;
                    int bottom = top + getDecoratedMeasuredHeight(newView);
                    layoutDecorated(newView, left, top, right, bottom);
                    scrolled += (childWidth + scrollOffset);

                }
            }
            // Apply scaling to all visible children
            float midpoint = getWidth() / 2.f;
            float d0 = 0.f;
            float d1 = mShrinkDistance * midpoint;
            float s0 = 1.3f; //1.f
            float s1 = 1.f - mShrinkAmount;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                float childMidpoint = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2.f;
                float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
                float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
                child.setScaleX(scale);
                child.setScaleY(scale);
            }
            return scrolled;
        } else {
            return 0;
        }
    }

}
