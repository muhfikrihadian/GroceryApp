package com.example.groceryapp.Buyers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryapp.LoginActivity;
import com.example.groceryapp.Prevalent.Prevalent;
import com.example.groceryapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {
    private String check = "";
    private TextView pageTittle, tittleQuestion;
    private EditText phoneNumber, question1, question2;
    private Button verifyButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check = getIntent().getStringExtra("check");


        pageTittle = findViewById(R.id.page_tittle);
        tittleQuestion = findViewById(R.id.tittle_question);
        phoneNumber = findViewById(R.id.find_phone_number);
        question1 = findViewById(R.id.question_1);
        question2 = findViewById(R.id.question_2);
        verifyButton = findViewById(R.id.verify_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();

        phoneNumber.setVisibility(View.GONE);

        if (check.equals("settings"))
        {
            pageTittle.setText("Set Pertanyaan");
            tittleQuestion.setText("Jawablah Pertanyaan Berikut Untuk Keamanan");
            verifyButton.setText("Set");

            displayPreviousAnswer();

            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAnswers();
                }
            });
        }
        else if (check.equals("login"))
        {
            phoneNumber.setVisibility(View.VISIBLE);

            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    verifyUser();
                }
            });
        }
    }


    private void setAnswers() {
        String answer1 = question1.getText().toString().toLowerCase();
        String answer2 = question2.getText().toString().toLowerCase();

        if (question1.equals("") && question2.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Tolong dijawab ya pertanyaannya.", Toast.LENGTH_SHORT).show();
        }
        else
            {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Users")
                    .child(Prevalent.currentOnlineUser.getPhone());

            HashMap<String, Object> userdataMap = new HashMap<>();
            userdataMap.put("Jawaban1", answer1);
            userdataMap.put("Jawaban2", answer2);

            ref.child("Pertanyaan Keamanan").updateChildren(userdataMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this, "Kamu sudah berhasil menjawab pertanyaan", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
        }
    }


    private void displayPreviousAnswer() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());

        ref.child("Security Question").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String ans1 = dataSnapshot.child("Jawaban1").getValue().toString();
                    String ans2 = dataSnapshot.child("Jawaban2").getValue().toString();

                    question1.setText(ans1);
                    question2.setText(ans2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void verifyUser() {
        final String phone = phoneNumber.getText().toString();
        final String answer1 = question1.getText().toString().toLowerCase();
        final String answer2 = question2.getText().toString().toLowerCase();

        if (!phone.equals("") && !answer1.equals("") && !answer2.equals("")) {
            final DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Users")
                    .child(phone);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        String mPhone = dataSnapshot.child("phone").getValue().toString();

                        if (dataSnapshot.hasChild("Pertanyaan Keamanan")) {
                            String ans1 = dataSnapshot.child("Pertanyaan Keamanan").child("Jawaban1").getValue().toString();
                            String ans2 = dataSnapshot.child("Pertanyaan Keamanan").child("Jawaban2").getValue().toString();

                            if (!ans1.equals(answer1))
                            {
                                Toast.makeText(ResetPasswordActivity.this, "Jawaban pertamamu salah", Toast.LENGTH_SHORT).show();
                            }
                            else if (!ans2.equals(answer2))
                            {
                                Toast.makeText(ResetPasswordActivity.this, "Jawaban keduamu salah", Toast.LENGTH_SHORT).show();
                            }
                            else
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                    builder.setTitle("Password Baru");

                                    final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                    newPassword.setHint("Ketik Password Baru..");
                                    builder.setView(newPassword);

                                    builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (!newPassword.getText().toString().equals("")) {
                                                ref.child("password")
                                                    .setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(ResetPasswordActivity.this, "Password Berhasil Diubah.", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent (ResetPasswordActivity.this, LoginActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                            }
                                        }
                                    });

                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            dialogInterface.cancel();
                                        }
                                    });

                                    builder.show();
                                }
                        }
                        else
                            {
                                Toast.makeText(ResetPasswordActivity.this, "Kamu tidak memiliki pertanyaan", Toast.LENGTH_SHORT).show();
                            }
                    }
                    else
                    {
                        Toast.makeText(ResetPasswordActivity.this, "Nomor ini tidak terdaftar.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        else
        {
            Toast.makeText(this, "Tolong Lengkapi pengisian form.", Toast.LENGTH_SHORT).show();
        }
    }
}