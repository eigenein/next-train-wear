package me.eigenein.nexttrainwear

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.wearable.activity.WearableActivity
import android.support.wearable.view.drawer.WearableNavigationDrawer
import android.util.Log
import me.eigenein.nexttrainwear.adapters.NavigationDrawerAdapter
import me.eigenein.nexttrainwear.fragments.StationsFragment
import me.eigenein.nexttrainwear.fragments.SettingsFragment
import me.eigenein.nexttrainwear.fragments.TrainsFragment
import me.eigenein.nexttrainwear.interfaces.AmbientListenable
import me.eigenein.nexttrainwear.interfaces.AmbientListener

class MainActivity :
    WearableActivity(),
    NavigationDrawerAdapter.OnItemSelectedListener,
    AmbientListenable {

    override var ambientListener: AmbientListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setAmbientEnabled()

        val navigationDrawer = findViewById(R.id.navigation_drawer) as WearableNavigationDrawer
        navigationDrawer.setAdapter(NavigationDrawerAdapter(this, this))
        navigationDrawer.setShouldOnlyOpenWhenAtTop(false)

        fragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, TrainsFragment())
            .commit()
    }

    public override fun onStart() {
        super.onStart()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)
        ambientListener?.onEnterAmbient()
    }

    override fun onUpdateAmbient() {
        super.onUpdateAmbient()
        ambientListener?.onUpdateAmbient()
    }

    override fun onExitAmbient() {
        super.onExitAmbient()
        ambientListener?.onExitAmbient()
    }

    override fun onItemSelected(index: Int) {
        fragmentManager.beginTransaction().replace(R.id.content_frame, FRAGMENTS[index]()).commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Permission is not granted")
        }
    }

    companion object {

        private val TAG = MainActivity::class.java.simpleName
        private val REQUEST_CODE_PERMISSIONS = 1
        private val FRAGMENTS = arrayOf(
            { TrainsFragment() },
            { StationsFragment() },
            { SettingsFragment() }
        )
    }
}
