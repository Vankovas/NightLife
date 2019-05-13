package nl.fontys.ivan.valentin.nightlife;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class StartupScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(StartupScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(StartupScreen.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        if (ContextCompat.checkSelfPermission(StartupScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(StartupScreen.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    2);
        }
        if (ContextCompat.checkSelfPermission(StartupScreen.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(StartupScreen.this,
                    new String[]{Manifest.permission.INTERNET},
                    3);
        }
        setContentView(R.layout.activity_startup_screen);

        Button loginBtn = findViewById(R.id.logInButton);
        Button registerBtn = findViewById(R.id.signUpButton);

        View.OnClickListener handler = new View.OnClickListener() {
            public void onClick(View v) {

                switch (v.getId()) {

                    case R.id.logInButton:
                        // doStuff
                        startActivity(new Intent(StartupScreen.this, Login.class));
                        break;
                    case R.id.signUpButton:
                        // doStuff
                        startActivity(new Intent(StartupScreen.this, sign_up.class));
                        break;
                }
            }
        };

        loginBtn.setOnClickListener(handler);
        registerBtn.setOnClickListener(handler);
    }

    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
