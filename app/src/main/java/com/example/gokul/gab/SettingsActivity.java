package com.example.gokul.gab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentuser;
    private CircleImageView mdp;
    private TextView mname;
    private TextView mstatus;
    private Button status_up;
    private Button new_dp;
    private StorageReference mStorageRef;
    private ProgressDialog mprogdiag;
    public final int Pic_Gal = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mdp = (CircleImageView) findViewById(R.id.dp);
        mname = (TextView) findViewById(R.id.dn);
        mstatus = (TextView) findViewById(R.id.sta);
        status_up = (Button) findViewById(R.id.stabut);
        new_dp = (Button) findViewById(R.id.dpbut);

        //To get current user
        mCurrentuser = FirebaseAuth.getInstance().getCurrentUser();
        String cur_uid = mCurrentuser.getUid();
        //Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(cur_uid);

        /*------------------------------OFFLINE CAPABILITIES BEGIN---------------------------------*/

        //Though only retrives string,not the image
        // As images is stored in the form of url

        mDatabase.keepSynced(true);

        /*------------------------------OFFLINE CAPABILITIES END---------------------------------*/


        //Firebase storage
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mDatabase.addValueEventListener(new ValueEventListener() {
            //For adding,retriving,modifying
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                mname.setText(name);
                mstatus.setText(status);
                //The if statement is used not to load image if it is set to default.Because if it loads then blank image will be shown as there is nothing stored as the dp for the particular user
                  if (!image.equals("default")){


                      /*-------------------- networkPolicy(NetworkPolicy.OFFLINE)in line no 100 is used to retrieve image offline---------------------*/
                Picasso.with(SettingsActivity.this)
                        .load(image)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.avatar)//The placeholder is used to show the default avatar until the image gets loaded into the circular image view i.e:into avatar
                        .into(mdp, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                                //------------If error ouccur load the image online from cloud-------------//

                                Picasso.with(SettingsActivity.this)
                                        .load(image)
                                        .placeholder(R.drawable.avatar)//The placeholder is used to show the default avatar until the image gets loaded into the circular image view i.e:into avatar
                                        .into(mdp);


                            }
                        });
            }
            }

            //For handling the errors
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        status_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status_value = mstatus.getText().toString();
                Intent sta_in = new Intent(SettingsActivity.this, StatusActivity.class);
                sta_in.putExtra("status_value", status_value);
                startActivity(sta_in);
            }
        });
        new_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //ArthurHub code for selecting and croping an image
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);

                 /*
                Intent gallery_in=new Intent();

                // Show only images, no videos or anything else

                gallery_in.setType("image/*");
                gallery_in.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery_in,"Select Image"),Pic_Gal);
                */


            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mprogdiag=new ProgressDialog(SettingsActivity.this);
                mprogdiag.setTitle("Updating");
                mprogdiag.setMessage("Your Display Picture is being Uploaded");
                mprogdiag.setCanceledOnTouchOutside(false);
                mprogdiag.show();
                //Uniform Resource Identifier is a string of characters used to identify a resource.i.e:either by location or a name or both
                //Here using uri we found cropped image resource
                Uri resultUri = result.getUri();

                //Here we are using thumb_file to store the path of the resource we found using resultUri
               File thumb_file=new File(resultUri.getPath());


                //final data type is used to declare variable only within this function
                final String cur_userid=mCurrentuser.getUid();

                //Bitmap is to used to store images in the form of series of tiny dots called pixels
                //Each pixel is actually a very small square that is assigned a color and then arranged in a pattern to form the image
                //Below code is used to make the custom bitmap compressor which determines the quality of the image as 75 instead of 100
               Bitmap compressedImageBitmap = new Compressor(this)
                       .setMaxWidth(200)
                       .setMaxHeight(200)
                       .setQuality(75)
                       .compressToBitmap(thumb_file);

               // Get the data from an ImageView as bytes and store it in thumb_byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                //Refering to file storage
                 //Assigning the current users id as images name
                StorageReference filepath=mStorageRef.child("profile_images").child(cur_userid + ".jpg");
                //Here same as above but a new folder called thumbnail is created to store compressed image
                final StorageReference thumb_filepath=mStorageRef.child("profile_images").child("thumbnails").child(cur_userid + ".jpg");
                //placing the obtained cropped image using resultUri and placing it the assigned filepath
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                   if(task.isSuccessful())
                   {
                   mStorageRef.child("profile_images").child(cur_userid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           final String DL_Url=uri.toString();
                           UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                           uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                   if (task.isSuccessful()){
                                       //Beginning of thumbnail storage Url assignment to Database by downloading and then assigning it to database thumb_image variable
                                       mStorageRef.child("profile_images").child("thumbnails").child(cur_userid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                           @Override
                                           public void onSuccess(Uri uri) {
                                           String thumb_DLurl=uri.toString();
                                           mDatabase.child("thumb_image").setValue(thumb_DLurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if (task.isSuccessful())
                                                   {

                                                       mprogdiag.dismiss();
                                                       Toast.makeText(SettingsActivity.this, "Thumbnail updated successfully", Toast.LENGTH_SHORT).show();
                                                   }
                                                   else
                                                       {

                                                       Toast.makeText(SettingsActivity.this, "Error in uploading thumbnail", Toast.LENGTH_SHORT).show();
                                                       mprogdiag.dismiss();
                                                   }
                                               }
                                           });

                                           }
                                       });   //Ending of Thumb_nail url assignment to database thumb_image variable


                                       mDatabase.child("image").setValue(DL_Url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()){
                                                   mprogdiag.dismiss();
                                                   Toast.makeText(SettingsActivity.this, "Display picture is updated sucessfully ", Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       });

                                   }
                               }
                           });


                       }
                   });


                   }
                   else
                       {
                       Toast.makeText(SettingsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                           mprogdiag.dismiss();
                   }


                    }
                });

            }
            //If error in cropping image then it provides exception i.e:the crop image result code error.
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
                
            }
        }
    }
}

