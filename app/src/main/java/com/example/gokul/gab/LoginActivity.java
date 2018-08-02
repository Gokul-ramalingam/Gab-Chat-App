package com.example.gokul.gab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText logemail;
    private EditText logpass;
    private ProgressDialog logprog;
    private Button logbutton;
    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //progress dialog
        logprog=new ProgressDialog(this);

              //Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //toolbar setup
        toolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.log_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //details
        logemail=(EditText)findViewById(R.id.leml);
        logpass=(EditText)findViewById(R.id.lpass);
        logbutton=(Button) findViewById(R.id.lbut);
        logbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String liemail=(logemail).getText().toString();
                String lipass=(logpass).getText().toString();
                if (!TextUtils.isEmpty(liemail)||!TextUtils.isEmpty(lipass)){
                    logprog.setTitle("Logging in");
                    logprog.setMessage("please wait until verification gets completed");
                    logprog.setCanceledOnTouchOutside(false);
                    logprog.show();
                    loginuser(liemail,lipass);
                }
            }
        });

    }

    private void loginuser(String liemail, String lipass) {
        mAuth.signInWithEmailAndPassword(liemail,lipass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    logprog.dismiss();
                    Intent logintent=new Intent(LoginActivity.this,MainActivity.class);
                    logintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logintent);
                    finish();
                }
                else{
                    logprog.hide();
                    Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


}
