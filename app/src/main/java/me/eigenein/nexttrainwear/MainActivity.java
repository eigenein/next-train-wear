package me.eigenein.nexttrainwear;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.drawer.WearableNavigationDrawer;

import java.util.Set;

import me.eigenein.nexttrainwear.adapters.NavigationDrawerAdapter;
import me.eigenein.nexttrainwear.fragments.SettingsFragment;
import me.eigenein.nexttrainwear.fragments.StationsFragment;
import me.eigenein.nexttrainwear.fragments.TrainsFragment;

public class MainActivity
    extends WearableActivity
    implements NavigationDrawerAdapter.OnItemSelectedListener {

    private static final int REQUEST_CODE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        final WearableNavigationDrawer navigationDrawer = (WearableNavigationDrawer)findViewById(R.id.navigation_drawer);
        navigationDrawer.setAdapter(new NavigationDrawerAdapter(this, this));
        navigationDrawer.setShouldOnlyOpenWhenAtTop(false);

        final Set<String> stations = Preferences.getStations(this);
        getFragmentManager()
            .beginTransaction()
            .replace(R.id.content_frame, stations.size() != 0 ? TrainsFragment.newInstance() : StationsFragment.newInstance())
            .commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
    }

    @Override
    public void onItemSelected(final int index) {
        final Fragment fragment;
        switch (index) {
            case 0:
                fragment = TrainsFragment.newInstance();
                break;
            case 1:
                fragment = StationsFragment.newInstance();
                break;
            case 2:
                fragment = SettingsFragment.newInstance();
                break;
            default:
                return;
        }
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void onRequestPermissionsResult(
        final int requestCode,
        @NonNull final String[] permissions,
        @NonNull final int[] grantResults
    ) {
        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            finish();
        }
    }
}
