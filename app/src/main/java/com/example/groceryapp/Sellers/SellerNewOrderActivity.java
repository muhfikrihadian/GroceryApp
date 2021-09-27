package com.example.groceryapp.Sellers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.Model.Order;
import com.example.groceryapp.Prevalent.Prevalent;
import com.example.groceryapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SellerNewOrderActivity extends AppCompatActivity {
    private RecyclerView ordersList;
    private DatabaseReference ordersRef;
    private String sID;
    private DatabaseReference sellersRef, sellerCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_new_order);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        sellersRef = FirebaseDatabase.getInstance().getReference().child("Sellers");
        sellerCart = FirebaseDatabase.getInstance().getReference().child("Cart List");

        sID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ordersList = findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));

        sellersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    sID = dataSnapshot.child("sid").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>().setQuery(ordersRef.orderByChild("sellerID").equalTo(sID), Order.class).build();
        FirebaseRecyclerAdapter<Order, AdminOrdersViewHolder> adapter = new FirebaseRecyclerAdapter<Order, AdminOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull Order model) {
                holder.userName.setText(model.getName());
                holder.userPhoneNumber.setText(model.getPhone());
                holder.userTotalPrice.setText("Rp " + model.getTotalAmount());
                holder.userDateTime.setText(model.getDate() + " " + model.getTime());
                holder.userShippingAddress.setText(model.getAddress() + ", " + model.getCity());

                holder.ShowOrdersBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uID = getRef(position).getKey();
                        Intent intent = new Intent(SellerNewOrderActivity.this, SellerUserProductsActivity.class);
                        intent.putExtra("uid", uID);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]{"Yes", "No"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(SellerNewOrderActivity.this);
                        builder.setTitle("Have you shipped this order? ");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    String uID = getRef(position).getKey();
                                    RemoverOrder(uID);
                                    RemoverSellerCart();
                                } else {
                                    finish();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                return new AdminOrdersViewHolder(view);
            }
        };

        ordersList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress;
        public Button ShowOrdersBtn;

        public AdminOrdersViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userShippingAddress = itemView.findViewById(R.id.order_address_city);
            ShowOrdersBtn = itemView.findViewById(R.id.show_all_products);

        }
    }

    private void RemoverSellerCart() {
        sellerCart.child("Seller View").child(Prevalent.currentOnlineUser.getPhone()).removeValue();
    }

    private void RemoverOrder(String uID) {
        ordersRef.child(uID).removeValue();
    }
}
