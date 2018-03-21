package sravya.example.com.techydukhaan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class InitialSplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent logout = new Intent(InitialSplashScreenActivity.this, LoginActivity.class);
                logout.putExtra("sender", "splash");
                startActivity(logout);
                finish();
            }
        }, SPLASH_TIME);
    }
}