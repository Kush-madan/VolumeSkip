package com.volumeskip;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.btn_toggle);
        TextView status = findViewById(R.id.tv_status);

        btn.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(this, VolumeService.class);
            if (!running) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
                running = true;
                btn.setText("Stop");
                status.setText("Active — screen can be off\nHold Vol Up = Next song\nHold Vol Down = Previous song");
            } else {
                serviceIntent.setAction("STOP");
                startService(serviceIntent);
                running = false;
                btn.setText("Start");
                status.setText("Tap Start, then turn off screen");
            }
        });
    }
}
