package com.soumen.springtodo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {
    Button btnSubmit2;
    TextView txtSignUp, forgotPassword;
    EditText edtSEmail, edtSPassword;
    private long backPressedTime;
    private Toast backToast;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtSEmail = findViewById(R.id.edtSName);
        edtSPassword = findViewById(R.id.edtSPassword);
        btnSubmit2 = findViewById(R.id.btnSubmit2);
        txtSignUp = findViewById(R.id.signUp);

        btnSubmit2.setOnClickListener(v -> {
            isAvailable();
        });

        forgotPassword = findViewById(R.id.txtForgotPassword);

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });

        txtSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void isAvailable() {
        if (!emailChecker() || !passwordChecker()) return;
        String userEmail = edtSEmail.getText().toString();
        String password = edtSPassword.getText().toString();
        checkServer(SignInActivity.this, userEmail, password);

    }

    public boolean emailChecker() {
        String email = edtSEmail.getText().toString();
        if (email.isEmpty()) {
            edtSEmail.setError("Name can't be empty.");
            return false;
        } else {
            return true;
        }
    }

    public boolean passwordChecker() {
        String password = edtSPassword.getText().toString().trim();

        if (password.isEmpty()) {
            edtSPassword.setError("Password can't be empty.");
            return false;
        } else {
            return true;  // Validation successful
        }

    }

    public void checkServer(Context context, String userEmail, String password) {
        IsServerOnOrOff isServerOnOrOff = new IsServerOnOrOff(context);
        isServerOnOrOff.checkServerStatus("http://192.168.105.150:8080/users/ping", new IsServerOnOrOff.ServerStatusCallback() {
            @Override
            public void onOnline() {
                RequestQueue requestQueue = Volley.newRequestQueue(SignInActivity.this);
                String url = "http://192.168.105.150:8080/users/login";

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("email", userEmail);
                    jsonBody.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                        Request.Method.POST,
                        url,
                        jsonBody.names(),
                        response -> {

                            try {
                                ArrayList<ToDoModel> todolist = new ArrayList<>();
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject todo = response.getJSONObject(i);
                                    String description;
                                    String title;
                                    int id;
                                    boolean complete = todo.getBoolean("completed");
                                    if (complete) {
                                        continue;
                                    } else {
                                        id = todo.getInt("id");
                                        title = todo.getString("tittle");
                                        description = todo.getString("description");
                                    }

                                    ToDoModel list = new ToDoModel(title, description, false);
                                    todolist.add(list);
                                    list.setId(id);
                                }
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                intent.putExtra("todoList", todolist);
                                intent.putExtra("email", userEmail);
                                intent.putExtra("password", password);

                                userName(userEmail, new VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {

                                        intent.putExtra("userName", result);
                                        Toast.makeText(context, "User Name" + (result), Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Toast.makeText(SignInActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(SignInActivity.this, "JSON Parsing Error", Toast.LENGTH_SHORT).show();
                            }
                        },
                        error -> {
                            Toast.makeText(SignInActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                ) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        return jsonBody.toString().getBytes();
                    }
                };

                requestQueue = Volley.newRequestQueue(SignInActivity.this);
                requestQueue.add(jsonArrayRequest);

            }

            @Override
            public void onOffline() {
                Toast.makeText(context, "Server Offline", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void userName(String email, VolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://192.168.105.150:8080/users/user-name?email=" + email;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        });
        requestQueue.add(request);
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