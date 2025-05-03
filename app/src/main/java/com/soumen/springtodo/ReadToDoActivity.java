package com.soumen.springtodo;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONObject;


import org.json.JSONException;

public class ReadToDoActivity extends AppCompatActivity {
TextView txtTask,txtTittle;
    FloatingActionButton fabEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_read_to_do);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtTittle=findViewById(R.id.taskTitle);
        txtTask=findViewById(R.id.fullTaskText);
        fabEdit=findViewById(R.id.fabEdit);

        Intent intent=getIntent();
        String task=intent.getStringExtra("task");
        String tittle=intent.getStringExtra("tittle");
        int taskId=intent.getIntExtra("taskId",0);
        txtTask.setText(task);
        txtTittle.setText(tittle);
        fabEdit.setOnClickListener(v->{
            Dialog dialog=new Dialog(ReadToDoActivity.this);
            dialog.setContentView(R.layout.add_task_lay);
            EditText edtTittle=dialog.findViewById(R.id.edtTask);
            EditText edtTask=dialog.findViewById(R.id.edtDes);
            Button btnEdit=dialog.findViewById(R.id.btnAdd);
            edtTittle.setText(tittle);
            edtTask.setText(task);
            btnEdit.setText("Edit");
            btnEdit.setOnClickListener(ve->{
                String editTask= (edtTask.getText().toString());
                String editTittle=edtTittle.getText().toString();
                RequestQueue requestQueue = Volley.newRequestQueue(ReadToDoActivity.this);
                String url="http://192.168.52.150:8080/users/updateTask/"+taskId;
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("tittle",editTittle);
                    jsonObject.put("description",editTask);
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                StringRequest stringRequest=new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                       txtTask.setText(editTask);
                        txtTittle.setText(editTittle);

                        AlertDialog.Builder builder=new AlertDialog.Builder(ReadToDoActivity.this);
                        builder.setMessage("Reopen the app for see the update.");
                        builder.show();
                        Toast.makeText(ReadToDoActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ReadToDoActivity.this, "Error: "+error, Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return jsonObject.toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };
                requestQueue.add(stringRequest);

                dialog.dismiss();
            });
            dialog.show();
        });
    }
}