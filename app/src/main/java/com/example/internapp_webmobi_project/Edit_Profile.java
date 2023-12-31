package com.example.internapp_webmobi_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

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


public class Edit_Profile extends AppCompatActivity {

     public AppCompatTextView profileUpdate;
     public CircleImageView profilepic;

    public EditText ename, eEmail, enumber, addrerss,intern,resume,password;
    public BottomNavigationView bottomNavigationView;
    FirebaseFirestore firestore;
    StorageReference storageReference;
    FirebaseAuth mauth;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        bottomNavigationView = findViewById(R.id.nav_view);
        ename = findViewById(R.id.name);
        enumber = findViewById(R.id.phn);
        eEmail = findViewById(R.id.email);
        addrerss = findViewById(R.id.add);
        intern = findViewById(R.id.intern);
        resume = findViewById(R.id.resume);
        password = findViewById(R.id.ppassword);


        profilepic = findViewById(R.id.sign_up_icon);


        // for fetching the data from firebase

        firestore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mauth = FirebaseAuth.getInstance();
        userid = mauth.getCurrentUser().getUid();
        DocumentReference documentReference = firestore.collection("User").document(userid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                ename.setText(value.getString("Name"));
                eEmail.setText(value.getString("Email id"));
                enumber.setText(value.getString("Mobile Number"));
                intern.setText(value.getString("Internship"));
                addrerss.setText(value.getString("City"));
                resume.setText(value.getString(""));
                password.setText(value.getString("Password"));
            }
        });
        // for bottom navigation view
        bottomNavigationView.setSelectedItemId(R.id.navprofile);
        // it will keep the home selected

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemid = item.getItemId();
                if (itemid == R.id.navhome) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else if (itemid == R.id.navprofile) {
                    //startActivity(new Intent(getApplicationContext(), profile.class));

                }
                return true;
            }
        });

    }

}
