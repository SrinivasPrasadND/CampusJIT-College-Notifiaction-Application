package com.example.srinivasprasad.campus_jit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;

    private FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(Color.WHITE);


    }

    @Override
    protected void onStart() {
        super.onStart();


        if(currentUser==null){

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    MainActivity.this.finish();
                }
            }, (long) SPLASH_TIME_OUT);
        }
        else{
            // Toast.makeText(MainActivity.this,"User currently login",Toast.LENGTH_LONG).show();
            MainActivity.this.startActivity(new Intent(MainActivity.this, HomeScreenActivity.class));
            MainActivity.this.finish();
        }
    }
}
