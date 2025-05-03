package com.soumen.springtodo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {
    MaterialButton btnDeleteAccount, btnClearHistory;
    String email, password;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnClearHistory = findViewById(R.id.button_delete_history);
        btnDeleteAccount = findViewById(R.id.button_delete_account);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");


        btnClearHistory.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Clear your ToDo history - are you sure?")
                    .setMessage("You can't recovery This data.\nIf Yes then Reopen this app to update.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            RequestQueue requestQueue = Volley.newRequestQueue(SettingActivity.this);

                            String url = "http://192.168.52.150:8080/users/delete-history?email=" + email;
                            Log.d("URL", url);
                            if (url == null || url.isEmpty()) {
                                return;
                            }

                            StringRequest stringRequest = new StringRequest(
                                    Request.Method.DELETE,
                                    url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Toast.makeText(SettingActivity.this, "History deleted: " + response, Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(SettingActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );

                            requestQueue.add(stringRequest);

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(SettingActivity.this, "Ok", Toast.LENGTH_SHORT).show();
                        }
                    });
            dialog.show();
        });


        btnDeleteAccount.setOnClickListener(v -> {
            Dialog dialog = new Dialog(SettingActivity.this);
            dialog.setContentView(R.layout.enter_repassword);

            EditText edtPassword = dialog.findViewById(R.id.edit_email_input);
            Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
            btnConfirm.setOnClickListener(view -> {
                String password2 = edtPassword.getText().toString();


                if (!password2.equals(password)) {
                    edtPassword.setError("Password doesn't match.");
                    return;
                }

                RequestQueue requestQueue = Volley.newRequestQueue(SettingActivity.this);
                String url = "http://192.168.52.150:8080/users/delete-user?email=" + email + "&password=" + password2;

                StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                        response -> {
                            Intent intent2 = new Intent(SettingActivity.this, SignUpActivity.class);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent2);
                            finish();
                        },
                        error -> Toast.makeText(SettingActivity.this, "Check Again", Toast.LENGTH_SHORT).show()
                );

                requestQueue.add(stringRequest);
                dialog.dismiss();
            });

            dialog.show();
        });

    }
}