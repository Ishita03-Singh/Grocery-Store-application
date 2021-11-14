package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
private ImageButton mbackbtn;
private EditText memail;
private Button mrecover;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mbackbtn=findViewById(R.id.iconbtnIV);
        memail=findViewById(R.id.emailtext);
        mrecover=findViewById(R.id.recoverButton);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        mbackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();

            }
        });
        mrecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               recoverPasswrd();

            }
        });

    }
private String emailid;
    private void recoverPasswrd() {
       emailid=memail.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(emailid).matches()){
            Toast.makeText(this,"Invalid Email Address",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Sending Instructions to reset Password...");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(emailid)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this,"Password reset instructions sent to your emailid",Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }
}