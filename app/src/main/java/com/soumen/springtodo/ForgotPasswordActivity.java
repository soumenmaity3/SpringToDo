package com.soumen.springtodo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity {
    Button btnReset, btnOtp;
    TextInputEditText edtEmail, edtOtp;
    TextView txtBack;
    private long backPressedTime;
    Toast backToast;

    @SuppressLint("MissingInflatedId")
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
        edtOtp = findViewById(R.id.edtOtp);
        btnOtp = findViewById(R.id.otpButton);

        txtBack.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnReset.setOnClickListener(v -> {
            String email = edtEmail.getText().toString();
            checkServer(ForgotPasswordActivity.this, email);
        });
        btnOtp.setOnClickListener(v -> {
            btnOtp.setEnabled(false);
            sendAndStoreOtpToEmail();

            Toast.makeText(this, "OTP", Toast.LENGTH_SHORT).show();
        });


    }

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }


    public void checkServer(Context context, String email) {
        IsServerOnOrOff isServerOnOrOff = new IsServerOnOrOff(context);
        isServerOnOrOff.checkServerStatus("http://192.168.169.150:8080/users/ping", new IsServerOnOrOff.ServerStatusCallback() {
            @Override
            public void onOnline() {
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                String url = "http://192.168.169.150:8080/users/check-email?email=" + email;
                Log.d("ForgotPassword", email);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        RequestQueue requestQueue = Volley.newRequestQueue(ForgotPasswordActivity.this);
                        String url = "http://192.168.169.150:8080/users/otp-checker";

                        JSONObject body = new JSONObject();
                        try {
                            body.put("email", email);
                            body.put("otp", edtOtp.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        StringRequest stringRequest = new StringRequest(
                                Request.Method.POST,
                                url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("VolleyResponse", response);
                                        if (response.equalsIgnoreCase("USed")) {
                                            Intent intent = new Intent(ForgotPasswordActivity.this, ResetPassword.class);
                                            intent.putExtra("email", email);
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(ForgotPasswordActivity.this, "OTP Matched!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ForgotPasswordActivity.this, "OTP Check Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }) {

                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                return body.toString().getBytes(StandardCharsets.UTF_8); // JSON body
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8";
                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                return headers;
                            }
                        };

                        requestQueue.add(stringRequest);


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

    private void sendAndStoreOtpToEmail() {
        String email = edtEmail.getText().toString().trim();
        String otp = generateOtp();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        String otpSendUrl = "http://192.168.169.150:8080/users/send-otp/" + otp + "?email=" + email;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest sendOtpRequest = new StringRequest(Request.Method.POST, otpSendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String storeURL = "http://192.168.169.150:8080/users/store-otp/" + otp;
                RequestQueue requestQueue1 = Volley.newRequestQueue(ForgotPasswordActivity.this);
                Toast.makeText(ForgotPasswordActivity.this, "OTP Send Successfully", Toast.LENGTH_SHORT).show();
                JSONObject json = new JSONObject();
                try {
                    json.put("used", false);

                    JSONObject userObject = new JSONObject();
                    userObject.put("email", email);

                    json.put("user", userObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest storeRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        storeURL,
                        json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("VolleyStoreError", error.toString());
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                };
                requestQueue1.add(storeRequest);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ForgotPasswordActivity.this, "Send Error", Toast.LENGTH_SHORT).show();
            }
        });
        sendOtpRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(sendOtpRequest);
    }

}