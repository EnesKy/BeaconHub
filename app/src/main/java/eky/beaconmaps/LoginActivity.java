package eky.beaconmaps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.buLogin)
    Button buLogin;
    @BindView(R.id.cbRememberMe)
    CheckBox cbRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this); // bind butterknife after setContectView(..)
        //Now you can access all view after bind your activity with butterknife
    }

    // Single view button click event
    @OnClick(R.id.buLogin)
    public void doThis(View view) {
        Intent i = new Intent(LoginActivity.this, MapsActivity.class);
        startActivity(i);
    }

}
