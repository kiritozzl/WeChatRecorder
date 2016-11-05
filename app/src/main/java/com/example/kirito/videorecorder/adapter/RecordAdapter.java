package com.example.kirito.videorecorder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kirito.videorecorder.MainActivity;
import com.example.kirito.videorecorder.R;

import java.util.List;

/**
 * Created by kirito on 2016.11.05.
 */

public class RecordAdapter extends ArrayAdapter<MainActivity.Recorder> {
    private Context mContext;
    private List<MainActivity.Recorder> list;
    private int itemMinWidth;
    private int itemMaxWidth;
    private LayoutInflater mLayoutInflater;

    private static final String TAG = "RecordAdapter";

    public RecordAdapter(Context context, List<MainActivity.Recorder> objects) {
        super(context, -1, objects);
        mContext = context;
        list = objects;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        //获取屏幕的宽度：metrics.widthPixels
        itemMaxWidth = (int) (metrics.widthPixels * 0.8f);
        itemMinWidth = (int) (metrics.widthPixels * 0.1f);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder = null;
        if (convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.list_item,parent,false);
            holder = new viewHolder();
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            //这里获取的是背景框的FrameLayout的view对象，为了改变背景框的长度
            holder.length = convertView.findViewById(R.id.fl);
            convertView.setTag(holder);
        }else {
            holder = (viewHolder) convertView.getTag();
        }
        MainActivity.Recorder item = list.get(position);
        //注意转义字符
        holder.tv_time.setText(Math.round(item.getTime()) + "\"");
        ViewGroup.LayoutParams lp = holder.length.getLayoutParams();
        //控制录音长度最长为：itemMinWidth + itemMaxWidth
        if (item.getTime() <= 60){
            lp.width = (int) (itemMinWidth + (itemMaxWidth / 60f * item.getTime()));
        }else {
            lp.width = itemMinWidth + itemMaxWidth;
        }
        return convertView;
    }

    class viewHolder{
        TextView tv_time;
        View length;
    }
}
