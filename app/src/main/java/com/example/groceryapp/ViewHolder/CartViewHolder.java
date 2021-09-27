package com.example.groceryapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.groceryapp.Interface.ItemClickListener;
import com.example.groceryapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName, txtProductPrice, txtProductQuantity, txtTotalPrice, txtProductToko;
    private ItemClickListener itemClickListener;
    public ImageView imageView;


    public CartViewHolder(@NonNull View itemView)
    {
        super(itemView);
        txtProductName = itemView.findViewById(R.id.cart_product_name);
        txtProductPrice= itemView.findViewById(R.id.cart_product_price);
        txtProductQuantity = itemView.findViewById(R.id.cart_product_quantity);
        txtProductToko = itemView.findViewById(R.id.cart_toko_name);
        txtTotalPrice = itemView.findViewById(R.id.cart_total_harga);
    }

    @Override
    public void onClick(View view)
    {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }
}
