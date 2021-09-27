package com.example.groceryapp.Sellers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.groceryapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SellerRegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText nameInput,phoneInput,emailInput,passwordInput,addressInput;
    private DatabaseReference rootRef, sellersRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_register);

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        sellersRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

        CreateAccountButton = (Button) findViewById(R.id.seller_register_btn);
        nameInput = (EditText) findViewById(R.id.seller_register_nama);
        phoneInput = (EditText) findViewById(R.id.seller_register_nohp);
        emailInput = (EditText) findViewById(R.id.seller_register_email);
        passwordInput = (EditText) findViewById(R.id.seller_register_password);
        addressInput = (EditText) findViewById(R.id.seller_register_alamat);
        loadingBar = new ProgressDialog(this);


        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                registerSeller();
            }
        });
    }

    private void registerSeller()
    {
        final String name = nameInput.getText().toString();
        final String phone = phoneInput.getText().toString();
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();
        final String address = addressInput.getText().toString();

        if (!name.equals("") && !phone.equals("") && !email.equals("") && !password.equals("") && !address.equals(""))
        {
            loadingBar.setTitle("Buat Akun Penjual");
            loadingBar.setMessage("Tolong tunggu, sedang pengecekan.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {

                            final DatabaseReference rootRef;
                            rootRef = FirebaseDatabase.getInstance().getReference();

                            String sid = mAuth.getCurrentUser().getUid();

                            HashMap<String, Object> sellerMap = new HashMap<>();
                            sellerMap.put("sid", sid);
                            sellerMap.put("phone", phone);
                            sellerMap.put("email", email);
                            sellerMap.put("address", address);
                            sellerMap.put("name", name);
                            sellerMap.put("password", password);

                            rootRef.child("Sellers").child(sid).updateChildren(sellerMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful()) {
                                                loadingBar.dismiss();
                                                Toast.makeText(SellerRegisterActivity.this, "Kamu berhasil mendaftar!", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(SellerRegisterActivity.this, SellerUploadPhotoToko.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                        }
                    });
        }
        else
        {
            Toast.makeText(SellerRegisterActivity.this, "Tolong Lengkapi Form Pendaftaran.", Toast.LENGTH_SHORT).show();
        }
    }
}
