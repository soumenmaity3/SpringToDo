package com.soumen.springtodo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
    MaterialButton btnDeleteAccount, btnClearHistory, btnRecoveryData , btnDeleteTask;
    String email, password;
    ProgressBar progressBar;

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
        btnRecoveryData = findViewById(R.id.button_recovery_data);
        progressBar=findViewById(R.id.progressBar);
        btnDeleteTask=findViewById(R.id.button_delete_task_history);

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
                            Dialog dialog1 = new Dialog(SettingActivity.this);
                            dialog1.setContentView(R.layout.enter_repassword);
                            dialog1.show();

                            EditText edtPassword = dialog1.findViewById(R.id.edit_email_input);
                            Button btnConfirm = dialog1.findViewById(R.id.btn_confirm);

                            btnConfirm.setOnClickListener(view -> {
                                progressBar.setVisibility(View.VISIBLE);
                                dialog1.dismiss();
                                String password2 = edtPassword.getText().toString();
                                if (!password2.equals(password)) {
                                    edtPassword.setError("Password doesn't match.");
                                    return;
                                }
                                RequestQueue requestQueue = Volley.newRequestQueue(SettingActivity.this);

                                String url = "http://192.168.169.150:8080/users/delete-history?email=" + email;

                                StringRequest stringRequest = new StringRequest(
                                        Request.Method.DELETE,
                                        url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                progressBar.setVisibility(View.GONE);

                                                Toast.makeText(SettingActivity.this, "History deleted: " + response, Toast.LENGTH_SHORT).show();
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(SettingActivity.this, "Already done.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );

                                requestQueue.add(stringRequest);
                            });


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

        btnDeleteTask.setOnClickListener(v->{
            Dialog dialog=new Dialog(this);
            dialog.setContentView(R.layout.enter_repassword);
            EditText editText=dialog.findViewById(R.id.edit_email_input);
            Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
            btnConfirm.setOnClickListener(view -> {
            if (editText.getText().toString().equals(password)) {
                Intent intent1=new Intent(SettingActivity.this,LostHistoryActivity.class);
                intent1.putExtra("email",email);
                startActivity(intent1);
                dialog.dismiss();
            }else {
                Toast.makeText(this, "Check Your Password", Toast.LENGTH_SHORT).show();
            }
            });
            dialog.show();
        });

        btnRecoveryData.setOnClickListener(v -> {
            Dialog dialog = new Dialog(SettingActivity.this);
            dialog.setContentView(R.layout.enter_repassword);
            EditText edtPassword = dialog.findViewById(R.id.edit_email_input);
            Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
            btnConfirm.setOnClickListener(view -> {
                progressBar.setVisibility(View.VISIBLE);
                dialog.dismiss();
                String password2 = edtPassword.getText().toString();
                if (!password2.equals(password)) {
                    edtPassword.setError("Password doesn't match.");
                    return;
                }

                RequestQueue requestQueue = Volley.newRequestQueue(SettingActivity.this);
                String url = "http://192.168.169.150:8080/users/recover-data?email=" + email;

                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SettingActivity.this, "Done", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this);
                        dialog.setMessage("Re login the app for update.");
                        dialog.show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SettingActivity.this, "Already Done. ", Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(stringRequest);
                dialog.dismiss();

            });
            dialog.show();
        });

        btnDeleteAccount.setOnClickListener(v -> {
            Dialog dialog=new Dialog(this);
            dialog.setContentView(R.layout.delete_account_dailog);
            Button delete=dialog.findViewById(R.id.delete_button);
            Button cancele=dialog.findViewById(R.id.cancel_button);
            delete.setOnClickListener(vi->{
                account();
                dialog.dismiss();
            });
            cancele.setOnClickListener(vi->{
                dialog.dismiss();
            });
            dialog.show();
        });


        Button aboutApp=findViewById(R.id.button_About_App);
        aboutApp.setOnClickListener(v->{
            Intent intent1=new Intent(SettingActivity.this,AboutPageActivity.class);
            startActivity(intent1);
        });
    }

    public void account(){
            Dialog dialog = new Dialog(SettingActivity.this);
            dialog.setContentView(R.layout.enter_repassword);

            EditText edtPassword = dialog.findViewById(R.id.edit_email_input);
            Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
            btnConfirm.setOnClickListener(view -> {
                String password2 = edtPassword.getText().toString();
                progressBar.setVisibility(View.VISIBLE);

                if (!password2.equals(password)) {
                    edtPassword.setError("Password doesn't match.");
                    return;
                }

                RequestQueue requestQueue = Volley.newRequestQueue(SettingActivity.this);
                String url = "http://192.168.169.150:8080/users/delete-user?email=" + email + "&password=" + password2;
                Log.d("null email",url);
                StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                        response -> {
                            progressBar.setVisibility(View.GONE);
                            Intent intent2 = new Intent(SettingActivity.this, SignUpActivity.class);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent2);
                            finish();
                        },
                        error -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SettingActivity.this, "Clear History First", Toast.LENGTH_SHORT).show();
                        });

                requestQueue.add(stringRequest);
                dialog.dismiss();
            });

            dialog.show();


    }
}