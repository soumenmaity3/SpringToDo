package com.soumen.springtodo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ResetPassword extends AppCompatActivity {
    TextInputEditText newPasswordEditText, confirmPasswordEditText;
    MaterialButton resetPasswordButton;
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        resetPasswordButton.setOnClickListener(v -> {
            if (!passwordChecker()) {
                return;
            }
            RequestQueue requestQueue = Volley.newRequestQueue(ResetPassword.this);
            String password = newPasswordEditText.getText().toString();
            String url = "http://192.168.169.150:8080/users/reset-password?email=" + email + "&password=" + password;

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Intent intent1 = new Intent(ResetPassword.this, SignInActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                    finish();
                    Toast.makeText(ResetPassword.this, "Done", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ResetPassword.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(stringRequest);

        });

    }

    public boolean passwordChecker() {
        String password = newPasswordEditText.getText().toString().trim();
        String confirm = confirmPasswordEditText.getText().toString().trim();
        if (password.isEmpty()) {
            newPasswordEditText.setError("Password cannot be empty.");
            return false;
        }
        if (confirm.isEmpty()) {
            confirmPasswordEditText.setError("Please confirm your password.");
            return false;
        }
        if (password.length() < 8) {
            newPasswordEditText.setError("Password must be at least 8 characters.");
            return false;
        }
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        if (!password.matches(passwordPattern)) {
            newPasswordEditText.setError("Password must contain uppercase, number, and special character.");
            return false;
        }

        if (!password.equals(confirm)) {
            confirmPasswordEditText.setError("Passwords do not match.");
            return false;
        }

        return true;
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