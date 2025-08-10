package ae.oleapp.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import ae.oleapp.R;

public class MyMarkerView extends MarkerView {

    private final TextView tvContent;
    private final TextView tvFilterType;
    private final TextView tvAmount;
    private final String type;

    public MyMarkerView(Context context, int layoutResource,String filterName) {
        super(context, layoutResource);
        // Get reference to the TextView in the custom marker layout
        tvContent = findViewById(R.id.tv_name);
        tvFilterType = findViewById(R.id.tv_filter_type);
        tvAmount = findViewById(R.id.tv_value);
        type = filterName;

        // Set the width and height of the marker view
        //setDimensions(80, 40);
    }

    // This method is called every time the MarkerView is redrawn
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // Get the value and name from the Entry object
        int value = (int) e.getY();
        String name = e.getData().toString();

        // Set the value and name in the TextView
        tvContent.setText(name);
        tvFilterType.setText(type);
        tvAmount.setText(String.valueOf(value));


        // Call the super method to update the content position
        super.refreshContent(e, highlight);
    }

    // This method is called when the MarkerView is drawn on the screen
    @Override
    public MPPointF getOffset() {
        // Offset the marker view to be centered above the bar
        return new MPPointF(-(getWidth() / 2f), -getHeight());
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
