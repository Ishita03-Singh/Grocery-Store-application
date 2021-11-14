package com.example.groceryapp;

import android.app.AlertDialog;
import android.content.Context;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct>productsList, filterList;
    private FilterProductUser filter;
    public AdapterProductUser(Context context,ArrayList<ModelProduct> productList){
        this.context=context;
        this.productsList=productList;
        this.filterList=productList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        //inflate layout
        View view= LayoutInflater.from(context).inflate(R.layout.show_product_costumer,parent,false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterProductUser.HolderProductUser holder, int position) {
        //get data
       final ModelProduct modelProduct=productsList.get(position);
        String itemName=modelProduct.getItemName();
        String discountAvailable=modelProduct.getDiscountAvailable();
        String discountNote=modelProduct.getDiscountNote();
        String discountPrice=modelProduct.getDiscountPrice();
        String itemCode=modelProduct.getItemCode();
        String productCategory=modelProduct.getProductCategory();
        String rate=modelProduct.getRate();
        String productIcon=modelProduct.getProductIcon();
        String rateType=modelProduct.getRateType();
        String cessRate=modelProduct.getCess_rate();
        String cgstRate=modelProduct.getCgst_rate();
        String hsnCode=modelProduct.getHsn_code();
        String sgstRate=modelProduct.getSgst_rate();
        String igstrate=modelProduct.getIgst_rate();


        holder.mtitle.setText(itemName);
        holder.mdiscountnote.setText(discountNote);
        holder.mdiscountprice.setText("Rs. "+discountPrice);
        holder.mratetype.setText(rateType);
        holder.mprice.setText("Rs. "+rate);

        if(  discountAvailable.equals("true")){
            holder.mdiscountprice.setVisibility(View.VISIBLE);
            holder.mdiscountnote.setVisibility(View.VISIBLE);
            holder.mprice.setPaintFlags(holder.mprice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);//add strike through on originalPrice
        }
        else {
            holder.mdiscountnote.setVisibility(View.GONE);
            holder.mdiscountprice.setVisibility(View.GONE);
            holder.mprice.setPaintFlags(0);
        }
        try{
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_shopping_add_primary).into(holder.mproductImage);
        }
        catch (Exception e){
            holder.mproductImage.setImageResource(R.drawable.ic_shopping_add_primary);
        }
        holder.maddtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle item clicks add to cart

                showQuantityDialog(modelProduct);

            }
        });

    }

    private double cost=0;
    private double finalCost=0;
private int quantity=0;
    private void showQuantityDialog(ModelProduct modelProduct) {
        //inflate layout for dialog
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity, null);
        //init layout views
        ImageView productimage = view.findViewById(R.id.productIv);
        TextView producttitle = view.findViewById(R.id.titleTV);
        final TextView productquantity = view.findViewById(R.id.productQuantityTV);
        TextView productdiscountnote = view.findViewById(R.id.discountNoteTV);
        TextView productrate = view.findViewById(R.id.originalPriceTV);
        TextView productdiscountrate = view.findViewById(R.id.discountPriceTV);
        final TextView productfinalrate = view.findViewById(R.id.finalTV);
        ImageButton mdecrementbtn = view.findViewById(R.id.decrementbtn);
        TextView productnumber = view.findViewById(R.id.quantityTV);
        ImageButton mincrementbtn = view.findViewById(R.id.incrementbtn);
        Button mcontinuebtn = view.findViewById(R.id.continuebtn);


        // get data from model

        String title = modelProduct.getItemName();
        String discountNote = modelProduct.getDiscountNote();
        final String itemCode = modelProduct.getItemCode();
        String cess=modelProduct.getCess_rate();
        String hsncode=modelProduct.getHsn_code();
        String igst=modelProduct.getIgst_rate();
        String sgst=modelProduct.getSgst_rate();
        String cgst=modelProduct.getCgst_rate();
        String productIcon = modelProduct.getProductIcon();
        String productratetype = modelProduct.getRateType();
        final String price;
        if (modelProduct.getDiscountAvailable().equals("true")) {
            price = modelProduct.getDiscountPrice();
            productdiscountnote.setVisibility(View.VISIBLE);
            productrate.setPaintFlags(productrate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);//add strike through on originalPrice
        } else {
            productdiscountnote.setVisibility(View.GONE);
            productdiscountrate.setVisibility(View.GONE);
            price = modelProduct.getRate();
        }
        cost = Double.parseDouble(price.replaceAll("Rs. ", ""));
        finalCost=Double.parseDouble(price.replaceAll("Rs. ", ""));
        quantity=1;

        //dialog
        AlertDialog.Builder builder= new AlertDialog.Builder(context);
        builder.setView(view);

        try{
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_cart_grey).into(productimage);
        }
        catch (Exception e){
            productimage.setImageResource(R.drawable.ic_cart_grey);
        }
        producttitle.setText(""+title);
        productquantity.setText(""+productratetype);
        productdiscountnote.setText(""+discountNote);
        productnumber.setText(""+quantity);
        productrate.setText("Rs. "+modelProduct.getRate());
        productdiscountrate.setText("Rs. "+modelProduct.getDiscountPrice());
        productfinalrate.setText("Rs. "+finalCost);

        AlertDialog dialog=builder.create();
        dialog.show();
         //increase quantity of product
        mincrementbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = finalCost + cost;
                quantity++;
                productfinalrate.setText("Rs. "+finalCost);
                productnumber.setText(""+quantity);

            }
        });
        //decrease quantity of product
        mdecrementbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity>1) {
                    finalCost = finalCost - cost;
                    quantity--;
                    productfinalrate.setText("Rs. " + finalCost);
                    productnumber.setText("" + quantity);
                }

            }
        });
        mcontinuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String title=producttitle.getText().toString().trim();
                String priceEach=price;
                String finalPrice=productrate.getText().toString().trim().replaceAll("Rs. ","");
                String quanTity=productnumber.getText().toString().trim();
                String productimage=productIcon;
                String productrateType=productquantity.getText().toString().trim();

                //add to SQLite db
                addtoCart(itemCode,title,priceEach,finalPrice,quanTity,productratetype,cess,hsncode,igst,cgst,sgst,productimage);
                dialog.dismiss();
            }
        });


    }
    private int itemId=1;
    private void addtoCart(String itemCode, String title, String priceEach, String Price, String quanTity,String ratetype,
                           String cess, String hsncode, String igst, String cgst, String sgst,String productimage) {
        if(cess==null)
            cess="0";
        if(hsncode==null)
            hsncode="0";
        if(igst==null)
            igst="0";
        if(cgst==null)
            cgst="0";
        if(sgst==null)
            sgst="0";

    itemId++;
        EasyDB easyDB=EasyDB.init(context,"ITEMS_DB")
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
        Boolean b=easyDB
                .addData("Item_Id",itemId)
                .addData("Item_Code",itemCode)
                .addData("Item_Name",title)
                .addData("Item_Price_Each",priceEach)
                .addData("Item_Price",Price)
                .addData("Item_Quantity",quanTity)
                .addData("Item_Cess",cess)
                .addData("Item_Hsn_Code",hsncode)
                .addData("Item_Cgst",igst)
                .addData("Item_Igst",cgst)
                .addData("Item_Sgst",sgst)
                .doneDataAdding();
        Toast.makeText(context,"Added to Cart...",Toast.LENGTH_SHORT).show();


    }




    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterProductUser(this,filterList);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        private ImageView mproductImage;
        private TextView mtitle,mdiscountnote,mdiscountprice,mprice,mratetype;
        private ImageButton maddtocart;
        public HolderProductUser(@NonNull  View itemView){
        super(itemView);

        mproductImage=itemView.findViewById(R.id.productIconIv);
           mtitle =itemView.findViewById(R.id.TitleTv);
            mdiscountnote=itemView.findViewById(R.id.discountedNoteTv);
            mdiscountprice=itemView.findViewById(R.id.discountedPriceTv);
            mprice=itemView.findViewById(R.id.originalPriceTv);
            mratetype=itemView.findViewById(R.id.rateTypeTV);
           maddtocart =itemView.findViewById(R.id.addproducttocart);

        }
    }
}
