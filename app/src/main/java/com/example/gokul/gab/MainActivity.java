package com.example.gokul.gab;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
     private FirebaseAuth mAuth;
     private android.support.v7.widget.Toolbar toolbar;
     private TabLayout tablayout;
     private ViewPager viewPager;
     private ViewPagerAdapter viewPagerAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        //toolbar
        toolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Gab");
        //tab layout and view pager
        tablayout=(TabLayout)findViewById(R.id.tabLayout);
        viewPager=(ViewPager)findViewById(R.id.viewpager);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new Request(),"REQUEST");
        viewPagerAdapter.addFragments(new Chats(),"CHATS");
        viewPagerAdapter.addFragments(new Friends(),"FRIENDS");
        viewPager.setAdapter(viewPagerAdapter);
        tablayout.setupWithViewPager(viewPager);


    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendtostart();
        }

    }

    private void sendtostart() {
        Intent start = new Intent(MainActivity.this, StartActivity.class);
        startActivity(start);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.lobut)
        {
           sendtostart();
        }
        if(item.getItemId()==R.id.setbut);
        {
           Intent settintent=new Intent(MainActivity.this,SettingsActivity.class);
           startActivity(settintent);
        }

         if (item.getItemId()==R.id.allubut)
         {
            Intent allusers=new Intent(MainActivity.this,Users.class);
            startActivity(allusers);

}
        return true;
    }
}

