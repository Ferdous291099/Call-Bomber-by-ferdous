package com.example.callbomber;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private EditText editTextPhoneNumber;
    private Button buttonStartBombing;
    private static final int CALL_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        buttonStartBombing = findViewById(R.id.buttonStartBombing);

        buttonStartBombing.setOnClickListener(v -> checkPermissionAndStart());
    }

    private void checkPermissionAndStart() {
        String phone = editTextPhoneNumber.getText().toString().trim();

        if (phone.isEmpty()) {
            Toast.makeText(this, "ফোন নাম্বার লিখুন", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) 
                != PackageManager.PERMISSION_GRANTED) {
            
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.CALL_PHONE}, 
                    CALL_PERMISSION_CODE);
        } else {
            startCalling(phone);
        }
    }

    private void startCalling(String phoneNumber) {
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                runOnUiThread(() -> makeCall(phoneNumber));
                try {
                    Thread.sleep(2500); // 2.5 seconds delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void makeCall(String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } catch (Exception e) {
            runOnUiThread(() -> 
                Toast.makeText(this, "কল করতে সমস্যা হচ্ছে", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CALL_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String phone = editTextPhoneNumber.getText().toString().trim();
                if (!phone.isEmpty()) {
                    startCalling(phone);
                }
            } else {
                Toast.makeText(this, "কল করার অনুমতি দিতে হবে", Toast.LENGTH_LONG).show();
            }
        }
    }
}
