package com.example.internapp_webmobi_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public AppCompatTextView name ,email, user;
    public BottomNavigationView bottomNavigationView;
    public CircleImageView pic;
    FirebaseAuth mauth;
    FirebaseFirestore firestore;
    StorageReference storageReference;
    String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView=findViewById(R.id.btmnav);
        name=findViewById(R.id.dname);
        email=findViewById(R.id.dmail);
        pic=findViewById(R.id.dashpic);
        user=findViewById(R.id.duser);

        //for bottom navigation view

        // from selecting navigation as home
        bottomNavigationView.setSelectedItemId(R.id.navhome);
        bottomNavigationView.setSelectedItemId(R.id.navlog);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemid= item.getItemId();
                if(itemid==R.id.navprofile)
                {
                    startActivity(new Intent(getApplicationContext(), Edit_Profile.class));
                }//dialouge
                else if (itemid==R.id.navlog) {
                    logoutMenu(MainActivity.this);
                }
                return true;
            }
        });

        // for getting data from firebase firestore
        mauth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        FirebaseStorage storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        userid=mauth.getCurrentUser().getUid();
        DocumentReference documentReference=firestore.collection("User").document(userid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name.setText(value.getString("Name"));
                email.setText(value.getString("Email id"));
                user.setText(value.getString("User id"));
            }
        });
        retrieveprofile();
    }
//logout method dialouge
    private void logoutMenu(MainActivity mainActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout ? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               finish();
            }
        });
        builder.setNegativeButton("N0", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void retrieveprofile() {
        // Get the reference to the user's profile image in Firebase Storage
        DocumentReference documentReference = firestore.collection("User").document(userid);
        documentReference.get().addOnSuccessListener(documentSnapshot ->
        {
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
            } else {
                Log.d("MAinActivity", "no such document");
            }
        }).addOnFailureListener(e -> Log.e("MainActivity", "Error fetching document:" + e.getMessage()));
    }

}