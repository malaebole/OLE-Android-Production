package ae.oleapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ae.oleapp.R;
import ae.oleapp.dialogs.OleCustomAlertDialog;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.UserInfo;
import kotlin.text.Regex;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

public class Functions {

    public static String getPrefValue(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        return prefs.getString(key, "");

    }

    public static String getAppLang(Context context) {
        if (getPrefValue(context, Constants.kAppLang).equalsIgnoreCase("ar")) {
            return Constants.kArLang;
        }
        else {
            return Constants.kEnLang;
        }
    }
    public static void setCurrentPage(Context context, String kCurrentPage) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.kCurrentPage, kCurrentPage);
        editor.apply();
    }

    public static String getCurrentPage(Context context, String kCurrentPage) {
        return getPrefValue(context, Constants.kCurrentPage);
    }


    public static void setSelectedCountry(Context context, String SelectedCountry) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.kSelectedCountry, SelectedCountry);
        editor.apply();
    }

    public static String getSelectedCountry(Context context, String SelectedCountry) {
        return getPrefValue(context, Constants.kSelectedCountry);
    }
    
    public static String getAppLangStr(Context context) {
        return getPrefValue(context, Constants.kAppLang);
    }

    public static void setAppLang(Context context, String lang) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.kAppLang, lang);
        editor.apply();
    }
    public static void setAppLangAr(Context context, String lang) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.kAppLangAr, lang);
        editor.apply();
    }
    public static String getAppLangAr(Context context) {
        return getPrefValue(context, Constants.kAppLangAr);
    }

//    public static void changeLanguage(Context context, String langStr) {
//        if (langStr.equalsIgnoreCase("")) {
//            langStr = "en";
//        }
//        Locale locale = new Locale(langStr);
//        Locale.setDefault(locale);
//        Configuration config = new Configuration();
//        config.locale = locale;
//        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
//    }

    public static void changeLanguage(Context context, String langStr) {
        if (langStr == null || langStr.isEmpty()) {
            langStr = "en";
        }

        Locale locale = new Locale(langStr);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setLocaleForApi24AndAbove(config, locale);
        } else {
            setLocaleForPreApi24(config, locale);
        }

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void setLocaleForApi24AndAbove(Configuration config, Locale locale) {
        config.setLocale(locale);
        LocaleList localeList = new LocaleList(locale);
        LocaleList.setDefault(localeList);
        config.setLocales(localeList);
    }

    private static void setLocaleForPreApi24(Configuration config, Locale locale) {
        config.locale = locale;
    }

    public static void showToast(Context context, String text, int type) {
        try {
            FancyToast.makeText(context, text, FancyToast.LENGTH_LONG, type, false).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast(Context context, String text, int type, int dur) {
        try {
            FancyToast.makeText(context, text, dur, type, false).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAlert(Context context, String title, String desc, OleCustomAlertDialog.OnDismiss dismiss) {
        OleCustomAlertDialog dialog = new OleCustomAlertDialog(context, title, desc);
        dialog.setOnDismiss(dismiss);
        dialog.show();
    }


    public static KProgressHUD showLoader(Context context, String image_processing) {
        return KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
//                .setBackgroundColor(context.getResources().getColor(R.color.blueColorNew))
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
//        GifImageView imageView = new GifImageView(context);
//        imageView.setImageResource(R.drawable.loader_hud);
//        return KProgressHUD.create(context)
//                .setCustomView(imageView)
//                .setSize((int)context.getResources().getDimension(R.dimen._40sdp), (int)context.getResources().getDimension(R.dimen._40sdp))
//                .setCancellable(false)
//                .setAnimationSpeed(2)
//                .setDimAmount(0.5f)
//                .setBackgroundColor(context.getResources().getColor(R.color.blueColor))
//                .show();
    }
    public static KProgressHUD showLoader(Context context) {
        return KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
//                .setBackgroundColor(Color.parseColor("#18707B"))
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

//    public void showLoader(Context context) {
//        KProgressHUD hud = KProgressHUD.create(context)
//                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setLabel("Loading...")
//                .setCancellable(false)
//                .setAnimationSpeed(2)
//                .setDimAmount(0.5f);
//
//        if (!((Activity) context).isFinishing()) {
//            hud.show();
//        }
//    }



    public static void hideLoader(KProgressHUD hud) {
        if (hud != null) {
            hud.dismiss();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isArabic(String text) {
        for (char charac : text.toCharArray()) {
            if (Character.UnicodeBlock.of(charac) == Character.UnicodeBlock.ARABIC) {
                return true;
            }
        }
        return false;
    }

    public static String getDayName(Date date) {
        SimpleDateFormat  dateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    public static void saveUserinfo(Context context, UserInfo info) {
        Gson gson = new Gson();
        String str = gson.toJson(info);
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.kUserInfo, str);
        editor.apply();
    }

    public static UserInfo getUserinfo(Context context) {
        Gson gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        String str = prefs.getString(Constants.kUserInfo, "");
        return gson.fromJson(str, UserInfo.class);
    }

    public static List<Date> getNextDays(int count) {
        List<Date> arr = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Date dt = new Date();
        calendar.setTime(dt);
        arr.add(dt);
        for (int i = 0; i < count; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            arr.add(calendar.getTime());
        }
        return arr;
    }

    public static List<OleKeyValuePair> getDays(Context context) {
        List<OleKeyValuePair> pairList = new ArrayList<>();
        pairList.add(new OleKeyValuePair("1", context.getString(R.string.saturday)));
        pairList.add(new OleKeyValuePair("2", context.getString(R.string.sunday)));
        pairList.add(new OleKeyValuePair("3", context.getString(R.string.monday)));
        pairList.add(new OleKeyValuePair("4", context.getString(R.string.tuesday)));
        pairList.add(new OleKeyValuePair("5", context.getString(R.string.wednesday)));
        pairList.add(new OleKeyValuePair("6", context.getString(R.string.thursday)));
        pairList.add(new OleKeyValuePair("7", context.getString(R.string.friday)));
        return pairList;
    }

    public static String getDayIdByName(String dayName) {
        String day = "";
        switch (dayName) {
            case "saturday":
                day = "1";
                break;
            case "sunday":
                day = "2";
                break;
            case "monday":
                day = "3";
                break;
            case "tuesday":
                day = "4";
                break;
            case "wednesday":
                day = "5";
                break;
            case "thursday":
                day = "6";
                break;
            case "friday":
                day = "7";
                break;
        }
        return day;
    }

    public static String getDayById(Context context, String dayId) {
        String day = "";
        switch (dayId) {
            case "1":
                day = context.getResources().getString(R.string.saturday);
                break;
            case "2":
                day = context.getResources().getString(R.string.sunday);
                break;
            case "3":
                day = context.getResources().getString(R.string.monday);
                break;
            case "4":
                day = context.getResources().getString(R.string.tuesday);
                break;
            case "5":
                day = context.getResources().getString(R.string.wednesday);
                break;
            case "6":
                day = context.getResources().getString(R.string.thursday);
                break;
            case "7":
                day = context.getResources().getString(R.string.friday);
                break;
        }
        return day;
    }

    public static String getEngDayById(Context context, String dayId) {
        String day = "";
        switch (dayId) {
            case "1":
                day = "Saturday";
                break;
            case "2":
                day = "Sunday";
                break;
            case "3":
                day = "Monday";
                break;
            case "4":
                day = "Tuesday";
                break;
            case "5":
                day = "Wednesday";
                break;
            case "6":
                day = "Thursday";
                break;
            case "7":
                day = "Friday";
                break;
        }
        return day;
    }

    public static String getTimeDifference(String time1, String time2) throws ParseException {
        String diff = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mma", Locale.ENGLISH);
        if (time1.endsWith("PM") && time2.endsWith("AM")) {
            dateFormat.applyPattern("yyyy-MM-dd");
            String todayDate = dateFormat.format(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            String nextDate = dateFormat.format(calendar.getTime());
            dateFormat.applyPattern("yyyy-MM-dd hh:mma");
            Date date1 = dateFormat.parse(String.format("%s %s", todayDate, time1));
            Date date2 = dateFormat.parse(String.format("%s %s", nextDate, time2));
            long mills = date2.getTime() - date1.getTime();
            int mins = (int) TimeUnit.MILLISECONDS.toMinutes(mills);
            diff = String.valueOf(mins);
        }
        else {
            Date date1 = dateFormat.parse(time1);
            Date date2 = dateFormat.parse(time2);
            long mills = date2.getTime() - date1.getTime();
            int mins = (int) TimeUnit.MILLISECONDS.toMinutes(mills);
            diff = String.valueOf(mins);
        }
        return  diff;
    }

    public static int getDateDifferenceInDays(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        return (int)(different / daysInMilli);
    }

    public static String getCreditCardType(String CreditCardNumber)
    {
        Regex regVisa = new Regex("^4[0-9]{6,}$");
        Regex regMaster = new Regex("^5[1-5][0-9]{5,}$");
        Regex regExpress = new Regex("^3[47][0-9]{13}$");
        Regex regDiners = new Regex("^3(?:0[0-5]|[68][0-9])[0-9]{11}$");
        Regex regDiscover = new Regex("^6(?:011|5[0-9]{2})[0-9]{12}$");
        Regex regJCB = new Regex("^(?:2131|1800|35\\d{3})\\d{11}$");

        if (regVisa.matches(CreditCardNumber))
            return "VISA";
        else if (regMaster.matches(CreditCardNumber))
            return "MASTER";
        else  if (regExpress.matches(CreditCardNumber))
            return "AEXPRESS";
        else if (regDiners.matches(CreditCardNumber))
            return "DINERS";
        else if (regDiscover.matches(CreditCardNumber))
            return "DISCOVERS";
        else if (regJCB.matches(CreditCardNumber))
            return "JCB";
        else
            return "invalid";
    }

    public static void getAddressFromLocation(
            final Location location, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> list = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                    if (list != null && list.size() > 0) {
                        Address address = list.get(0);
                        // sending back first address line and locality
                        if (address.getLocality() != null) {
                            result = address.getAddressLine(0) + ", " + address.getLocality();
                        }
                        else {
                            result = address.getAddressLine(0);
                        }
                    }
                } catch (IOException e) {
                    Log.e("getAddressFromLocation", "Impossible to connect to Geocoder", e);
                } finally {
                    Message msg = Message.obtain();
                    msg.setTarget(handler);
                    if (result != null) {
                        msg.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        msg.setData(bundle);
                    } else
                        msg.what = 0;
                    msg.sendToTarget();
                }
            }
        };
        thread.start();
    }

    public static List<Date> getMonthAndYearBetween(String start, String end, String dateFormat) {
        List<Date> dates = new ArrayList<>();
        SimpleDateFormat df1 = new SimpleDateFormat(dateFormat, Locale.ENGLISH);

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1 .parse(start);
            date2 = df1 .parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calStart = Calendar.getInstance();
        calStart.setTime(date1);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(date2);

        while(!calStart.after(calEnd))
        {
            dates.add(calStart.getTime());
            calStart.add(Calendar.MONTH, 1);
        }
        return dates;
    }

    private static String getAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }

    public static String getIPAddress() {
        String ipv4 = getAddress(true);
        if (!ipv4.isEmpty()) {
            return ipv4;
        }
        String ipv6 = getAddress(false);
        if (!ipv6.isEmpty()) {
            return ipv6;
        }
        return "";
    }

}
