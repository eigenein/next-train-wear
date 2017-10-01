package me.eigenein.nexttrainwear.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.wear.widget.drawer.WearableNavigationDrawerView
import me.eigenein.nexttrainwear.R

class NavigationDrawerAdapter(context: Context) : WearableNavigationDrawerView.WearableNavigationDrawerAdapter() {

    private val items = arrayOf(
        Pair(context.getString(R.string.navigation_drawer_trains), context.getDrawable(R.drawable.ic_train_black_24dp)),
        Pair(context.getString(R.string.navigation_drawer_stations), context.getDrawable(R.drawable.ic_list_black_24dp))
        // Pair(context.getString(R.string.navigation_drawer_settings), context.getDrawable(R.drawable.ic_settings_black_24dp))
    )

    init {
        items.forEach { it.second.setTint(WHITE) }
    }

    override fun getItemText(index: Int): String = items[index].first
    override fun getItemDrawable(index: Int): Drawable = items[index].second
    override fun getCount(): Int = items.size

    companion object {

        @ColorInt
        private val WHITE = 0xFFFFFFFF.toInt()
    }
}
