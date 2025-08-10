package ae.oleapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSeekBar;

import ae.oleapp.R;


public class FlexibleThumbSeekbar extends AppCompatSeekBar {

    public FlexibleThumbSeekbar(Context context) {
        super(context);
    }

    public FlexibleThumbSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(R.drawable.profile_skills_thumbl, 70);
    }

    private void init(int thumbId, int height) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), thumbId);
        Bitmap thumb = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(thumb);
        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new Rect(0, 0, thumb.getWidth(), thumb.getHeight()), null);
        Drawable drawable = new BitmapDrawable(getResources(), thumb);
        setThumb(drawable);
    }

    public void setThumbImage(int thumbId, int height) {
        init(thumbId, height);
    }
}
