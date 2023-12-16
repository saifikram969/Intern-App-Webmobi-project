package com.example.internapp_webmobi_project;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class Edit_Profile extends AppCompatActivity {

     public AppCompatTextView profileUpdate;
     public CircleImageView profilepic;
    public TextInputLayout pname, phone, pEmail, txtcity,txtintern,txtdob,txtapplied ;
    public TextInputEditText ename, eEmail, enumber, editcity,editintern,editdob,editapply;
    public BottomNavigationView bottomNavigationView;
    FirebaseFirestore firestore;
    StorageReference storageReference;
    FirebaseAuth mauth;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        bottomNavigationView=findViewById(R.id.nav_view);
//called textInputLayout id
        pname = findViewById(R.id.pname);
        phone = findViewById(R.id.pphn);
        pEmail = findViewById(R.id.pmail);
        txtcity = findViewById(R.id.txtcity);
        txtintern=findViewById(R.id.pintern);
        txtapplied=findViewById(R.id.app_date);
        txtdob=findViewById(R.id.txtdob);

        // called TextInputEditText id
        ename = pname.findViewById(R.id.ename);
        eEmail =pEmail.findViewById(R.id.eEmail);
        enumber =phone.findViewById(R.id.Enum);
        editcity =txtcity.findViewById(R.id.editcity);
        editintern=txtintern.findViewById(R.id.intern);
        editapply=txtapplied.findViewById(R.id.apply);
        editdob=txtdob.findViewById(R.id.dob);

        profileUpdate=findViewById(R.id.btnupdate);
        profilepic=findViewById(R.id.sign_up_icon);


        // for fetching the data from firebase

        firestore=FirebaseFirestore.getInstance();
        FirebaseStorage storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        mauth=FirebaseAuth.getInstance();
        userid=mauth.getCurrentUser().getUid();
        DocumentReference documentReference=firestore.collection("User").document(userid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                ename.setText(value.getString("Name"));
                eEmail.setText(value.getString("Email id"));
                enumber.setText(value.getString("Mobile Number"));
                editintern.setText(value.getString("Internship"));
                editcity.setText(value.getString("City"));
                editapply.setText(value.getString("Applied On"));
                editdob.setText(value.getString("DOB"));
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


        // for updating the data
        profileUpdate.setOnClickListener(view->{
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Edit_Profile.this);
            View dialogView = getLayoutInflater().inflate(R.layout.progress_dialog, null);
            ProgressBar progressBarDialog = dialogView.findViewById(R.id.progressBar);
            TextView textViewCustom = dialogView.findViewById(R.id.textViewCustom);
            textViewCustom.setText("Your data updating");
            dialogBuilder.setView(dialogView);
            //dialogBuilder.setTitle("Your data updating");

            dialogBuilder.setCancelable(false);
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
            // trim data for storing it
            String Name=ename.getText().toString().trim();
            String Email=eEmail.getText().toString().trim();
            String num=enumber.getText().toString().trim();
            String intern=editintern.getText().toString().trim();
            String apply=editapply.getText().toString().trim();
            String  city=editcity.getText().toString().trim();
            String dob=editdob.getText().toString().trim();
            HashMap<String,Object>hashMap=new HashMap<>();
            hashMap.put("Name",Name);
            hashMap.put("Email id",Email);
            hashMap.put("Mobile Number",num);
            hashMap.put("Internship",intern);
            hashMap.put("Applied On",apply);
            hashMap .put("City",city);
            hashMap.put("DOB",dob);
            userid=mauth.getCurrentUser().getUid();

            // fro updating the data instead of using "SET" we use "UPDATE'
            DocumentReference df=firestore.collection("User").document(userid);
            df.update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dialog.dismiss();
                    Toast.makeText(Edit_Profile.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Successfullt updated");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(Edit_Profile.this, "Error Updating profile"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Error updating Docuemnt"+e.toString());
                }
            });



        });

        // for updating the profile
        profilepic.setOnClickListener(view ->{

        });
    }
}
