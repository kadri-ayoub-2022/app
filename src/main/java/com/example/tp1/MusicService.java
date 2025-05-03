package com.example.tp1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;


    private final IBinder binder = new LocalBinder();
    private int currentSongIndex = 0;
    private int[] songList;
    private String[] songTitles;
    private boolean isPlaying = false;


    public class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        songList = new int[]{R.raw.dakir, R.raw.song};
        songTitles = new String[]{"dakir.mp3", "song.mp3"};
        mediaPlayer = MediaPlayer.create(this, songList[currentSongIndex]);
    }

    public void playSong(int index) {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        currentSongIndex = index;
        mediaPlayer = MediaPlayer.create(this, songList[currentSongIndex]);
        mediaPlayer.start();
        isPlaying = true;
        showNotification();
    }

    public void startMusic() {
        if (!isPlaying && mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            showNotification();
        }
    }

    public void pauseMusic() {
        if (isPlaying && mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
            showNotification();
        }
    }


    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
        stopForeground(true);
        stopSelf();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public String getCurrentSongTitle() {
        return songTitles[currentSongIndex];
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    public int[] getSongList() {
        return songList;
    }

    public String[] getSongTitles() {
        return songTitles;
    }


    private void showNotification() {
        String channelId = "music_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Music Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }


        Intent playIntent = new Intent(this, MusicService.class);
        playIntent.setAction("PLAY");
        PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        Intent pauseIntent = new Intent(this, MusicService.class);
        pauseIntent.setAction("PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        Intent openAppIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notes_de_musique)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                .setContentTitle("Lecture en cours")
                .setContentText(songTitles[currentSongIndex])
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .addAction(isPlaying ? R.drawable.pause : R.drawable.play,
                        isPlaying ? "Pause" : "Play",
                        isPlaying ? pausePendingIntent : playPendingIntent);

        Notification notification = builder.build();
        startForeground(1, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "PLAY":
                    startMusic();
                    break;
                case "PAUSE":
                    pauseMusic();
                    break;
            }
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMusic();
    }
}