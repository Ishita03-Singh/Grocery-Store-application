package com.example.groceryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem>{

private Context context;
private ArrayList<ModelCartItem> cartItems;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems ;
    }



    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
View view= LayoutInflater.from(context).inflate(R.layout.row_cart_item,parent,false);
return new HolderCartItem(view);

    }







    @Override
    public void onBindViewHolder(@NonNull  AdapterCartItem.HolderCartItem holder, int position) {
       ModelCartItem modelCartItem=cartItems.get(position);
        String id=modelCartItem.getId();
        String pid=modelCartItem.getPid();
        String title=modelCartItem.getName();
        String price=modelCartItem.getPrice();
        String number=modelCartItem.getNumber();
        String cess=modelCartItem.getCess();
        String cgst=modelCartItem.getCgst();
        String hsn=modelCartItem.getHsn();
        String igst=modelCartItem.getIgst();
        String sgst=modelCartItem.getSgst();



        //set data
        holder.mcartproducttitle.setText(title);
        holder.mcartproductprice.setText("Rs. "+String.valueOf(Double.parseDouble(price)*Double.parseDouble(number))) ;
        holder.mcartproductratetype.setText("");
        holder.mcartproductnumber.setText(number);


        holder.mremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create table if not exist
                EasyDB easyDB=EasyDB.init(context,"ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                        .addColumn(new Column("Item_Code",new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Name",new String[]{"text"}))
                        .addColumn(new Column("Item_Price_Each",new String[]{"text"}))
                        .addColumn(new Column("Item_Price",new String[]{"text"}))
                        .addColumn(new Column("Item_Quantity",new String[]{"text"}))
                        .addColumn(new Column("Item_RateType",new String[]{"text"}))
                        .addColumn(new Column("Item_Cess",new String[]{"text"}))
                        .addColumn(new Column("Item_Hsn_Code",new String[]{"text"}))
                        .addColumn(new Column("Item_Cgst",new String[]{"text"}))
                        .addColumn(new Column("Item_Igst",new String[]{"text"}))
                        .addColumn(new Column("Item_Sgst",new String[]{"text"}))
                        .doneTableColumn();

            easyDB.deleteRow(1,id);
                Toast.makeText(context,"Removed item from Cart...",Toast.LENGTH_SHORT).show();


                //refresh list
                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

                double tx=Double.parseDouble((((MainCustomerActivity)context).  mstotalMrpTV.getText().toString().trim().replace("Rs. ","")));
                double totalPrice=tx-Double.parseDouble( price.replace("Rs. ",""));


                double  tax=Double.parseDouble(cess)+Double.parseDouble(hsn)
                        +Double.parseDouble(cgst)+Double.parseDouble(igst)
                        +Double.parseDouble(sgst);

                double taxPrice=Double.parseDouble((((MainCustomerActivity)context).  mtaxtv.getText().toString().trim().replace("Rs. ","")));

                double thistax=taxPrice-tax;

                double sTotal=Double.parseDouble(String.format("%.2f",totalPrice))+Double.parseDouble(String.format("%.2f",thistax));

                ( (MainCustomerActivity)context).allmrpsum=0.0;
                ( (MainCustomerActivity)context).alltaxessum=0.0;
                ( (MainCustomerActivity)context).mfinaltotalTV.setText("RS. "+String.format("%.2f",sTotal));
                ( (MainCustomerActivity)context).mstotalMrpTV.setText("RS. "+String.format("%.2f",Double.parseDouble(String.format("%2f",totalPrice))));
                ( (MainCustomerActivity)context).mtaxtv.setText("RS. "+String.format("%.2f",Double.parseDouble(String.format("%2f",thistax))));



                
            }
        });
    }





    @Override
    public int getItemCount() {
        return cartItems.size();
    }


    class HolderCartItem extends RecyclerView.ViewHolder{
         private ImageView mcartproductImage;
         private TextView mcartproducttitle,mcartproductprice,
                 mcartproductratetype,mcartproductnumber,mremove;

         public HolderCartItem(@NonNull View itemView)
         {
             super(itemView);

             mcartproductImage=itemView.findViewById(R.id.cartproductIconIv);
             mcartproducttitle =itemView.findViewById(R.id.cartproductTitleTv);
             mcartproductprice =itemView.findViewById(R.id.cartproductPriceTv);
             mcartproductratetype =itemView.findViewById(R.id.cartproductquantityTV);
             mcartproductnumber=itemView.findViewById(R.id.cartproductAmountTV);
             mremove=itemView.findViewById(R.id.cartitemremoveTV);

         }

     }

}
