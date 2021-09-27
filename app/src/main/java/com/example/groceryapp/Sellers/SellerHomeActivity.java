package com.example.groceryapp.Sellers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.groceryapp.MainActivity;
import com.example.groceryapp.R;

import androidx.appcompat.app.AppCompatActivity;

public class SellerHomeActivity extends AppCompatActivity {

    private Button daftarProduk,tambahProduk,updateProduk,cekOrder,infoBtn,historiBtn;
    private ImageView logoutBtn;
    private long exitTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

        daftarProduk = (Button) findViewById(R.id.daftar_produk_seller_btn);
        tambahProduk = (Button) findViewById(R.id.tambah_btn);
        updateProduk = (Button) findViewById(R.id.maintain_btn);
        cekOrder = (Button) findViewById(R.id.check_orders_btn);
        logoutBtn = (ImageView) findViewById(R.id.seller_logout_btn);
        infoBtn = (Button) findViewById(R.id.info_btn);
        historiBtn = (Button) findViewById(R.id.histori_btn);

        daftarProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerHomeActivity.this, SellerDaftarProductActivity.class);
                startActivity(intent);
            }
        });
        tambahProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerHomeActivity.this, SellerAddNewProductActivity.class);
                startActivity(intent);
            }
        });
        updateProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerHomeActivity.this, SellerUpdateProductActivity.class);
                startActivity(intent);
            }
        });
        cekOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerHomeActivity.this, SellerNewOrderActivity.class);
                startActivity(intent);
            }
        });
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerHomeActivity.this, SellerMoreActivity.class);
                startActivity(intent);
            }
        });
        historiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerHomeActivity.this, SellerHistoriOrderActivity.class);
                startActivity(intent);
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SellerHomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
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
}
