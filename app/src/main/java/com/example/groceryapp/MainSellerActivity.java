package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainSellerActivity extends AppCompatActivity {
private TextView nametext,mshopnametext,memailtext,mtabPoductsTV,mtabOrdersTV,mfilteredProductTV;
private EditText msearchproductET;
private ImageButton logoutbtn,meditProfilebtn,maddproductbtn,mfilterProductbtn;
private  ImageView mprofileIv;
private RecyclerView mproductsRV;

private RelativeLayout mproductRL,morderRL;
private FirebaseAuth firebaseAuth;
private ProgressDialog progressDialog;

private ArrayList<ModelProduct> productList;
private AdapterProductSeller adapterProductSeller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);

        nametext=findViewById(R.id.Nametv);
        logoutbtn=findViewById(R.id.logoutbtnIV);
        maddproductbtn=findViewById(R.id.addproductbtnV);
        mprofileIv=findViewById(R.id.profileIV);
        mshopnametext=findViewById(R.id.shopNametv);
        memailtext=findViewById(R.id.Emailidtv);
        mtabPoductsTV=findViewById(R.id.tabPoductsTV);
        mtabOrdersTV=findViewById(R.id.tabOrdersTV);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        meditProfilebtn=findViewById(R.id.editprofilebtnV);
        mproductRL=findViewById(R.id.productRL);
        morderRL=findViewById(R.id.orderRL);
        msearchproductET=findViewById(R.id.searchproductET);
        mfilterProductbtn=findViewById(R.id.filterProductbtn);
        mfilteredProductTV=findViewById(R.id.filteredProductTV);
        mproductsRV=findViewById(R.id.productsRV);

        firebaseAuth=FirebaseAuth.getInstance();
        checkUser();
        loadAllProducts();
        showproductsUI();
        //search
        msearchproductET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
             adapterProductSeller.getFilter().filter(s);
                }
                catch (Exception e){
             e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makemeOffline();
              firebaseAuth.signOut();
              checkUser();
            }
        });
        meditProfilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start edit profile activity
                startActivity(new Intent(MainSellerActivity.this,ProfileSellerActivity.class));
            }
        });
        maddproductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //open edit add product activity
                startActivity(new Intent(MainSellerActivity.this,AddProductActivity.class));
            }
        });

        mtabPoductsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //load products
                showproductsUI();
            }
        });
        mtabOrdersTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load orders
                showordersUI();
            }
        });
        mfilterProductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Choose Category :")
                        .setItems(Constants.productcategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected items
                                String selected=Constants.productcategories1[which];
                                mfilteredProductTV.setText(selected);
                                if(selected.equals("All")){
                                 //load all
                                    loadAllProducts();
                                }else{
                                    //load filtered
                                    loadFilteredProducts(selected);
                                }

                            }
                        }).show();
            }
        });
    }

    private void loadFilteredProducts(String selected) {
        productList=new ArrayList<>();
        //get all products
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot datasnapshot) {
                        //before getting reset list
                        productList.clear();
                        for(DataSnapshot ds: datasnapshot.getChildren()){

                            String productCategory=""+ds.child("productCategory").getValue();
                            //if selected category matches product category then add in list
                            if(selected.equals(productCategory)){
                                ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }


                        }
                        //setup adapter
                        adapterProductSeller=new AdapterProductSeller(MainSellerActivity.this,productList);
                        //set Adapter
                        mproductsRV.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
    }

    private void loadAllProducts() {
        productList=new ArrayList<>();
        //get all products
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot datasnapshot) {
                        //before getting reset list
                        productList.clear();
                        for(DataSnapshot ds: datasnapshot.getChildren()){
                            ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        //setup adapter
                        adapterProductSeller=new AdapterProductSeller(MainSellerActivity.this,productList);
                        //set Adapter
                        mproductsRV.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

    }

    private void showordersUI() {
        mproductRL.setVisibility(View.GONE);
        morderRL.setVisibility(View.VISIBLE);

        mtabPoductsTV.setTextColor(getResources().getColor(R.color.white));
        mtabPoductsTV.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mtabOrdersTV.setTextColor(getResources().getColor(R.color.Grey03));
        mtabOrdersTV.setBackgroundResource(R.drawable.shape_rect04);

    }

    private void showproductsUI() {
        mproductRL.setVisibility(View.VISIBLE);
        morderRL.setVisibility(View.GONE);

        mtabPoductsTV.setTextColor(getResources().getColor(R.color.Grey03));
        mtabPoductsTV.setBackgroundResource(R.drawable.shape_rect04);

        mtabOrdersTV.setTextColor(getResources().getColor(R.color.white));
        mtabOrdersTV.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    private void makemeOffline() {
        progressDialog.setTitle("Logging Out...");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        //update value to db
        //save to db
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        firebaseAuth.signOut();
                        checkUser();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                //failed to update db
                progressDialog.dismiss();
                Toast.makeText(MainSellerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void checkUser() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            startActivity(new Intent(MainSellerActivity.this,LoginActivity.class));
            finish();

        }else{
            loadmyInfo();
        }
    }

    private void loadmyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          for(DataSnapshot ds:dataSnapshot.getChildren()){
                              String name=""+ds.child("name").getValue();
                              String accountType=""+ds.child("accountType").getValue();
                              String email=""+ds.child("email").getValue();
                              String shopName=""+ds.child("shopName").getValue();
                              String profileImage=""+ds.child("profileImage").getValue();

                              nametext.setText(name);
                             memailtext.setText(email);
                              mshopnametext.setText(shopName);
                              try{
                                  Picasso.get().load(profileImage).placeholder(R.drawable.ic_storeicon_grey).into(mprofileIv);
                              }
                              catch(Exception e){
                                 mprofileIv.setImageResource(R.drawable.ic_storeicon_grey);
                              }
                          }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}