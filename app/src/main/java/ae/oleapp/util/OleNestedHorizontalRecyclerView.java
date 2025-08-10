package ae.oleapp.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;


public class OleNestedHorizontalRecyclerView extends RecyclerView {
    public OleNestedHorizontalRecyclerView(Context context) {
        super(context);
    }

    public OleNestedHorizontalRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OleNestedHorizontalRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(event);
    }

}
