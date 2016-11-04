package com.example.kirito.videorecorder.support;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.util.UUID;

/**
 * Created by kirito on 2016.11.03.
 */

public class AudioManager {
    private static AudioManager mInstance;
    private String mDir;
    private String mCurrentPath;

    private MediaRecorder mMediaRecorder;
    //标志位MediaRecorder prepare是否完成
    private boolean isPrepared;

    public audioStateListener mListener;

    private static final String TAG = "AudioManager";

    public String getFilePath() {
        return mCurrentPath;
    }

    //添加AudioManager prepared完毕之后的回调接口
    public interface audioStateListener{
        void audioPrepared();
    }
    public void setAudioStateListener(audioStateListener listener){
        mListener = listener;
    }

    public static AudioManager getInstance(String path){
        if (mInstance == null){
            synchronized (AudioManager.class){
                mInstance = new AudioManager(path);
            }
        }
        return mInstance;
    }

    public AudioManager(String path) {
        mDir = path;
    }

    public void audioPrepare(){
        String fileName = getFileName();
        File dir = new File(mDir);
        if (!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dir,fileName);
        mCurrentPath = file.getAbsolutePath();
        try{
            isPrepared = false;
            mMediaRecorder = new MediaRecorder();
            //注意mMediaRecorder的各项属性的初始化顺序
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            mMediaRecorder.prepare();
            mMediaRecorder.start();

            isPrepared = true;
            if (mListener != null){
                mListener.audioPrepared();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getVoiceLevel(int maxLevel){
        if (isPrepared){//mMediaRecorder.getMaxAmplitude()值在1-12367之间
            try{
                //返回值在1-7之间
//                Log.e(TAG, "getVoiceLevel: mMediaRecorder.getMaxAmplitude()---"+mMediaRecorder.getMaxAmplitude() );
//                Log.e(TAG, "getVoiceLevel: ---"+maxLevel *  mMediaRecorder.getMaxAmplitude() / 12368 + 1 );
                return maxLevel * mMediaRecorder.getMaxAmplitude() / 12368 + 1;
            }catch (Exception e){

            }
        }
        return 1;
    }

    public void releaseAudio(){
        if (mMediaRecorder != null){
            try{
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }catch (RuntimeException e){
                e.printStackTrace();
            }
        }
    }

    public void cancelAudio(){
        releaseAudio();
        if (mCurrentPath != null){
            File file = new File(mCurrentPath);
            file.delete();
            mCurrentPath = null;
        }
    }

    private String getFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }
}
