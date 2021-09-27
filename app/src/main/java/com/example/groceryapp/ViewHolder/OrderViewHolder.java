package com.example.groceryapp.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.groceryapp.Interface.ItemClickListener;
import com.example.groceryapp.R;

import androidx.recyclerview.widget.RecyclerView;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{
    public TextView txtOrderId, txtOrderPhone, txtOrderAddress, txtOrderPrice, txtOrderDateTime;
    public Button ShowOrdersBtn;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderId = itemView.findViewById(R.id.order_user_name);
        txtOrderPhone = itemView.findViewById(R.id.order_phone_number);
        txtOrderPrice = itemView.findViewById(R.id.order_total_price);
        txtOrderDateTime = itemView.findViewById(R.id.order_date_time);
        txtOrderAddress = itemView.findViewById(R.id.order_address_city);
        ShowOrdersBtn = itemView.findViewById(R.id.show_all_products);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo){
        contextMenu.setHeaderTitle("Select The Action");

        contextMenu.add(0,0,getAdapterPosition(), "Update");
        contextMenu.add(0,1,getAdapterPosition(), "Update");
    }
}
