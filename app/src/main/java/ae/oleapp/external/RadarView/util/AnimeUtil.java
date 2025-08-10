package ae.oleapp.external.RadarView.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ae.oleapp.external.RadarView.RadarData;
import ae.oleapp.external.RadarView.RadarView;


/**
 * Created by chqiu on 2016/9/5.
 */
public class AnimeUtil {
    private final WeakReference<RadarView> mWeakRadarView;
    private final HashMap<RadarData, ValueAnimator> mAnimes;

    public AnimeUtil(RadarView view) {
        mWeakRadarView = new WeakReference<>(view);
        mAnimes = new HashMap<>();
    }

    public void animeValue(AnimeType type, int duration, RadarData data) {
        if (type == AnimeType.ZOOM) {
            startZoomAnime(duration, data);
        }
    }

    public boolean isPlaying() {
        boolean isPlaying = false;
        for (ValueAnimator anime : mAnimes.values()) {
            isPlaying = anime.isStarted();
            if (isPlaying) {
                break;
            }
        }
        return isPlaying;
    }

    public boolean isPlaying(RadarData data) {
        ValueAnimator anime = mAnimes.get(data);
        return anime != null && anime.isStarted();
    }

    private void startZoomAnime(final int duration, final RadarData data) {
//        final ValueAnimator anime = ValueAnimator.ofFloat(0, 1f);
        final List<Float> values = data.getValue();
        final List<Float> values2 = new ArrayList<>(values);
        for (int i = 0; i < values.size(); i++) {
            values.set(i, values2.get(i));
        }
//        anime.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                RadarView view = mWeakRadarView.get();
//                if (view == null) {
//                    anime.end();
//                } else {
//                    float percent = Float.parseFloat(animation.getAnimatedValue().toString());
//                    for (int i = 0; i < values.size(); i++) {
//                        values.set(i, values2.get(i) * percent);
//                    }
//                    view.invalidate();
//                }
//            }
//        });
//        anime.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mAnimes.remove(data);
//            }
//        });
//        anime.setDuration(duration).start();
//        mAnimes.put(data, anime);
    }

    public enum AnimeType {
        ZOOM, ROTATE
    }
}
