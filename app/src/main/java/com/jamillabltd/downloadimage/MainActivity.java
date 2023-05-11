package com.jamillabltd.downloadimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.Manifest;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        saveImage();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                    }
                } else {
                    saveImage();
                }
            }
        });


    }

    private void saveImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img);
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("MainActivity", "Error creating media file, check storage permissions: ");
            Toast.makeText(MainActivity.this, "Error creating media file, check storage permissions", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            Toast.makeText(MainActivity.this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.d("MainActivity", "Error saving image: " + e.getMessage());
            Toast.makeText(MainActivity.this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }

    private File getOutputMediaFile() {
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Jamil Lab LTD");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MainActivity", "failed to create directory");
                return null;
            }
        }
        String timeStamp = String.valueOf(System.currentTimeMillis());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            } else {
                Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}