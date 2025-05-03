package com.soumen.springtodo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
TextView txtSignUp;
EditText edtSEmail,edtSPassword;
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

        edtSEmail=findViewById(R.id.edtSName);
        edtSPassword=findViewById(R.id.edtSPassword);
        btnSubmit2=findViewById(R.id.btnSubmit2);
        txtSignUp=findViewById(R.id.signUp);

        btnSubmit2.setOnClickListener(v->{
            isAvailable();
        });

        txtSignUp.setOnClickListener(v->{
            Intent intent=new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void isAvailable() {
        if (!emailChecker() || !passwordChecker()) return;

        RequestQueue requestQueue = Volley.newRequestQueue(SignInActivity.this);
        String url = "http://192.168.52.150:8080/users/login";

        String userEmail=edtSEmail.getText().toString();
        String password=edtSPassword.getText().toString();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email",userEmail);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.POST,
                url,
                jsonBody.names(),
                response -> {
                     // Pass this dynamically
                    String url2 = "http://192.168.52.150:8080/users/user-name?email=" + userEmail;
                    Log.d("useremail", userEmail);
                    RequestQueue queue = Volley.newRequestQueue(this);

                    StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(SignInActivity.this, "Find It- "+userEmail, Toast.LENGTH_SHORT).show();
                            Log.d("userName", response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(SignInActivity.this, "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                            queue.add(stringRequest2);

                    try {
                        ArrayList<ToDoModel>todolist=new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject todo = response.getJSONObject(i);
                            String description;
                            String title;
                            int id;
                            boolean complete=todo.getBoolean("completed");
                            if (complete) {
                                continue;
                            } else {
                                 id=todo.getInt("id");
                                title = todo.getString("tittle");
                                description = todo.getString("description");
                            }

                            ToDoModel list = new ToDoModel(title, description, false);
                            todolist.add(list);
                            list.setId(id);


                        }
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        Log.d("SignInActivity", "todoList size: " + todolist.size());
                        intent.putExtra("todoList", todolist);
                        intent.putExtra("email", userEmail);
                        intent.putExtra("password",password);
                        startActivity(intent);
                        finish();
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
}