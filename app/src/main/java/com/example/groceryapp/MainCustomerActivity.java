package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
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

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class MainCustomerActivity extends AppCompatActivity {
    private TextView nametext,memailtext,mphonetext,mtabPoductsTV,mtabOrdersTV,mfilteredProductTV;
    private EditText msearchproductET;
    private ImageButton logoutbtn,meditProfilebtn,mcontactbtn,mfilterProductbtn,mcartbtn;
    private RecyclerView mproductsRV;
    private FirebaseAuth firebaseAuth;
    ImageView mprofileIv;
    RelativeLayout mproductRL,morderRL;
    private ProgressDialog progressDialog;
private String myLat,myLong;
    private String shopUid;
    private ArrayList<ModelProduct> productList;
    private AdapterProductUser adapterProductUser;
    private String deliveryFee;
    private static final String TAG = "MyActivity";

    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_customer);

        nametext=findViewById(R.id.Nametv);
        memailtext=findViewById(R.id.Emailidtv);
        mphonetext=findViewById(R.id.phoneTv);
        mcontactbtn=findViewById(R.id.contactusbtn);
       mcartbtn =findViewById(R.id.cartbtn);
        logoutbtn=findViewById(R.id.logoutbtnIV);
      meditProfilebtn=findViewById(R.id.editprofilebtnV);
      mprofileIv=findViewById(R.id.profileIV);
        mtabPoductsTV=findViewById(R.id.tabPoductsTV);
        mtabOrdersTV=findViewById(R.id.tabOrdersTV);
        mproductsRV=findViewById(R.id.customershowProductRV);

        mproductRL=findViewById(R.id.productRL);
        morderRL=findViewById(R.id.orderRL);
        msearchproductET=findViewById(R.id.searchproductET);
        mfilterProductbtn=findViewById(R.id.filterProductbtn);
        mfilteredProductTV=findViewById(R.id.filteredProductTV);

        //
        shopUid="d6gtTJ7p7PVSJ9EDVWdLZkY0b7X2";
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).addValueEventListener(new ValueEventListener() {
                                                     @Override
                                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                       deliveryFee=""+snapshot.child("deliveryFee").getValue();
                                                     }

                                                     @Override
                                                     public void onCancelled(@NonNull  DatabaseError error) {

                                                     }
                                                 });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth=FirebaseAuth.getInstance();
        checkUser();
        loadAllProducts();
        showproductsUI();



        msearchproductET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    // add from video
                    adapterProductUser.getFilter().filter(s);
                    //
                    //
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

            }
        });
        mcartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //open cart
                openCartDialog();

            }
        });
       meditProfilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //start edit profile activity
                startActivity(new Intent(MainCustomerActivity.this,ProfileCustomerActivity.class));
            }
        });
        mcontactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open edit add product activity
                Intent i=new Intent(MainCustomerActivity.this,ContactSeller.class);
                i.putExtra("myLat",myLat);
                i.putExtra("myLong",myLong);
                startActivity(i);
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
                AlertDialog.Builder builder=new AlertDialog.Builder(MainCustomerActivity.this);
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
                                    adapterProductUser.getFilter().filter(selected);
                                }

                            }
                        }).show();
            }
        });

    }



    public  double allmrpsum = 0.00;
    public  double alltaxessum = 0.00;
    public  double hencetotal = 0.00;




    public TextView mstotalMrpTV,mdfeeTV,mtaxtv,mfinaltotalTV;
    private void openCartDialog() {

        cartItemList=new ArrayList<>();
        //inflate cart layoout

        View view=LayoutInflater.from(MainCustomerActivity.this).inflate(R.layout.dialog_cart,null);
    RecyclerView mcartItemRV=view.findViewById(R.id.cartItemRV);
         mstotalMrpTV=view.findViewById(R.id.sMrpTV);
        mdfeeTV=view.findViewById(R.id.sdFeeTV);
         mtaxtv=view.findViewById(R.id.sTaxtv);
        mfinaltotalTV=view.findViewById(R.id.sTotalTV);
       Button mplaceorderBtn=view.findViewById(R.id.placeorderBtn);



       AlertDialog.Builder builder= new AlertDialog.Builder(this);
       builder.setView(view);

        EasyDB easyDB=EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_Code",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text"}))
                .addColumn(new Column("Item_Price",new String[]{"text"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text"}))
                .addColumn(new Column("Item_Cess",new String[]{"text"}))
                .addColumn(new Column("Item_Hsn_Code",new String[]{"text"}))
                .addColumn(new Column("Item_Cgst",new String[]{"text"}))
                .addColumn(new Column("Item_Igst",new String[]{"text"}))
                .addColumn(new Column("Item_Sgst",new String[]{"text"}))
                .doneTableColumn();

        //get all records from db

        Cursor res=easyDB.getAllData();


        while (res.moveToNext()){

            String id=res.getString(1);
            String code=res.getString(2);
            String name=res.getString(3);
            String priceeach=res.getString(4);
            String price=res.getString(5);
            String quantity=res.getString(6);
            String cess=res.getString(7);
            String hsn=res.getString(8);
            String cgst=res.getString(9);
            String igst=res.getString(10);
            String sgst=res.getString(11);

            allmrpsum= allmrpsum+(Double.parseDouble(priceeach)*Double.parseDouble(quantity));
            if(cess==null)
                cess="0";
            if(hsn==null)
                hsn="0";
            if(cgst==null)
                cgst="0";
            if(igst==null)
                igst="0";
            if(sgst==null)
                sgst="0";
            Double tax=Double.parseDouble(cess)+Double.parseDouble(sgst)+Double.parseDouble(cgst)+
                    Double.parseDouble(igst)+Double.parseDouble(hsn);
            alltaxessum =alltaxessum+Double.parseDouble(String.valueOf(tax))*Double.parseDouble(quantity);
            ModelCartItem modelCartItem=new ModelCartItem(
             ""+id,
              ""+code,
                    ""+name,
                    ""+priceeach,
                    ""+quantity,
                    ""+cess,
                    ""+hsn,
                    ""+igst,
                    ""+cgst,
                    ""+sgst
            );
            cartItemList.add(modelCartItem);
            Log.i(TAG,"cursor moved");

        }
        //setup adapter
        adapterCartItem=new AdapterCartItem(this,cartItemList);
        mcartItemRV.setAdapter(adapterCartItem);


        mdfeeTV.setText("RS. "+deliveryFee);
        mstotalMrpTV.setText("Rs. "+String.format("%.2f",allmrpsum));
        mtaxtv.setText("Rs. "+String.format("%.2f",alltaxessum));
        mfinaltotalTV.setText("Rs. "+(alltaxessum+allmrpsum+Double.parseDouble(deliveryFee.replaceAll("Rs. ",""))));

       //show dialog
        AlertDialog dialog=builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allmrpsum=0.0;
                alltaxessum=0.0;
            }
        });

    }


    private void loadAllProducts() {
        // add from video

        productList= new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       productList.clear();
                       for (DataSnapshot ds: snapshot.getChildren()){
                           ModelProduct modelProduct =ds.getValue(ModelProduct.class);
                           productList.add(modelProduct);
                       }
                       adapterProductUser=new AdapterProductUser(MainCustomerActivity.this,productList);
                       mproductsRV.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
                Toast.makeText(MainCustomerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void checkUser() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            startActivity(new Intent(MainCustomerActivity.this,LoginActivity.class));
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
                            String phonenumber=""+ds.child("phone").getValue();
                            String city=""+ds.child("city").getValue();
                            myLat=""+ds.child("latitude").getValue();
                            myLong=""+ds.child("longitude").getValue();

                            String profileImage=""+ds.child("profileImage").getValue();
                            nametext.setText(name);
                            memailtext.setText(email);
                            mphonetext.setText(phonenumber);
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