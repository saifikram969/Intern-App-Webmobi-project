package com.example.internapp_webmobi_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Update_Profile_Activity extends AppCompatActivity {

    public CircleImageView pic;
    public TextView chnge;
    public Button upload,delete;
    private static final int pick_image = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private Uri imguri;
    FirebaseAuth mauth;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseFirestore firestore;
    private static final String KEY_IMAGE_URI = "imageURL"; // Key to store image URI in savedInstanceState


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        pic=findViewById(R.id.profile_pic);
        chnge=findViewById(R.id.chng);
        upload=findViewById(R.id.uplod);
        delete=findViewById(R.id.del);
        mauth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        firestore=FirebaseFirestore.getInstance();
        chnge.setOnClickListener(view->{
            chooseImage();
        });
        upload.setOnClickListener(view->{
            uploadImage();
        });
        delete.setOnClickListener(view-> deleteImage());
            // Check if there is a saved image URI when the activity is created/recreated
            if(savedInstanceState!=null && savedInstanceState.getParcelable(KEY_IMAGE_URI)!=null){
            imguri=savedInstanceState.getParcelable(KEY_IMAGE_URI);

            // to display selected image in image view
                pic.setImageURI(imguri);
            }
        // method for reterving the profilepic from database
        retrieveProfileImage();

    }

    // method for reterving  and display the profilepic from database

    private void retrieveProfileImage() {
        FirebaseUser currentuser = mauth.getCurrentUser();
        String userID = currentuser.getUid();
        // Get the reference to the user's profile image in Firebase Storage
        DocumentReference documentReference = firestore.collection("User").document(userID);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String imgurl = documentSnapshot.getString("imageURl");
                // Load image into CircleImageView using Glide
                if (imgurl != null && !imgurl.isEmpty()) {
                   Glide.with(this)
                           .load(imgurl)
                           .apply(RequestOptions.circleCropTransform())

                           .placeholder(R.drawable.login_vector)
                           .error(R.drawable.person)
                           .into(pic);
                }
            }

        }).addOnFailureListener(e -> Log.e("ProfileActivity", "Error fetching document:" + e.getMessage()));

    }
    // for choosing the image
    private void chooseImage() {
        Intent i =new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"choose image"),pick_image);

    }
    //for uploading the image and retriving the exixting image

    private void uploadImage() {
        FirebaseUser currentuser= mauth.getCurrentUser();
        if (currentuser!=null&&imguri!=null)
        {
            String userid=currentuser.getUid();
            StorageReference imgstorageref=storageReference.child("user_img/"+userid+"/"+imguri.getLastPathSegment());
            // for Fetch the existing image URL from Firestore

            DocumentReference documentReference = firestore.collection("User").document(userid);
            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String existingimage = documentSnapshot.getString("imageURL");


                    if (existingimage != null && !existingimage.isEmpty()) {

                        // delete the previous image from firebase storage

                        StorageReference existingimageref = storage.getReferenceFromUrl(existingimage);
                        existingimageref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                uploadnewimage(imgstorageref, userid);
                            }
                        }).addOnFailureListener(e -> {
                            Log.e("Delete Image", "erroe in deleting previous image" + e.getMessage());
                            uploadnewimage(imgstorageref, userid);
                        });
                    } else {
                        //if no exiting image is found in firesStore
                        uploadnewimage(imgstorageref, userid);
                    }
                }
            }).addOnFailureListener(e -> {
                Log.e("FetchImageURl", "Error fetching existing image" + e.getMessage());
            });
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
        }

    private void uploadnewimage(StorageReference imgstorageref, String userid) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.progress_dialog, null);
        ProgressBar progressBarDialog = dialogView.findViewById(R.id.progressBar);
        TextView textViewCustom = dialogView.findViewById(R.id.textViewCustom);
        textViewCustom.setText("Your image uploading");
        dialogBuilder.setView(dialogView);
        //dialogBuilder.setTitle("Your data updating");

        dialogBuilder.setCancelable(false);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        imgstorageref.putFile(imguri)
                .addOnSuccessListener(taskSnapshot -> {
                    imgstorageref.getDownloadUrl().addOnSuccessListener(uri -> {
                        dialog.dismiss();
                        String dwnldurl = uri.toString();
                        updateProfile(dwnldurl, userid);
                        Toast.makeText(this, "uploaded successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                    });
                }).addOnFailureListener(e -> {
                            dialog.dismiss();
                            Log.e("Upload", "Error uploading image: ", e);
                            Toast.makeText(this, "Upload failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                );
    }
    //thids method help to save the image in firestore
    private void updateProfile(String dwnldurl, String userid) {
        Map<String, Object> hashmap = new HashMap<>();
        hashmap.put("imageURl", dwnldurl);

        DocumentReference documentReference = firestore.collection("User").document(userid);
        documentReference.update(hashmap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FIRESTORE", "Profile image url updated Successfully!!");
                    }
                }).addOnFailureListener(e -> Log.e("FIRESTORE", "Error updating profile image url:", e));

    }

    // this method used for set the selected image in Circular image view
    protected void onActivityResult(int requestcode, int resultcode, @Nullable Intent data) {
        super.onActivityResult(requestcode, resultcode, data);
        if (requestcode == pick_image && resultcode == RESULT_OK && data != null && data.getData() != null) {
            imguri = data.getData();
            try {
                //display the selcted image
                pic.setImageURI(imguri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //allowing  to restore the selected image URI after the activity is recreated.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the imguri when the activity is about to be destroyed (e.g., on screen rotation)
        if (imguri != null) {
            outState.putParcelable(KEY_IMAGE_URI, imguri);
        }
    }

    private void deleteImage() {
    }





}