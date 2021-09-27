package com.example.groceryapp.Sellers;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.Model.Products;
import com.example.groceryapp.R;
import com.example.groceryapp.ViewHolder.SelleritemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class SellerDaftarProductActivity extends AppCompatActivity {

    private TextView namaToko;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;
    private DatabaseReference ProductsRef;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private String sID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_daftar_product);

        namaToko = (TextView) findViewById(R.id.nama_toko);

        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Products");
        recyclerView = findViewById(R.id.recycler_daftarproduk_seller);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(unverifiedProductsRef.orderByChild("sID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()), Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, SelleritemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, SelleritemViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final SelleritemViewHolder holder, int position, @NonNull final Products model) {
                        holder.txtShopName.setText(model.getsellerName());
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductPrice.setText("Rp " + model.getPrice() + ",-");
                        holder.txtProductState.setText(model.getProductState());
                        Picasso.with(SellerDaftarProductActivity.this).load(model.getImage()).into(holder.imageView, new Callback() {
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

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String productID = model.getPid();

                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(SellerDaftarProductActivity.this);
                                builder.setTitle("Apakah Kamu ingin Menghapus Menu ini? Apa kamu Yakin?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position) {
                                        if (position == 0) {
                                            deleteProduct(productID);
                                        }
                                        if (position == 1) {

                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }


                    @NonNull
                    public SelleritemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sellerproducts_items_layout, parent, false);
                        SelleritemViewHolder holder = new SelleritemViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteProduct(String productID) {
        unverifiedProductsRef.child(productID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SellerDaftarProductActivity.this, "Item Berhasil Dihapus.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
