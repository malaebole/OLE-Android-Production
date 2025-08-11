package ae.oleapp.socket;

import ae.oleapp.MyApp;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.parser.Base64;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocketManager {
    private static SocketManager instance;
    private Socket socket;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private SocketManager() {
        initSocketConnection();
    }

    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }




    private void initSocketConnection() {
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            Map<String, List<String>> extraHeaders = new HashMap<>();
            extraHeaders.put("Authorization", Collections.singletonList("Bearer " + Functions.getPrefValue(getContext(), Constants.kaccessToken)));
            opts.extraHeaders = extraHeaders;
            opts.reconnectionAttempts = 3;
            opts.reconnectionDelay = 200;
//            socket = IO.socket("http://api.ole-app.ae:3000/lineup", opts); //Test Socket Url
            socket = IO.socket("http://node.ole-sports.com:5000/lineup", opts);  //Live Socket Url
            socket.connect();

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    handler.post(() -> {
                       // Functions.showToast(getContext(),"Connected to Socket.io",FancyToast.SUCCESS);
                    });

                }
            });

            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                          //  Functions.showToast(getContext(),"Disconnected from Socket.io",FancyToast.ERROR);
                        }
                    });
                }
            });


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    // Utility function to show a toast on the UI thread
    // Utility function to get the application's context
    private Context getContext() {
        return MyApp.getAppContext();
    }
    public static synchronized void resetInstance() {
        instance = null;
    }

}
