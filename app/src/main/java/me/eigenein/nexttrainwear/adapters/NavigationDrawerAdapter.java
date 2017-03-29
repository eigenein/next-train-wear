package me.eigenein.nexttrainwear.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.wearable.view.drawer.WearableNavigationDrawer.WearableNavigationDrawerAdapter;

import me.eigenein.nexttrainwear.R;

public class NavigationDrawerAdapter extends WearableNavigationDrawerAdapter {

    @ColorInt
    private static final int WHITE = 0xFFFFFFFF;

    private final OnItemSelectedListener listener;
    private final String[] texts;
    private final Drawable[] icons;

    public NavigationDrawerAdapter(final Context context, final OnItemSelectedListener listener) {
        this.listener = listener;
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
        listener.onItemSelected(index);
    }

    @Override
    public int getCount() {
        return texts.length;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(final int index);
    }
}
