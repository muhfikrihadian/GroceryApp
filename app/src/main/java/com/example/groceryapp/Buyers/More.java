package com.example.groceryapp.Buyers;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.groceryapp.Helpers.DataHelper;
import com.example.groceryapp.MainActivity;
import com.example.groceryapp.Model.Users;
import com.example.groceryapp.Prevalent.Prevalent;
import com.example.groceryapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class More extends AppCompatActivity {
    private Button Logout, Update;
    private TextView namaUser, emailUser, noHpUser, alamatUser, kotaUser;
    private CircleImageView profileImageView;
    DatabaseReference ref;
    FirebaseDatabase mDatabase;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        namaUser = (TextView) findViewById(R.id.profil_name);
        emailUser = (TextView) findViewById(R.id.profil_email);
        noHpUser = (TextView) findViewById(R.id.profil_nomorhp);
        alamatUser = (TextView) findViewById(R.id.profil_alamat);
        kotaUser = (TextView) findViewById(R.id.profil_kota);
        Logout = (Button) findViewById(R.id.logout);
        Update = (Button) findViewById(R.id.update_btn);
        profileImageView = (CircleImageView) findViewById(R.id.profil_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        userInfoDisplay(profileImageView);
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(More.this, UpdateInfoProfileActivity.class);
                startActivity(intent);
            }
        });
        mDatabase = FirebaseDatabase.getInstance();
        ref = mDatabase.getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String namauser = dataSnapshot.child("name").getValue(String.class);
                String emailuser = dataSnapshot.child("email").getValue(String.class);
                String nohpuser = dataSnapshot.child("phone").getValue(String.class);
                String alamatuser = dataSnapshot.child("address").getValue(String.class);
                String kotauser = dataSnapshot.child("city").getValue(String.class);
                namaUser.setText(namauser);
                emailUser.setText(emailuser);
                noHpUser.setText(nohpuser);
                alamatUser.setText(alamatuser);
                kotaUser.setText(kotauser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().destroy();
                DataHelper dataHelper = new DataHelper(More.this);
                dataHelper.deletePref();
                Intent intent = new Intent(More.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
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

    private void userInfoDisplay(final CircleImageView profileImageView) {
        DataHelper dataHelper = new DataHelper(More.this);
        Users users = new Gson().fromJson(dataHelper.getUser(), Users.class);
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone() != null ? Prevalent.currentOnlineUser.getPhone() : users.getPhone());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("image").exists()) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.with(More.this).load(image).into(profileImageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                profileImageView.setImageResource(R.mipmap.ic_launcher);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
