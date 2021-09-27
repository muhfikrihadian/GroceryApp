package com.example.groceryapp.Buyers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.Model.Cart;
import com.example.groceryapp.Prevalent.Prevalent;
import com.example.groceryapp.R;
import com.example.groceryapp.ViewHolder.HistoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HistoryOrderActivity extends AppCompatActivity {
    private RecyclerView productsList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartListRef, historyListRef;
    private String userID = "";

    private int deliveryFee = 5000;
    private int overTotalPrice = 0;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_order);
        userID = getIntent().getStringExtra("uid");
        productsList = findViewById(R.id.histori_list);
        productsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);
        historyListRef = FirebaseDatabase.getInstance().getReference().child("Histori Order").child(Prevalent.currentOnlineUser.getPhone()).child("Products");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("History Order");
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
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>().setQuery(historyListRef, Cart.class).build();
        FirebaseRecyclerAdapter<Cart, HistoryViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, HistoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HistoryViewHolder holder, int position, @NonNull Cart model) {
                holder.txtDate.setText(model.getDate());
                holder.txtTime.setText(model.getTime());
                holder.txtTokoName.setText(model.getSellerName());
                holder.txtProductName.setText(model.getPname());
                holder.txtProductPrice.setText("Rp " + model.getPrice());
                holder.txtProductQuantity.setText(model.getQuantity());
                int oneTypeProductPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity()) + deliveryFee;
                overTotalPrice = overTotalPrice + oneTypeProductPrice;
                holder.txtTotalPrice.setText("Rp " + oneTypeProductPrice);
            }

            @NonNull
            @Override
            public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_items_layout, parent, false);
                HistoryViewHolder holder = new HistoryViewHolder(view);
                return holder;
            }
        };
        productsList.setAdapter(adapter);
        adapter.startListening();
    }
}
