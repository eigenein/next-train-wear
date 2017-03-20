package me.eigenein.nexttrainwear;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout containerView;
    private TextView textView;
    private TextView clockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        containerView = (BoxInsetLayout)findViewById(R.id.container);
        textView = (TextView)findViewById(R.id.text);
        clockView = (TextView)findViewById(R.id.clock);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            containerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            textView.setTextColor(getResources().getColor(android.R.color.white));
            clockView.setVisibility(View.VISIBLE);

            clockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            containerView.setBackground(null);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            clockView.setVisibility(View.GONE);
        }
    }
}
