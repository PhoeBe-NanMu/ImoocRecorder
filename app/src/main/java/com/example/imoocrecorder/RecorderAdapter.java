package com.example.imoocrecorder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by LeiYang on 2016/9/10 0010.
 */
public class RecorderAdapter extends ArrayAdapter<Recorder> {

    private int mMinItemWidth;
    private int mMaxItemWidth;

    private LayoutInflater layoutInflater;

    public RecorderAdapter(Context context, List<Recorder> mDataList) {
        super(context, -1,mDataList);

        layoutInflater = LayoutInflater.from(context);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        mMaxItemWidth = (int) (displayMetrics.widthPixels * 0.7f);
        mMinItemWidth = (int) (displayMetrics.widthPixels * 0.15f);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_list,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.secondsTextView = (TextView) convertView.findViewById(R.id.id_recorder_time);
            viewHolder.lengthView = convertView.findViewById(R.id.id_recorder_length);
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.secondsTextView.setText(Math.round(getItem(position).getTime())+"\"" +
                "");
        //ViewGroup.LayoutParams : 获取View的参数对象
        ViewGroup.LayoutParams layoutParams = viewHolder.lengthView.getLayoutParams();
        layoutParams.width = (int) (mMinItemWidth + mMaxItemWidth/60f*getItem(position).getTime());

        return convertView;
    }

    private class ViewHolder{
        TextView secondsTextView;
        View lengthView;
    }
}
