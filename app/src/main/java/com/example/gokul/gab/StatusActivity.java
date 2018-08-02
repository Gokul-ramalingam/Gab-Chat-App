package com.example.gokul.gab;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private Toolbar statustoolbar;
    private TextInputLayout statusinput;
    private Button statusbut;

    //Firebase
    private DatabaseReference mstatusdb;
    private FirebaseUser mcurrentuser;

    //ProgressBar
    private ProgressDialog mprogressta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //Firebase
        mcurrentuser=FirebaseAuth.getInstance().getCurrentUser();
        String mcurrent_uid=mcurrentuser.getUid();
        mstatusdb= FirebaseDatabase.getInstance().getReference().child("Users").child(mcurrent_uid);

        statustoolbar=(Toolbar)findViewById(R.id.sta_bar);
        setSupportActionBar(statustoolbar);
        getSupportActionBar().setTitle("Account Status");
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //Getting retrieved status from setting intent to status i.e:this;
       String status_value=getIntent().getStringExtra("status_value");

       statusinput=(TextInputLayout) findViewById(R.id.status_input);
       statusbut=(Button)findViewById(R.id.sta_conf);

          //Assigning retrieved data from settings activity
       statusinput.getEditText().setText(status_value);

       statusbut.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               //Progress bar context declaration
               mprogressta=new ProgressDialog(StatusActivity.this);
               mprogressta.setTitle("Updating");
               mprogressta.setMessage("Please be patient! your status is updating ");
               mprogressta.show();


               String fstatus=statusinput.getEditText().getText().toString();
               mstatusdb.child("status").setValue(fstatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           mprogressta.dismiss();
                       }
                       else
                       {
                           Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
           }
       });
    }
}
