package com.soumen.springtodo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    EditText edtName, edtEmail, edtPassword, edtConfirm;
    Button btnSubmit;
    TextView txtSignIn;

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


        btnSubmit.setOnClickListener(v -> {
            try {
                newRegister();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://192.168.226.150:8080/users/signup";

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
        if (email.isEmpty()) {
            edtEmail.setError("Name can't be empty.");
            return false;
        } else {
            return true;
        }
    }

    public boolean passwordChecker() {
        String password = edtPassword.getText().toString().trim();
        String confirm = edtConfirm.getText().toString().trim();

        if (password.isEmpty() || confirm.isEmpty()) {
            edtPassword.setError("Password can't be empty.");
            return false;
        } else if (!password.equals(confirm)) {  // Use .equals() instead of ==
            edtConfirm.setError("Passwords do not match!");
            return false;
        } else {
            return true;  // Validation successful
        }

    }
}