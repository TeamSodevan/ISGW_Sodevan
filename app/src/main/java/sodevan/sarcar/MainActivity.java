package sodevan.sarcar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this)  ;
        String uid = preferences.getString("id", "newuser");
        if (uid.equals("newuser")) {

            Intent ch = new Intent(this, Map.class);
            startActivity(ch);
            finish();
        }





    }
}
