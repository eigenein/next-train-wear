package me.eigenein.nexttrainwear;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.drawer.WearableNavigationDrawer;

public class MainActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        final WearableNavigationDrawer navigationDrawer = (WearableNavigationDrawer)findViewById(R.id.navigation_drawer);
        navigationDrawer.setAdapter(new NavigationDrawerAdapter(this));
        navigationDrawer.setShouldOnlyOpenWhenAtTop(false);

        // TODO
        getFragmentManager().beginTransaction().replace(R.id.content_frame, StationsFragment.newInstance()).commit();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        // TODO
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        // TODO
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        // TODO
    }
}
