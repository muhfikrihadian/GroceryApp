package com.example.groceryapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.groceryapp.Interface.ItemClickListener;
import com.example.groceryapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtDate, txtTime, txtProductName, txtProductPrice, txtProductQuantity, txtTotalPrice, txtTokoName;
    private ItemClickListener itemClickListener;
    public ImageView imageView;


    public HistoryViewHolder(@NonNull View itemView)
    {
        super(itemView);
        txtDate = itemView.findViewById(R.id.histori_date);
        txtTime = itemView.findViewById(R.id.histori_time);
        txtTokoName = itemView.findViewById(R.id.histori_toko_name);
        txtProductName = itemView.findViewById(R.id.histori_product_name);
        txtProductPrice= itemView.findViewById(R.id.histori_product_price);
        txtProductQuantity = itemView.findViewById(R.id.histori_product_quantity);
        txtTotalPrice = itemView.findViewById(R.id.histori_total_harga);
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
