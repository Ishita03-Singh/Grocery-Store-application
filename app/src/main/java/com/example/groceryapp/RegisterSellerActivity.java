package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class RegisterSellerActivity extends AppCompatActivity implements LocationListener {
    private ImageButton bkbtn,gpsbtn;
    private ImageView profileiv;
    private EditText mnametv,memailtv,mcitytv,mcountrytv,mstatetv,mshopnametv,
           mdeliverytv, mphonetv,maddresstv,mpasstv,mcpasstv;
    private Button registerbtn;
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

    private Uri imageUri;

    private LocationManager locationManager;
    private double mlatitude=0.0,mlongitude=0.0;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_seller);

        bkbtn=findViewById(R.id.backbtnIV);
        gpsbtn=findViewById(R.id.gpsbtnIV);
        mnametv=findViewById(R.id.Nametv);
        memailtv=findViewById(R.id.emailtext);
        mpasstv=findViewById(R.id.passwordtext);
        mcpasstv=findViewById(R.id.confirmpasswordtext);
        mcitytv=findViewById(R.id.Citytext);
        mstatetv=findViewById(R.id.Statetext);
        mcountrytv=findViewById(R.id.CountryTV);
        maddresstv=findViewById(R.id.addresstext);
        mphonetv=findViewById(R.id.PhoneTV);
        registerbtn=findViewById(R.id.registerButton);
        profileiv=findViewById(R.id.ProfileIV);
      mshopnametv=findViewById(R.id.ShopNametv);
      mdeliverytv=findViewById(R.id.deliveryTV);

      //init permission array
        locationpermissions= new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
       camerapermissions= new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
      storagepermissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

      firebaseAuth = FirebaseAuth.getInstance();
      progressDialog = new ProgressDialog(this);
      progressDialog.setTitle("Please Wait");
      progressDialog.setCanceledOnTouchOutside(false);

        bkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        gpsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //detect current location
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
        profileiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // detect image
                showImagePicker();
            }
        });
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //register user
                inputData();
            }
        });

    }
  private String fullname,shopname,emailid,passwrd,cpasswrd,phoneno,deliveryfee,country,state,city,address;
    private void inputData() {
        //input data;
        fullname=mnametv.getText().toString().trim();
        shopname=mshopnametv.getText().toString().trim();
       emailid=memailtv.getText().toString().trim();
        passwrd=mpasstv.getText().toString().trim();
       cpasswrd=mcpasstv.getText().toString().trim();
        phoneno=mphonetv.getText().toString().trim();
        deliveryfee=mdeliverytv.getText().toString().trim();
        country=mcountrytv.getText().toString().trim();
        city=mcitytv.getText().toString().trim();
        state=mstatetv.getText().toString().trim();
        address=maddresstv.getText().toString().trim();
        //validate data
        if(TextUtils.isEmpty(fullname)){
            Toast.makeText(this,"Enter full name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(shopname)){
            Toast.makeText(this,"Enter shop name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phoneno)){
            Toast.makeText(this,"Enter Phone No. ",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(deliveryfee)){
            Toast.makeText(this,"Enter Delivery Charges",Toast.LENGTH_SHORT).show();
            return;
        }
        if(mlatitude==0.0 || mlongitude==0.0){
            Toast.makeText(this,"Click on GPS button to detect location",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailid).matches()){
            Toast.makeText(this,"Invalid Email Address",Toast.LENGTH_SHORT).show();
            return;
        }
        if(passwrd.length()<6){
            Toast.makeText(this,"Password must be atleast 6 characters long",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!passwrd.equals(cpasswrd)){
            Toast.makeText(this,"Password doesnot match",Toast.LENGTH_SHORT).show();
            return;
        }
        createAccount();
    }

    private void createAccount() {
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
        //create account
        firebaseAuth.createUserWithEmailAndPassword(emailid,passwrd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //account created
                        saverFirebasedata();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                //failed creating account
                progressDialog.dismiss();
                Toast.makeText(RegisterSellerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void saverFirebasedata() {
        progressDialog.setMessage("Saving Account info...");
        progressDialog.show();
        String timeStamp=""+System.currentTimeMillis();
        if(imageUri==null){
            //save info without image
            //setup data to save
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("email",""+emailid);
            hashMap.put("name",""+fullname);
            hashMap.put("shopName",""+shopname);
            hashMap.put("phone",""+phoneno);
            hashMap.put("deliveryFee",""+deliveryfee);
            hashMap.put("country",""+country);
            hashMap.put("city",""+city);
            hashMap.put("state",""+state);
            hashMap.put("address",""+address);
            hashMap.put("latitude",""+mlatitude);
            hashMap.put("longitude",""+mlongitude);
            hashMap.put("timestamp",""+timeStamp);
            hashMap.put("accountType","Seller");
            hashMap.put("online","true");
            hashMap.put("shopOpen","true");
            hashMap.put("profileImage","");

            //save to db
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //db updated
                     progressDialog.dismiss();
                     startActivity(new Intent(RegisterSellerActivity.this,MainSellerActivity.class));
                     finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull  Exception e) {
                //failed to update db
                    progressDialog.dismiss();
                    startActivity(new Intent(RegisterSellerActivity.this,MainSellerActivity.class));
                    finish();
                }
            });


        }
        else {
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
                                hashMap.put("uid",""+firebaseAuth.getUid());
                                hashMap.put("email",""+emailid);
                                hashMap.put("name",""+fullname);
                                hashMap.put("shopName",""+shopname);
                                hashMap.put("phone",""+phoneno);
                                hashMap.put("deliveryFee",""+deliveryfee);
                                hashMap.put("country",""+country);
                                hashMap.put("city",""+city);
                                hashMap.put("state",""+state);
                                hashMap.put("address",""+address);
                                hashMap.put("latitude",""+mlatitude);
                                hashMap.put("longitude",""+mlongitude);
                                hashMap.put("timestamp",""+timeStamp);
                                hashMap.put("accountType","Seller");
                                hashMap.put("online","true");
                                hashMap.put("shopOpen","true");
                                hashMap.put("profileImage",""+dowloadIamgeUri);//url of uploaded image

                                //save to db
                                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //db updated
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterSellerActivity.this,MainSellerActivity.class));
                                                finish();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull  Exception e) {
                                        //failed to update db
                                        progressDialog.dismiss();
                                        startActivity(new Intent(RegisterSellerActivity.this,MainSellerActivity.class));
                                        finish();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull  Exception e) {
                 progressDialog.dismiss();
                    Toast.makeText(RegisterSellerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }

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

    private void PickImagefromGallery(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICKER_GALLERY_CODE);
    }
    private void PickImagefromCamera(){
        ContentValues contentValues =new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image_Description");
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
              Location  location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    mlatitude = location.getLatitude();
                    mlongitude = location.getLongitude();
                }
            }
            findAddress();
        }catch (SecurityException e){
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    private  boolean checkLocationPermission(){
        boolean result= ContextCompat.checkSelfPermission
                (this,Manifest.permission.ACCESS_FINE_LOCATION)== (PackageManager.PERMISSION_GRANTED);
        return  result;
    }
    private  void  requestLocationPermission(){
        ActivityCompat.requestPermissions(this,locationpermissions,LOCATION_REQUEST_CODE);
    }

    private boolean ckeckStoragePermission(){
        boolean result=ContextCompat.checkSelfPermission
                (this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);
        return  result;
    }
    private  void  requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagepermissions,STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermission(){
        boolean result=ContextCompat.checkSelfPermission
                (this,Manifest.permission.CAMERA)== (PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission
                (this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);
        return  result&&result1;
    }
    private  void  requestCameraPermission(){
        ActivityCompat.requestPermissions(this,camerapermissions,CAMERA_REQUEST_CODE);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
         //location detected
        mlatitude=location.getLatitude();
        mlongitude=location.getLongitude();
        findAddress();
    }

    private void findAddress() {
        //find address ,country,state,city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder= new Geocoder(this, Locale.getDefault());
        try{
            DecimalFormat df = new DecimalFormat();

            df.setMaximumFractionDigits(3);

           mlatitude = Double.parseDouble(df.format(mlatitude));

          mlongitude = Double.parseDouble(df.format(mlongitude));

            addresses=geocoder.getFromLocation(mlatitude,mlongitude,1);

            String address=addresses.get(0).getAddressLine(0);
            String city=addresses.get(0).getLocality();
            String state=addresses.get(0).getAdminArea();
            String country=addresses.get(0).getCountryName();
             // set addresses
            mcountrytv.setText(country);
            mcitytv.setText(city);
            maddresstv.setText(address);
            mstatetv.setText(state);
        }
        catch (Exception e){
            Toast.makeText(this," " + e.getMessage() ,Toast.LENGTH_SHORT).show();
        }
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
        Toast.makeText(this,"Please turn on location...",Toast.LENGTH_SHORT).show();
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
            break;
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
            break;
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
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==IMAGE_PICKER_GALLERY_CODE){
                //get picked image
                imageUri =data.getData();
                //set to imageview
                profileiv.setImageURI(imageUri);
            }
            else if(requestCode==IMAGE_PICKER_CAMERA_CODE){
                //set to imageview
                profileiv.setImageURI(imageUri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}