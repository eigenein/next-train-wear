package me.eigenein.nexttrainwear;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.drawer.WearableDrawerLayout;
import android.support.wearable.view.drawer.WearableNavigationDrawer;

public class MainActivity extends WearableActivity {

    private WearableDrawerLayout drawerLayout;
    private WearableNavigationDrawer navigationDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        // https://developer.android.com/training/wearables/ui/ui-nav-actions.html
        drawerLayout = (WearableDrawerLayout)findViewById(R.id.drawer_layout);
        navigationDrawer = (WearableNavigationDrawer)findViewById(R.id.navigation_drawer);
        navigationDrawer.setShouldOnlyOpenWhenAtTop(false);
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
