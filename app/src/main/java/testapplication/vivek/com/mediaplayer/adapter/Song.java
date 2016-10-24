package testapplication.vivek.com.mediaplayer.adapter;

import android.net.Uri;

/**
 * Created by VIVEK on 10/22/2016.
 */

public class Song {
    private String songname,songfullpath,songAlbuname,songDuratrion;
    private int songid;
    private Uri songUri;

    public Song(){ }
    public Song(String name,String allbumName,String duration,String fullpath, int id ,Uri songUri){
        this.songname=name;
        this.songAlbuname=allbumName;
        this.songfullpath=fullpath;
        this.songDuratrion=duration;
        this.songid=id;
        this.songUri=songUri;
    }
    public String getSongName(){return songname;}
    public void setSongName(String songname){this.songname=songname;}



    public String getSongAlbumName(){return songAlbuname;}
    public void setSongAlbumName(String songAlbuname) {
        this.songAlbuname = songAlbuname;
    }


    public String getSongDuratrion() {
        return songDuratrion;}
    public void setSongDuratrion(String songDuratrion) {
        this.songDuratrion = songDuratrion;
    }



    public String getSongFullpath() {
        return songfullpath;
    }
    public void setSongfullpath(String songfullpath){ this.songfullpath=songfullpath; }

    public int getSongId(){return songid;}
    public void setSongId(int songid){ this.songid=songid;}



    public Uri getSongUri() {
        return songUri;
    }
    public void setUri(Uri songUri){this.songUri=songUri;}


}

