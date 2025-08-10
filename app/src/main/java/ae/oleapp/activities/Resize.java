package ae.oleapp.activities;

import android.content.Context;

public class Resize {
    public static int mDeviceHeight = 0;
    public static int mDeviceWidth = 1080;
    public static double ratio = 1.0d;
    int mBaseWidth = 1080;

    public Resize(Context context) {
        int i = context.getResources().getDisplayMetrics().widthPixels;
        mDeviceWidth = i;
        ratio = ((double) i) / ((double) this.mBaseWidth);
    }

    public Resize() {
        ratio = ((double) mDeviceWidth) / ((double) 1080);
    }

    public int size(int before) {
        return (int) Math.floor(ratio * ((double) before));
    }
}