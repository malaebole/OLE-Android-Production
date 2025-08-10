package ae.oleapp.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerInfo;

public class OleNormalPreviewFieldView extends FrameLayout {

    private TextView tvName;
    private ImageView imageView;
    private ImageView profileImgVu;
    private float dX, dY;
    private int screenWidth, screenHeight;
    private RelativeLayout parentView;
    private OlePlayerInfo olePlayerInfo;
    private String tShirt = "";
    private final Context context;
    private NormalPreviewFieldViewCallback previewFieldACallback;
    private NormalPreviewFieldItemCallback previewFieldItemCallback;
    float CLICK_ACTION_THRESHOLD = 10.0f;
    float startX = 0, startY = 0;

    public OleNormalPreviewFieldView(@NonNull Context context) {
        super(context);
        initView(context, null);
        this.context = context;
    }

    public OleNormalPreviewFieldView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
        this.context = context;
    }

    public OleNormalPreviewFieldView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        this.context = context;
    }

    public void setPreviewFieldACallback(NormalPreviewFieldViewCallback previewFieldACallback) {
        this.previewFieldACallback = previewFieldACallback;
    }

    public void setPreviewFieldItemCallback(NormalPreviewFieldItemCallback previewFieldItemCallback) {
        this.previewFieldItemCallback = previewFieldItemCallback;
    }

    public void setParentViewSize(int screenWidth, int screenHeight) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        setOnTouchListener(onTouchListener);
    }

    public ImageView getProfileImgVu() {
        return profileImgVu;
    }

    public OlePlayerInfo getPlayerInfo() {
        return olePlayerInfo;
    }

    public void setPlayerInfo(OlePlayerInfo olePlayerInfo, String tShirt) {
        this.olePlayerInfo = olePlayerInfo;
        this.tShirt = tShirt;
        populateData();
    }

    private void initView(Context context, AttributeSet attrs) {
        View.inflate(getContext(), R.layout.olenormal_preview_field_view, this);

        imageView = findViewById(R.id.player_image);
        profileImgVu = findViewById(R.id.profile_img_vu);
        tvName = findViewById(R.id.tv_name);

        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.NormalPreviewFieldView, 0, 0);
        imageView.setImageResource(attributes.getResourceId(R.styleable.NormalPreviewFieldView_shirt_image, 0));
        profileImgVu.setImageResource(attributes.getResourceId(R.styleable.NormalPreviewFieldView_profile_image, 0));
        tvName.setText(attributes.getString(R.styleable.NormalPreviewFieldView_player_name));
    }

    private void populateData() {
        if (olePlayerInfo != null) {
            tvName.setText(olePlayerInfo.getName());
            Glide.with(context).load(olePlayerInfo.getPhotoUrl()).into(profileImgVu);
            if (tShirt.isEmpty()) {
                imageView.setImageResource(R.drawable.black_shirt);
            }
            else {
                Resources resources = context.getResources();
                imageView.setImageResource(resources.getIdentifier(tShirt, "drawable", context.getPackageName()));
            }
        }
        else {
            tvName.setText("Name");
            imageView.setImageResource(0);
        }
    }

    public void setImage(String tShirt) {
        this.tShirt = tShirt;
        if (tShirt.isEmpty()) {
            imageView.setImageResource(R.drawable.player_active);
        }
        else {
            Resources resources = context.getResources();
            imageView.setImageResource(resources.getIdentifier(tShirt, "drawable", context.getPackageName()));
        }
    }

    OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            float newX, newY;
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    newX = 0;
                    newY = 0;
                    startX = view.getX();
                    startY = view.getY();
                    dX = view.getX() - event.getRawX();
                    dY = view.getY() - event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:

                    newX = event.getRawX() + dX;
                    newY = event.getRawY() + dY;

                    float halfW = view.getWidth()/2;
                    float halfH = view.getHeight()/2;

                    if ((newX <= 0-halfW || newX >= screenWidth - halfW) || (newY <= 0-halfH || newY >= screenHeight - halfH)) {
                        return true;
                    }

                    view.animate()
                            .x(event.getRawX() + dX)
                            .y(event.getRawY() + dY)
                            .setDuration(0)
                            .start();
                    break;
                case MotionEvent.ACTION_UP:
                    if (shouldClickActionWork(startX, view.getX(), startY, view.getY())) {
                        previewFieldItemCallback.itemClicked(olePlayerInfo);
                        return true;
                    }
                    newX = event.getRawX() + dX;
                    newY = event.getRawY() + dY;

                    if (previewFieldACallback != null) {
                        previewFieldACallback.didEndDrag(olePlayerInfo, newX, newY);
                    }

                    break;
                default:
                    return false;
            }
            return true;
        }
    };

    private boolean shouldClickActionWork(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return (CLICK_ACTION_THRESHOLD > differenceX) && (CLICK_ACTION_THRESHOLD > differenceY);
    }

    public interface NormalPreviewFieldViewCallback {
        void didStartDrag();
        void didEndDrag(OlePlayerInfo olePlayerInfo, float newX, float newY);
    }

    public interface NormalPreviewFieldItemCallback {
        void itemClicked(OlePlayerInfo olePlayerInfo);
    }
}
