package testapplication.vivek.com.mediaplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import testapplication.vivek.com.mediaplayer.R;

/**
 * Created by VIVEK on 10/22/2016.
 */

public class SongListApdater extends BaseAdapter {

    public Context context;
    private ArrayList<Song> songlist;

    public SongListApdater(Context mContext, ArrayList<Song> list){
        context=mContext;
        this.songlist=list;
    }
    @Override
    public int getCount() {
        return songlist.size();
    }

    @Override
    public Object getItem(int position) {
        return songlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView= LayoutInflater.from(context).inflate(R.layout.song_list,null);
        }
        ImageView i=(ImageView)convertView.findViewById(R.id.img);
        TextView t1=(TextView)convertView.findViewById(R.id.txt1);
        TextView t2=(TextView)convertView.findViewById(R.id.txt2);
        TextView duration=(TextView)convertView.findViewById(R.id.txt3);


        i.setImageResource(R.drawable.radio);
        t1.setText(songlist.get(position).getSongName());
        t2.setText(songlist.get(position).getSongAlbumName());
        duration.setText(songlist.get(position).getSongDuratrion());


        return convertView;
    }


    public void seTsongList(ArrayList<Song> list){
        songlist=list;
        this.notifyDataSetChanged();

    }
}
