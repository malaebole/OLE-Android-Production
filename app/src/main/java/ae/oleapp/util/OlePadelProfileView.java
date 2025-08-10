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

import com.bumptech.glide.Glide;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerLevel;

public class OlePadelProfileView extends FrameLayout {

    private final Context context;
    private TextView tvName, tvQmark, tvRank;
    private ImageView imageView;

    public OlePadelProfileView(@NonNull Context context) {
        super(context);
        initView(context, null);
        this.context = context;
    }

    public OlePadelProfileView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
        this.context = context;
    }

    public OlePadelProfileView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        this.context = context;
    }

    public void populateData(String name, String photo, OlePlayerLevel olePlayerLevel, boolean isQMarkHidden) {
        tvName.setText(name);
        if (olePlayerLevel != null && !olePlayerLevel.isEmpty() && !olePlayerLevel.getValue().equalsIgnoreCase("")) {
            tvRank.setVisibility(View.VISIBLE);
            tvRank.setText(String.format("LV: %s", olePlayerLevel.getValue()));
        }
        else {
            tvRank.setVisibility(View.INVISIBLE);
        }
        if (isQMarkHidden) {
            tvQmark.setVisibility(GONE);
            Glide.with(context).load(photo).placeholder(R.drawable.player_active).into(imageView);
        }
        else {
            tvQmark.setVisibility(VISIBLE);
            imageView.setImageResource(0);
            imageView.setImageDrawable(null);
        }
    }

    private void initView(Context context, AttributeSet attrs) {
        View.inflate(getContext(), R.layout.olepadel_profile_view, this);

        imageView = findViewById(R.id.player_image_vu);
        tvName = findViewById(R.id.tv_name);
        tvQmark = findViewById(R.id.tv_q_mark);
        tvRank = findViewById(R.id.tv_rank);

        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.PadelProfileView, 0, 0);
        imageView.setImageResource(attributes.getResourceId(R.styleable.PadelProfileView_p_p_image, 0));
        tvName.setText(attributes.getString(R.styleable.PadelProfileView_p_p_name));
        tvQmark.setVisibility(GONE);
        tvRank.setVisibility(View.INVISIBLE);
    }
}
