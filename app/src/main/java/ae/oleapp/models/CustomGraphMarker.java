package ae.oleapp.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;

import javax.annotation.Nullable;

import ae.oleapp.R;

public class CustomGraphMarker extends MarkerView {

    private final TextView tvName;
    private final ImageView pImage;
    private final TextView tvValue; // TextView for displaying the value and "booking"
    private final List<Player> list;

    public CustomGraphMarker(Context context, int layoutResource, List<Player> playerList) {
        super(context, layoutResource);
        tvName = findViewById(R.id.tv_name);
        pImage = findViewById(R.id.img);
        tvValue = findViewById(R.id.tv_booking_count); // Initialize the TextView for the value and "booking"
        list = playerList;

        //Set the width and height of the marker view
        //setDimensions(120, 80); // Adjust the dimensions as needed
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int xIndex = (int) e.getX();
        Player player = list.get((int) e.getX());
        tvName.setText(player.getName());
        pImage.setImageDrawable(null);

        if (player.getImage() != null) {
            Glide.with(getContext())
                    .asBitmap()
                    .load(player.getImage())
                    .apply(RequestOptions.circleCropTransform().centerInside()) // Apply circleCrop transformation
                    .downsample(DownsampleStrategy.AT_MOST)
                    .override(100, 100)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            // Handle load failure, if needed
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            // Image loading is successful
                            // Now you have a circular Bitmap; set it to the ImageView
                            pImage.setImageBitmap(resource);
                            return true;
                        }
                    })
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);  // Load the original image size
        } else {
            // Load a placeholder image
            Glide.with(getContext())
                    .load(R.drawable.face_inactive)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.face_inactive)
                    .into(pImage);
        }

        String value = String.valueOf((int) e.getY());
        String valueWithBooking = value + " bookings";
        tvValue.setText(valueWithBooking);

        super.refreshContent(e, highlight);
    }



    // This method is called when the MarkerView is drawn on the screen
    @Override
    public MPPointF getOffset() {
        // Offset the marker view to be centered above the bar
        return new MPPointF(-(getWidth() / 2f), -getHeight() - 20);
    }

    // Optional: Customize the appearance of the MarkerView
    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        // Set a background color for the marker view
        int bgColor = ContextCompat.getColor(getContext(), R.color.transparent);
        canvas.drawColor(bgColor);

        // Call the super method to draw the marker view
        super.draw(canvas, posX, posY);
    }
}


//
//    @Override
//    public void refreshContent(Entry e, Highlight highlight) {
//        // Get the x-index of the entry
//        int xIndex = (int) e.getX();
//
//
//        // Check if the x-index is within the bounds of the player list
//        if (xIndex >= 0 && xIndex < list.size()) {
//            Player player = list.get(xIndex);
//
//            tvName.setText(player.getName());
//            if (!player.getEmojiUrl().isEmpty()) {
//                Glide.with(getContext()).load(player.getEmojiUrl()).placeholder(R.drawable.face_inactive)
//                        .apply(RequestOptions.circleCropTransform()).into(pImage);
//            }
//            String value = String.valueOf((int) e.getY());
//            String valueWithBooking = value + " bookings";
//            tvValue.setText(valueWithBooking);
//        }
//
//        super.refreshContent(e, highlight);
//    }
