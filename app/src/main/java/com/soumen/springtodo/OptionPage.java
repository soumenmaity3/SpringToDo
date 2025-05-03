package com.soumen.springtodo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class OptionPage extends AppCompatActivity {
Button btnSignUp,btnSignIn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_option_page);

        btnSignIn  =findViewById(R.id.btnSignIn);
        btnSignUp=findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(v->{
            Intent intent = new Intent(OptionPage.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        btnSignIn.setOnClickListener(v->{
            Intent intent=new Intent(OptionPage.this,SignInActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

    }
}