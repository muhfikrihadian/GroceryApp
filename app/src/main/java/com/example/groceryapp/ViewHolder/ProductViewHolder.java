package com.example.groceryapp.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.groceryapp.Interface.ItemClickListener;
import com.example.groceryapp.R;

import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName, txtProductDescription, txtProductPrice, txtShopName;
    public ImageView imageView;
    public Button Pesanbtn;
    public ItemClickListener listener;


    public ProductViewHolder(View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.select_product_image);
        txtShopName = (TextView) itemView.findViewById(R.id.shop_name);
        txtProductName = (TextView) itemView.findViewById(R.id.product_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.product_price);
        Pesanbtn = (Button) itemView.findViewById(R.id.pesan_btn);
    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View view)
    {
        listener.onClick(view, getAdapterPosition(), false);
    }
}