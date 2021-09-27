package com.example.groceryapp.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.Helpers.DataHelper;
import com.example.groceryapp.Helpers.RemoveListener;
import com.example.groceryapp.Model.Sellers;
import com.example.groceryapp.Model.Users;
import com.example.groceryapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SellerAdapter extends RecyclerView.Adapter<SellerAdapter.MenuViewHolder> {
    StorageReference storageReference;
    ArrayList<Sellers> dataList;
    DatabaseReference database;
    RemoveListener removeListener;
    Context context;
    DataHelper dataHelper;
    Users users;


    public SellerAdapter(Context context, ArrayList<Sellers> dataList) {
        this.dataList = dataList;
        this.context = context;
        database = FirebaseDatabase.getInstance().getReference();
        dataHelper = new DataHelper(context);
        users = new Gson().fromJson(dataHelper.getUser(), Users.class);
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_seller, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MenuViewHolder holder, final int position) {
        final Sellers data = dataList.get(position);
        holder.tvName.setText(data.getName());
        holder.tvAddress.setText(data.getAddress());
        Picasso.with(context).load(data.getLogo()).into(holder.ivImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                holder.ivImage.setImageResource(R.mipmap.ic_launcher);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{"Delete", "Cancel"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Cart Options: ");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            final ProgressDialog progressDialog = new ProgressDialog(context);
                            progressDialog.setMessage("Loading...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            database.child("Sellers").child(dataList.get(position).getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Berhasil hapus seller", Toast.LENGTH_SHORT).show();
                                    removeListener.onClicked();
                                }
                            });
                        }
                    }
                });
                builder.show();
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
        TextView tvName, tvAddress, btnDelete;
        ImageView ivImage;

        public MenuViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            btnDelete = (TextView) itemView.findViewById(R.id.btnDelete);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        }
    }
}
