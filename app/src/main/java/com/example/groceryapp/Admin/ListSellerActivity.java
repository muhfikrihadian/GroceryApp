package com.example.groceryapp.Admin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.Adapter.SellerAdapter;
import com.example.groceryapp.Buyers.ProductDetailActivity;
import com.example.groceryapp.Helpers.RemoveListener;
import com.example.groceryapp.Model.Sellers;
import com.example.groceryapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ListSellerActivity extends AppCompatActivity {
    ArrayList<Sellers> arrayList = new ArrayList<>();
    SellerAdapter sellerAdapter;
    RecyclerView rcySeller;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_seller);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rcySeller = (RecyclerView) findViewById(R.id.rcySeller);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Management Seller");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addData();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    void addData() {
        final ProgressDialog progressDialog = new ProgressDialog(ListSellerActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        arrayList = new ArrayList<>();
        arrayList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Sellers");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Sellers sellers = issue.getValue(Sellers.class);
                        sellers.setId(issue.getKey());
                        arrayList.add(sellers);
                    }
                    Log.e("ModelSeller", new Gson().toJson(arrayList));
                    sellerAdapter = new SellerAdapter(ListSellerActivity.this, arrayList);
                    rcySeller.setLayoutManager(new LinearLayoutManager(ListSellerActivity.this));
                    rcySeller.setAdapter(sellerAdapter);
                    sellerAdapter.setOnRemoveListener(new RemoveListener() {
                        @Override
                        public void onClicked() {
                            ListSellerActivity.this.recreate();
                        }
                    });
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }
}