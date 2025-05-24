package com.soumen.springtodo;

import static com.soumen.springtodo.R.drawable.*;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ToDoAdapter adapter;
    FloatingActionButton floatAction, floatingMenu;
    String email, password,userName;
    private long backPressedTime;
    private Toast backToast;
    ArrayList<ToDoModel> todoList;
    CardView cardAi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView marqueeText = findViewById(R.id.marqueeText);
        marqueeText.setSelected(true);
        marqueeText.setOnClickListener(v -> {
            AlertDialog.Builder dialog2 = new AlertDialog.Builder(this);
            dialog2.setTitle("Alert")
                    .setMessage("To edit a task, long-press on it!\n Stay focused. Stay productive. \uD83D\uDCAA")
                    .show();
        });


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        floatAction = findViewById(R.id.floatingButtonForAddTask);
        floatingMenu = findViewById(R.id.floatingMenu);
        cardAi=findViewById(R.id.card);
        todoList = (ArrayList<ToDoModel>) getIntent().getSerializableExtra("todoList");

        cardAi.setOnClickListener(v->{
            Intent intent=new Intent(MainActivity.this, AiAssistant.class);
            startActivity(intent);
        });

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        userName=intent.getStringExtra("userName");
        floatAction.setOnClickListener(v -> {
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.add_task_lay);
            EditText edtTask = dialog.findViewById(R.id.edtTask);
            EditText edtDes = dialog.findViewById(R.id.edtDes);
            Button btnAdd = dialog.findViewById(R.id.btnAdd);
            CheckBox checkBoxTimer = dialog.findViewById(R.id.checkBoxTimer);
            EditText editTextHr = dialog.findViewById(R.id.editTextHr);
            EditText editTextMin = dialog.findViewById(R.id.editTextMin);

            checkBoxTimer.setOnCheckedChangeListener((buttonView, isChecked) -> {
                editTextHr.setEnabled(isChecked);
                editTextMin.setEnabled(isChecked);
            });

            btnAdd.setOnClickListener(view -> {
                String title = edtTask.getText().toString().trim();
                String description = edtDes.getText().toString().trim();
                boolean complete = false;

                if (title.isEmpty() || description.isEmpty()) {
                    Toast.makeText(this, "enter all", Toast.LENGTH_SHORT).show();
                    return;
                }
                todoList.add(new ToDoModel(title, description, false));
                adapter.notifyItemInserted(todoList.size() - 1);
                dialog.dismiss();
                addTask(email, title, description);
            });
            dialog.show();
        });
        floatingMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingMenu.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.close));
                Dialog dialog3 = new Dialog(MainActivity.this);
                dialog3.setContentView(R.layout.list_dialog);
                TextView txtProfile, txtAlarm, txtSetting;
                txtProfile = dialog3.findViewById(R.id.editTask);
                txtAlarm = dialog3.findViewById(R.id.setTimer);
                txtSetting = dialog3.findViewById(R.id.deleteTask);
                txtProfile.setText("Profile");
                txtAlarm.setText("Alarm");
                txtSetting.setText("Setting");
                txtSetting.setTextColor(Color.parseColor("#2E47D4"));

                txtProfile.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("userName",userName);
                    Log.d("MainUserName", userName);
                    startActivity(intent);
                    dialog3.dismiss();
                    floatingMenu.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.menu));
                });

                txtSetting.setOnClickListener(v -> {
                    Intent intent1 = new Intent(MainActivity.this, SettingActivity.class);
                    intent1.putExtra("email", email);
                    intent1.putExtra("password", password);
                    startActivity(intent1);
                    dialog3.dismiss();
                    floatingMenu.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.menu));
                });
                dialog3.show();
                dialog3.setOnDismissListener(dialog -> {
                    floatingMenu.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.menu));
                });

            }
        });

        adapter = new ToDoAdapter(todoList, this,email);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }

    public void addTask(String email, String title, String description) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://192.168.105.150:8080/users/add_task";

        JSONObject userObject = new JSONObject();
        try {
            userObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject taskObject = new JSONObject();
        try {
            taskObject.put("tittle", title);
            taskObject.put("description", description);
            taskObject.put("completed", false);
            taskObject.put("user", userObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this, "Task added: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return taskObject.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        requestQueue.add(stringRequest);
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

