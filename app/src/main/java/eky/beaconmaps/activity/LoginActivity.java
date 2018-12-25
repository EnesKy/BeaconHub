package eky.beaconmaps.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import eky.beaconmaps.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button buLogin;
    CheckBox cbRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        buLogin = findViewById(R.id.buLogin);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        buLogin.setOnClickListener(this);

        //TODO: FirebaseAuthentication ekle..

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.buLogin:
                Intent i = new Intent(LoginActivity.this, MapsActivity.class);
                startActivity(i);
                break;
        }
    }

}
