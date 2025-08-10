package ae.oleapp.external;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class OleOnlyVerticalSwipeRefreshLayout extends SwipeRefreshLayout {

    private final int touchSlop;
    private float prevX;
    private boolean declined;

    public OleOnlyVerticalSwipeRefreshLayout(Context context, AttributeSet attrs ) {
        super( context, attrs );
        touchSlop = ViewConfiguration.get( context ).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event ) {
        switch( event.getAction() ){
            case MotionEvent.ACTION_DOWN:
                prevX = MotionEvent.obtain( event ).getX();
                declined = false; // New action
                break;

            case MotionEvent.ACTION_MOVE:
                final float eventX = event.getX();
                float xDiff = Math.abs( eventX - prevX );
                if( declined || xDiff > touchSlop ){
                    declined = true; // Memorize
                    return false;
                }
                break;
        }
        return super.onInterceptTouchEvent( event );
    }
}
