package be.ehb.bxlight;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import be.ehb.bxlight.fragments.AboutFragment;
import be.ehb.bxlight.fragments.ComicListFragment;
import be.ehb.bxlight.fragments.MapFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    MapFragment mf = MapFragment.newInstance();
                    switchFragment(mf);
                    return true;
                case R.id.navigation_overview:
                    ComicListFragment clf = ComicListFragment.newInstance();
                    switchFragment(clf);
                    return true;
                case R.id.navigation_about:
                    AboutFragment af = AboutFragment.newInstance();
                    switchFragment(af);
                    return true;
            }
            return false;
        }
    };

    private void switchFragment(Fragment mf) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, mf)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        MapFragment mf = MapFragment.newInstance();
        switchFragment(mf);
    }

    public void onBackPressed() {
        finish();
    }
}
