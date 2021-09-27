package com.example.groceryapp.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.Adapter.MenuAdapter;
import com.example.groceryapp.Model.ModelReview;
import com.example.groceryapp.Model.Products;
import com.example.groceryapp.R;
import com.example.groceryapp.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HomeActivity extends AppCompatActivity {
    private long exitTime = 0;
    private RecyclerView recyclerView;
    private SearchView inputText;
    private RecyclerView searchList;
    private String Searchinput;
    FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter;
    MenuAdapter menuAdapter, adapterSearch;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<Products> arrayList, searchProducts;
    ArrayList<ModelReview> modelReviews;
    Integer totalData = 0;
    Integer totalCount = 0;
    Integer indRating;
    ProgressDialog progressDialog;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        arrayList = new ArrayList<>();
        searchProducts = new ArrayList<>();
        inputText = (SearchView) findViewById(R.id.search_product_name);
        searchList = findViewById(R.id.recycler_roduk);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchList.setLayoutManager(new GridLayoutManager(this, 2));

        inputText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                inputText.clearFocus();
                searchProducts(query.toLowerCase());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    searchProducts(newText.toLowerCase());
                } else {
                    recyclerView.setAdapter(menuAdapter);
                }
                return false;
            }
        });
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Products");
        recyclerView = findViewById(R.id.recycler_roduk);
        setup();
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
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    void setup() {
        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Products").orderByChild("productState").equalTo("Approved");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        Products products = noteDataSnapshot.getValue(Products.class);
                        products.setId(noteDataSnapshot.getKey());
                        Log.e("Product", new Gson().toJson(products));
                        arrayList.add(products);
                    }
                    new GetRating().execute();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                finish();
                Toast.makeText(HomeActivity.this, "Maaf saat ini server sedang sibuk", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class GetRating extends AsyncTask<Void, Integer, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            totalData = arrayList.size();
            totalCount = 0;
        }

        protected String doInBackground(Void... arg0) {
            for (int i = 0; i < arrayList.size(); i++) {
                setRating(i);
            }
            return "You are at PostExecute";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            inquiry();
        }
    }

    void setRating(final Integer i) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Review").orderByChild("idmenu").equalTo(arrayList.get(i).getPid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    modelReviews = new ArrayList<>();
                    modelReviews.clear();
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        ModelReview modelReview = issue.getValue(ModelReview.class);
                        modelReview.setId(issue.getKey());
                        modelReviews.add(modelReview);
                    }
                    if (modelReviews.size() > 0) {
                        Integer rating = 0, qty = 0;
                        for (int listRating = 0; listRating < modelReviews.size(); listRating++) {
                            rating += modelReviews.get(listRating).getRating();
                            qty++;
                        }
                        Double doubleRating = new Double(rating / qty);
                        Integer totalRating = doubleRating.intValue();
                        arrayList.get(i).setRating(totalRating);
                    } else {
                        arrayList.get(i).setRating(0);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        totalCount++;
    }

    void inquiry() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (totalCount == totalData) {
                    setData();
                } else {
                    inquiry();
                }
            }
        }, 1000);
    }

    void setData() {
        Collections.sort(arrayList, new Comparator<Products>() {
            @Override
            public int compare(Products lhs, Products rhs) {
                return rhs.getRating().compareTo(lhs.getRating());
            }
        });
        menuAdapter = new MenuAdapter(HomeActivity.this, arrayList, MenuAdapter.TipePublik);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(menuAdapter);
        progressDialog.dismiss();
    }

    private void searchProducts(String name) {
        searchProducts.clear();
        for (Products v : arrayList) {
            if (v.getPname().toLowerCase().contains(name)) {
                searchProducts.add(v);
            }
        }
        adapterSearch = new MenuAdapter(HomeActivity.this, searchProducts, MenuAdapter.TipePublik);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterSearch);
    }

}

