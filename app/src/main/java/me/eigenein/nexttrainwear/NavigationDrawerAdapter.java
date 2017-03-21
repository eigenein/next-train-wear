package me.eigenein.nexttrainwear;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.wearable.view.drawer.WearableNavigationDrawer.WearableNavigationDrawerAdapter;

public class NavigationDrawerAdapter extends WearableNavigationDrawerAdapter {

    @ColorInt
    private static final int WHITE = 0xFFFFFFFF;

    private final String[] texts;
    private final Drawable[] icons;

    public NavigationDrawerAdapter(final Context context) {
        this.texts = context.getResources().getStringArray(R.array.navigation_drawer_texts);

        // Load icons.
        final TypedArray icons = context.getResources().obtainTypedArray(R.array.navigation_drawer_icons);
        this.icons = new Drawable[icons.length()];
        for (int i = 0; i < icons.length(); i++) {
            this.icons[i] = icons.getDrawable(i);
            this.icons[i].setTint(WHITE);
        }
        icons.recycle();
    }

    @Override
    public String getItemText(final int index) {
        return texts[index];
    }

    @Override
    public Drawable getItemDrawable(final int index) {
        return icons[index];
    }

    @Override
    public void onItemSelected(final int index) {
        // TODO
    }

    @Override
    public int getCount() {
        return texts.length;
    }
}
