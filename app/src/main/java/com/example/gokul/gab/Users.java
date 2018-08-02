package com.example.gokul.gab;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Users extends AppCompatActivity {
    private Toolbar malltoolbar;
    private RecyclerView mallrecycle;
    private DatabaseReference mUserDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        malltoolbar=(Toolbar)findViewById(R.id.alluserbar);
        setSupportActionBar(malltoolbar);
        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase DB Reference
        mUserDb= FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDb.keepSynced(true);

        mallrecycle=(RecyclerView)findViewById(R.id.allusr_recycle);
        mallrecycle.setHasFixedSize(true);
        mallrecycle.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Allusers,UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Allusers, UserViewHolder>(
                Allusers.class,
                R.layout.users_layout,
                UserViewHolder.class,
                mUserDb)
        {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, Allusers model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setTimage(model.getThumb_image(),getApplicationContext());

                //Using the below code we can get the id of the selected user i.e:From a list of users when a particular user is clicked then it shows his detail so to do that we fetch the id of the user using below reference
                final String user_id=getRef(position).getKey();

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profile_in=new Intent(Users.this,ProfileActivity.class);
                        //We are passing the user_id using the profile_intent
                        profile_in.putExtra("user_id",user_id);
                        startActivity(profile_in);

                    }
                });
            }
        };

      mallrecycle.setAdapter(firebaseRecyclerAdapter);
    }
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mview;

        public UserViewHolder(View itemView)
        {
            super(itemView);
            mview = itemView;

        }

        public void setName(String name)
        {
            TextView textname=(TextView) mview.findViewById(R.id.audn);
            textname.setText(name);
        }

        public void setStatus(String status) {
          TextView textstatus=(TextView) mview.findViewById(R.id.auds);
          textstatus.setText(status);
        }

        public void setTimage(final String thumb_image, final Context applicationContext) {
            final CircleImageView thumb_circle=(CircleImageView) mview.findViewById(R.id.audp);
            Picasso
                    .with(applicationContext)
                    .load(thumb_image)
                    .networkPolicy(NetworkPolicy.OFFLINE) //It brings the image from cache memory
                    .placeholder(R.drawable.avatar)
                    .into(thumb_circle, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            //------------If error occur load the image online from cloud-------------//

                            Picasso
                                    .with(applicationContext)
                                    .load(thumb_image)
                                    .placeholder(R.drawable.avatar)
                                    .into(thumb_circle);

                        }
                    });

        }
    }
}
