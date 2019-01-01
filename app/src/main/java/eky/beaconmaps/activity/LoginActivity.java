package eky.beaconmaps.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import eky.beaconmaps.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button buLogin;
    CheckBox cbRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(R.string.app_name);
        setSupportActionBar(toolbar);

        buLogin = findViewById(R.id.buLogin);
        buLogin.setOnClickListener(this);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        cbRememberMe.setOnClickListener(this);

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
            case R.id.cbRememberMe:
                break;
        }
    }

}
