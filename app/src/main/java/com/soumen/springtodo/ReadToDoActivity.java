package com.soumen.springtodo;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class ReadToDoActivity extends AppCompatActivity {

    TextView txtTask, txtTittle;
    FloatingActionButton fabEdit;
    View dimOverlay;
    Animation rotateForward,rotateBackward,fabOpenAnim, fabCloseAnim;;

    // NEW FAB MENU
    FloatingActionButton fabMain, fabAbout, fabClose;
    private LinearLayout fabAboutLayout, fabEditLayout, fabCloseLayout;
    private boolean isMenuOpen = false;
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

        txtTittle = findViewById(R.id.taskTitle);
        txtTask = findViewById(R.id.fullTaskText);
        fabEdit = findViewById(R.id.fabEdit);

        // NEW FAB INITIALIZATION
        fabMain = findViewById(R.id.fabMain);
        fabAbout = findViewById(R.id.fabAbout);
        fabClose = findViewById(R.id.fabClose);
        fabAboutLayout = findViewById(R.id.fabAboutLayout);
        fabEditLayout = findViewById(R.id.fabEditLayout);
        fabCloseLayout = findViewById(R.id.fabCloseLayout);

        dimOverlay = findViewById(R.id.dimOverlay); // Add this in XML too
         rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
         rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
        LinearLayout fabEditLayout = findViewById(R.id.fabEditLayout);
        LinearLayout fabAboutLayout = findViewById(R.id.fabAboutLayout);


        fabOpenAnim = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabCloseAnim = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        fabMain.setOnClickListener(v -> toggleMenu());
        fabClose.setOnClickListener(v -> toggleMenu());

        fabAbout.setOnClickListener(v ->
                Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show());

        Intent intent = getIntent();
        String task = intent.getStringExtra("task");
        String tittle = intent.getStringExtra("tittle");
        int taskId = intent.getIntExtra("taskId", 0);
        txtTask.setText(task);
        txtTittle.setText(tittle);

        fabEdit.setOnClickListener(v -> {
            Dialog dialog = new Dialog(ReadToDoActivity.this);
            dialog.setContentView(R.layout.add_task_lay);
            EditText edtTittle = dialog.findViewById(R.id.edtTask);
            EditText edtTask = dialog.findViewById(R.id.edtDes);
            Button btnEdit = dialog.findViewById(R.id.btnAdd);
            CheckBox checkBoxTimer = dialog.findViewById(R.id.checkBoxTimer);
            EditText editTextHr = dialog.findViewById(R.id.editTextHr);
            EditText editTextMin = dialog.findViewById(R.id.editTextMin);
            TextView textView=dialog.findViewById(R.id.textView);
            textView.setText("Edit Task");

            checkBoxTimer.setOnCheckedChangeListener((buttonView, isChecked) -> {
                editTextHr.setEnabled(isChecked);
                editTextMin.setEnabled(isChecked);
            });

            edtTittle.setText(tittle);
            edtTask.setText(task);
            btnEdit.setText("Edit Task");

            btnEdit.setOnClickListener(ve -> {
                String editTask = edtTask.getText().toString();
                String editTittle = edtTittle.getText().toString();
                RequestQueue requestQueue = Volley.newRequestQueue(ReadToDoActivity.this);
                String url = "http://192.168.226.150:8080/users/updateTask/" + taskId;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("tittle", editTittle);
                    jsonObject.put("description", editTask);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                        response -> {
                            txtTask.setText(editTask);
                            txtTittle.setText(editTittle);
                            AlertDialog.Builder builder = new AlertDialog.Builder(ReadToDoActivity.this);
                            builder.setMessage("Re-login the app to see the update.");
                            builder.show();
                            Toast.makeText(ReadToDoActivity.this, "Done", Toast.LENGTH_SHORT).show();
                        },
                        error -> Toast.makeText(ReadToDoActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show()
                ) {
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

    private void toggleMenu() {
        if (isMenuOpen) {
            dimOverlay.setVisibility(View.GONE);
            animateHide(fabEditLayout, 50);
            animateHide(fabAboutLayout, 100);
            fabMain.startAnimation(rotateBackward);
            fabMain.setImageResource(R.drawable.ic_arrow_up);
        } else {
            dimOverlay.setVisibility(View.VISIBLE);
            fabEditLayout.setVisibility(View.VISIBLE);
            fabAboutLayout.setVisibility(View.VISIBLE);

            animateShow(fabEditLayout, 50);
            animateShow(fabAboutLayout, 100);
            fabMain.startAnimation(rotateForward);
            fabMain.setImageResource(R.drawable.ic_close);
        }
        isMenuOpen = !isMenuOpen;
    }

    private void animateShow(View view, long delay) {
        view.setAlpha(0f);
        view.setTranslationY(60f);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setStartDelay(delay)
                .start();
    }

    private void animateHide(View view, long delay) {
        view.animate()
                .alpha(0f)
                .translationY(60f)
                .setDuration(200)
                .setStartDelay(delay)
                .withEndAction(() -> view.setVisibility(View.GONE))
                .start();
    }
}
