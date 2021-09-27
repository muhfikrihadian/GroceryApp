package com.example.groceryapp.Sellers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.groceryapp.Buyers.UpdateInfoProfileActivity;
import com.example.groceryapp.Model.Products;
import com.example.groceryapp.R;
import com.example.groceryapp.ViewHolder.SelleritemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SellerUpdateProductActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter<Products, SelleritemViewHolder> adapter;
    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private TextView namaToko;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_update_product);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Products");
        namaToko = (TextView) findViewById(R.id.nama_toko);

        recyclerView = findViewById(R.id.recyclerview_update_produk);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        loadData();
    }

    private void loadData()
    {
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(databaseReference.orderByChild("sID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()),Products.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Products, SelleritemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SelleritemViewHolder holder, int position, @NonNull final Products model) {

                holder.txtProductName.setText(model.getPname());
                holder.txtProductPrice.setText("Rp " + model.getPrice() + ",-");
                holder.txtProductState.setText(model.getProductState());
                Picasso.with(SellerUpdateProductActivity.this).load(model.getImage()).into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        holder.imageView.setImageResource(R.mipmap.ic_launcher);
                    }
                });
                String NamaToko = String.valueOf(model.getsellerName());
                namaToko.setText(NamaToko);

                holder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(SellerUpdateProductActivity.this, SellerMaintainProductActivity.class);
                        intent.putExtra("pid", model.getPid());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public SelleritemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.sellerproducts_items_layout,parent,false);
                return new SelleritemViewHolder(view);
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
