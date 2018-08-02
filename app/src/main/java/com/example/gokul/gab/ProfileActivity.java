package com.example.gokul.gab;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {
    private ImageView mprofiledp;
    private TextView mprofiledn;
    private TextView mprofileds;
    private TextView mprofiletf;
    private Button mrequestbutton;
    private Button mdenybut;
    private DatabaseReference profileDB;
    private ProgressDialog proglog;
    private int mCur_state;


    /*-----------------------------------------Request Database Structure-----------------------------------------*/
    //This new reference is declared because we don't want to point to users instance or table
    //We need to point to a new table called request in which we store two values which is user id of the person who is making request and user id of person who has received the request
    //DB Format:
             //First reference takes place
    //           + Users
             //Second reference takes place
    //            - Request
    //             1. user1
    //                user2
    //                   request  "sent"
    //             2.user2
    //                user1
    //                   request "received


    private DatabaseReference requestDB;
    private FirebaseUser mCurre_User;
    private DatabaseReference friendDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //From the below statement we get the user id of the particular person who is viewed from a list of users
        //That is if user_1 views a list of users then he may want to view a particular user profile lets take that particular user as user_2 so user_2 id is passed using intent and obtained below
        final String User_id=getIntent().getStringExtra("user_id");

        profileDB= FirebaseDatabase.getInstance().getReference().child("Users").child(User_id);

        profileDB.keepSynced(true);

        requestDB=FirebaseDatabase.getInstance().getReference().child("Request");// This reference does not point to users it points to a new instance called Requests i.e:This reference does not point to user table it point to a new table called Request.

        requestDB.keepSynced(true);

        friendDB=FirebaseDatabase.getInstance().getReference().child("Friends");//This reference points to new table called Friends it doesn't point neither Users nor Friends.

        friendDB.keepSynced(true);

        mCurre_User= FirebaseAuth.getInstance().getCurrentUser();

        mprofiledp=(ImageView)findViewById(R.id.pimageView);
        mprofiledn=(TextView)findViewById(R.id.pdn);
        mprofileds=(TextView)findViewById(R.id.pds);
        mprofiletf=(TextView)findViewById(R.id.pdtotalfrnds);
        mrequestbutton=(Button)findViewById(R.id.pbutton);
        mdenybut=(Button)findViewById(R.id.pdenybutton);

       mCur_state=0; // 0 Represents not a friend

        //Progressbar
        proglog=new ProgressDialog(this);
        proglog.setTitle("User Loading");
        proglog.setMessage("Please wait the user data is being loaded");
        proglog.setCanceledOnTouchOutside(false);
        proglog.show();

        profileDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              String mprofile_dn=dataSnapshot.child("name").getValue().toString();
              String mprofile_ds=dataSnapshot.child("status").getValue().toString();
              final String mprofile_dp=dataSnapshot.child("image").getValue().toString();
              mprofiledn.setText(mprofile_dn);
              mprofileds.setText(mprofile_ds);


              Picasso
                      .with(ProfileActivity.this)
                      .load(mprofile_dp)
                      .networkPolicy(NetworkPolicy.OFFLINE) //It brings the image from cache memory
                      .placeholder(R.drawable.avatar)
                      .into(mprofiledp, new Callback() {
                          @Override
                          public void onSuccess() {

                          }

                          @Override
                          public void onError() {

                              //------------If error occur load the image online from cloud-------------//

                              Picasso
                                      .with(ProfileActivity.this)
                                      .load(mprofile_dp)
                                      .placeholder(R.drawable.avatar)
                                      .into(mprofiledp);

                          }
                      });

              //Friend List / Request feature
                //Here it refers to the current user id in the Request database
              requestDB.child(mCurre_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Here it refers to the child User_id that is the particular user that we need to check
                      if (dataSnapshot.hasChild(User_id)) {
                          //Here within that particular user the request attribute value is need to be checked so we retrive it here
                          String req_value = dataSnapshot.child(User_id).child("request").getValue().toString();

                          //Here if the request value equals received then the send request button of that particular person whom we sent request changes to Accept Request when he visits our profile.
                          if (req_value.equals("received")) {
                              //2 Represents that request has been received
                              mCur_state = 2;
                              mrequestbutton.setText("Accept Request");
                          }
                          //Here if the request value equals sent then the send request button of --US-- changes to Cancel Request
                          else if (req_value.equals("sent")) {
                              //1 Represent request has been sent
                              mCur_state = 1;
                              mrequestbutton.setText("Cancel Request");
                          }
                          proglog.dismiss();
                      }

                          else
                          {
                            friendDB.child(mCurre_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(User_id)){
                                        //3 Represents friends
                                        mCur_state=3;
                                        mrequestbutton.setText("Unfriend");
                                    }
                                    proglog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    proglog.dismiss();
                                }
                            });
                          }


                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {
                      proglog.dismiss();
                  }
              });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mrequestbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Once the button is set to false it cannot be clicked again
                mrequestbutton.setEnabled(false);

                /*-----------------------------------------Sending Friend Request------------------------------------------*/

                // 0 Represents not a friend
                // so if it is current state = 0 we can do the following as mentioned in Request Database at the top under variable type declaration
                if (mCur_state==0){
               requestDB.child(mCurre_User.getUid()).child(User_id).child("request").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       //now line no 46 takes place till 48
                  if (task.isSuccessful()){
                      requestDB.child(User_id).child(mCurre_User.getUid()).child("request").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void aVoid) {
                              mrequestbutton.setEnabled(true);
                              //1 Represents request has been sent
                              //Here it is set 1 because once the request is sent the user cannot send the request again
                              //As only if current state equals 0 the above function can be executed
                              mCur_state=1;

                              mrequestbutton.setText("Cancel Request");


                              Toast.makeText(ProfileActivity.this, "Request sent successfully", Toast.LENGTH_SHORT).show();
                          }
                      });

                  }else
                      {
                      Toast.makeText(ProfileActivity.this, "Request attempt failed", Toast.LENGTH_SHORT).show();
                  }
                   }
               });

                }

                /*--------------------------------------------Cancelling Friend Request------------------------------------------*/

                if (mCur_state==1){
                    //Here we are deleting Current user and it also has another id within it which is User_id and in which it has a attribute request and its value i.e value may be sent or received

                    requestDB.child(mCurre_User.getUid()).child(User_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            //Here we are deleting User_id and it also has another id within it which is Current user_id and in which it has a attribute request and its value i.e value may be sent or received

                            requestDB.child(User_id).child(mCurre_User.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mrequestbutton.setEnabled(true);
                                    //0 Represents request has not been sent
                                    //Here it is set 0 because once the request is not sent the user can send the request again
                                    //As only if current state equals 0 the above function can be executed
                                    mCur_state=0;

                                    mrequestbutton.setText("Make a Friend Request");
                                    Toast.makeText(ProfileActivity.this, "Request cancelled successfully", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    });

                }

                /*----------------------------------------------------Accepting Request-------------------------------------------------*/
                if(mCur_state==2){
                    final String Cur_Date= DateFormat.getDateTimeInstance().format(new Date());

                    friendDB.child(mCurre_User.getUid()).child(User_id).setValue(Cur_Date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendDB.child(User_id).child(mCurre_User.getUid()).setValue(Cur_Date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                               //Same thing taken from mCur_state==1
                                    //Here we are deleting mcurrent user and it also has another id within which is User_id and in which it has a attribute request and its value i.e value may be sent or received


                                    requestDB.child(mCurre_User.getUid()).child(User_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            //Here we are deleting User_id and it also has another id within which is Current user_id and in which it has a attribute request and its value i.e value may be sent or received

                                            requestDB.child(User_id).child(mCurre_User.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mrequestbutton.setEnabled(true);
                                                    //3 Represents friends
                                                    //Here it is set 0 because once the request is not sent the user can send the request again
                                                    //As only if current state equals 0 the above function can be executed
                                                    mCur_state=3;

                                                    mrequestbutton.setText("Unfriend");

                                                }
                                            });

                                        }
                                    });

                                }
                            });


                        }
                    });

                }

                /*-------------------------------------------------Un-Friend------------------------------------------------------*/
                if (mCur_state==3){
                    friendDB.child(mCurre_User.getUid()).child(User_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                       friendDB.child(User_id).child(mCurre_User.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               mrequestbutton.setEnabled(true);
                               //0 Represents request has not been sent
                               //Here it is set 0 because once the request is not sent the user can send the request again
                               //As only if line no 112 equals 0 the above function can be executed
                               mCur_state=0;

                               mrequestbutton.setText("Make a Friend Request");
                               //Toast.makeText(ProfileActivity.this, "Un-friend successful", Toast.LENGTH_SHORT).show();

                           }
                       });
                        }
                    });
                }

            }
        });
        mdenybut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCur_state==2)
                requestDB.child(User_id).child(mCurre_User.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Here we are deleting Current user and it also has another id within it which is User_id and in which it has a attribute request and its value i.e value may be sent or received

                        requestDB.child(mCurre_User.getUid()).child(User_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mCur_state=0;
                                mrequestbutton.setText("Make a Friend Request");
                                Toast.makeText(ProfileActivity.this, "Request Denied successfully", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
            }
        });

    }
}
/*-------------------------------------------------------------Friends Database Structure------------------------------------------------------------------*/
/*
* User1:
*       User 2:DD/MM/YY TIME(value)
*       User 3:DD/MM/YY TIME(value)
*       User 4:DD/MM/YY TIME(value)
*       User 5:DD/MM/YY TIME(value)
* User2:
*      User 1:DD/MM/YY TIME(value)
*      User 3:DD/MM/YY TIME(value)
*      User 4:DD/MM/YY TIME(value)
*      User 5:DD/MM/YY TIME(value)
*
*      ------------Continues-------------
*/