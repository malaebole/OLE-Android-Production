package ae.oleapp.adapters;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class OleOverlapDecoration extends RecyclerView.ItemDecoration {

    private int vertOverlap = -20;

    public OleOverlapDecoration(int overlap) {
        this.vertOverlap = overlap;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int itemPosition = parent.getChildAdapterPosition(view);
        outRect.set(0, 0, 0, 0);
        if (itemPosition < parent.getAdapter().getItemCount() - 1) {
//            outRect.set(0, 0, vertOverlap, 0);
            outRect.set(vertOverlap, 0, 0, 0);
        }
    }
}
