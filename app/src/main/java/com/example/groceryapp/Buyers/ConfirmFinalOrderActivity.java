package com.example.groceryapp.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.Adapter.MenuAdapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    private EditText nameEditText, phoneEditText, addressEditText, cityEditText;
    private Button confirmOrderBtn;
    private DatabaseReference cartRef;
    private String totalAmount = "";
    private String sID, pname, price, quantity, sellerName, sellerPhone;
    private String productID = "", state = "Normal", tipeProduk = "";
    MenuAdapter menuAdapter;
    ArrayList<Products> arrayList;
    ArrayList<ModelReview> modelReviews;
    RecyclerView rcyRecommend;
    ProgressDialog progressDialog;
    Integer totalData = 0;
    Integer totalCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);
        totalAmount = getIntent().getStringExtra("Total Harga");
        productID = getIntent().getStringExtra("pid");
        tipeProduk = getIntent().getStringExtra("tipe");
        cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Seller View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID);
        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_btn);
        nameEditText = (EditText) findViewById(R.id.shippment_name);
        phoneEditText = (EditText) findViewById(R.id.shippment_phone_number);
        addressEditText = (EditText) findViewById(R.id.shippment_address);
        cityEditText = (EditText) findViewById(R.id.shippment_city);
        rcyRecommend = (RecyclerView) findViewById(R.id.rcyRecommend);
        arrayList = new ArrayList<>();
        modelReviews = new ArrayList<>();
        userInfoDisplay(nameEditText, phoneEditText, addressEditText, cityEditText);
        getRekomendasi();

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }
        });

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    sID = dataSnapshot.child("sID").getValue().toString();
                    sellerName = dataSnapshot.child("sellerName").getValue().toString();
                    sellerPhone = dataSnapshot.child("sellerPhone").getValue().toString();
                    pname = dataSnapshot.child("pname").getValue().toString();
                    price = dataSnapshot.child("price").getValue().toString();
                    quantity = dataSnapshot.child("quantity").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void getRekomendasi() {
        progressDialog = new ProgressDialog(ConfirmFinalOrderActivity.this);
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
                Toast.makeText(ConfirmFinalOrderActivity.this, "Maaf saat ini server sedang sibuk", Toast.LENGTH_SHORT).show();
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
        menuAdapter = new MenuAdapter(ConfirmFinalOrderActivity.this, arrayList, MenuAdapter.TipeRecommend);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ConfirmFinalOrderActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rcyRecommend.setHasFixedSize(true);
        rcyRecommend.setLayoutManager(layoutManager);
        rcyRecommend.setAdapter(menuAdapter);
        progressDialog.dismiss();
    }

    private void Check() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())) {
            Toast.makeText(this, "Tolong Masukkan Nama Lengkap.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phoneEditText.getText().toString())) {
            Toast.makeText(this, "Tolong Masukkan Nomor HP.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(addressEditText.getText().toString())) {
            Toast.makeText(this, "Tolong Masukkan Alamat Lengkap.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cityEditText.getText().toString())) {
            Toast.makeText(this, "Tolong Masukkan Nama Kota", Toast.LENGTH_SHORT).show();
        } else {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {
        final String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());
        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("pname", pname);
        ordersMap.put("price", price);
        ordersMap.put("quantity", quantity);
        ordersMap.put("shipping cost", 5000);
        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("sellerID", sID);
        ordersMap.put("sellerName", sellerName);
        ordersMap.put("sellerPhone", sellerPhone);
        ordersMap.put("name", nameEditText.getText().toString());
        ordersMap.put("phone", phoneEditText.getText().toString());
        ordersMap.put("address", addressEditText.getText().toString());
        ordersMap.put("city", cityEditText.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("state", "not shipped");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(Prevalent.currentOnlineUser.getPhone()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ConfirmFinalOrderActivity.this, "Pesananmu berhasil ditempatkan.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    private void userInfoDisplay(final EditText nameEditText, final EditText phoneEditText, final EditText addressEditText, final EditText cityEditText) {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String phone = dataSnapshot.child("phone").getValue().toString();
                    String address = dataSnapshot.child("address").getValue().toString();
                    String city = dataSnapshot.child("city").getValue().toString();
                    nameEditText.setText(name);
                    phoneEditText.setText(phone);
                    addressEditText.setText(address);
                    cityEditText.setText(city);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
