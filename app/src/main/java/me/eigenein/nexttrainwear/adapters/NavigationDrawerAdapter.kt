package me.eigenein.nexttrainwear.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.wearable.view.drawer.WearableNavigationDrawer.WearableNavigationDrawerAdapter
import me.eigenein.nexttrainwear.R

class NavigationDrawerAdapter(
    context: Context,
    private val listener: NavigationDrawerAdapter.OnItemSelectedListener
) : WearableNavigationDrawerAdapter() {

    private val texts = arrayOf(
        context.getString(R.string.navigation_drawer_trains),
        context.getString(R.string.navigation_drawer_stations),
        context.getString(R.string.navigation_drawer_settings)
    )
    private val icons = arrayOf(
        context.getDrawable(R.drawable.ic_train_black_24dp),
        context.getDrawable(R.drawable.ic_list_black_24dp),
        context.getDrawable(R.drawable.ic_settings_black_24dp)
    )

    init {
        icons.forEach { it.setTint(WHITE) }
    }

    override fun getItemText(index: Int): String {
        return texts[index]
    }

    override fun getItemDrawable(index: Int): Drawable {
        return icons[index]
    }

    override fun onItemSelected(index: Int) {
        listener.onItemSelected(index)
    }

    override fun getCount(): Int {
        return texts.size
    }

    interface OnItemSelectedListener {
        fun onItemSelected(index: Int)
    }

    companion object {

        @ColorInt
        private val WHITE = 0xFFFFFFFF.toInt()
    }
}
