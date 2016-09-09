package com.example.imoocrecorder;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.PublicKey;

/**
 * Created by LeiYang on 2016/9/9 0009.
 */

public class DialogManager {
    private Dialog mDialog;

    private ImageView mIcon;
    private ImageView mVoice;

    private TextView mLabel;

    private Context mContext;

    public DialogManager(Context context) {
        mContext = context;
    }

    public void showRecordingDialog(){
        mDialog = new Dialog(mContext,R.style.Theme_Audio_Dialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_recorder,null);
        mDialog.setContentView(view);
        mIcon = (ImageView) mDialog.findViewById(R.id.dialog_recorder_icon);
        mVoice = (ImageView) mDialog.findViewById(R.id.dialog_recorder_voice);
        mLabel = (TextView) mDialog.findViewById(R.id.dialog_recorder_label);
        mDialog.show();
    }

    public void recording(){
        if (mDialog != null &&mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.recorder);
            mLabel.setText("手指上划，取消发送");

        }
    }
    public void wantToCancel(){
        if (mDialog != null &&mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.cancel);
            mLabel.setText("松开手指，取消发送");

        }
    }

    public void tooShort(){
        if (mDialog != null &&mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.voice_to_short);
            mLabel.setText("录音时间过短");

        }
    }

    public void dismissDialog(){
        if (mDialog != null &&mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void updateVoiceLevel(int level){
        if (mDialog != null &&mDialog.isShowing()) {
//            注释以下代码：更新音时不改变控件是否VISIBLE
//            mIcon.setVisibility(View.VISIBLE);
//            mVoice.setVisibility(View.VISIBLE);
//            mLabel.setVisibility(View.VISIBLE);

            //通过方法名找到资源
            int resId = mContext.getResources().getIdentifier("v"+level,"drawable",mContext.getPackageName());
            Log.i("info","资源名："+mContext.getPackageName());
            mVoice.setImageResource(resId);
        }
    }


}
