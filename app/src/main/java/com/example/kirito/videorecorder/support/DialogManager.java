package com.example.kirito.videorecorder.support;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kirito.videorecorder.R;

/**
 * Created by kirito on 2016.11.03.
 */

public class DialogManager {
    private Dialog mDialog;
    private Context mContext;
    private ImageView iv_icon,iv_voice;
    private TextView tv;

    private static final String TAG = "DialogManager";

    public DialogManager(Context mContext) {
        this.mContext = mContext;
    }

    public void showDialog(){
        mDialog = new Dialog(mContext, R.style.Dialog_Theme);

        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        View view = mLayoutInflater.inflate(R.layout.dialog,null);
        mDialog.setContentView(view);
        iv_icon = (ImageView) mDialog.findViewById(R.id.iv_icon);
        iv_voice = (ImageView) mDialog.findViewById(R.id.iv_voice);
        tv = (TextView) mDialog.findViewById(R.id.tv);
        mDialog.show();
    }

    public void showRecording(){
        if (mDialog != null && mDialog.isShowing()){
            iv_icon.setVisibility(View.VISIBLE);
            iv_voice.setVisibility(View.VISIBLE);

            iv_voice.setImageResource(R.mipmap.v1);
            tv.setText("手指上滑，取消发送");
        }
    }

    public void wantToCancel(){
        if (mDialog != null && mDialog.isShowing()){
            iv_icon.setVisibility(View.GONE);
            iv_voice.setVisibility(View.VISIBLE);

            iv_voice.setImageResource(R.mipmap.cancel);
            tv.setText("手指松开，取消发送");
        }
    }

    public void  tooShort(){
        Log.e(TAG, "tooShort: ---" );
        if (mDialog != null && mDialog.isShowing()){
            Log.e(TAG, "tooShort: ---111" );
            iv_icon.setVisibility(View.VISIBLE);
            iv_voice.setVisibility(View.GONE);

            iv_icon.setImageResource(R.mipmap.voice_to_short);
            tv.setText("录音时间过短");
        }
    }

    public void dismissDialog(){
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 根据level去更新voice上的图片
     * @param level 1-7
     */
    public void setVoiceLevel(int level){
        if (mDialog != null && mDialog.isShowing()){
            iv_icon.setVisibility(View.VISIBLE);
            iv_voice.setVisibility(View.VISIBLE);

            int res_id = mContext.getResources().getIdentifier("v" + level,"mipmap",mContext.getPackageName());
//            Log.e(TAG, "setVoiceLevel: ---" + res_id);
//            Log.e(TAG, "setVoiceLevel: R.drawable.v1---"+R.drawable.v1 );
            iv_voice.setImageResource(res_id);
        }
    }
}
