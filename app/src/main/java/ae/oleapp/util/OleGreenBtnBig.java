package ae.oleapp.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ae.oleapp.R;

public class OleGreenBtnBig extends FrameLayout {

    private final Context context;
    private TextView tvTitle;
    private ImageView imageView;


    public OleGreenBtnBig(@NonNull Context context) {
        super(context);
        initView(context, null);
        this.context = context;
    }

    public OleGreenBtnBig(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
        this.context = context;
    }

    public OleGreenBtnBig(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        this.context = context;
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setTitle(int title) {
        tvTitle.setText(context.getString(title));
    }

    public void setBackgroundImage(boolean isPadel) {
        if (isPadel) {
            imageView.setImageResource(R.drawable.padel_green_btn);
        } else {
            imageView.setImageResource(R.drawable.green_btn_bg);
        }
    }

    private void initView(Context context, AttributeSet attrs) {
        View.inflate(getContext(), R.layout.olegreen_btn_big, this);

        tvTitle = findViewById(R.id.tv_title);
        imageView = findViewById(R.id.img_vu);
        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.GreenBtnBig, 0, 0);
        tvTitle.setText(attributes.getString(R.styleable.GreenBtnBig_btn_title));
        tvTitle.setTextAppearance(context, attributes.getResourceId(R.styleable.GreenBtnBig_btn_text_appearance, 0));
        tvTitle.setTextColor(attributes.getColor(R.styleable.GreenBtnBig_btn_title_color, 0));

        if (isInEditMode()) {
            imageView.setImageResource(attributes.getResourceId(R.styleable.GreenBtnBig_btn_image, 0));
        }
        else {
            if (Functions.getPrefValue(context, Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
                imageView.setImageResource(R.drawable.padel_green_btn);
            } else {
                imageView.setImageResource(R.drawable.green_btn_bg);
            }
        }
    }
}
