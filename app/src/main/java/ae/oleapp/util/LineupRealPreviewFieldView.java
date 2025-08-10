package ae.oleapp.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.activities.Resize;
import ae.oleapp.models.LineupGlobalPlayers;
import ae.oleapp.models.LineupRealPlayerInfo;
import ae.oleapp.models.PlayerInfo;
import ae.oleapp.models.PlayerStatus;

public class LineupRealPreviewFieldView extends FrameLayout {
    private TextView tvName, tvStatus;
    private MaterialCardView tvNameVu;
    private ImageView imageView, captainIc;
    private ImageView profileImgVu;
    private SeekBar seekBar;
    private float dX, dY;
    private int screenWidth, screenHeight;
    private RelativeLayout statusVu;
    private LineupGlobalPlayers playerInfo;
    private String shirtUrl = "", gkShirtUrl = "";
    private final Context context;
    private LineupRealPreviewFieldViewCallback lineupRealPreviewFieldViewCallback;
    private LineupRealPreviewFieldItemCallback lineupRealPreviewFieldItemCallback;
    float CLICK_ACTION_THRESHOLD = 10.0f;
    float after_x;
    float after_y;
    int before_x;
    int before_y;
    float f52dx;
    float f53dy;
    public List<Integer> locationXList;
    public List<Integer> locationYList;
    int move_magnet = this.move_magnet_px;
    int move_magnet_px = 40;
    int move_x;
    int move_y;
    float touch_x;
    float touch_y;
    Resize f54re;
    int usuri,i;
    float halfW;
    float halfH;
    boolean istouchListerActive = false;
    //    private final Handler uiHandler = new Handler(Looper.getMainLooper());
//    private Socket socket;
//    private final boolean isMover = true;
    //public String player_Id = "0";
    public String gameid="";





    public LineupRealPreviewFieldView(@NonNull Context context) {
        super(context);
        initView(context, null);
        this.context = context;
    }

    public LineupRealPreviewFieldView(@NonNull Context context, LineupGlobalPlayers info) {
        super(context);
        this.context = context;
        this.playerInfo = info;
    }


    public LineupRealPreviewFieldView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
        this.context = context;
    }

    public LineupRealPreviewFieldView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        this.context = context;
    }

    public void setPreviewFieldACallback(LineupRealPreviewFieldViewCallback lineupRealPreviewFieldViewCallback) {
        this.lineupRealPreviewFieldViewCallback = lineupRealPreviewFieldViewCallback;
    }

    public void setPreviewFieldItemCallback(LineupRealPreviewFieldItemCallback lineupRealPreviewFieldItemCallback) {
        this.lineupRealPreviewFieldItemCallback = lineupRealPreviewFieldItemCallback;
    }

    public boolean isIstouchListerActive() {
        return istouchListerActive;
    }

    //Screen boundries
    public void setParentViewSize(int screenWidth, int screenHeight) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        setOnTouchListener(onTouchListener);
        istouchListerActive = true;
    }



    public ImageView getProfileImgVu() {
        return profileImgVu;
    }

    public LineupGlobalPlayers getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayerInfo(LineupGlobalPlayers playerInfo, String shirtUrl, String gkShirtUrl) {
        this.playerInfo = playerInfo;
        this.shirtUrl = shirtUrl;
        this.gkShirtUrl = gkShirtUrl;
        populateData();
    }

    private void initView(Context context, AttributeSet attrs) {
        View.inflate(getContext(), R.layout.preview_field_view, this);
        locationXList = new ArrayList();
        locationYList = new ArrayList();
        Resize resize = new Resize();
        f54re = resize;
        move_magnet = resize.size(move_magnet_px);
        imageView = findViewById(R.id.player_image);
        profileImgVu = findViewById(R.id.profile_img_vu);
        tvName = findViewById(R.id.tv_name);
        tvNameVu = findViewById(R.id.name_vu);
        statusVu = findViewById(R.id.status_vu);
        tvStatus = findViewById(R.id.tv_status);
        captainIc = findViewById(R.id.captain_ic);
        seekBar = findViewById(R.id.skills_progressbar);
        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.PreviewFieldView, 0, 0);
        imageView.setImageResource(attributes.getResourceId(R.styleable.PreviewFieldView_lineupshirt_image, 0));
        profileImgVu.setImageResource(attributes.getResourceId(R.styleable.PreviewFieldView_lineupprofile_image, 0));
        tvName.setText(attributes.getString(R.styleable.PreviewFieldView_lineupplayer_name));


    }

    private void populateData() {
        if (playerInfo != null) {
            statusVu.setVisibility(GONE);
            captainIc.setVisibility(GONE);
            tvStatus.setVisibility(GONE);
            seekBar.setVisibility(GONE);
            String[] arr = playerInfo.getNickName().split(" ");
            if (arr.length > 0) {
                String firstName = arr[0];
                String lastName = arr.length > 1 ? arr[1] : "";
                String fullName = firstName + " " + lastName;
                tvName.setText(fullName);
                //tvName.setText(arr[0]);
                tvName.setSelected(true);
            }
            else {
                tvName.setText(playerInfo.getNickName());
                tvName.setSelected(true);
            }


            if (Functions.getPrefValue(getContext(), Constants.kFaceHide).equalsIgnoreCase("true")){
                tvName.setBackground(getResources().getDrawable(R.drawable.textbgl));
            }else{
                Glide.with(context).load(playerInfo.getEmojiUrl()).into(profileImgVu);
                tvName.setBackground(null);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    tvName.setShadowLayer(0.30f, -2, 2,   getResources().getColor(R.color.blackColor));
                }
                tvNameVu.setBackground(null);
                tvNameVu.setCardBackgroundColor(null);
                tvNameVu.setCardElevation(0);

            }

            if (Functions.getPrefValue(getContext(), Constants.kNameHide).equalsIgnoreCase("true")){
                tvName.setText(playerInfo.getArabicName());
            }else{
                tvName.setText(playerInfo.getNickName());
            }

            if (shirtUrl.isEmpty()) {
                imageView.setImageResource(R.drawable.shirtl);
            }
            else {
                if (playerInfo.getIsGoalkeeper() != null && playerInfo.getIsGoalkeeper().equalsIgnoreCase("1")) {
                    Glide.with(context).load(gkShirtUrl).into(imageView);
                }
                else {
                    Glide.with(context).load(shirtUrl).into(imageView);
                }
            }



        }
        else {
            tvName.setText("Name");
            imageView.setImageResource(0);

        }
    }

    public void setImage(String shirtUrl) {
        this.shirtUrl = shirtUrl;
        if (shirtUrl.isEmpty()) {
            imageView.setImageResource(R.drawable.shirtl);
        }
        else {
            Glide.with(context).load(shirtUrl).into(imageView);
        }
    }


    public void hideShowface(Boolean isHide){
        if (isHide){
            tvName.setBackground(getResources().getDrawable(R.drawable.textbgl));
            Glide.with(context.getApplicationContext()).load("").into(profileImgVu);

        }else{
            Glide.with(context.getApplicationContext()).load(playerInfo.getEmojiUrl()).into(profileImgVu);
            tvName.setBackground(null);
            tvName.setShadowLayer(0.30f, -2, 2,   getResources().getColor(R.color.blackColor));
            tvNameVu.setBackground(null);
            tvNameVu.setCardBackgroundColor(null);
            tvNameVu.setCardElevation(0);

        }
    }

    public void hideShowName(Boolean isHide){
        if (isHide){
            tvName.setText(playerInfo.getArabicName());
        }else{
            tvName.setText(playerInfo.getNickName());
        }
    }


    OnTouchListener onTouchListener = new OnTouchListener() {
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    before_x = (int) view.getX();
                    before_y = (int) view.getY();
                    touch_x = event.getRawX();
                    touch_y = event.getRawY();
                    f52dx = touch_x - view.getX();
                    f53dy = touch_y - view.getY();
                    break;

                case MotionEvent.ACTION_MOVE:

                    halfW = view.getWidth();
                    halfH = view.getHeight();
                    int set_x;
                    move_x = (int) (event.getRawX() - f52dx);
                    move_y = (int) (event.getRawY() - f53dy);
                    after_x = event.getRawX();
                    after_y = event.getRawY();
                    int limitMaxX = (int) (screenWidth-halfW);
                    int limitMinY = f54re.size(0) - move_magnet;
                    int limitMaxY = (int) (screenHeight-halfH); // put this value to align 1250 //660 py theak tha original value halfH thii

                    if (move_x > -move_magnet && move_x < limitMaxX) {
                        usuri = move_magnet - ((f54re.size(540) - (view.getWidth() / 2)) % move_magnet);
                        set_x = move_x;
                        int cc = move_x;

                        for (i = 0; i < 50 && cc > move_magnet; i++) {
                            cc -= move_magnet;
                        }
                        i = (set_x - usuri) + (move_magnet - cc);
                        if (i <= move_magnet - usuri) {
                            i = 0;
                        }

                        view.setX((float) i);

                    }

                    if (move_y > limitMinY) {
                        if (move_y < limitMaxY) {
                            usuri = move_y;
                            set_x = move_y % move_magnet;
                            if (set_x > 0) {
                                usuri = move_y + (move_magnet - set_x);
                            }
                            if (usuri < f54re.size(0) + move_magnet) {
                                usuri = f54re.size(0);
                            }
                            view.setY((float) usuri);

                        }

                    }

                    //((MainActivity)context).cordEmitter(playerInfo.getId(),i, usuri);

                    if (lineupRealPreviewFieldViewCallback != null) {
                        lineupRealPreviewFieldViewCallback.didStartDrag(LineupRealPreviewFieldView.this, playerInfo, i, usuri);
                    }

                    //Log.d("PlayerID", player_Id);
                    // Log.d("FINALX", String.valueOf(move_x)); //Final X co-ordinates
                    // Log.d("FINALLY", String.valueOf(move_y)); //Final Y co-ordinates

                    break;

                case MotionEvent.ACTION_UP:
                    if (shouldClickActionWork(before_x, view.getX(), before_y, view.getY())) {
                        if (lineupRealPreviewFieldItemCallback != null) {
                            lineupRealPreviewFieldItemCallback.itemClicked(playerInfo);
                        }
                        return true;
                    }
                    i = (int) (event.getRawX() - f52dx);
                    usuri = (int) (event.getRawY() - f53dy);


                    if (i < 0) {
                        i = 0;
                    } else if (i > screenWidth - halfW) {
                        i = (int) (screenWidth - halfW);

                    }
                    if (usuri < 0) {
                        usuri = 0;
                    } else if (usuri > screenHeight - halfH) {
                        usuri = (int) (screenHeight - halfH);
                    }

                    if (lineupRealPreviewFieldViewCallback != null) {
                        lineupRealPreviewFieldViewCallback.didEndDrag(LineupRealPreviewFieldView.this, playerInfo, i, usuri);
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

    public void moveImage(float x, float y) {
        this.setX(x);
        this.setY(y);
    }

    public void setGameId(String gameId){
        this.gameid = gameId;
    }

    public String getGameID(){
        return gameid;
    }


    public interface LineupRealPreviewFieldViewCallback {
        void didStartDrag(LineupRealPreviewFieldView view, LineupGlobalPlayers playerInfo, float i, float usuri);
        void didEndDrag(LineupRealPreviewFieldView view, LineupGlobalPlayers playerInfo, float i, float usuri);
    }

    public interface LineupRealPreviewFieldItemCallback {
        void itemClicked(LineupGlobalPlayers playerInfo);
    }
}
