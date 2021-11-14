package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class  LoginActivity extends AppCompatActivity {
 private EditText memailtxt,mpasswordtxt;
 private TextView mforgottv,noaccounttv;
 private Button mloginbtn;


    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        memailtxt=findViewById(R.id.emailtext);
        mpasswordtxt=findViewById(R.id.passwordtext);
        mforgottv=findViewById(R.id.forgetTV);
        noaccounttv=findViewById(R.id.noAccountTV);
        mloginbtn=findViewById(R.id.loginButton);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

       noaccounttv.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(LoginActivity.this,RegisterCustomerActivity.class));
               finish();
           }
       });
      mforgottv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });

        mloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            loginUser();

            }
        });
    }
private String emailid,password;
    private void loginUser() {
       emailid=memailtxt.getText().toString().trim();
        password=mpasswordtxt.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(emailid).matches()){
            Toast.makeText(this,"Invalid Email Address",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Enter password...",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(emailid,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                   // logged in successfully
                        makeMeOnline();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        //failed logging in
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void makeMeOnline() {
        //after log in maake user online
        progressDialog.setTitle("Checking User...");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("online","true");

        //update value to db
        //save to db
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //db updated successfully
                         checkUserType();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                //failed to update db
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void checkUserType() {
        //if user is seller start seller activity
        //if user is customer start customer activity
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            String accountType=""+ds.child("accountType").getValue();
                            if(accountType.equals("Seller") )
                            {progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this,MainSellerActivity.class));
                                finish();

                            }else{
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this,MainCustomerActivity.class));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
    }
}