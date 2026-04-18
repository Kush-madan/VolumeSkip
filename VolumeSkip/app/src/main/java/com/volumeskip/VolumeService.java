package com.volumeskip;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.view.KeyEvent;
import androidx.core.app.NotificationCompat;

public class VolumeService extends Service {

    private static final String CHANNEL_ID = "VolumeSkipChannel";
    private static final int NOTIF_ID = 1;
    private static final long HOLD_THRESHOLD_MS = 600;

    private AudioManager audioManager;
    private long volUpPressTime = 0;
    private long volDownPressTime = 0;

    private BroadcastReceiver mediaButtonReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (event != null) handleKey(event);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        createNotificationChannel();
        startForeground(NOTIF_ID, buildNotification());
        registerVolumeReceiver();
    }

    private void registerVolumeReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(mediaButtonReceiver, filter);
    }

    private void handleKey(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (action == KeyEvent.ACTION_DOWN) {
                volUpPressTime = System.currentTimeMillis();
            } else if (action == KeyEvent.ACTION_UP) {
                long held = System.currentTimeMillis() - volUpPressTime;
                if (held >= HOLD_THRESHOLD_MS) {
                    skipNext();
                } else {
                    skipNext();
                }
                volUpPressTime = 0;
            }
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (action == KeyEvent.ACTION_DOWN) {
                volDownPressTime = System.currentTimeMillis();
            } else if (action == KeyEvent.ACTION_UP) {
                long held = System.currentTimeMillis() - volDownPressTime;
                if (held >= HOLD_THRESHOLD_MS) {
                    skipPrevious();
                }
                volDownPressTime = 0;
            }
        }
    }

    private void skipNext() {
        Intent i = new Intent(Intent.ACTION_MEDIA_NEXT);
        sendBroadcast(i);
        // Also try via AudioManager for wider app support
        KeyEvent down = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT);
        KeyEvent up = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT);
        audioManager.dispatchMediaKeyEvent(down);
        audioManager.dispatchMediaKeyEvent(up);
    }

    private void skipPrevious() {
        Intent i = new Intent(Intent.ACTION_MEDIA_PREVIOUS);
        sendBroadcast(i);
        KeyEvent down = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        KeyEvent up = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        audioManager.dispatchMediaKeyEvent(down);
        audioManager.dispatchMediaKeyEvent(up);
    }

    private Notification buildNotification() {
        Intent stopIntent = new Intent(this, VolumeService.class);
        stopIntent.setAction("STOP");
        PendingIntent stopPending = PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("VolumeSkip active")
            .setContentText("Hold Vol Up = Next  |  Hold Vol Down = Previous")
            .setSmallIcon(android.R.drawable.ic_media_next)
            .addAction(android.R.drawable.ic_delete, "Stop", stopPending)
            .setOngoing(true)
            .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "VolumeSkip", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Running in background to intercept volume keys");
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "STOP".equals(intent.getAction())) {
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try { unregisterReceiver(mediaButtonReceiver); } catch (Exception ignored) {}
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
