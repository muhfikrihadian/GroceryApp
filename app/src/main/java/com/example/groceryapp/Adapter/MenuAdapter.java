package com.example.groceryapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.Buyers.ProductDetailActivity;
import com.example.groceryapp.Model.Products;
import com.example.groceryapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    public static final int TipePublik = 1, TipeRecommend = 2;
    Integer Tipe = 0;
    ArrayList<Products> dataList;
    Context context;


    public MenuAdapter(Context context, ArrayList<Products> dataList, Integer tipe) {
        this.dataList = dataList;
        this.context = context;
        this.Tipe = tipe;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (Tipe == TipePublik) {
            View view = layoutInflater.inflate(R.layout.item_product, parent, false);
            return new MenuViewHolder(view);
        } else {
            View view = layoutInflater.inflate(R.layout.item_recommend, parent, false);
            return new MenuViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final MenuViewHolder holder, final int position) {
        final Products data = dataList.get(position);
        holder.txtRestoName.setText(data.getsellerName());
        holder.txtProductName.setText(data.getPname());
        holder.txtProductPrice.setText("Rp " + data.getPrice() + ",-");
        holder.ratingBar.setRating(data.getRating());
        Picasso.with(context).load(data.getImage()).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                holder.imageView.setImageResource(R.mipmap.ic_launcher);
            }
        });
        holder.Pesanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("pid", data.getPid());
                intent.putExtra("tipe", data.getTipe());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView txtRestoName, txtProductName, txtProductPrice;
        ImageView imageView;
        Button Pesanbtn;
        RatingBar ratingBar;

        public MenuViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.select_product_image);
            txtRestoName = (TextView) itemView.findViewById(R.id.resto_name);
            txtProductName = (TextView) itemView.findViewById(R.id.product_name);
            txtProductPrice = (TextView) itemView.findViewById(R.id.product_price);
            Pesanbtn = (Button) itemView.findViewById(R.id.pesan_btn);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }
    }
}
