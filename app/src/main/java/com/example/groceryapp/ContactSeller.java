package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ContactSeller extends AppCompatActivity {
    private ImageView mshopImage;
    private ImageButton mcallbtn,mmapbtn,mbackbtn;
    private TextView mshopname,mphone,memail,maddress,mdeliveryfee,mshopopen;
   private String shopname,shaopphone,shopemail,shopopen,shopaddress,shopLat,shopLong;
  private String shopUid,myLat,myLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_seller);


        myLat=getIntent().getStringExtra("myLat");
        myLong=getIntent().getStringExtra("myLong");

        mshopImage=findViewById(R.id.shopIV);
        mshopname=findViewById(R.id.shopnameTV);
        maddress=findViewById(R.id.addressTV);
        mphone=findViewById(R.id.phoneTV);
        memail=findViewById(R.id.emailTV);
        mdeliveryfee=findViewById(R.id.deiveryfeeTV);
        mshopopen=findViewById(R.id.opencloseTV);
        mcallbtn=findViewById(R.id.callbtn);
        mmapbtn=findViewById(R.id.mapbtn);
        mbackbtn=findViewById(R.id.backbtn);


        shopUid="d6gtTJ7p7PVSJ9EDVWdLZkY0b7X2";


        mcallbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //call option
                dialPhone();
            }
        });
        mmapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // show loaction on maps
                openmap();
            }
        });
        mbackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
            shopname=""+snapshot.child("shopName").getValue();
           shopemail=""+snapshot.child("email").getValue();
           shaopphone=""+snapshot.child("phone").getValue();
           shopaddress=""+snapshot.child("address").getValue();
           shopopen=""+snapshot.child("shopOpen").getValue();
          String  shopfee=""+snapshot.child("deliveryFee").getValue();
           shopLat=""+snapshot.child("latitude").getValue();
           shopLong=""+snapshot.child("longitude").getValue();
           String shopImage=""+snapshot.child("profileImage").getValue();
                String name=""+snapshot.child("name").getValue();

                mshopname.setText(shopname);
                memail.setText(shopemail);
                mphone.setText(shaopphone);
                mdeliveryfee.setText("DeliveryFee: Rs."+shopfee);
                maddress.setText(shopaddress);
                if(shopopen.equals("true")){
                    mshopopen.setText("Open");
                }
                else{
                    mshopopen.setText("Closed");
                }
                try{
                    Picasso.get().load(shopImage).placeholder(R.drawable.ic_storeicon_grey).into(mshopImage);
                }
                catch(Exception e){
                    mshopImage.setImageResource(R.drawable.ic_storeicon_grey);
                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

    }

    private void openmap() {
        String address="https://maps.google.com/maps?saddr="+myLat+","+myLong+"&daddr="+shopLat+","+shopLong;
        Intent intent =new Intent(Intent.ACTION_VIEW,Uri.parse(address));
        startActivity(intent);

    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(shaopphone))));
        Toast.makeText(ContactSeller.this,""+shaopphone,Toast.LENGTH_SHORT).show();
    }
}