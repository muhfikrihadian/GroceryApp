package com.example.groceryapp.Sellers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryapp.Admin.AdminCheckNewProductsActivity;
import com.example.groceryapp.Buyers.UpdateInfoProfileActivity;
import com.example.groceryapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class SellerUpdateInfoToko extends AppCompatActivity
{
    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText, emailEditText;
    private TextView profileChangeTextBtn, closeTextBtn, saveTextButton;

    private String productID = "", state = "Normal";
    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private DatabaseReference ProductsRef;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_update_info_toko);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Logo Shop Images");
        productID = getIntent().getStringExtra("pid");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image);
        fullNameEditText = (EditText) findViewById(R.id.settings_name);
        userPhoneEditText = (EditText) findViewById(R.id.settings_phone);
        emailEditText = (EditText) findViewById(R.id.settings_email);
        addressEditText = (EditText) findViewById(R.id.settings_alamat);
        profileChangeTextBtn = (TextView) findViewById(R.id.profile_image_change_btn);
        saveTextButton = (TextView) findViewById(R.id.update_account_settings_btn);
        closeTextBtn = (TextView) findViewById(R.id.close_settings_btn);
        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText, emailEditText);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();

                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SellerUpdateInfoToko.this);
            }
        });
    }

    private void updateOnlyUserInfo()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Sellers");

        HashMap<String, Object> sellerMap = new HashMap<>();
        sellerMap.put("name", fullNameEditText.getText().toString());
        sellerMap.put("address", addressEditText.getText().toString());
        sellerMap.put("phone", userPhoneEditText.getText().toString());
        sellerMap.put("email", emailEditText.getText().toString());
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(sellerMap);

        startActivity(new Intent(SellerUpdateInfoToko.this, SellerMoreActivity.class));
        Toast.makeText(SellerUpdateInfoToko.this, "Update Info Berhasil!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Error, Coba Lagi.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SellerUpdateInfoToko.this, SellerUpdateInfoToko.class));
            finish();
        }
    }

    private void userInfoSaved()
    {
        if (TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Toast.makeText(this, "Nama masih kosong", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Alamat masih kosong", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Nomor HP masih kosong", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emailEditText.getText().toString()))
        {
            Toast.makeText(this, "Email masih kosong", Toast.LENGTH_SHORT).show();
        }
        else if (checker.equals("clicked"))
        {
            uploadImage();
        }
    }

    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profil");
        progressDialog.setMessage("Tolong tunggu, Sedang update informasi akun.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePictureRef
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw  task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            if (task.isSuccessful())
                            {

                                Uri downloadUrl = task.getResult();

                                myUrl = downloadUrl.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Sellers");

                                HashMap<String, Object> sellerMap = new HashMap<>();
                                sellerMap.put("name", fullNameEditText.getText().toString());
                                sellerMap.put("address", addressEditText.getText().toString());
                                sellerMap.put("phone", userPhoneEditText.getText().toString());
                                sellerMap.put("email", emailEditText.getText().toString());
                                sellerMap.put("logo", myUrl);
                                ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(sellerMap);

                                progressDialog.dismiss();

                                startActivity(new Intent(SellerUpdateInfoToko.this, SellerMoreActivity.class));
                                Toast.makeText(SellerUpdateInfoToko.this, "Update Info Berhasil!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(SellerUpdateInfoToko.this, "Error.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "Gambar tidak terpilih.", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText, final EditText emailEditText)
    {
        DatabaseReference SellersRef = FirebaseDatabase.getInstance().getReference().child("Sellers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        SellersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("logo").exists())
                    {
                        String image = dataSnapshot.child("logo").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();
                        Picasso.with(SellerUpdateInfoToko.this).load(image).into(profileImageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                profileImageView.setImageResource(R.mipmap.ic_launcher);
                            }
                        });
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                        emailEditText.setText(email);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}
