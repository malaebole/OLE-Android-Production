package ae.oleapp.player;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import ae.oleapp.R;
import ae.oleapp.activities.MyMarkerView;
import ae.oleapp.adapters.OlePlayerFieldAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityPlayerClubDetailBinding;
import ae.oleapp.dialogs.OleClubRateDialog;
import ae.oleapp.dialogs.PopUpClass;
import ae.oleapp.models.Club;
import ae.oleapp.models.CustomGraphMarker;
import ae.oleapp.models.Day;
import ae.oleapp.models.Field;
import ae.oleapp.models.GameTeam;
import ae.oleapp.models.GiftData;
import ae.oleapp.models.GiftWinner;
import ae.oleapp.models.GraphData;
import ae.oleapp.models.OleShiftTime;
import ae.oleapp.models.Player;
import ae.oleapp.owner.OleBookingActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OlePlayerClubDetailActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityPlayerClubDetailBinding binding;
    private Club club;
    private final List<Field> fieldList = new ArrayList<>();
    private OlePlayerFieldAdapter adapter;
    private GiftData giftData;
    private GiftWinner giftWinner;
    private KProgressHUD hud;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityPlayerClubDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.club_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String str = bundle.getString("club", "");
            Gson gson = new Gson();
            club = gson.fromJson(str, Club.class);
        }

        binding.relNotes.setVisibility(View.INVISIBLE);
        populateData();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);

        adapter = new OlePlayerFieldAdapter(getContext(), fieldList);
        adapter.setOnItemClick(clickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.bar.backBtn.setOnClickListener(this);
        binding.mapVu.setOnClickListener(this);
        binding.rateVu.setOnClickListener(this);
        binding.btnCall.setOnClickListener(this);
        binding.btnFav.setOnClickListener(this);
        binding.infoIcon.setOnClickListener(this);

        getAllFields(true);

    }

    OlePlayerFieldAdapter.OnItemClickListener clickListener = new OlePlayerFieldAdapter.OnItemClickListener() {
        @Override
        public void itemClicked(View view, int position) {
            Field field = fieldList.get(position);
            Intent intent = new Intent(getContext(), OleBookingActivity.class);
            intent.putExtra("field_id", field.getId());
            Gson gson = new Gson();
            intent.putExtra("club", gson.toJson(club));
            startActivity(intent);
        }
    };

    private void populateData() {
        binding.tvName.setText(club.getName());
        String distance;
        if (club.getDistance().isEmpty()) {
            distance = getResources().getString(R.string.km_place, "0");
        }
        else {
            distance = getResources().getString(R.string.km_place, club.getDistance());
        }
        binding.tvLoc.setText(String.format("%s - %s", distance, club.getCity().getName()));
        if (club.getRating().isEmpty()) {
            binding.tvRate.setText("0.0");
        }
        else {
            binding.tvRate.setText(club.getRating());
        }
        if (club.getStartPrice().isEmpty()) {
            binding.tvPrice.setText(String.format("0 %s", club.getCurrency()));
        }
        else {
            binding.tvPrice.setText(String.format("%s %s", club.getStartPrice(), club.getCurrency()));
        }
        if (club.getFavorite().equalsIgnoreCase("1")) {
            binding.btnFav.setImageResource(R.drawable.fav_green);
        }
        else {
            binding.btnFav.setImageResource(R.drawable.club_unfav);
        }
        if (club.getFavoriteCount().isEmpty()) {
            binding.tvFavCount.setText("0");
        }
        else {
            binding.tvFavCount.setText(club.getFavoriteCount());
        }
        if (club.getContact().isEmpty()) {
            binding.btnCall.setVisibility(View.GONE);
        }
        else {
            binding.btnCall.setVisibility(View.VISIBLE);
        }
        String todayName = Functions.getDayName(new Date());
        Day day = checkDayExist(todayName);
        if (day != null) {
            if (day.getShifting().size() == 2) {
                binding.tvShift1.setVisibility(View.VISIBLE);
                OleShiftTime time1 = day.getShifting().get(0);
                binding.tvShift1.setText(time1.getOpening()+" - "+time1.getClosing());
                binding.tvShift2.setVisibility(View.VISIBLE);
                OleShiftTime time2 = day.getShifting().get(1);
                binding.tvShift2.setText(time2.getOpening()+" - "+time2.getClosing());
            }
            else if (day.getShifting().size() == 1) {
                binding.tvShift1.setVisibility(View.VISIBLE);
                OleShiftTime time1 = day.getShifting().get(0);
                binding.tvShift1.setText(time1.getOpening()+" - "+time1.getClosing());
                binding.tvShift2.setVisibility(View.GONE);
            }
        }
        else {
            binding.tvShift1.setVisibility(View.GONE);
            binding.tvShift2.setVisibility(View.GONE);
        }

        if (!club.getLatitude().equalsIgnoreCase("") && !club.getLongitude().equalsIgnoreCase("")) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            String url = "https://maps.google.com/maps/api/staticmap?center=" + club.getLatitude() + "," + club.getLongitude() + "&zoom=16&size="+width+"x300&sensor=false&key="+getString(R.string.maps_api_key);
            Glide.with(getApplicationContext()).load(url).into(binding.mapVu);
        }
    }

    private void populateDiscountData(JSONObject obj) throws JSONException {
        binding.relNotes.setVisibility(View.VISIBLE);
        String type = obj.getString("discount_type");
        String discount = obj.getString("discount");
        String playerBookings = obj.getString("player_bookings");
        String remainingBookings = obj.getString("remaining_bookings");
        String targetBookings = obj.getString("total_target");
        String currency = obj.getString("currency");
        String discountExpiry = obj.getString("discount_expiry");

        if (!playerBookings.equalsIgnoreCase("") && !playerBookings.equalsIgnoreCase("0")) {
            binding.stepView.setCompletedPosition(Integer.parseInt(playerBookings)-1);
            binding.stepView.setProgressColorIndicator(getResources().getColor(R.color.greenColor));
        }
        else {
            binding.stepView.setProgressColorIndicator(getResources().getColor(R.color.separatorColor));
        }
        if (!targetBookings.equalsIgnoreCase("")) {
            int value = Integer.parseInt(targetBookings);
            String[] arr = new String[value];
            for (int i = 0; i < value; i++) {
                arr[i] = String.valueOf(i+1);
            }
            binding.stepView.setLabels(arr);
        }
        if (playerBookings.equalsIgnoreCase(targetBookings)) {
            binding.imgVuTick.setImageResource(R.drawable.discount_tick_green);
        }
        else {
            binding.imgVuTick.setImageResource(R.drawable.discount_tick_gray);
        }
        if (remainingBookings.equalsIgnoreCase("0")) {
            binding.tvNotes.setText(R.string.discount_on_next_booking);
        }
        else {
            binding.tvNotes.setText(String.format(getResources().getString(R.string.booking_remaining_discount), remainingBookings, playerBookings, targetBookings));
        }
        if (type.equalsIgnoreCase("percent")) {
            binding.tvPerc.setText(String.format("%s%%", discount));
        }
        else {
            binding.tvPerc.setText(String.format("%s %s", discount, currency));
        }
        binding.tvExpiry.setText(getString(R.string.expired_on_place, discountExpiry));
    }

    private Day checkDayExist(String dayName) {
        for (Day day: club.getTimings()) {
            if (day.getDayName().equalsIgnoreCase(dayName)) {
                return day;
            }
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.mapVu) {
            mapClicked();
        }
        else if (v == binding.rateVu) {
            rateVuClicked();
        }
        else if (v == binding.btnCall) {
            callClicked();
        }
        else if (v == binding.btnFav) {
            favClicked();
        }
        else if (v == binding.infoIcon) {
            infoIconClicked(v);

        }
    }

    private void mapClicked() {
        if (club != null && !club.getLatitude().equalsIgnoreCase("") && !club.getLongitude().equalsIgnoreCase("")) {
            String uri = "http://maps.google.com/maps?daddr=" + club.getLatitude() + "," + club.getLongitude();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        }
    }

    private void rateVuClicked() {
        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        OleClubRateDialog rateDialog = new OleClubRateDialog(getContext(), club.getId());
        rateDialog.show();
    }

    private void callClicked() {
        makeCall(club.getContact());
    }

    private void favClicked() {
        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        if (club.getFavorite().equalsIgnoreCase("1")) {
            addRemoveFavClub(club.getId(), "0");
        }
        else {
            addRemoveFavClub(club.getId(), "1");
        }
    }
    private void infoIconClicked(View view) {
        //binding.tvGiftType.setText(giftData.getGiftName());
        PopUpClass popUpClass = new PopUpClass();
        popUpClass.showPopupWindow(view, false, giftData.getDetails());

    }

    private void addRemoveFavClub(String clubId, String status) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        addRemoveFavAPI(clubId, status, "club", new BaseActivity.FavCallback() {
            @Override
            public void addRemoveFav(boolean success, String msg) {
                Functions.hideLoader(hud);
                if (success) {
                    Functions.showToast(getContext(), msg, FancyToast.SUCCESS);
                    club.setFavorite(status);
                    if (status.equalsIgnoreCase("1")) {
                        binding.btnFav.setImageResource(R.drawable.fav_green);
                    }
                    else {
                        binding.btnFav.setImageResource(R.drawable.club_unfav);
                    }
                }
                else {
                    Functions.showToast(getContext(), msg, FancyToast.ERROR);
                }
            }
        });
    }

    private void getAllFields(boolean isLoader) {
        hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getAllFields(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), club.getId());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               // Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            fieldList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                fieldList.add(gson.fromJson(arr.get(i).toString(), Field.class));
                            }
                            adapter.notifyDataSetChanged();
                            JSONObject obj = object.getJSONObject("player_discount");
                            if (obj.length() > 0) {
                                populateDiscountData(obj);
                            }
                            else {
                                binding.relNotes.setVisibility(View.GONE);
                            }
                            JSONObject giftObj = object.getJSONObject("gift_data");
                            giftData = new Gson().fromJson(giftObj.toString(), GiftData.class);
                            JSONObject winnerObj = object.getJSONObject("gift_winner");
                            giftWinner = new Gson().fromJson(winnerObj.toString(), GiftWinner.class);

                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    showGraph();
                                }
                            };

                            // Post the Runnable with a delay
                            handler.postDelayed(runnable, 200);
                        }


                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void showGraph() {

        if (giftData.getGiftId() != null){
            downloadImages(true);
            binding.graphLay.setVisibility(View.VISIBLE);
            Context context = getContext();
            BarChart barChart = binding.barChart;
            List<Player> players = giftData.getPlayers();
            List<BarEntry> entries = new ArrayList<>();

            //Configure bar chart settings
            barChart.getDescription().setEnabled(false);
            //barChart.setDrawValueAboveBar(false);
            barChart.setPinchZoom(false);
            barChart.setDoubleTapToZoomEnabled(false);
            barChart.setDragEnabled(true);
            barChart.setScaleEnabled(false);
            barChart.setDrawGridBackground(false);
            barChart.getLegend().setEnabled(false);
            barChart.setTouchEnabled(false);


            XAxis xAxis = barChart.getXAxis();
            xAxis.setEnabled(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setLabelRotationAngle(0);

            // Adjust label density
            int maxLabelCount = players.size() > 5 ? 6 : players.size();
            xAxis.setLabelCount(maxLabelCount);
            xAxis.setGranularity(1f);

            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.setAxisMinimum(0f);
            leftAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value);
                }
            });
            leftAxis.setAxisMaximum(50f);
            leftAxis.setGranularity(5f);

            barChart.getAxisRight().setEnabled(false);


            for (int i = 0; i < players.size(); i++) {
                Player dataObject = players.get(i);
                float value = Float.parseFloat(dataObject.getTotalBookings());
                String label = dataObject.getName();
                entries.add(new BarEntry(i, value, label));
            }


            BarDataSet dataSet = new BarDataSet(entries, "Bar Data");
            dataSet.setValueTextSize(10f);
            dataSet.setColors(Color.parseColor("#49D483"));
            dataSet.setDrawValues(false);
            dataSet.setHighLightColor(Color.parseColor("#49D483"));


            BarData barData = new BarData(dataSet);
            barData.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value);
                }
            });

            barChart.setData(barData);
            barChart.setDrawMarkers(true);

            CustomGraphMarker markerView = new CustomGraphMarker(context, R.layout.custom_gift_marker, players);
            markerView.setChartView(barChart);
            barChart.setMarker(markerView);

            barChart.animateX(0);
            barChart.setVisibleXRangeMaximum(6);
            float fixedBarWidth = 0.3f;
            barChart.getBarData().setBarWidth(fixedBarWidth);
            barChart.moveViewToX(entries.size() - 6);

            String[] xAxisValues = new String[players.size()];
            for (int i = 0; i < players.size(); i++) {
                String text = "";
                if (i == 0) {
                    text = "st";
                } else if (i == 1) {
                    text = "nd";
                } else if (i == 2) {
                    text = "rd";
                } else if (i == 3 && i <= 4) {
                    text = "th";
                } else {
                    text = "th";
                }
                xAxisValues[i] = (i + 1) + text;
            }
            xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
            List<Highlight> allHighlights = new ArrayList<>(); // Create a list to accumulate highlights

            for (int i = 0; i < barData.getDataSetCount(); i++) {
                IBarDataSet dataSett = barData.getDataSetByIndex(i);

                for (int entryIndex = 0; entryIndex < dataSett.getEntryCount(); entryIndex++) {
                    float xValue = dataSett.getEntryForIndex(entryIndex).getX();
                    Highlight highlight = new Highlight(xValue, i, entryIndex);
                    allHighlights.add(highlight); // Accumulate the highlights
                }
            }

            //Apply all the accumulated highlights
            barChart.highlightValues(allHighlights.toArray(new Highlight[0]));
            barChart.invalidate();


            if (giftData.getPhoto() !=null){
                Glide.with(getApplicationContext()).load(giftData.getPhoto()).into(binding.imgVu);
            }
            binding.tvGiftType.setText(giftData.getGiftName());
            binding.tvTitle.setText(giftData.getName());
            binding.tvDate.setText(giftData.getEndDate());
            handler.post(updateTimerRunnable);
            Functions.hideLoader(hud);
        }
        if (giftWinner.getGiftId() != null){
            binding.graphLay.setVisibility(View.GONE);
            binding.giftWinnerLay.setVisibility(View.VISIBLE);
            if (giftWinner.getEmojiUrl() !=null){
                Glide.with(getApplicationContext()).load(giftWinner.getEmojiUrl()).into(binding.emojiImgVu);
            }
            if (giftWinner.getBibUrl() !=null){
                Glide.with(getApplicationContext()).load(giftWinner.getBibUrl()).into(binding.shirtImgVu);
            }
            binding.giftTitle.setText(giftWinner.getGiftName());
            binding.playerName.setText(giftWinner.getPlayerName());
            binding.giftDetails.setText(giftWinner.getMessage());
            Functions.hideLoader(hud);
        }
        Functions.hideLoader(hud);

    }

    private void downloadImages(Boolean isLoader) {
        if (giftData == null || giftData.getPlayers().isEmpty()) {
            return;
        }
        final CountDownLatch countDownLatch = new CountDownLatch(giftData.getPlayers().size());
        for (Player player : giftData.getPlayers()) {
            if (!player.getEmojiUrl().isEmpty()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(player.getEmojiUrl());
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.connect();

                            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                InputStream inputStream = connection.getInputStream();
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                player.setImage(bitmap);
                            }

                            connection.disconnect();
                        } catch (Exception ignored) {
                            //Exception
                        } finally {
                            countDownLatch.countDown();
                        }
                    }
                }).start();
            } else {
                countDownLatch.countDown();
            }
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Now you have downloaded and set all the images in their original form
        // Perform any further operations as needed.
    }



//    private void downloadImages() {
//        if (giftData == null || giftData.getPlayers().isEmpty()) {
//            return;
//        }
//        final CountDownLatch countDownLatch = new CountDownLatch(giftData.getPlayers().size());
//
//        for (Player player : giftData.getPlayers()) {
//            if (!player.getEmojiUrl().isEmpty()) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            URL url = new URL(player.getEmojiUrl());
//                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                            connection.connect();
//
//                            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                                InputStream inputStream = connection.getInputStream();
//                                Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
//                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                                originalBitmap.compress(Bitmap.CompressFormat.PNG, 10, outputStream);
//                                byte[] compressedBytes = outputStream.toByteArray();
//                                Bitmap compressedBitmap = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.length);
//                                player.setImage(compressedBitmap);
//                            }
//
//                            connection.disconnect();
//                        } catch (Exception e) {
//                            // Handle any exceptions
//                        } finally {
//                            countDownLatch.countDown();
//                        }
//                    }
//                }).start();
//            } else {
//                countDownLatch.countDown();
//            }
//        }
//
//        try {
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
////        runOnUiThread(new Runnable() {
////            @Override
////            public void run() {
////                Functions.hideLoader(hud);
////                showGraph();
////            }
////        });
//    }





//    private void autoHighlightAllBars(BarChart barChart) {
//        BarData barData = barChart.getBarData();
//
//        if (barData != null) {
//            List<Highlight> allHighlights = new ArrayList<>(); // Create a list to accumulate highlights
//
//            for (int dataSetIndex = 0; dataSetIndex < barData.getDataSetCount(); dataSetIndex++) {
//                IBarDataSet dataSet = barData.getDataSetByIndex(dataSetIndex);
//
//                for (int entryIndex = 0; entryIndex < dataSet.getEntryCount(); entryIndex++) {
//                    float xValue = dataSet.getEntryForIndex(entryIndex).getX();
//                    Highlight highlight = new Highlight(xValue, dataSetIndex, entryIndex);
//                    allHighlights.add(highlight); // Accumulate the highlights
//                }
//            }
//
//            // Apply all the accumulated highlights
//            barChart.highlightValues(allHighlights.toArray(new Highlight[0]));
//            barChart.invalidate();
//        }
//    }



    private final Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            updateTimerDisplay();
            handler.postDelayed(this, 1000); // Update every 1 second
        }
    };

    private void updateTimerDisplay(){
        String inputDateStr = giftData.getEndDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        try {
            Date inputDate = dateFormat.parse(inputDateStr);
            Date currentDate = new Date();

            long timeDifferenceMillis = inputDate.getTime() - currentDate.getTime();

            long daysRemaining = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis);
            binding.days.setText(String.valueOf(daysRemaining));

            long hoursRemaining = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis) % 24;
            binding.h1.setText(Character.toString((hoursRemaining < 10) ? '0' : String.valueOf(hoursRemaining).charAt(0)));
            binding.h2.setText(Character.toString((hoursRemaining < 10) ? String.valueOf(hoursRemaining).charAt(0) : String.valueOf(hoursRemaining).charAt(1)));

            long minutesRemaining = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis) % 60;
            binding.m1.setText(Character.toString((minutesRemaining < 10) ? '0' : String.valueOf(minutesRemaining).charAt(0)));
            binding.m2.setText(Character.toString((minutesRemaining < 10) ? String.valueOf(minutesRemaining).charAt(0) : String.valueOf(minutesRemaining).charAt(1)));

            long secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceMillis) % 60;
            binding.s1.setText(Character.toString((secondsRemaining < 10) ? '0' : String.valueOf(secondsRemaining).charAt(0)));
            binding.s2.setText(Character.toString((secondsRemaining < 10) ? String.valueOf(secondsRemaining).charAt(0) : String.valueOf(secondsRemaining).charAt(1)));

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

//    private void autoHighlightFirstBars(BarChart barChart) {
//        BarData barData = barChart.getBarData();
//
//        if (barData != null) {
//            for (int dataSetIndex = 0; dataSetIndex < barData.getDataSetCount(); dataSetIndex++) {
//                IBarDataSet dataSet = barData.getDataSetByIndex(dataSetIndex);
//
//                if (dataSet.getEntryCount() > 0) {
//                    float xValue = dataSet.getEntryForIndex(0).getX();
//                    barChart.highlightValue(new Highlight(xValue, dataSetIndex, 0), false);
//                }
//            }
//
//            // Refresh the chart to apply the highlights
//            barChart.invalidate();
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the callback to stop updating when the activity is destroyed
        handler.removeCallbacks(updateTimerRunnable);
    }



}
