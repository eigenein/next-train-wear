package me.eigenein.nexttrainwear;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.drawer.WearableNavigationDrawer;

import me.eigenein.nexttrainwear.adapters.NavigationDrawerAdapter;
import me.eigenein.nexttrainwear.fragments.SettingsFragment;
import me.eigenein.nexttrainwear.fragments.StationsFragment;
import me.eigenein.nexttrainwear.fragments.TrainsFragment;

public class MainActivity
    extends WearableActivity
    implements NavigationDrawerAdapter.OnItemSelectedListener {

    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private static final Fragment FRAGMENTS[] = {
        TrainsFragment.newInstance(),
        StationsFragment.newInstance(),
        SettingsFragment.newInstance(),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        final WearableNavigationDrawer navigationDrawer = (WearableNavigationDrawer)findViewById(R.id.navigation_drawer);
        navigationDrawer.setAdapter(new NavigationDrawerAdapter(this, this));
        navigationDrawer.setShouldOnlyOpenWhenAtTop(false);

        getFragmentManager()
            .beginTransaction()
            .replace(R.id.content_frame, TrainsFragment.newInstance())
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
    public void onEnterAmbient(final Bundle ambientDetails) {
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
        getFragmentManager().beginTransaction().replace(R.id.content_frame, FRAGMENTS[index]).commit();
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
