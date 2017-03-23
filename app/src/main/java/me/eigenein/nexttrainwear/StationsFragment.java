package me.eigenein.nexttrainwear;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StationsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private String param1;

    public StationsFragment() {
        // Do nothing.
    }

    public static StationsFragment newInstance() {
        final StationsFragment fragment = new StationsFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // param1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        final ViewGroup container,
        final Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_stations, container, false);
    }
}
