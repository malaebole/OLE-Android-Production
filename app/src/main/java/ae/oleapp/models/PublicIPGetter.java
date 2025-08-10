package ae.oleapp.models;

import android.os.AsyncTask;
import android.util.Log;

import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ae.oleapp.util.Functions;

public class PublicIPGetter extends AsyncTask<Void, Void, String> {

    private static final String TAG = PublicIPGetter.class.getSimpleName();
    private static final String API_URL = "https://api.ipify.org?format=json";

    private final IPListener listener;

    public PublicIPGetter(IPListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(API_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching public IP address: " + e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
//        if (result != null) {
//            listener.onIPReceived(result);
//        } else {
//            listener.onIPFailed();
//        }
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String ipAddress = jsonObject.getString("ip");
                listener.onIPReceived(ipAddress);
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON response: " + e.getMessage());
                listener.onIPFailed();
            }
        } else {
            listener.onIPFailed();
        }



    }

    public interface IPListener {
        void onIPReceived(String ipAddress);
        void onIPFailed();
    }
}
