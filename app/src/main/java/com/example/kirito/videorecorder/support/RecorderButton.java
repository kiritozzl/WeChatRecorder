package com.example.kirito.videorecorder.support;

import android.content.Context;
import android.util.AttributeSet;
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
    private boolean isRecording;

    public RecorderButton(Context context) {
        this(context,null);
    }

    public RecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        cur_state = BTN_STATE_NORMAL;

        mDialogManager = new DialogManager(context);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //在mediarecorder 初始化之后
                mDialogManager.showDialog();
                isRecording = true;
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                if (cur_state == BTN_STATE_NORMAL){
                    changeButtonState(BTN_STATE_RECORDING);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (cur_state == BTN_STATE_CANCEL){

                }else if (cur_state == BTN_STATE_RECORDING){
                    //TODO获取录制好的音频

                }
                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                if (wantToCancel(x,y)){
                    changeButtonState(BTN_STATE_CANCEL);
                }else {
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
        setBackgroundResource(R.drawable.btn_normal_background);
        setText(R.string.btn_state_normal);
        cur_state = BTN_STATE_NORMAL;
        mDialogManager.dismissDialog();
    }
}
