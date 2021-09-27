package com.example.groceryapp.Sellers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.groceryapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class SellerUploadPhotoToko extends AppCompatActivity {

    private String saveCurrentDate, saveCurrentTime;
    private Button AddNewLogo;
    private CircleImageView InputShopImage;
    private static final int GalleryPict = 1;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference ShopImageRef;
    private DatabaseReference ShopLogoRef, sellersRef;
    private ProgressDialog loadingBar;

    private String sName, sAddress, sPhone, sEmail, sID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_uploadphoto_toko);

        ShopImageRef = FirebaseStorage.getInstance().getReference().child("Logo Shop Images");
        ShopLogoRef = FirebaseDatabase.getInstance().getReference().child("Logo Shop Images");
        sellersRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

        AddNewLogo = (Button) findViewById(R.id.add_new_logo);
        InputShopImage = (CircleImageView) findViewById(R.id.logo_image);
        loadingBar = new ProgressDialog(this);

        InputShopImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        AddNewLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });

        sellersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            sName = dataSnapshot.child("name").getValue().toString();
                            sAddress = dataSnapshot.child("address").getValue().toString();
                            sPhone = dataSnapshot.child("phone").getValue().toString();
                            sID = dataSnapshot.child("sid").getValue().toString();
                            sEmail = dataSnapshot.child("email").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPict);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPict && resultCode==RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            InputShopImage.setImageURI(ImageUri);
        }
    }

    private void ValidateProductData()
    {
        if (ImageUri == null)
        {
            Toast.makeText(this, "Upload gambarnya mana? :(", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreRestoInformation();
        }
    }

    private void StoreRestoInformation()
    {
        loadingBar.setTitle("Menambahkan Logo Toko");
        loadingBar.setMessage("Tunggu sebentar, Selama Penambahan Logo Toko");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + "," + saveCurrentTime;

        final StorageReference filePath = ShopImageRef.child(ImageUri.getLastPathSegment() + productRandomKey);

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(SellerUploadPhotoToko.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SellerUploadPhotoToko.this, "Logo Toko uploaded Successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(SellerUploadPhotoToko.this, "Logo Toko Berhasil Disimpan...", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();
                        }
                    }
                });

            }
        });
    }

    private void SaveProductInfoToDatabase()
    {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("logotoko", downloadImageUrl);

        productMap.put("sellerName", sName);
        productMap.put("sellerAddress", sAddress);
        productMap.put("sellerPhone", sPhone);
        productMap.put("sellerEmail", sEmail);
        productMap.put("sID", sID);

        ShopLogoRef.child(sID).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(SellerUploadPhotoToko.this, SellerLoginActivity.class);
                            intent.putExtra("Admin", "Admin");
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(SellerUploadPhotoToko.this, "Logo Berhasil diUpload", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(SellerUploadPhotoToko.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
