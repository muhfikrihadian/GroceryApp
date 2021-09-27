package com.example.groceryapp.Buyers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.Model.Cart;
import com.example.groceryapp.Prevalent.Prevalent;
import com.example.groceryapp.R;
import com.example.groceryapp.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recycleView;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference historyRef, sellerhistoryRef;
    private Button NextProcessBtn;
    private TextView txtTotalAmount, txtMsg1, totalHargaView;
    private String productID = "";
    private int deliveryFee = 5000;
    private int overTotalPrice = 0;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        productID = getIntent().getStringExtra("pid");
        historyRef = FirebaseDatabase.getInstance().getReference().child("Histori Order");
        sellerhistoryRef = FirebaseDatabase.getInstance().getReference().child("Histori Order Seller");
        recycleView = findViewById(R.id.cart_list);
        recycleView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(layoutManager);

        NextProcessBtn = (Button) findViewById(R.id.next_process_btn);
        txtMsg1 = (TextView) findViewById(R.id.msg1);
        txtTotalAmount = (TextView) findViewById(R.id.total_price);
        totalHargaView = (TextView) findViewById(R.id.total_harga_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            startActivity(new Intent(this, HomeActivity.class));
        } else if (item.getItemId() == R.id.cart) {
            startActivity(new Intent(this, CartActivity.class));
        } else if (item.getItemId() == R.id.history) {
            startActivity(new Intent(this, HistoryOrderActivity.class));
        } else if (item.getItemId() == R.id.profile) {
            startActivity(new Intent(this, More.class));
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products"), Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                holder.txtProductName.setText(model.getPname());
                holder.txtProductPrice.setText("Rp " + model.getPrice());
                holder.txtProductQuantity.setText(model.getQuantity());
                holder.txtProductToko.setText(model.getSellerName());

                int oneTypeProductPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity()) + deliveryFee;
                overTotalPrice = overTotalPrice + oneTypeProductPrice;
                holder.txtTotalPrice.setText("Rp " + oneTypeProductPrice);
                totalHargaView.setText("Rp " + overTotalPrice);
                NextProcessBtn.setVisibility(View.VISIBLE);

                NextProcessBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                        intent.putExtra("Total Harga", String.valueOf(overTotalPrice));
                        intent.putExtra("pid", model.getPid());
                        intent.putExtra("pname", model.getPname());
                        intent.putExtra("price", model.getPrice());
                        intent.putExtra("tipe", model.getTipe());
                        intent.putExtra("date", model.getDate());
                        intent.putExtra("time", model.getTime());
                        intent.putExtra("quantity", model.getQuantity());
                        intent.putExtra("sellerName", model.getSellerName());
                        intent.putExtra("sellerAddress", model.getSelleraddress());
                        intent.putExtra("sellerPhone", model.getSellerphone());
                        intent.putExtra("sellerEmail", model.getSelleremail());
                        intent.putExtra("sID", model.getSID());
                        startActivity(intent);
                        finish();
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]{"Edit", "Remove"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options: ");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    Intent intent = new Intent(CartActivity.this, ProductDetailActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                if (i == 1) {
                                    cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(model.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(CartActivity.this, "Item Berhasil Dihapus.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                    cartListRef.child("Seller View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(model.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(CartActivity.this, "Item Berhasil Dihapus.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    historyRef.child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(model.getPid()).removeValue();
                                    sellerhistoryRef.child("Products").child(model.getPid()).removeValue();
                                    NextProcessBtn.setVisibility(View.INVISIBLE);
                                    totalHargaView.setText("-");
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recycleView.setAdapter(adapter);
        adapter.startListening();
    }

    private void CheckOrderState() {
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if (shippingState.equals("shipped")) {
                        txtTotalAmount.setText("Untuk " + userName + "\n Pesanan berhasil dikirim.");
                        recycleView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Selamat, Pesanan anda berhasil dikirim. Tunggu Pesanan di Depan Rumah");
                        NextProcessBtn.setVisibility(View.GONE);
                    } else if (shippingState.equals("not shipped")) {
                        txtTotalAmount.setText("Shipping State = Not Shipped");
                        recycleView.setVisibility(View.GONE);
                        totalHargaView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        NextProcessBtn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
