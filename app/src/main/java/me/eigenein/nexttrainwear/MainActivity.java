package me.eigenein.nexttrainwear;

import android.app.Fragment;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.drawer.WearableNavigationDrawer;

public class MainActivity extends WearableActivity implements NavigationDrawerAdapter.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        final WearableNavigationDrawer navigationDrawer = (WearableNavigationDrawer)findViewById(R.id.navigation_drawer);
        navigationDrawer.setAdapter(new NavigationDrawerAdapter(this, this));
        navigationDrawer.setShouldOnlyOpenWhenAtTop(false);

        getFragmentManager().beginTransaction().replace(R.id.content_frame, TrainsFragment.newInstance()).commit();
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
}
