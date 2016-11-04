package com.example.kirito.videorecorder.support;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.kirito.videorecorder.R;

/**
 * Created by kirito on 2016.11.03.
 */

public class RecorderButton extends Button {
    private static final int BTN_STATE_NORMAL = 0x101;
    private static final int BTN_STATE_RECORDING = 0x102;
    private static final int BTN_STATE_CANCEL = 0x103;

    private int cur_state;

    private DialogManager mDialogManager;
    //是否初始化了mediarecord
    private boolean isRecording;

    private AudioManager mAudioManager;

    private static final int MSG_AUDIO_PREPARED = 0x110;
    private static final int MSG_VOICE_CHANGE = 0x111;
    private static final int MSG_DIALOG_DISMISS = 0x112;

    private float mTime;
    //是否触发onLongClick
    private boolean isLongClick;
    private audioFinishedListener mListener;

    private static final String TAG = "RecorderButton";

    public void setAudioFinishedListener(audioFinishedListener listener){
        mListener = listener;
    }

    public interface audioFinishedListener{
        void onFinishedRecord(float seconds,String filePath);
    }

    public RecorderButton(Context context) {
        this(context,null);
    }

    public RecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        cur_state = BTN_STATE_NORMAL;

        mDialogManager = new DialogManager(context);

        mAudioManager = AudioManager.getInstance(Environment.getExternalStorageDirectory() + "/imooc_zzl");
        mAudioManager.setAudioStateListener(new AudioManager.audioStateListener() {
            @Override
            public void audioPrepared() {
                mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongClick = true;
                mAudioManager.audioPrepare();
                return false;
            }
        });
    }

    private Runnable mGetVoiceLevel = new Runnable() {
        @Override
        public void run() {
            //不加while循环线程无法持续下去，同时导致无法记录音量的实时更新
            while (isRecording){
                try {
                    Thread.sleep(100);
                    mTime += 0.1;
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_AUDIO_PREPARED:
                    //在mediarecorder 初始化之后
                    mDialogManager.showDialog();
                    isRecording = true;

                    new Thread(mGetVoiceLevel).start();
                    break;
                case MSG_VOICE_CHANGE:
                    mDialogManager.setVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DISMISS:
                    mDialogManager.dismissDialog();
                    break;
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                changeButtonState(BTN_STATE_RECORDING);
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "onTouchEvent: mTime---"+mTime );
                if (!isLongClick){
                    reset();
                    return super.onTouchEvent(event);
                }else if (!isRecording || mTime < 0.6f){
                    Log.e(TAG, "onTouchEvent:tooshort mTime---"+mTime );
                    Log.e(TAG, "onTouchEvent: mDialogManager---"+mDialogManager.toString() );
                    mDialogManager.tooShort();
                    mAudioManager.cancelAudio();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS,6300);
                }
                else if (cur_state == BTN_STATE_CANCEL){
                    mDialogManager.dismissDialog();
                    mAudioManager.cancelAudio();
                }else if (cur_state == BTN_STATE_RECORDING){
                    //TODO获取录制好的音频
                    mDialogManager.dismissDialog();
                    mAudioManager.releaseAudio();
                    if (mListener != null){
                        mListener.onFinishedRecord(mTime,mAudioManager.getFilePath());
                    }
                }
                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                if (wantToCancel(x,y)){
                    changeButtonState(BTN_STATE_CANCEL);
                }else if (!wantToCancel(x,y)){
                    changeButtonState(BTN_STATE_RECORDING);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > getWidth()){
            return true;
        }else if (y < -getHeight() || y > getHeight()){
            return true;
        }
        return false;
    }

    public void changeButtonState(int ste){
        if (cur_state != ste){
            cur_state = ste;
            switch (cur_state){
                case BTN_STATE_NORMAL:
                    reset();
                    break;
                case BTN_STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recording_background);
                    setText(R.string.btn_state_recording);
                    if (isRecording){
                        mDialogManager.showRecording();
                    }
                    break;
                case BTN_STATE_CANCEL:
                    setBackgroundResource(R.drawable.btn_recording_background);
                    setText(R.string.btn_state_recording);
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }

    public void reset(){
        isRecording = false;
        isLongClick = false;
        mTime = 0;
        setBackgroundResource(R.drawable.btn_normal_background);
        setText(R.string.btn_state_normal);
        cur_state = BTN_STATE_NORMAL;
        mDialogManager.dismissDialog();
    }
}
