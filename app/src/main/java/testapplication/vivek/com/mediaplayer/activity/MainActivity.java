package testapplication.vivek.com.mediaplayer.activity;


import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;

import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.os.IBinder;
import android.provider.MediaStore;

import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import testapplication.vivek.com.mediaplayer.R;
import testapplication.vivek.com.mediaplayer.adapter.Song;
import testapplication.vivek.com.mediaplayer.adapter.SongListApdater;
import testapplication.vivek.com.mediaplayer.service.MusicService;


public class MainActivity extends ListActivity implements View.OnClickListener ,AdapterView.OnItemClickListener{

    private LinearLayout ll;
    private RelativeLayout rr;
    private SongListApdater mAdapterListFiles;
    private String[] STAR = {"*"};
    private ArrayList<Song> mSongList;
    private MusicService mService;
    private Intent playIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        Button b1 = (Button) findViewById(R.id.button1);
        ListView list = (ListView) findViewById(R.id.ls1);
        ll = (LinearLayout) findViewById(R.id.linear);
        rr = (RelativeLayout) findViewById(R.id.activity_my_music);

        b1.setOnClickListener(MainActivity.this);

        mSongList = new ArrayList<Song>();

        mAdapterListFiles = new SongListApdater(MainActivity.this, mSongList);
        list.setAdapter(mAdapterListFiles);


    }
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.PlayerBinder binder = (MusicService.PlayerBinder) service;
           mService=binder.getService();
            mService.setSongList(mSongList);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onClick(View v) {
        mSongList = listAllSong();
        mAdapterListFiles.seTsongList(mSongList);
        ll.setVisibility(View.VISIBLE);
        rr.setVisibility(View.GONE);
    }



    private ArrayList<Song> listAllSong() {
        Cursor cursor;
        ArrayList<Song> songList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        if (isSdCard()) {
            cursor = managedQuery(uri, STAR, selection, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        Song song = new Song();
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        String[] res = data.split("//.");

                        song.setSongName(res[0]);

                        song.setSongfullpath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));

                        song.setSongId(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));

                        song.setSongAlbumName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));


                        song.setUri(ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID))));

                        String duration = getDuration(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                        song.setSongDuratrion(duration);
                        songList.add(song);
                    }
                    while (cursor.moveToNext());
                    return songList;

                }
                cursor.close();
            }
        }
        return null;
    }

    private static String getDuration(long milis) {
        if (milis < 0) {
            throw new IllegalArgumentException("Duration Must BE Greater them Zero");

        }
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milis);
        milis -= TimeUnit.MILLISECONDS.toMillis(minutes);
        long second = TimeUnit.MILLISECONDS.toSeconds(milis);

        String ab = (minutes < 10 ? "0" + minutes : minutes) +
                ":" +
                (second < 10 ? "0" + second : second);

        return ab;
    }


    private boolean isSdCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
     mService.setSelectedSong(position,MusicService.NOTIFICATION_ID);
    }
    @Override
    protected void onStart(){
        if(playIntent == null){
            playIntent=new Intent(this,MusicService.class);
            bindService(playIntent,connection, Context.BIND_AUTO_CREATE);
            startService(playIntent);

        }
    }
    @Override
    protected void onDestroy(){
        stopService(playIntent);
        super.onDestroy();
    }
}
