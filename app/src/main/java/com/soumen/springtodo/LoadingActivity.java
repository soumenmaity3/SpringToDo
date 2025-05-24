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
        IsServerOnOrOff isServerOnOrOff = new IsServerOnOrOff(LoadingActivity.this);
        new Handler().postDelayed(() -> isServerOnOrOff.checkServerStatus("http://192.168.105.150:8080/users/ping", new IsServerOnOrOff.ServerStatusCallback() {
            @Override
            public void onOnline() {
                Intent intent = new Intent(LoadingActivity.this, OptionPage.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onOffline() {
                serverError.setVisibility(View.VISIBLE);
                txtSever.setVisibility(View.VISIBLE);
                sendRequest.setVisibility(View.VISIBLE);
                Toast.makeText(LoadingActivity.this, "Server is offline", Toast.LENGTH_LONG).show();
                pgBar.setVisibility(ProgressBar.INVISIBLE);
            }
        }), 1000);

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


}