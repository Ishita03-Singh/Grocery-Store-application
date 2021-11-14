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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class EditProductSellerActivity extends AppCompatActivity {
    private String mproductId;

    private ImageButton mbackBtn;
    private ImageView mproductIv;
    private EditText mtitleET,mitem_codeText;
    private TextView mcategoryET,mpriceET,mdiscountpriceET,mrateTypetext
            ,mdiscountnoteET,migstrateText,msgstrateText,mcgstrateText,mcessrateText,mhsnrateText;
    private SwitchCompat mswitchdiscount;
    private Button mupdateProductButton;

    // permission constants
    private  static  final int CAMERA_REQUEST_CODE=200;
    private  static  final int STORAGE_REQUEST_CODE=300;

    //image picker constants
    private  static  final int IMAGE_PICKER_GALLERY_CODE=400;
    private  static  final int IMAGE_PICKER_CAMERA_CODE=500;

    // permission arrays
    private String[] camerapermissions;
    private String[] storagepermissions;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_seller);
        //get id of product from intent
        mproductId=getIntent().getStringExtra("productId");

        mbackBtn=findViewById(R.id.backbtnIV);
        mproductIv=findViewById(R.id.productIconIV);
        mitem_codeText=findViewById(R.id.itemcodetext);
        mtitleET=findViewById(R.id.titleET);
        mcategoryET=findViewById(R.id.categoryET);
        mrateTypetext=findViewById(R.id.rateTypetext);
        migstrateText  =findViewById(R.id.igstratetext);
        mcgstrateText =findViewById(R.id.cgstrateText);
        msgstrateText     =findViewById(R.id.sgstrateText);
        mcessrateText     =findViewById(R.id.cesstext);
        mhsnrateText        =findViewById(R.id.hsntext);
        mpriceET=findViewById(R.id.priceET);

        mswitchdiscount= findViewById(R.id.discountSC);
        mdiscountpriceET=findViewById(R.id.discountPriceET);
        mdiscountnoteET= findViewById(R.id.discountedNoteET);

        //on start keep both of them hide until switch is checked
        mdiscountpriceET.setVisibility(View.GONE);
        mdiscountnoteET.setVisibility(View.GONE);

        mupdateProductButton= findViewById(R.id.updateProductButton);
        firebaseAuth = FirebaseAuth.getInstance();
        loadProductdetails();//to set on views
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //init permission array
        camerapermissions= new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagepermissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

//if discount switch true->show discountprice,note else if false then hide both
        mswitchdiscount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //checked->show both discount price and note
                    mdiscountpriceET.setVisibility(View.VISIBLE);
                    mdiscountnoteET.setVisibility(View.VISIBLE);
                }
                else{
                    //unchecked->hide both discount price and note
                    mdiscountpriceET.setVisibility(View.GONE);
                    mdiscountnoteET.setVisibility(View.GONE);
                }
            }
        });
        mbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        mupdateProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Flow:
                //1) input data
                //2) validate data
                //1) add data to db
              inputData();

            }
        });
        mproductIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open image picker
                showImagePicker();
            }
        });
        mcategoryET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //pick category
                categoryDialog();
            }
        });
    }

    private void loadProductdetails() {



        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(mproductId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot dataSnapshot) {
                     //get data
                        String productcode=""+dataSnapshot.child("itemCode").getValue();
                        String productTitle =""+dataSnapshot.child("itemName").getValue();
                        String productCategory=""+dataSnapshot.child("productCategory").getValue();
                        String productrateType=""+dataSnapshot.child("rateType").getValue();
                        String productIcon=""+dataSnapshot.child("productIcon").getValue();
                        String productPrice=""+dataSnapshot.child("rate").getValue();
                        String discountNote=""+dataSnapshot.child("discountNote").getValue();
                        String discountAvailable=""+dataSnapshot.child("discountAvailable").getValue();
                        String uid=""+dataSnapshot.child("uid").getValue();
                        String discountPrice=""+dataSnapshot.child("discountPrice").getValue();
                        String cessrate=""+dataSnapshot.child("CESS").getValue();
                        String hsnratee=""+dataSnapshot.child("HSNCODE").getValue();
                        String igstrate=""+dataSnapshot.child("IGST").getValue();
                        String sgstrate=""+dataSnapshot.child("SGST").getValue();
                        String cgstrate=""+dataSnapshot.child("CGST").getValue();
                        //set data to views
                        if(discountAvailable.equals("true")){
                            mswitchdiscount.setChecked(true);
                            mdiscountnoteET.setVisibility(View.VISIBLE);
                        }else {
                            mswitchdiscount.setChecked(false);
                            mdiscountnoteET.setVisibility(View.GONE);
                        }
                        mitem_codeText.setText(productcode);
                        mtitleET.setText(productTitle);
                        mrateTypetext .setText(productrateType);
                        migstrateText.setText(igstrate);
                        mcgstrateText.setText(cgstrate);
                        msgstrateText .setText(sgstrate);
                        mcessrateText.setText(cessrate);
                        mhsnrateText.setText(hsnratee);
                        mcategoryET.setText(productCategory);
                        mdiscountnoteET.setText(discountNote);
                        mpriceET.setText(productPrice);
                       mdiscountpriceET.setText(discountPrice);
                       try {
                           Picasso.get().load(productIcon).placeholder(R.drawable.ic_shopping_add_white).into(mproductIv);
                       }
                       catch(Exception e){
                           mproductIv.setImageResource(R.drawable.ic_shopping_add_white);


                       }

                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

    }


    private String producttitle,productcategory,originalprice,productratetype,productcode,productigst,productsgst,producthsn,productcgst,productcess,discountedpricee,discountednote;
    private boolean discountAvailable=false;
    private void inputData() {
        //input data
        producttitle=mtitleET.getText().toString().trim();
        productcode=mitem_codeText.getText().toString().trim();
        productcategory=mcategoryET.getText().toString().trim();
        originalprice=mpriceET.getText().toString().trim();
        productratetype=mrateTypetext.getText().toString().trim();
        productcess =mcessrateText.getText().toString().trim();
        productcgst=mcgstrateText.getText().toString().trim();
        producthsn=mhsnrateText.getText().toString().trim();
        productigst  =migstrateText.getText().toString().trim();
        productsgst=msgstrateText.getText().toString().trim();
        discountAvailable=mswitchdiscount.isChecked();
        //validate data
        if(TextUtils.isEmpty(producttitle)){
            Toast.makeText(this,"Title is required...",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(productcategory)){
            Toast.makeText(this,"Category is required...",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(originalprice)){
            Toast.makeText(this,"Price is required...",Toast.LENGTH_SHORT).show();
            return;
        }
        if(discountAvailable)
        {//product is with discount
            discountedpricee=mdiscountpriceET.getText().toString().trim();
            discountednote=mdiscountnoteET.getText().toString().trim();

            if(TextUtils.isEmpty(discountedpricee)){
                Toast.makeText(this,"Discounted Price is required...",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else {//product is without  discount
            discountedpricee="0";
            discountednote="";

        }
        updateProduct();
    }


    private void updateProduct() {
        //show progress
        progressDialog.setMessage("Updating Product...");
        progressDialog.show();
        if(imageUri==null){
          //update without image
            // setup data to save
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("itemName", "" + producttitle);//
            hashMap.put("productCategory", "" + productcategory);//
            hashMap.put("rateType", "" + productratetype);
            hashMap.put("itemCode", "" + productcode);//
            hashMap.put("CGST", "" +productcgst );//
            hashMap.put("SGST", "" + productsgst);//
            hashMap.put("IGST", "" + productigst);//
            hashMap.put("HSNCODE", "" + producthsn);//
            hashMap.put("CESS", "" + productcess);//
            hashMap.put("productIcon", ""); //no image//
            hashMap.put("rate", "" + originalprice);//
            hashMap.put("discountPrice", "" + discountedpricee);//
            hashMap.put("discountNote", "" +discountednote);//
            hashMap.put("discountAvailable", "" +discountAvailable);//
            hashMap.put("uid", "" +firebaseAuth.getUid());//
       ///update to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Products").child(mproductId).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //added to db
                            progressDialog.dismiss();
                            Toast.makeText(EditProductSellerActivity.this, "Product Updated successfully..", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //failed to add to  db
                    progressDialog.dismiss();
                    Toast.makeText(EditProductSellerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }
        else{
            //update with image
            //upload image
            //name and path of image
            String filePathandName ="product_images/"+ "" +mproductId;//override previous image using same id
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

                                // setup data to save
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("itemName", "" + producttitle);//
                                hashMap.put("productCategory", "" + productcategory);//
                                hashMap.put("rateType", "" + productratetype);
                                hashMap.put("itemCode", "" + productcode);//
                                hashMap.put("CGST", "" +productcgst );//
                                hashMap.put("SGST", "" + productsgst);//
                                hashMap.put("IGST", "" + productigst);//
                                hashMap.put("HSNCODE", "" + producthsn);//
                                hashMap.put("CESS", "" + productcess);//
                                hashMap.put("productIcon", ""+dowloadIamgeUri);
                                hashMap.put("rate", "" + originalprice);//
                                hashMap.put("discountPrice", "" + discountedpricee);//
                                hashMap.put("discountNote", "" +discountednote);//
                                hashMap.put("discountAvailable", "" +discountAvailable);//
                                hashMap.put("uid", "" +firebaseAuth.getUid());//

                                ///update to db
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).child("Products").child(mproductId).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //added to db
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProductSellerActivity.this, "Product Updated successfully..,", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failed to add to  db
                                        progressDialog.dismiss();
                                        Toast.makeText(EditProductSellerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull  Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(EditProductSellerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void categoryDialog() {
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constants.productcategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get picked category
                        String category =Constants.productcategories[which];
                        //set picked category
                        mcategoryET.setText(category);
                    }
                }).show();
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull  int[] grantResults) {
        switch (requestCode){

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
                mproductIv.setImageURI(imageUri);
            }
            else if(requestCode==IMAGE_PICKER_CAMERA_CODE){
                //set to imageview
                mproductIv.setImageURI(imageUri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
