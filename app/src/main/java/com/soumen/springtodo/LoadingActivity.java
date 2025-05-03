package com.soumen.springtodo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadingActivity extends AppCompatActivity {

    ProgressBar pgBar;
    ImageView serverError;
    TextView txtSever;
    MaterialButton sendRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        pgBar = findViewById(R.id.pgBar);
        serverError = findViewById(R.id.serverError);
        txtSever = findViewById(R.id.txtSever);
        sendRequest = findViewById(R.id.sendRequest);

        new Handler().postDelayed(() -> checkServerStatus("http://192.168.52.150:8080/users/ping"), 1000);

        sendRequest.setOnClickListener(v -> {
            String email = "sm8939912@gmail.com";
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + email));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Make Server Online");
            intent.putExtra(Intent.EXTRA_TEXT, "Please make the server online.\nI Went to use your app.");
            try {
                startActivity(Intent.createChooser(intent, "Choose Email App"));
            } catch (Exception e) {
                Toast.makeText(v.getContext(), "No email apps installed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkServerStatus(String urlString) {
        new Thread(() -> {
            boolean isOnline = false;
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000); // 3 seconds timeout
                conn.connect();
                int code = conn.getResponseCode();
                isOnline = (code == 200); // HTTP OK
            } catch (IOException e) {
                e.printStackTrace();
            }

            boolean finalIsOnline = isOnline;
            runOnUiThread(() -> {
                if (finalIsOnline) {
                    Intent intent = new Intent(LoadingActivity.this, OptionPage.class);
                    startActivity(intent);
                    finish();
                } else {
                    serverError.setVisibility(View.VISIBLE);
                    txtSever.setVisibility(View.VISIBLE);
                    sendRequest.setVisibility(View.VISIBLE);
                    Toast.makeText(LoadingActivity.this, "Server is offline", Toast.LENGTH_LONG).show();
                    pgBar.setVisibility(ProgressBar.INVISIBLE);
                }
            });
        }).start();
    }
}