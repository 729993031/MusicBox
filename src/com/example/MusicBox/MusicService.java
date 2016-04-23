package com.example.MusicBox;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;

/**
 * Created by Administrator on 2016/4/22.
 */
public class MusicService extends Service {
    MyReceiver serviceReceiver;
    AssetManager am;
    String[] musics=new String[]{"wish.mp3","promise.mp3","beatuiful.mp3"};
    MediaPlayer mediaPlayer;
    int status=0x11;
    int current=0;
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        am=getAssets();
        serviceReceiver=new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(MyActivity.CTL_ACTION);
        registerReceiver(serviceReceiver,filter);
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                current++;
                if (current>=3)
                {
                    current=0;
                }
                Intent sendIntent=new Intent(MyActivity.UPDATE_ACTION);
                sendIntent.putExtra("current",current);
                sendBroadcast(sendIntent);
                prepareAndPlay(musics[current]);
            }
        });
    }
    public class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(final Context context,Intent intent)
        {
            int control=intent.getIntExtra("control",-1);
            switch (control)
            {
                case 1:
                    if (status==0x11)
                    {
                        prepareAndPlay(musics[current]);
                        status=0x12;
                    }
                    else if (status==0x12)
                    {
                        mediaPlayer.pause();
                        status=0x13;
                    }
                    else  if (status==0x13)
                    {mediaPlayer.start();
                        status=0x12;
            }
                    break;
                case 2:
                    if (status==0x12||status==0x13)
                    {
                        mediaPlayer.stop();
                        status=0x11;
                    }
        }
            Intent sendIntent=new Intent(MyActivity.UPDATE_ACTION);
            sendIntent.putExtra("update",status);
            sendIntent.putExtra("current",current);
            sendBroadcast(sendIntent);
    }
}
private void prepareAndPlay(String music)
{
try {
    AssetFileDescriptor assetFileDescriptor=am.openFd(music);
    mediaPlayer.reset();;
    mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),assetFileDescriptor.getStartOffset(),assetFileDescriptor.getLength());
    mediaPlayer.prepare();
    mediaPlayer.start();

}
catch (IOException e) {
    e.printStackTrace();
    }
    }
}

