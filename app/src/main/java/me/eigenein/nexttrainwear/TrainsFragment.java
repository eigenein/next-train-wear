package me.eigenein.nexttrainwear;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TrainsFragment extends Fragment {

    public static TrainsFragment newInstance() {
        final TrainsFragment fragment = new TrainsFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TrainsFragment() {
        // Do nothing.
    }

    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        final ViewGroup container,
        final Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_trains, container, false);
    }

}
