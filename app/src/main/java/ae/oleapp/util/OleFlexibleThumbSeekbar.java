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

public class OleFlexibleThumbSeekbar extends AppCompatSeekBar {

    public OleFlexibleThumbSeekbar(Context context) {
        super(context);
    }

    public OleFlexibleThumbSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.slider_thumb);
        Bitmap thumb = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(thumb);
        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new Rect(0, 0, thumb.getWidth(), thumb.getHeight()), null);
        Drawable drawable = new BitmapDrawable(getResources(), thumb);
        setThumb(drawable);
    }
}
