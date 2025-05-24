package com.soumen.springtodo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends AppCompatActivity {
    Button btnReset;
    TextInputEditText edtEmail;
    TextView txtBack;
    private long backPressedTime;
    Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnReset = findViewById(R.id.btnSendReset);
        edtEmail = findViewById(R.id.edtEmail);
        txtBack = findViewById(R.id.tvBackToLogin);

        txtBack.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnReset.setOnClickListener(v -> {
            String email = edtEmail.getText().toString();
            checkServer(ForgotPasswordActivity.this, email);
        });

    }

    public void checkServer(Context context, String email) {
        IsServerOnOrOff isServerOnOrOff = new IsServerOnOrOff(context);
        isServerOnOrOff.checkServerStatus("http://192.168.105.150:8080/users/ping", new IsServerOnOrOff.ServerStatusCallback() {
            @Override
            public void onOnline() {
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                String url = "http://192.168.105.150:8080/users/check-email?email=" + email;
                Log.d("ForgotPassword", email);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(context, ResetPassword.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                        Toast.makeText(context, email, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Email Not Found", Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(stringRequest);
            }

            @Override
            public void onOffline() {
                Toast.makeText(context, "Server Offline", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            if (backToast != null) backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

}