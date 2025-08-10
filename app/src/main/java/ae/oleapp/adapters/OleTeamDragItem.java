package ae.oleapp.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woxthebox.draglistview.DragItem;

import ae.oleapp.R;

public class OleTeamDragItem extends DragItem {

    public OleTeamDragItem(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    public void onBindDragView(View clickedView, View dragView) {
        TextView tvName = dragView.findViewById(R.id.tv_name);
        TextView tvPosition = dragView.findViewById(R.id.tv_position);
        ImageView imageView = dragView.findViewById(R.id.img_vu);

        tvName.setText(((TextView)clickedView.findViewById(R.id.tv_name)).getText());
        tvPosition.setText(((TextView)clickedView.findViewById(R.id.tv_position)).getText());
        imageView.setImageDrawable(((ImageView)clickedView.findViewById(R.id.img_vu)).getDrawable());
    }

    @Override
    public void onMeasureDragView(View clickedView, View dragView) {
        LinearLayout dragCard = dragView.findViewById(R.id.main_layout);
        LinearLayout clickedCard = clickedView.findViewById(R.id.main_layout);
        int widthDiff = dragCard.getPaddingLeft() - clickedCard.getPaddingLeft() + dragCard.getPaddingRight() -
                clickedCard.getPaddingRight();
        int heightDiff = dragCard.getPaddingTop() - clickedCard.getPaddingTop() + dragCard.getPaddingBottom() -
                clickedCard.getPaddingBottom();
        int width = clickedView.getMeasuredWidth() + widthDiff;
        int height = clickedView.getMeasuredHeight() + heightDiff;
        dragView.setLayoutParams(new FrameLayout.LayoutParams(width, height));

        int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        dragView.measure(widthSpec, heightSpec);
    }

    @Override
    public void onStartDragAnimation(View dragView) {
        LinearLayout dragCard = dragView.findViewById(R.id.main_layout);
        ObjectAnimator anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", 0, 40);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(ANIMATION_DURATION);
        anim.start();
    }

    @Override
    public void onEndDragAnimation(View dragView) {
        LinearLayout dragCard = dragView.findViewById(R.id.main_layout);
        ObjectAnimator anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", 0, 6);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(ANIMATION_DURATION);
        anim.start();
    }
}
