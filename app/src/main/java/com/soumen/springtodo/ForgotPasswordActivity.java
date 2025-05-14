package com.soumen.springtodo;

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

        btnReset=findViewById(R.id.btnSendReset);
        edtEmail=findViewById(R.id.edtEmail);
        txtBack=findViewById(R.id.tvBackToLogin);

        txtBack.setOnClickListener(v->{
            Intent intent=new Intent(ForgotPasswordActivity.this,SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnReset.setOnClickListener(v->{
            RequestQueue requestQueue= Volley.newRequestQueue(ForgotPasswordActivity.this);
            String email=edtEmail.getText().toString();
            String url="http://192.168.226.150:8080/users/check-email?email="+email;
            Log.d("ForgotPassword",email );

            StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Intent intent=new Intent(ForgotPasswordActivity.this, ResetPassword.class);
                    intent.putExtra("email",email);
                    startActivity(intent);
                    finish();
                    Toast.makeText(ForgotPasswordActivity.this, email, Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ForgotPasswordActivity.this, "Error:", Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(stringRequest);
        });

    }
}