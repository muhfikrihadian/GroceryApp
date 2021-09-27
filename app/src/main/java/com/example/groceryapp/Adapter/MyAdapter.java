package com.example.groceryapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.groceryapp.Buyers.ProductDetailActivity;
import com.example.groceryapp.Model.Products;
import com.example.groceryapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyAdapterViewHolder> {

    public Context c;
    public ArrayList<Products> arrayList;
    public MyAdapter(Context c, ArrayList<Products> arrayList) {
        this.c=c;
        this.arrayList=arrayList;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_items_layout,parent,false);
        return new MyAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyAdapterViewHolder holder, int position) {
        Products products = arrayList.get(position);

        holder.txtShopName.setText(products.getsellerName());
        holder.txtProductName.setText(products.getPname());
        holder.txtProductPrice.setText("Rp " + products.getPrice());
        Picasso.with(c).load(products.getImage()).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                holder.imageView.setImageResource(R.mipmap.ic_launcher);
            }
        });
    }

    public class MyAdapterViewHolder extends RecyclerView.ViewHolder
    {

        public TextView txtProductName, txtProductDescription, txtProductPrice, txtShopName;
        public ImageView imageView;
        public Button Pesanbtn;

        public MyAdapterViewHolder(View itemview){
            super(itemview);

            imageView = (ImageView) itemView.findViewById(R.id.select_product_image);
            txtShopName = (TextView) itemView.findViewById(R.id.shop_name);
            txtProductName = (TextView) itemView.findViewById(R.id.product_name);
            txtProductDescription = (TextView) itemView.findViewById(R.id.product_description);
            txtProductPrice = (TextView) itemView.findViewById(R.id.product_price);
            Pesanbtn = (Button) itemView.findViewById(R.id.pesan_btn);
        }
    }
}
