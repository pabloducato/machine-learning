package com.machinelearningapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageId);

        textView = findViewById(R.id.textId);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }
    }

    public void doProcess(View view) {
        textView.setText("");
        imageView.setImageBitmap(null);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 101);
    }

    public void clearResults(View view) {
        textView.setText("");
        imageView.setImageBitmap(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case 0:
                Toast.makeText(MainActivity.this, "Zdjęcie nie zostało wykonane", Toast.LENGTH_SHORT).show();
                break;
            case -1:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    imageView.setImageBitmap(bitmap);
                    FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
                    FirebaseVision firebaseVision = FirebaseVision.getInstance();
                    FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();
                    Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);

                    task.addOnSuccessListener(firebaseVisionText -> {
                        String s = firebaseVisionText.getText();
                        textView.setText(s);
                    });

                    task.addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show());
                }
                return;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }
}