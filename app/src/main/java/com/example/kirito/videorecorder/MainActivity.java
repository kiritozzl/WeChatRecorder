package com.example.kirito.videorecorder;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.kirito.videorecorder.adapter.RecordAdapter;
import com.example.kirito.videorecorder.support.RecorderButton;

import java.util.ArrayList;
import java.util.List;

import static android.media.MediaPlayer.*;

public class MainActivity extends AppCompatActivity {
    private ListView lv;
    private RecorderButton recorderButton;
    private Recorder mRecorder;
    private ArrayAdapter<Recorder> adapter;
    private List<Recorder> list = new ArrayList<>();
    private View mAnimeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.lv);
        recorderButton = (RecorderButton) findViewById(R.id.btn);
        /**
         * recorderButton只需提供录音时间的秒数以及录音文件存放的路径
         * 通过回调来增添并更新listview
         */
        recorderButton.setAudioFinishedListener(new RecorderButton.audioFinishedListener() {
            @Override
            public void onFinishedRecord(float seconds, String filePath) {
                mRecorder = new Recorder(seconds,filePath);
                list.add(mRecorder);
                adapter.notifyDataSetChanged();
                //每次更新完listview指向最后的item
                lv.setSelection(list.size() - 1);
            }
        });

        adapter = new RecordAdapter(this,list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //每次点击播放前，先把mAnimeView设置为默认状态
                if (mAnimeView != null){
                    mAnimeView.setBackgroundResource(R.mipmap.adj);
                    mAnimeView = null;
                }

                //播放动画
                mAnimeView = view.findViewById(R.id.record_length);
                mAnimeView.setBackgroundResource(R.drawable.play_anime);
                AnimationDrawable anime = (AnimationDrawable) mAnimeView.getBackground();
                anime.start();
                //播放音频，传入录音文件路径，播放完成的回调
                MediaManager.playSound(list.get(position).filePath, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mAnimeView.setBackgroundResource(R.mipmap.adj);
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MediaManager.release();
    }

    //Recorder放置录音的时间，路径
    public class Recorder{
        private float time;
        private String filePath;

        public Recorder(float time, String filePath) {
            this.time = time;
            this.filePath = filePath;
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
}
