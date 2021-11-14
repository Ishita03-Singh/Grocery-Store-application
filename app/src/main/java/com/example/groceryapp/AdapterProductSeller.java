package com.example.groceryapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller>implements Filterable {
    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProduct filter;

  public AdapterProductSeller(Context context,ArrayList<ModelProduct> productList){
      this.context=context;
      this.productList=productList;
      this.filterList=productList;
  }

    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      //inflate layout
        View view= LayoutInflater.from(context).inflate(R.layout.row_product_seller,parent,false);
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  AdapterProductSeller.HolderProductSeller holder, int position) {
      //get data
        ModelProduct modelProduct=productList.get(position);
        String itemName=modelProduct.getItemName();
        String itemCode=modelProduct.getItemCode();
        String HsnCode=modelProduct.getHsn_code();
        String igstRate=modelProduct.getIgst_rate();
        String cessRate=modelProduct.getCess_rate();
        String sgstRate=modelProduct.getSgst_rate();
        String cgstRate=modelProduct.getCgst_rate();
        String rateType=modelProduct.getRateType();
        String rate=modelProduct.getRate();
        String uid=modelProduct.getUid();

        String discountAvailable=modelProduct.getDiscountAvailable();


        String discountPrice=modelProduct.getDiscountPrice();
        String discountNote=modelProduct.getDiscountNote();
        String productCategory=modelProduct.getProductCategory();

        String icon =modelProduct.getProductIcon();


        //set data
        holder.mtitleTV.setText(itemName);
        holder.mproductCode.setText(itemCode);
        holder.mcgst.setText(cgstRate);
        holder.msgst.setText(sgstRate);
        holder.migst.setText(igstRate);
        holder.mhsn.setText(HsnCode);
        holder.mcess.setText(cessRate);
        holder.mrateType.setText(rateType);
        holder.mdiscountpriceTV.setText("Rs."+discountPrice);
        holder.mdiscountNoteTV.setText(discountNote);
        holder.moriginalpriceIv.setText("Rs."+rate);
        if(  discountAvailable.equals("true")){
            holder.mdiscountpriceTV.setVisibility(View.VISIBLE);
            holder.mdiscountNoteTV.setVisibility(View.VISIBLE);
            holder.moriginalpriceIv.setPaintFlags(holder.moriginalpriceIv.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);//add strike through on originalPrice
        }
        else {
            holder.mdiscountpriceTV.setVisibility(View.GONE);
            holder.mdiscountNoteTV.setVisibility(View.GONE);
        }
        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_shopping_add_primary).into(holder.mproductIconIV);
        }
        catch (Exception e){
             holder.mproductIconIV.setImageResource(R.drawable.ic_shopping_add_primary);
               }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle item clicks show item details
                detailsBottomSheet(modelProduct); //here modelProduct contains details of clicked product

            }
        });


    }

    private void detailsBottomSheet(ModelProduct modelProduct) {
      //bottom sheet
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(context);
        //inflate view fo bottomsheet
        View view=LayoutInflater.from(context).inflate(R.layout.bs_product_details_seller,null);
        //set view to bottomsheet
        bottomSheetDialog.setContentView(view);


        //init view to bottomsheet
        ImageButton mbkbtn=view.findViewById(R.id.backbtn);
        ImageButton mdeletebtn =view.findViewById(R.id.deletebtn);
        ImageButton meditbtn=view.findViewById(R.id.editbtn);
        TextView  mNameTv=view.findViewById(R.id.nameTv);
        ImageView mProductIconIv=view.findViewById(R.id.productIconIv);
        TextView mDiscountnoteTv =view.findViewById(R.id.discountnoteTv);
        TextView  mTitleTv=view.findViewById(R.id.titleTv);
        TextView  mItem_Code=view.findViewById(R.id.product_CodeTv);
        TextView mCategoryTv =view.findViewById(R.id.categoryTv);

        TextView mRateType =view.findViewById(R.id.RateTypeTV);

        TextView mHsnCode =view.findViewById(R.id.HsnTV);
        TextView mCessRate =view.findViewById(R.id.CessTV);
        TextView mIgstRate =view.findViewById(R.id.IgstTV);
        TextView mCgstRate =view.findViewById(R.id.CgstTV);
        TextView mSgstRate =view.findViewById(R.id.sgstTV);


        TextView mdiscountPriceTv=view.findViewById(R.id.discountPriceTv);
        TextView  moriginalPriceTv=view.findViewById(R.id.originalPriceTv);
         //get data
        String itemName=modelProduct.getItemName();
        String itemCode=modelProduct.getItemCode();
        String Hsn_code=modelProduct.getHsn_code();
        String igst_rate=modelProduct.getIgst_rate();
        String cess_rate=modelProduct.getCess_rate();
        String sgst_rate=modelProduct.getSgst_rate();
        String cgst_rate=modelProduct.getCgst_rate();
        String rateType=modelProduct.getRateType();
        String rate=modelProduct.getRate();
        String uid=modelProduct.getUid();
        String discountAvailable=modelProduct.getDiscountAvailable();
        String discountPrice=modelProduct.getDiscountPrice();
        String discountNote=modelProduct.getDiscountNote();
        String productCategory=modelProduct.getProductCategory();
        String icon =modelProduct.getProductIcon();


        //set data
        mTitleTv.setText(itemName);
        mItem_Code.setText(itemCode);
        mRateType.setText(rateType);
        mCessRate.setText(cess_rate);
        mHsnCode.setText(Hsn_code);
        mIgstRate.setText(igst_rate);
        mSgstRate.setText(sgst_rate);
        mCgstRate.setText(cgst_rate);
        mCategoryTv.setText(productCategory);
        mDiscountnoteTv.setText(discountNote);
        mdiscountPriceTv.setText("Rs."+discountPrice);
        moriginalPriceTv.setText("Rs."+rate);

        if(discountAvailable.equals("true")){
           mdiscountPriceTv.setVisibility(View.VISIBLE);
            mDiscountnoteTv.setVisibility(View.VISIBLE);
          moriginalPriceTv.setPaintFlags(moriginalPriceTv.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);//add strike through on originalPrice
        }
        else {
          mdiscountPriceTv.setVisibility(View.GONE);
            mDiscountnoteTv.setVisibility(View.GONE);
        }
        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_shopping_add_primary).into(mProductIconIv);
        }
        catch (Exception e){
            mProductIconIv.setImageResource(R.drawable.ic_shopping_add_primary);
        }

        bottomSheetDialog.show();
             //edit click
        meditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.dismiss();
                //open edit product activity pass id of product
                Intent intent=new Intent(context,EditProductSellerActivity.class);
                intent.putExtra("productId",itemCode);
                context.startActivity(intent);

            }
        });
        mdeletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //show delete config dialog
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete product "+itemName+" ?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete
                                deleteProduct(itemCode);//id is product id
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            //cancel ,dismiss dialog
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });
        mbkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           bottomSheetDialog.dismiss();
            }
        });

    }

    private void deleteProduct(String id) {
      //delete product using its id
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //product deleted
                        Toast.makeText(context,"Product deleted...",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                    //failed to delete
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
      if(filter==null){
          filter= new FilterProduct(this,filterList);
      }
        return filter;
    }


    class HolderProductSeller extends RecyclerView.ViewHolder {
             private ImageView mproductIconIV;
             private TextView mdiscountNoteTV,mtitleTV,mproductCode,mdiscountpriceTV,moriginalpriceIv ,mrateType,mcess,migst,msgst,mcgst,mhsn;
                private ImageButton mnextbtn;
        public HolderProductSeller(@NonNull  View itemView) {
            super(itemView);
            mproductIconIV=itemView.findViewById(R.id.productIconIv);
            mdiscountNoteTV=itemView.findViewById(R.id.discountedNoteTv);
          mtitleTV=itemView.findViewById(R.id.TitleTv);
          mproductCode=itemView.findViewById(R.id.product_codeTv);
          mdiscountpriceTV=itemView.findViewById(R.id.discountedPriceTv);
           moriginalpriceIv=itemView.findViewById(R.id.originalPriceTv);
            mnextbtn=itemView.findViewById(R.id.nextIv);
            mrateType=itemView.findViewById(R.id.rateTypeTV);
            mcess=itemView.findViewById(R.id.cessTV);
            mhsn=itemView.findViewById(R.id.hsnTV);
            migst=itemView.findViewById(R.id.igstTV);
            msgst=itemView.findViewById(R.id.SgstTV);
            mcgst=itemView.findViewById(R.id.cgstTV);
        }
    }
}
