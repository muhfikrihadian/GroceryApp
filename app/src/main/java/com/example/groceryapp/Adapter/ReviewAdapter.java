package com.example.groceryapp.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.Helpers.DataHelper;
import com.example.groceryapp.Helpers.RemoveListener;
import com.example.groceryapp.Model.ModelReview;
import com.example.groceryapp.Model.Users;
import com.example.groceryapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MenuViewHolder> {
    ArrayList<ModelReview> dataList;
    DatabaseReference database;
    RemoveListener removeListener;
    Context context;
    String uid = "";
    DataHelper dataHelper;
    Users users;


    public ReviewAdapter(Context context, ArrayList<ModelReview> dataList) {
        this.dataList = dataList;
        this.context = context;
        database = FirebaseDatabase.getInstance().getReference();
        dataHelper = new DataHelper(context);
        users = new Gson().fromJson(dataHelper.getUser(), Users.class);
        uid = users.getId();
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_review, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MenuViewHolder holder, final int position) {
        final ModelReview data = dataList.get(position);
        holder.tvName.setText(data.getNama());
        holder.tvReview.setText(data.getReview());
        holder.ratingBar.setRating(data.getRating());
        if (data.getIduser().equalsIgnoreCase(uid)) {
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                database.child("Review").child(dataList.get(position).getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Berhasil hapus review", Toast.LENGTH_SHORT).show();
                        removeListener.onClicked();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public void setOnRemoveListener(RemoveListener removeListener) {
        this.removeListener = removeListener;
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvReview, btnDelete;
        RatingBar ratingBar;

        public MenuViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvReview = (TextView) itemView.findViewById(R.id.tvReview);
            btnDelete = (TextView) itemView.findViewById(R.id.btnDelete);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }
    }
}
