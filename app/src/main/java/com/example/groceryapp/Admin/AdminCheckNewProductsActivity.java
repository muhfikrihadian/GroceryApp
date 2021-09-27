package com.example.groceryapp.Admin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.groceryapp.Buyers.ProductDetailActivity;
import com.example.groceryapp.Model.Products;
import com.example.groceryapp.R;
import com.example.groceryapp.ViewHolder.SelleritemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdminCheckNewProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;
    private DatabaseReference sellerproductRef;
    private String sID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_new_products);

        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerView = findViewById(R.id.admin_products_checklist);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(unverifiedProductsRef.orderByChild("productState").equalTo("Not Approved"), Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, SelleritemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, SelleritemViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final SelleritemViewHolder holder, int position, @NonNull final Products model)
                    {
                        holder.txtShopName.setText(model.getsellerName());
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductPrice.setText("Rp " + model.getPrice() + ",-");
                        holder.txtProductState.setText(model.getProductState());
                        Picasso.with(AdminCheckNewProductsActivity.this).load(model.getImage()).into(holder.imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                holder.imageView.setImageResource(R.mipmap.ic_launcher);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                final String productID = model.getPid();

                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminCheckNewProductsActivity.this);
                                builder.setTitle("Apakah Kamu ingin menyetujui Menu ini? Apa kamu Yakin?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position)
                                    {
                                        if (position == 0)
                                        {
                                            ChangeProductState(productID);
                                        }
                                        if (position == 1)
                                        {
                                            DeleteProductState(productID);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }



                    @NonNull
                    @Override
                    public SelleritemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sellerproducts_items_layout, parent, false);
                        SelleritemViewHolder holder = new SelleritemViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void DeleteProductState(String productID)
    {
        unverifiedProductsRef.child(productID)
                .removeValue();
    }


    private void ChangeProductState(String productID)
    {
        unverifiedProductsRef.child(productID)
                .child("productState")
                .setValue("Approved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Toast.makeText(AdminCheckNewProductsActivity.this, "Item ini berhasil diubah, dan sekarang tersedia untuk penjualan ^^", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
