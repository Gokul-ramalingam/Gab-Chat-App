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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.PriorityQueue;

public class RegActivity extends AppCompatActivity {
      private EditText username,pass;
      private EditText email;
      private Button rbutton;

      //progressDialog

      private ProgressDialog regprog;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    //toolbar
    private android.support.v7.widget.Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        regprog=new ProgressDialog(this);

        //toolbar setup
        toolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.reg_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase authentication

        mAuth = FirebaseAuth.getInstance();
        //details
        username=(EditText)findViewById(R.id.un);
        email=(EditText)findViewById(R.id.eml);
        pass=(EditText)findViewById(R.id.pas);
        rbutton=(Button)findViewById(R.id.rbut);
        rbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rusername=(username).getText().toString();
                String remail=(email).getText().toString();
                String rpass=(pass).getText().toString();

                if (!TextUtils.isEmpty(rusername) ||!TextUtils.isEmpty(remail)||!TextUtils.isEmpty(rpass)){
                    regprog.setTitle("Registering User");
                    regprog.setMessage("Please wait until registration completes");
                    regprog.setCanceledOnTouchOutside(false);
                    regprog.show();
                    register(rusername,remail,rpass);
                }

            }
        });

    }

    private void register(final String rusername, String remail, String rpass) {
        mAuth.createUserWithEmailAndPassword(remail,rpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    //Getting fire base user id
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    //Retriving database instance

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    //storing in Database

                    HashMap<String, String> usermap = new HashMap<>();
                    usermap.put("name", rusername);
                    usermap.put("status", "Gab Says! Ignore Negativity");
                    usermap.put("image", "default");
                    usermap.put("thumb_image", "default");

                    //Main phase in storing

                    mDatabase.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                regprog.dismiss();
                                Intent mintent = new Intent(RegActivity.this, MainActivity.class);
                                mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mintent);
                                finish();
                            }
                        }
                    });

                }
                else{
                    regprog.hide();
                    Toast.makeText(RegActivity.this, "Error occurred while registering!", Toast.LENGTH_SHORT).show();
                    
                }
            }
        });
    }
}
