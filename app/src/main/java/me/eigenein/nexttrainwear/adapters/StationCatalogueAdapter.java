package me.eigenein.nexttrainwear.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.Set;

import me.eigenein.nexttrainwear.Preferences;
import me.eigenein.nexttrainwear.R;
import me.eigenein.nexttrainwear.Station;
import me.eigenein.nexttrainwear.StationCatalogue;

public class StationCatalogueAdapter extends RecyclerView.Adapter {

    private final Set<String> checkedStations;

    public StationCatalogueAdapter(final Set<String> checkedStations) {
        this.checkedStations = checkedStations;
    }

    @Override
    public int getItemViewType(final int position) {
        return position != 0 ? R.layout.list_item_station : R.layout.list_item_stations_hint;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return viewType == R.layout.list_item_station ? new StationViewHolder(view) : new DummyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (position != 0) {
            ((StationViewHolder)holder).bind(StationCatalogue.STATIONS[position - 1]);
        }
    }

    @Override
    public int getItemCount() {
        return StationCatalogue.STATIONS.length + 1;
    }

    private static class DummyViewHolder extends RecyclerView.ViewHolder {

        public DummyViewHolder(final View itemView) {
            super(itemView);
        }
    }

    private class StationViewHolder
        extends RecyclerView.ViewHolder
        implements CompoundButton.OnCheckedChangeListener {

        private Station station;
        private CheckBox checkBox;

        public StationViewHolder(final View itemView) {
            super(itemView);
            checkBox = (CheckBox)itemView.findViewById(R.id.list_item_station_checkbox);
            checkBox.setOnCheckedChangeListener(this);
        }

        public void bind(final Station station) {
            this.station = station;
            checkBox.setText(station.longName);
            checkBox.setChecked(checkedStations.contains(station.code));
        }

        @Override
        public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
            if (isChecked) {
                checkedStations.add(station.code);
            } else {
                checkedStations.remove(station.code);
            }
            Preferences.setFavoriteStations(checkBox.getContext(), checkedStations);
        }
    }
}
