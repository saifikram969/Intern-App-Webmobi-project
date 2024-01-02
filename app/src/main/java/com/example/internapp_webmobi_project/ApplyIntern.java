package com.example.internapp_webmobi_project;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ApplyIntern extends AppCompatActivity {

    EditText editText;
    Button btn;
    StorageReference storageReference;
    DatabaseReference databaseReference;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_intern);
        editText = findViewById(R.id.editText);
        btn = findViewById(R.id.upload_pdf);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploadPDF");

        btn.setEnabled(false);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDF();
            }
        });

    }

    private void selectPDF() {
        Intent intent = new Intent();
        intent.setType("applicatin/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"PDF FILE SELECT"),12);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==12 && requestCode==RESULT_OK && data!=null && data.getData()!=null){
           btn.setEnabled(true);
           editText.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/")) + 1);

           btn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   uploadPDFFileFirebase(data.getData());


               }
           });
        }
    }

    private void uploadPDFFileFirebase(Uri data) {

       final ProgressDialog progressDialog = new ProgressDialog(this);
       progressDialog.setTitle("File is loading... ");
       progressDialog.show();
       StorageReference reference = storageReference.child("uploadPDF"+System.currentTimeMillis()+" .pdf");
       reference.putFile(data)
               .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                       Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                       while (!uriTask.isComplete());
                       Uri uri = uriTask.getResult();

                       pdfAddicted pdfAddicted = new pdfAddicted(editText.getText().toString(),uri.toString());
                       databaseReference.child(databaseReference.push().getKey()).setValue(pdfAddicted);
                       Toast.makeText(ApplyIntern.this,"File upload",Toast.LENGTH_LONG).show();
                       progressDialog.dismiss();

                   }
               }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                       double progress=(100* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                       progressDialog.setMessage("File Uploaded..."+ (int) progress+ "%");

                   }
               });

    }
}