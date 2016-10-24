package testapplication.vivek.com.mediaplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;

import testapplication.vivek.com.mediaplayer.R;
import testapplication.vivek.com.mediaplayer.adapter.Song;

/**
 * Created by VIVEK on 10/22/2016.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mPlayer;
    private Uri mUri;
    private ArrayList<Song> mSongList;
    private int SONG_POS = 0;

    private final IBinder mBind = new PlayerBinder();

    private final String ACTION_STOP = "testapplication.vivek.com.mediaplayer.STOP";
    private final String ACTION_NEXT = "testapplication.vivek.com.mediaplayer.NEXT";
    private final String ACTION_PREVIOUS = "testapplication.vivek.com.mediaplayer.PREVIOUS";
    private final String ACTION_PAUSE = "testapplication.vivek.com.mediaplayer.PAUSE";

    private static final int STATED_PAUSED = 1;
    private static final int STATED_PLAYING = 2;
    private int mState = 0;

    private static final int REQUEST_CODE_PAUSE = 101;
    private static final int REQUEST_CODE_PREVIOUS = 102;
    private static final int REQUEST_CODE_NEXT = 103;
    private static final int REQUEST_CODE_STOP = 104;
    public static int NOTIFICATION_ID = 11;
    private Notification.Builder notificationBuilder;
    private Notification notification;

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayer.start();
    }

    public void setSongURI(Uri uri) {
        this.mUri = uri;
    }

    public void setSelectedSong(int position, int notificationId) {
        SONG_POS = position;
        NOTIFICATION_ID = notificationId;
        setSongURI(mSongList.get(SONG_POS).getSongUri());
        showNotification();
        startSong(mSongList.get(SONG_POS).getSongUri(), mSongList.get(SONG_POS).getSongName());
    }

    public void setSongList(ArrayList<Song> listSong) {
        mSongList = listSong;
    }

    public void showNotification() {
        PendingIntent pendingIntent;
        Intent intent;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification);
        notificationView.setTextViewText(R.id.notifi1, mSongList.get(SONG_POS).getSongName());

        intent = new Intent(ACTION_STOP);
        pendingIntent = PendingIntent.getService(getApplicationContext(), REQUEST_CODE_STOP, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.n1, pendingIntent);

        intent = new Intent(ACTION_PAUSE);
        pendingIntent = PendingIntent.getService(getApplicationContext(), REQUEST_CODE_PAUSE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.n2, pendingIntent);

        intent = new Intent(ACTION_PREVIOUS);
        pendingIntent = PendingIntent.getService(getApplicationContext(), REQUEST_CODE_PREVIOUS, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.n3, pendingIntent);

        intent = new Intent(ACTION_NEXT);
        pendingIntent = PendingIntent.getService(getApplicationContext(), REQUEST_CODE_NEXT, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.n4, pendingIntent);

        notification = notificationBuilder.setSmallIcon(R.drawable.radio).setOngoing(true)
                .setWhen(System.currentTimeMillis()).setContent(notificationView).setDefaults(Notification.FLAG_NO_CLEAR).build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void updateNotification(String songName) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification.contentView.setTextViewText(R.id.notifi1, songName);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public class PlayerBinder extends Binder {
        public MusicService getService() {
            Log.d("test", "getService()");
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initPlayer();
        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        notificationBuilder = new Notification.Builder(getApplicationContext());
    }

    private void initPlayer() {
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void startSong(Uri songuri, String songName) {
        mPlayer.reset();
        mState = STATED_PLAYING;
        mUri = songuri;
        try {
            mPlayer.setDataSource(getApplicationContext(), mUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);

        }
        mPlayer.prepareAsync();
        updateNotification(songName);
    }

    public void playPauseSong() {
        if (mState == STATED_PAUSED) {
            mState = STATED_PLAYING;
            mPlayer.start();
        } else {
            mState = STATED_PAUSED;
            mPlayer.pause();
        }
    }

    public void stopSong() {
        mPlayer.stop();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
        System.exit(0);
    }

    public void nextSong() {
        startSong(mSongList.get(SONG_POS + 1).getSongUri(), mSongList.get(SONG_POS + 1).getSongName());
        SONG_POS++;
    }

    public void previousSong() {
        startSong(mSongList.get(SONG_POS - 1).getSongUri(), mSongList.get(SONG_POS - 1).getSongName());
        SONG_POS--;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("test", "onBind called");

        return mBind;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mPlayer.stop();
        return super.onUnbind(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(ACTION_PAUSE)) {
                    playPauseSong();
                } else if (action.equals(ACTION_NEXT)) {
                    nextSong();
                } else if (action.equals(ACTION_PREVIOUS)) {
                    previousSong();
                } else if (action.equals(ACTION_STOP)) {
                    stopSong();
                    stopSelf();
                }
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }
}

