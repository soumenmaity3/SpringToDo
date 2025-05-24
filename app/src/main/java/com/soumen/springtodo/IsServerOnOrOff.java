package com.soumen.springtodo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class IsServerOnOrOff {

    private final Context context;

    public IsServerOnOrOff(Context context) {
        this.context = context;
    }

    public interface ServerStatusCallback {
        void onOnline();

        void onOffline();
    }

    public void checkServerStatus(String urlString, ServerStatusCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        callback.onOnline();
                    } else {
                        callback.onOffline();
                    }
                });

            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(callback::onOffline);
            }
        }).start();
    }
}
