package com.example.groceryapp.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.groceryapp.Helpers.DataHelper;
import com.example.groceryapp.Model.ModelReview;
import com.example.groceryapp.Model.Users;
import com.example.groceryapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;


public class AddReviewActivity extends AppCompatActivity {
    RatingBar ratingBar;
    EditText etReview;
    ImageView btnPostReview;

    Integer rating = 0;
    String idMenu = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        etReview = (EditText) findViewById(R.id.etReview);
        btnPostReview = (ImageView) findViewById(R.id.btnPostReview);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            Toast.makeText(AddReviewActivity.this, "Tidak dapat mengakses fitur ini sementara", Toast.LENGTH_SHORT).show();
        } else {
            idMenu = intent.getStringExtra("IdMenu");
        }
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float nilai, boolean b) {
                rating = Math.round(nilai);
            }
        });
        btnPostReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadReview();
            }
        });
    }

    void uploadReview() {
        final ProgressDialog progressDialog = new ProgressDialog(AddReviewActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        DataHelper dataHelper = new DataHelper(AddReviewActivity.this);
        Users users = new Gson().fromJson(dataHelper.getUser(), Users.class);

        long unixTime = System.currentTimeMillis() / 1000L;
        String id = "Review-" + unixTime;
        String name = users.getName();
        String review = etReview.getText().toString();
        String uid = users.getId();

        if (rating == 0) {
            progressDialog.dismiss();
            Toast.makeText(AddReviewActivity.this, "Rating tidak boleh kosong !", Toast.LENGTH_SHORT).show();
        } else if (isEmpty(review)) {
            progressDialog.dismiss();
            Toast.makeText(AddReviewActivity.this, "Komentar tidak boleh kosong !", Toast.LENGTH_SHORT).show();
        } else {
            ModelReview modelReview = new ModelReview(id, idMenu, uid, name, review, rating);
            database.child("Review").push().setValue(modelReview).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    Toast.makeText(AddReviewActivity.this, "Review dipublikasikan", Toast.LENGTH_SHORT).show();
                    etReview.setText("");
                    etReview.clearFocus();
                    ratingBar.setRating(0);
                    finish();
                }
            });
        }
    }

    private boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }
}