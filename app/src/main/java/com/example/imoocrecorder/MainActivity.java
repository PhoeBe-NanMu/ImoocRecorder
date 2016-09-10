package com.example.imoocrecorder;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView mlistView;
    private ArrayAdapter<Recorder> mAdapter;
    private List<Recorder> mDataList = new ArrayList<>();
    private RecorderButton mRecorderButton;

    private View animView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mlistView = (ListView) findViewById(R.id.recorder_list);
        mRecorderButton = (RecorderButton) findViewById(R.id.recorder_btn);

         /*录音结束后的回调*/
        mRecorderButton.setAudioFinishRecorderListener(new RecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                Recorder recorder = new Recorder(filePath,seconds);
                mDataList.add(recorder);
                mAdapter.notifyDataSetChanged();
                mlistView.setSelection(mDataList.size()-1);
            }
        });
        mAdapter = new RecorderAdapter(this,mDataList);
        mlistView.setAdapter(mAdapter);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //播放动画
                if (animView != null) {
                    animView.setBackgroundResource(R.drawable.adj);
                    animView = null;
                }
                animView = view.findViewById(R.id.id_recorder_anim);
                animView.setBackgroundResource(R.drawable.paly_anim);
                AnimationDrawable anim = (AnimationDrawable) animView.getBackground();
                anim.start();

                //播放音频
                MediaManager.playSound(mDataList.get(position).filePath, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        animView.setBackgroundResource(R.drawable.adj);
                    }
                });
            }
        });
    }


    @Override
    protected void onResume() {

        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }

}



class Recorder{
    float time;
    String filePath;

    public Recorder(String filePath, float time) {
        this.filePath = filePath;
        this.time = time;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}

