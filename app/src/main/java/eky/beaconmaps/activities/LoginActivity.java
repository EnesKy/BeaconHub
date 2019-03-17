package eky.beaconmaps.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import eky.beaconmaps.R;
import me.anwarshahriar.calligrapher.Calligrapher;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "font/MuseoSans.otf", true);

        MaterialButton button = findViewById(R.id.button_login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
