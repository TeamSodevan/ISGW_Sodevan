package sodevan.sarcar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
        EditText et1;
        Button bt1;
    SharedPreferences sharedpreferences ;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et1= (EditText) findViewById(R.id.phone);
        bt1= (Button) findViewById(R.id.button2);
        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number=et1.getText().toString();
                editor.putString("phone",number);
                editor.commit();
                Intent i=new Intent(LoginActivity.this,Map.class);
                startActivity(i);
            }
        });

    }
}
