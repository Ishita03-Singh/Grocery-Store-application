package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ProfileSellerActivity extends AppCompatActivity implements LocationListener {
    private ImageButton mbckbtn,mgpsbtn;

    private Button mupdatebtn;
    private EditText mnameEV,mPhoneEV,maddressEV,mcityEV,mCountryEV,mStateEV,mshopnameEV,mdeliveryfee;
    private ImageView mprofileIV;
    private SwitchCompat mshopopenSC;
    // permission constants
    private  static  final int LOCATION_REQUEST_CODE=100;
    private  static  final int CAMERA_REQUEST_CODE=200;
    private  static  final int STORAGE_REQUEST_CODE=300;

    //image picker constants
    private  static  final int IMAGE_PICKER_GALLERY_CODE=400;
    private  static  final int IMAGE_PICKER_CAMERA_CODE=500;

    // permission arrays
    private String[] locationpermissions;
    private String[] camerapermissions;
    private String[] storagepermissions;

    private double mlatitude=0.0,mlogitude=0.0;
    private Uri imageUri;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_seller);

        mbckbtn=findViewById(R.id.backbtnIV);
        mgpsbtn= findViewById(R.id.gpsbtnIV);
        mnameEV=findViewById(R.id.Nametv);
        mshopnameEV=findViewById(R.id.ShopNametv);
        mPhoneEV=findViewById(R.id.PhoneTV);
       mdeliveryfee=findViewById(R.id.deliveryTV);
        mcityEV=findViewById(R.id.Citytext);
        mStateEV=findViewById(R.id.Statetext);
        mCountryEV=findViewById(R.id.CountryTV);
        maddressEV=findViewById(R.id.addresstext);
        mprofileIV=findViewById(R.id.ProfileIV);
        mupdatebtn=findViewById(R.id.updateButton);
        mshopopenSC=findViewById(R.id.shopopenSC);
        //init permission array
        locationpermissions= new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        camerapermissions= new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagepermissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        checkUser();

        //init ui views
        mbckbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mgpsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //detect location
                if(checkLocationPermission()){
                    //already allowed
                    detectLocation();
                }
                else {
                    //denied so request
                    requestLocationPermission();

                }

            }
        });
        mprofileIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // detect image
                showImagePicker();
            }
        });
        mupdatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             inputData();
            }
        });
    }
    private String fullname,shopname,phoneno,deliveryfee,country,state,city,address;
    private boolean shopopen;
    private void inputData() {
        //input data;
        fullname=mnameEV.getText().toString().trim();
        shopname=mshopnameEV.getText().toString().trim();
        phoneno=mPhoneEV.getText().toString().trim();
        deliveryfee=mdeliveryfee.getText().toString().trim();
        country=mCountryEV.getText().toString().trim();
        city=mcityEV.getText().toString().trim();
        state=mStateEV.getText().toString().trim();
        address=maddressEV.getText().toString().trim();
        shopopen=mshopopenSC.isChecked();

        updateProfile();
    }

    private void updateProfile() {
        progressDialog.setMessage("Saving Account info...");
        progressDialog.show();
        String timeStamp=""+System.currentTimeMillis();
        if(imageUri==null) {
            //save info without image
            //setup data to save
            HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put("name", "" + fullname);
            hashMap.put("shopName", "" + shopname);
            hashMap.put("phone", "" + phoneno);
            hashMap.put("deliveryFee", "" + deliveryfee);
            hashMap.put("country", "" + country);
            hashMap.put("city", "" + city);
            hashMap.put("state", "" + state);
            hashMap.put("address", "" + address);
            hashMap.put("latitude", "" + mlatitude);
            hashMap.put("longitude", "" + mlogitude);
            hashMap.put("shopOpen", "" + shopopen);
            //update to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //db updated
                            progressDialog.dismiss();
                            Toast.makeText(ProfileSellerActivity.this, "Profile updated successfully..,", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //failed to update db
                    progressDialog.dismiss();
                    Toast.makeText(ProfileSellerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }
        else{
            //save info with image
            //name and path of image
            String filePathandName ="profile_image/"+firebaseAuth.getUid();
            //upload image
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathandName);
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //get url of uploaded image
                            Task<Uri> uriTask =taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri dowloadIamgeUri=uriTask.getResult();
                            if(uriTask.isSuccessful()){
                                //setup data to save
                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put("name",""+fullname);
                                hashMap.put("shopName",""+shopname);
                                hashMap.put("phone",""+phoneno);
                                hashMap.put("deliveryFee",""+deliveryfee);
                                hashMap.put("country",""+country);
                                hashMap.put("city",""+city);
                                hashMap.put("state",""+state);
                                hashMap.put("address",""+address);
                                hashMap.put("latitude",""+mlatitude);
                                hashMap.put("longitude",""+mlogitude);
                                hashMap.put("shopOpen",""+shopopen);
                                hashMap.put("profileImage",""+dowloadIamgeUri);//url of uploaded image

                                //save to db
                                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //db updated
                                                progressDialog.dismiss();
                                                Toast.makeText(ProfileSellerActivity.this, "Profile updated successfully..,", Toast.LENGTH_SHORT).show();


                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull  Exception e) {
                                        //failed to update db
                                        progressDialog.dismiss();
                                        Toast.makeText(ProfileSellerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull  Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileSellerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        }

    private void checkUser() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            startActivity(new Intent(ProfileSellerActivity.this,LoginActivity.class));
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
                            String address=""+ds.child("address").getValue();
                            String city=""+ds.child("city").getValue();
                            String state=""+ds.child("state").getValue();
                            String country=""+ds.child("country").getValue();
                            String deliveryFee=""+ds.child("deliveryFee").getValue();
                            String email=""+ds.child("email").getValue();
                            mlatitude= Double.parseDouble(""+ds.child("latitude").getValue());
                            mlogitude= Double.parseDouble(""+ds.child("longitude").getValue());
                            String online=""+ds.child("online").getValue();
                            String phone=""+ds.child("phone").getValue();
                            String profileImage=""+ds.child("profileImage").getValue();
                            String timestamp=""+ds.child("timestamp").getValue();
                            String shopName=""+ds.child("shopName").getValue();
                            String shopOpen=""+ds.child("shopOpen").getValue();
                            String uid=""+ds.child("uid").getValue();

                           mnameEV.setText(name);
                            mshopnameEV.setText(shopName);
                            mPhoneEV.setText(phone);
                          mdeliveryfee.setText(deliveryFee);
                           mCountryEV.setText(country);
                            mcityEV.setText(city);
                           mStateEV.setText(state);
                            maddressEV.setText(address);
                            if(shopOpen.equals("true")){
                                mshopopenSC.setChecked(true);

                            }else{
                                mshopopenSC.setChecked(false);
                            }
                            try{
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_storeicon_grey).into(mprofileIV);
                            }
                            catch(Exception e){
                          mprofileIV.setImageResource(R.drawable.ic_storeicon_grey);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showImagePicker() {
        //options to display in dialog
        String[] options= {"Camera","Gallery"};
        //dialog
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setTitle("Pick image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle clicks
                        if(which==0){
                            //camera clicked
                            if(checkCameraPermission()){//camera permission allowed
                                PickImagefromCamera();
                            }else{//denied,request
                                requestCameraPermission();
                            }
                        }
                        else{
                            //gallery clicked
                            if(ckeckStoragePermission()){//storage permission allowed
                                PickImagefromGallery();
                            }else{
                                //denied,request
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private boolean checkCameraPermission(){
        boolean result= ContextCompat.checkSelfPermission
                (this, Manifest.permission.CAMERA)== (PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission
                (this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);
        return  result&&result1;
    }
    private  void  requestCameraPermission(){
        ActivityCompat.requestPermissions(this,camerapermissions,CAMERA_REQUEST_CODE);
    }

    private boolean ckeckStoragePermission(){
        boolean result=ContextCompat.checkSelfPermission
                (this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);
        return  result;
    }
    private  void  requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagepermissions,STORAGE_REQUEST_CODE);
    }

    private  boolean checkLocationPermission(){
        boolean result= ContextCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION)== (PackageManager.PERMISSION_GRANTED);
        return  result;
    }
    private  void  requestLocationPermission(){
        ActivityCompat.requestPermissions(this,locationpermissions,LOCATION_REQUEST_CODE);
    }


    private void PickImagefromGallery(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICKER_GALLERY_CODE);
    }
    private void PickImagefromCamera(){
        ContentValues contentValues =new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Image Description");
        imageUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,IMAGE_PICKER_CAMERA_CODE);
    }
    private void detectLocation() {
        Toast.makeText(this,"Please wait..",Toast.LENGTH_LONG).show();
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

            if (locationManager != null) {
                Location location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {
                    mlatitude = location.getLatitude();
                    mlogitude = location.getLongitude();

                }

            } findAddress();
        }catch (SecurityException e){
            e.printStackTrace();
        }

    }
    private void findAddress() {
        //find address ,country,state,city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder= new Geocoder(this, Locale.getDefault());

        try{

            addresses=geocoder.getFromLocation(mlatitude,mlogitude,1);


            String address=addresses.get(0).getAddressLine(0);
            String city=addresses.get(0).getLocality();
            String state=addresses.get(0).getAdminArea();
            String country=addresses.get(0).getCountryName();
            // set addresses
            mCountryEV.setText(country);
            mcityEV.setText(city);
            maddressEV.setText(address);
            mStateEV.setText(state);
        }
        catch (Exception e){
            Toast.makeText(this,"" + e.getMessage() ,Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        //location detected
        mlatitude=location.getLatitude();
        mlogitude=location.getLongitude();
        findAddress();
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
// gps location disabled
        Toast.makeText(this,"Location is disabled...",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean locationAccepted=grantResults[0] ==PackageManager.PERMISSION_GRANTED;
                    if(locationAccepted){
                        //permission allowed
                        detectLocation();
                    }
                    else{
                        Toast.makeText(this,"location permission is necessary...",Toast.LENGTH_SHORT).show();
                        //permission denied
                    }
                }
            }

            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0] ==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1] ==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        //permission allowed
                        PickImagefromCamera();
                    }
                    else{
                        Toast.makeText(this,"Camera permission are necessary...",Toast.LENGTH_SHORT).show();
                        //permission denied
                    }
                }
            }

            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted=grantResults[0] ==PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        //permission allowed
                        PickImagefromGallery();
                    }
                    else{
                        Toast.makeText(this,"Storage permission are necessary...",Toast.LENGTH_SHORT).show();
                        //permission denied
                    }
                }
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==IMAGE_PICKER_GALLERY_CODE){
                //get picked image
                imageUri =data.getData();
                //set to imageview
                mprofileIV.setImageURI(imageUri);
            }
            else if(requestCode==IMAGE_PICKER_CAMERA_CODE){
                //set to imageview
                mprofileIV.setImageURI(imageUri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}