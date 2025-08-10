package ae.oleapp.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ae.oleapp.R;

public class OleCustomTabView extends LinearLayout {

    public ImageView iconVu;
    public TextView tvTitle;

    public OleCustomTabView(Context context) {
        super(context);
        LayoutInflater  mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.olecustom_tab, this, true);
        iconVu = findViewById(R.id.img_vu);
        tvTitle = findViewById(R.id.tv_title);
    }
}
