package com.example.groceryapp.Sellers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.groceryapp.Model.Cart;
import com.example.groceryapp.Prevalent.Prevalent;
import com.example.groceryapp.R;
import com.example.groceryapp.ViewHolder.HistoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SellerHistoriOrderActivity extends AppCompatActivity {
    private RecyclerView productsList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference historyListRef;
    private String userID = "";

    private int deliveryFee = 5000;
    private int overTotalPrice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_histori_order);
        userID = getIntent().getStringExtra("uid");
        productsList = findViewById(R.id.seller_histori_list);
        productsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);
        historyListRef = FirebaseDatabase.getInstance().getReference().child("Histori Order Seller").child("Products");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>().setQuery(historyListRef.orderByChild("sID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()), Cart.class).build();
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
