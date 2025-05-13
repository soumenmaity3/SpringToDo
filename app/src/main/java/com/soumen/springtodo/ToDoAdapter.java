package com.soumen.springtodo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.viewHolder> {
    ArrayList<ToDoModel> arrayList=new ArrayList<>();
    Context context;
    boolean complete=false;

    public ToDoAdapter(ArrayList<ToDoModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ToDoAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoAdapter.viewHolder holder, int position) {
        ToDoModel model=arrayList.get(position);
        holder.txtTask.setText(model.getTask());
        holder.txtTitle.setText(model.getTitle());
        holder.btnComplete.setText("Complete");
        holder.btnComplete.setOnClickListener(v -> {
            updateCompleteStatus(model.getId());
            if (complete) {
                model.setComplete(true);
                arrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, arrayList.size());
            }
        });

        holder.cardView.setOnLongClickListener(v->{
            Dialog optionsDialog=new Dialog(context);
            optionsDialog.setContentView(R.layout.list_dialog);
            optionsDialog.show();
            TextView editText = optionsDialog.findViewById(R.id.editTask);
            TextView setTimer = optionsDialog.findViewById(R.id.setTimer);
            TextView deleteTask = optionsDialog.findViewById(R.id.deleteTask);

            editText.setOnClickListener(view->{
                optionsDialog.dismiss();
                Dialog edtDialog=new Dialog(context);
                edtDialog.setContentView(R.layout.add_task_lay);
                Button btnEdt;
                EditText edtTask,edtDescription;
                edtTask=edtDialog.findViewById(R.id.edtTask);
                edtDescription=edtDialog.findViewById(R.id.edtDes);
                btnEdt=edtDialog.findViewById(R.id.btnAdd);
                CheckBox checkBoxTimer = edtDialog.findViewById(R.id.checkBoxTimer);
                EditText editTextHr =edtDialog.findViewById(R.id.editTextHr);
                EditText editTextMin =edtDialog.findViewById(R.id.editTextMin);

                checkBoxTimer.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    editTextHr.setEnabled(isChecked);
                    editTextMin.setEnabled(isChecked);
                });


                edtDescription.setText(model.getTask());
                edtTask.setText(model.getTitle());
                btnEdt.setText("Edti Task");
                btnEdt.setOnClickListener(s->{
                    edtDialog.dismiss();
                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                    String url="http://192.168.226.150:8080/users/updateTask/"+model.getId();
                    String tittle=edtTask.getText().toString();
                    String description=edtDescription.getText().toString();
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("tittle",tittle);
                        jsonObject.put("description",description);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    StringRequest stringRequest=new StringRequest(Request.Method.PUT, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    holder.txtTask.setText(description);
                                    holder.txtTitle.setText(tittle);
                                    Toast.makeText(context, "Task Update Done", Toast.LENGTH_SHORT).show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(context, "Something went Wrong", Toast.LENGTH_SHORT).show();
                                }
                            }) {
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
                });
                edtDialog.show();
            });

            return true;
        });

        holder.cardView.setOnClickListener(view -> {
            String tittle=model.getTitle();
            String task=model.getTask();
            Intent intent =new Intent(context, ReadToDoActivity.class);
            intent.putExtra("tittle",tittle);
            intent.putExtra("task",task);
            intent.putExtra("taskId",model.getId());
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        Button btnComplete;
        TextView txtTitle,txtTask;
        CardView cardView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            btnComplete=itemView.findViewById(R.id.btnComplete);
            txtTask=itemView.findViewById(R.id.txtToDo);
            txtTitle=itemView.findViewById(R.id.txtTitle);
            cardView=itemView.findViewById(R.id.cardView);
        }
    }

    private void updateCompleteStatus(int todoId) {
        String url = "http://192.168.226.150:8080/users/updateComplete/" + todoId;

        StringRequest request = new StringRequest(Request.Method.PUT, url,
                response -> {
                    Toast.makeText(context, "Task marked complete in backend!", Toast.LENGTH_SHORT).show();
                    complete=true;
                },
                error -> {
                    Toast.makeText(context, "Failed to update backend", Toast.LENGTH_SHORT).show();
                    complete=false;
                }
        );

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }



}
