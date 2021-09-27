package com.example.groceryapp.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.groceryapp.Adapter.MenuAdapter;
import com.example.groceryapp.Adapter.ReviewAdapter;
import com.example.groceryapp.Helpers.RemoveListener;
import com.example.groceryapp.Model.ModelReview;
import com.example.groceryapp.Model.Products;
import com.example.groceryapp.Prevalent.Prevalent;
import com.example.groceryapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ProductDetailActivity extends AppCompatActivity {
    private LinearLayout btnAddReview;
    private Button addToCartButton;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice, productDescription, productName;
    private String productID = "", state = "Normal";
    private String sID = "";
    private String sid = "";
    private DatabaseReference sellersRef;
    private String sName, sAddress, sPhone, sEmail;
    private ArrayList<ModelReview> modelReviews, modelReviewsRecommend;
    private ReviewAdapter adapter;
    private RecyclerView rcyReview;
    MenuAdapter menuAdapter;
    Toolbar toolbar;
    String tipeProduk = "";
    ArrayList<Products> arrayList;
    RecyclerView rcyRecommend;
    ProgressDialog progressDialog;
    Integer totalData = 0;
    Integer totalCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        productID = getIntent().getStringExtra("pid");
        sid = getIntent().getStringExtra("sID");
        sellersRef = FirebaseDatabase.getInstance().getReference().child("Products");
        btnAddReview = (LinearLayout) findViewById(R.id.btnAddReview);
        addToCartButton = (Button) findViewById(R.id.pd_add_to_cart_button);
        numberButton = (ElegantNumberButton) findViewById(R.id.number_btn);
        productImage = (ImageView) findViewById(R.id.product_image_details);
        productName = (TextView) findViewById(R.id.product_name_details);
        productDescription = (TextView) findViewById(R.id.product_description_details);
        productPrice = (TextView) findViewById(R.id.product_price_details);
        rcyReview = (RecyclerView) findViewById(R.id.rcyReview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rcyRecommend = (RecyclerView) findViewById(R.id.rcyRecommend);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Detail Product");

        arrayList = new ArrayList<>();
        modelReviewsRecommend = new ArrayList<>();
        modelReviews = new ArrayList<>();
        getProductDetails(productID);

        btnAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, AddReviewActivity.class);
                intent.putExtra("IdMenu", productID);
                startActivity(intent);
            }
        });

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state.equals("Order Placed") || state.equals("Order Shipped")) {
                    Toast.makeText(ProductDetailActivity.this, "Kamu dapat membayar lebih saat pesanan sampai atau terkonfirmasi.", Toast.LENGTH_LONG).show();
                } else {
                    addingToCartList();
                    addingToHistory();
                    addingToSellerHistory();
                }
            }
        });

        sellersRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    sName = dataSnapshot.child("sellerName").getValue().toString();
                    sAddress = dataSnapshot.child("sellerAddress").getValue().toString();
                    sPhone = dataSnapshot.child("sellerPhone").getValue().toString();
                    sID = dataSnapshot.child("sID").getValue().toString();
                    sEmail = dataSnapshot.child("sellerEmail").getValue().toString();
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
        CheckOrderState();
        addData();
    }

    private void addingToCartList() {
        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("tipe", tipeProduk);
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", numberButton.getNumber());
        cartMap.put("sellerName", sName);
        cartMap.put("sellerAddress", sAddress);
        cartMap.put("sellerPhone", sPhone);
        cartMap.put("sellerEmail", sEmail);
        cartMap.put("sID", sID);


        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    cartListRef.child("Seller View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProductDetailActivity.this, "Ditambahkan Ke Daftar Keranjang", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ProductDetailActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }

    private void addingToHistory() {
        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());
        final DatabaseReference historyListRef = FirebaseDatabase.getInstance().getReference().child("Histori Order");
        final HashMap<String, Object> historyMap = new HashMap<>();
        historyMap.put("pid", productID);
        historyMap.put("pname", productName.getText().toString());
        historyMap.put("price", productPrice.getText().toString());
        historyMap.put("quantity", numberButton.getNumber());
        historyMap.put("date", saveCurrentDate);
        historyMap.put("time", saveCurrentTime);
        historyMap.put("sellerName", sName);
        historyMap.put("sellerAddress", sAddress);
        historyMap.put("sellerPhone", sPhone);
        historyMap.put("sellerEmail", sEmail);
        historyMap.put("sID", sID);

        historyListRef.child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID).updateChildren(historyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    private void addingToSellerHistory() {
        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference sellerhistoryListRef = FirebaseDatabase.getInstance().getReference().child("Histori Order Seller");

        final HashMap<String, Object> sellerhistoryMap = new HashMap<>();
        sellerhistoryMap.put("pid", productID);
        sellerhistoryMap.put("pname", productName.getText().toString());
        sellerhistoryMap.put("price", productPrice.getText().toString());
        sellerhistoryMap.put("date", saveCurrentDate);
        sellerhistoryMap.put("time", saveCurrentTime);
        sellerhistoryMap.put("quantity", numberButton.getNumber());
        sellerhistoryMap.put("sellerName", sName);
        sellerhistoryMap.put("sID", sID);

        sellerhistoryListRef.child("Products").child(productID).updateChildren(sellerhistoryMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    private void getProductDetails(String productID) {
        final DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Products products = dataSnapshot.getValue(Products.class);
                    productName.setText(products.getPname());
                    productPrice.setText(products.getPrice());
                    productDescription.setText(products.getDescription());
                    Picasso.with(ProductDetailActivity.this).load(products.getImage()).into(productImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            productImage.setImageResource(R.mipmap.ic_launcher);
                        }
                    });
                    tipeProduk = products.getTipe();
                    Log.e("ProductDetail", new Gson().toJson(products));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CheckOrderState() {
        final DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    if (shippingState.equals("shipped")) {
                        state = "Order Shipped";
                    } else if (shippingState.equals("not shipped")) {
                        state = "Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void addData() {
        final ProgressDialog progressDialog = new ProgressDialog(ProductDetailActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        modelReviews = new ArrayList<>();
        modelReviews.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Review").orderByChild("idmenu").equalTo(productID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        ModelReview modelReview = issue.getValue(ModelReview.class);
                        modelReview.setId(issue.getKey());
                        modelReviews.add(modelReview);
                    }
                    Log.e("ModelReview", new Gson().toJson(modelReviews));
                    adapter = new ReviewAdapter(ProductDetailActivity.this, modelReviews);
                    rcyReview.setLayoutManager(new LinearLayoutManager(ProductDetailActivity.this));
                    rcyReview.setAdapter(adapter);
                    adapter.setOnRemoveListener(new RemoveListener() {
                        @Override
                        public void onClicked() {
                            ProductDetailActivity.this.recreate();
                        }
                    });
                }
                progressDialog.dismiss();
                getRecommend();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    void getRecommend() {
        progressDialog = new ProgressDialog(ProductDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        arrayList = new ArrayList<>();
        arrayList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Products").orderByChild("productState").equalTo("Approved");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        Products products = noteDataSnapshot.getValue(Products.class);
                        if (products.getTipe().equalsIgnoreCase(tipeProduk)) {
                            Log.e("Product", new Gson().toJson(products));
                            products.setId(noteDataSnapshot.getKey());
                            arrayList.add(products);
                        }
                    }
                    new GetRating().execute();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                finish();
                Toast.makeText(ProductDetailActivity.this, "Maaf saat ini server sedang sibuk", Toast.LENGTH_SHORT).show();
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
                    modelReviewsRecommend = new ArrayList<>();
                    modelReviewsRecommend.clear();
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        ModelReview modelReview = issue.getValue(ModelReview.class);
                        modelReview.setId(issue.getKey());
                        modelReviewsRecommend.add(modelReview);
                    }
                    if (modelReviewsRecommend.size() > 0) {
                        Integer rating = 0, qty = 0;
                        for (int listRating = 0; listRating < modelReviewsRecommend.size(); listRating++) {
                            rating += modelReviewsRecommend.get(listRating).getRating();
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
        menuAdapter = new MenuAdapter(ProductDetailActivity.this, arrayList, MenuAdapter.TipeRecommend);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ProductDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rcyRecommend.setHasFixedSize(true);
        rcyRecommend.setLayoutManager(layoutManager);
        rcyRecommend.setAdapter(menuAdapter);
        progressDialog.dismiss();
    }
}
