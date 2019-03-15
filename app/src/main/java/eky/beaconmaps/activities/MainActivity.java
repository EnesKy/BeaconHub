package eky.beaconmaps.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import eky.beaconmaps.R;
import me.anwarshahriar.calligrapher.Calligrapher;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.title_main_activity);

        //Call this on every view ... i guess
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "font/MuseoSans.otf", true);

    }
}
