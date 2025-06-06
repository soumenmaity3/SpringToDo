package com.soumen.springtodo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {
    EditText edtName, edtEmail, edtPassword, edtConfirm,edtOtp;
    Button btnSubmit,btnOtp;
    TextView txtSignIn;
    String otp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirm = findViewById(R.id.edtConfirm);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtSignIn = findViewById(R.id.logIn);
        edtOtp = findViewById(R.id.edtOtpSign);
        btnOtp = findViewById(R.id.otpButtonSign);


        btnSubmit.setOnClickListener(v -> {
            try {
                newRegister();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        btnOtp.setOnClickListener(v->{
            sendOtpToEmail();
        });

        txtSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void newRegister() throws JSONException {

        if (!passwordChecker() || !nameChecker() || !emailChecker()) {
            return;
        }
        checkServer(SignUpActivity.this);

    }

    public boolean nameChecker() {
        String name = edtName.getText().toString();
        if (name.isEmpty()) {
            edtName.setError("Name can't be empty.");
            return false;
        } else {
            return true;
        }
    }

    public boolean emailChecker() {
        String email = edtEmail.getText().toString();
        if (email.isEmpty()||!isValidEmail(email)) {
            edtEmail.setError("Enter Valid Email");
            return false;
        } else {
            return true;
        }
    }

    public boolean passwordChecker() {
        String password = edtPassword.getText().toString().trim();
        String confirm = edtConfirm.getText().toString().trim();
        if (password.isEmpty()) {
            edtPassword.setError("Password cannot be empty.");
            return false;
        }
        if (confirm.isEmpty()) {
            edtConfirm.setError("Please confirm your password.");
            return false;
        }
        if (password.length() < 8) {
            edtPassword.setError("Password must be at least 8 characters.");
            return false;
        }
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        if (!password.matches(passwordPattern)) {
            edtPassword.setError("Password must contain uppercase, number, and special character.");
            return false;
        }

        if (!password.equals(confirm)) {
            edtConfirm.setError("Passwords do not match.");
            return false;
        }

        return true;
    }

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }


    private void sendOtpToEmail() {
        String email = edtEmail.getText().toString().trim();
        otp = generateOtp();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        String otpSendUrl = "http://192.168.169.150:8080/users/send-otp/" + otp + "?email=" + email;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest sendOtpRequest = getStringRequest(otpSendUrl);
        requestQueue.add(sendOtpRequest);
    }


    public void checkServer(Context context){
        IsServerOnOrOff isServerOnOrOff=new IsServerOnOrOff(context);
        isServerOnOrOff.checkServerStatus("http://192.168.169.150:8080/users/ping", new IsServerOnOrOff.ServerStatusCallback() {
            @Override
            public void onOnline() {
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                String url = "http://192.168.169.150:8080/users/signup";

                String otpChecker=edtOtp.getText().toString();

                if (otpChecker.isEmpty()||!otpChecker.equals(otp)){
                    edtOtp.setError("OTP not matched");
                    return;
                }

                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        edtName.setText(null);
                        edtConfirm.setText(null);
                        edtEmail.setText(null);
                        edtPassword.setText(null);
                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 409) {
                            Toast.makeText(SignUpActivity.this, "Email already have an account", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                        error.printStackTrace();}
                }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("user_name", edtName.getText().toString());
                        params.put("email", edtEmail.getText().toString());
                        params.put("password", edtPassword.getText().toString());
                        return params;
                    }
                };
                requestQueue.add(request);
            }

            @Override
            public void onOffline() {
                Toast.makeText(context, "Server offline", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;

        // Basic regex for email validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        return email.matches(emailRegex);
    }




    @NonNull
    private StringRequest getStringRequest(String otpSendUrl) {
        StringRequest sendOtpRequest = new StringRequest(Request.Method.POST, otpSendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(SignUpActivity.this, "OTP Send Successfully", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignUpActivity.this, "Send Error", Toast.LENGTH_SHORT).show();
            }
        });
        sendOtpRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return sendOtpRequest;
    }


}