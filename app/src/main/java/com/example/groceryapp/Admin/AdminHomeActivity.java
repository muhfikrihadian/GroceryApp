package com.example.groceryapp.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.groceryapp.MainActivity;
import com.example.groceryapp.R;

import androidx.appcompat.app.AppCompatActivity;

public class AdminHomeActivity extends AppCompatActivity {
    private Button LogoutBtn;
    LinearLayout checkApproveMenuBtn, btnSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        LogoutBtn = (Button) findViewById(R.id.admin_logout_btn);
        checkApproveMenuBtn = (LinearLayout) findViewById(R.id.check_approve_menu_btn);
        btnSeller = (LinearLayout) findViewById(R.id.btnSeller);

        checkApproveMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminCheckNewProductsActivity.class);
                startActivity(intent);
            }
        });

        btnSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, ListSellerActivity.class);
                startActivity(intent);
            }
        });

        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

    }
}
