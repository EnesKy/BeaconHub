package eky.beaconmaps.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import eky.beaconmaps.R;
import me.anwarshahriar.calligrapher.Calligrapher;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        setTitle(R.string.title_signin_activity);

        //Call this on every view ... i guess
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "font/MuseoSans.otf", true);


    }
}
