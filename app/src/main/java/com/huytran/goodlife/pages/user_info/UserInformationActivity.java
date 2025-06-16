package com.huytran.goodlife.pages.user_info;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.huytran.goodlife.R;
import com.huytran.goodlife.pages.home.HomeActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserInformationActivity extends AppCompatActivity {
    private String name, date, gender, email;
    private ImageButton backButton;
    private TextView userName, userGender, userDateOfBirth, userEmail, userPhone, userLocation;
    private CardView phone, location;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        // Make status bar fully transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

        // Set the layout to extend into the status bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        firebaseFirestore = FirebaseFirestore.getInstance();

        backButton = findViewById(R.id.back_button);
        userName = findViewById(R.id.user_name);
        userGender = findViewById(R.id.user_gender);
        userDateOfBirth = findViewById(R.id.user_date_of_birth);
        userEmail = findViewById(R.id.user_email);
        userPhone = findViewById(R.id.user_phone);
        userLocation = findViewById(R.id.user_location);
        phone = findViewById(R.id.phone);
        location = findViewById(R.id.location);

        SharedPreferences sp = getSharedPreferences("Data", Context.MODE_PRIVATE);

        name = sp.getString("Name", null);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        Query userDatabase = reference.orderByChild("name").equalTo(name);

        userName.setText(name);

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gender = snapshot.child(name).child("gender").getValue(String.class);
                date = snapshot.child(name).child("date_of_birth").getValue(String.class);
                email = snapshot.child(name).child("email").getValue(String.class);

                if (gender.equals("Nu")) gender = "Nữ";
                userGender.setText(gender);
                userDateOfBirth.setText(makeDateString(date));
                userEmail.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        firebaseFirestore.collection("GoodLife").document(name).collection("Thông tin").document("Phone").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String phoneNumber = documentSnapshot.getString("Phone");

                            if (phoneNumber != null) {
                                userPhone.setText(phoneNumber);
                            } else {
                                userPhone.setText("Chưa cập nhật số điện thoại");
                            }

                        } else {
                            Log.d("Firestore", "No such document");
                        }
                    }
                });


        firebaseFirestore.collection("GoodLife").document(name).collection("Thông tin").document("Location").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String locationValue = documentSnapshot.getString("Location");

                            if (locationValue != null) {
                                userLocation.setText(locationValue);
                            } else {
                                userLocation.setText("Chưa cập nhật địa chỉ");
                            }
                        } else {
                            Log.d("Firestore", "No such document");
                        }
                    }
                });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhone(UserInformationActivity.this);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLocation(UserInformationActivity.this);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInformationActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addPhone(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.input_layout, null);

        EditText input_val = view.findViewById(R.id.input_value);


        builder.setView(view)
                .setTitle("Nhập thông tin")
                .setPositiveButton("Save", (dialog, which) -> {
                    String val = input_val.getText().toString().trim();

                    userPhone.setText(val);

                    Map<String, Object> item = new HashMap<>();
                    item.put("Phone", val);

                    firebaseFirestore.collection("GoodLife").document(name).collection("Thông tin").document("Phone").set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("Firestore", "Adding item to database successfully");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firestore", "Error adding item to database", e);
                        }
                    });

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void addLocation(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.input_location_layout, null);

        EditText input_val = view.findViewById(R.id.input_value);


        builder.setView(view)
                .setTitle("Nhập thông tin")
                .setPositiveButton("Save", (dialog, which) -> {
                    String val = input_val.getText().toString().trim();

                    userLocation.setText(val);

                    Map<String, Object> item = new HashMap<>();
                    item.put("Location", val);

                    firebaseFirestore.collection("GoodLife").document(name).collection("Thông tin").document("Location").set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("Firestore", "Adding item to database successfully");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firestore", "Error adding item to database", e);
                        }
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }


    String getNumberMonthFormat(String month) {
        if (Objects.equals(month, "JAN")) return "1";
        if (Objects.equals(month, "FEB")) return "2";
        if (Objects.equals(month, "MAR")) return "3";
        if (Objects.equals(month, "APR")) return "4";
        if (Objects.equals(month, "MAY")) return "5";
        if (Objects.equals(month, "JUN")) return "6";
        if (Objects.equals(month, "JUL")) return "7";
        if (Objects.equals(month, "AUG")) return "8";
        if (Objects.equals(month, "SEP")) return "9";
        if (Objects.equals(month, "OCT")) return "10";
        if (Objects.equals(month, "NOV")) return "11";
        if (Objects.equals(month, "DEC")) return "11";

        return "1";
    }

    private String makeDateString(String date) {
        String tempDateOfBirth = date;
        String[] birth = tempDateOfBirth.split("/");

        int month = Integer.parseInt(getNumberMonthFormat(birth[0]));
        int day = Integer.parseInt(birth[1]);
        int year = Integer.parseInt(birth[2]);
        return day+ "/" + month + "/" + year;
    }
}