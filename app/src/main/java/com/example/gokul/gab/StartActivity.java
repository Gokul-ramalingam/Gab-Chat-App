package com.example.gokul.gab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
     private Button srbutton;
     private Button loginbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        srbutton=(Button) findViewById(R.id.srbut);
        loginbutton=(Button) findViewById(R.id.logbut);
        srbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent srintent=new Intent(StartActivity.this,RegActivity.class);
                startActivity(srintent);

            }
        });
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signintent=new Intent(StartActivity.this,LoginActivity.class);
                startActivity(signintent);
            }
        });

    }
}
