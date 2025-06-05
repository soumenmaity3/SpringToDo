package com.soumen.springtodo;

import android.annotation.SuppressLint;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

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
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = "http://192.168.169.150:8080/users/check-email?email=" + edtEmail.getText().toString();
            Log.d("ForgotPassword", edtEmail.getText().toString());

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    sendAndStoreOtpToEmail();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ForgotPasswordActivity.this, "Email Not Found", Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(stringRequest);

            Toast.makeText(this, "OTP", Toast.LENGTH_SHORT).show();
        });


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
                        RequestQueue requestQueue1 = Volley.newRequestQueue(context);
                        if (edtOtp.getText().toString() == null) {
                            edtOtp.setError("Enter Otp.");
                            return;
                        }
                        String otp = edtOtp.getText().toString();
                        String url = "http://192.168.169.150:8080/users/otp-checker";
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("email", email);
                            jsonObject.put("otp", otp);
                        } catch (Exception e) {
                            Toast.makeText(ForgotPasswordActivity.this, "Nothing", Toast.LENGTH_SHORT).show();
                        }
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Intent intent = new Intent(ForgotPasswordActivity.this, ResetPassword.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                finish();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        requestQueue.add(jsonObjectRequest);


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
        String email = edtEmail.getText().toString();

        RequestQueue requestSendOtp = Volley.newRequestQueue(this);
        String otpSendUrl = "http://192.168.169.150:8080/users/send-otp";
        JSONObject sendOtpBody = new JSONObject();
        try {
            sendOtpBody.put("email", email);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        JsonObjectRequest otpRequest = new JsonObjectRequest(Request.Method.POST, otpSendUrl, sendOtpBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ForgotPasswordActivity.this, "Otp Send Successfully", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ForgotPasswordActivity.this, "Have an error", Toast.LENGTH_SHORT).show();
            }
        });

        requestSendOtp.add(otpRequest);
    }

    //    }
//    String email = edtEmail.getText().toString().trim();
//
//        if (email.isEmpty()) {
//        Toast.makeText(this, "Enter email address", Toast.LENGTH_SHORT).show();
//        return;
//    }
//
//    String url = "http://192.168.169.150:8080/users/send-otp";
//
//    JSONObject jsonBody = new JSONObject();
//        try {
//        jsonBody.put("email", email);
//    } catch (JSONException e) {
//        e.printStackTrace();
//    }
//
//    JsonObjectRequest request = new JsonObjectRequest(
//            Request.Method.POST, url, jsonBody,
//            response -> {
//                Toast.makeText(this, "OTP sent successfully!", Toast.LENGTH_SHORT).show();
//
//                JSONObject storeOtpBody = new JSONObject();
//                try {
//                    JSONObject userObject = new JSONObject();
//                    userObject.put("email", email); // Pass user email
//
//                    storeOtpBody.put("user", userObject); // user object
//                    storeOtpBody.put("used", false);      // used = false
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                JsonObjectRequest storeOtpRequest = new JsonObjectRequest(
//                        Request.Method.POST, url2, storeOtpBody,
//                        response1 -> Toast.makeText(ForgotPasswordActivity.this, "Stored", Toast.LENGTH_SHORT).show(),
//                        error -> Log.e("VolleyError", error.toString())
//                );
//
//                requestQueue.add(storeOtpRequest);
//
//
//                Log.d("VolleySuccess", response.toString());
//            },
//            error -> {
//                Log.e("VolleyError2", error.toString());
//            }
//    );
//
//    RequestQueue queue = Volley.newRequestQueue(this);
//        queue.add(request);
//}
}