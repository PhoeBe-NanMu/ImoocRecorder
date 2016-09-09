package com.example.imoocrecorder;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by LeiYang on 2016/9/8 0008.
 */

public class RecorderButton extends Button implements AudioManager.AudioStateListener {

    private static final int DISTANCE_Y_CANCEL = 50;
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;

    private int mCurSate = STATE_NORMAL;
    boolean isRecording = false;

    /*是否触发longClick*/
    private boolean mReady;

    private DialogManager mDialogManager;
    private AudioManager mAudioManager;

    /*记录时间*/
    private float mTime ;

    public RecorderButton(Context context) {
        this(context,null);
    }


    Runnable mGetAudioVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                //只要isRecording = true 就不停记录时间(+0.1s)和动态显示音量
                while (isRecording) {
                            /*开启线程获取音量,因为获取VoiceLevel是有时间间隔的*/
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGE);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    public RecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);

//        String dir = Environment.getExternalStorageState()+"/imooc_recorder_audios";
        String dir = Environment.getExternalStorageDirectory()+"/imooc_recorder_audios/";
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/imooc_recorder_audios/";
        Log.i("info",dir);
        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(this);


        mDialogManager = new DialogManager(getContext());
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //真正显示因该是在audio prepare()之后
//                mDialogManager.showRecordingDialog();
//                isRecording = true;
                mReady = true;
                mAudioManager.prepareAudio();


                return false;
            }
        });
    }



    /*录音结束后的回调*/
    public interface AudioFinishRecorderListener{
        void onFinish(float seconds,String filePath);
    }

    AudioFinishRecorderListener mListener;

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener){
        mListener = listener;
    }


    private static final int MSG_AUDIO_PREPARED = 0x110;
    private static final int MSG_VOICE_CHANGE = 0x111;
    private static final int MSG_DIALOG_DISMISS = 0x112;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    //真正显示因该是在audio prepare()之后
                    mDialogManager.showRecordingDialog();
                    isRecording = true;

                    /*开启线程获取音量,因为获取VoiceLevel是有时间间隔的*/
                    new Thread(mGetAudioVoiceLevelRunnable).start();
                    break;

                case MSG_VOICE_CHANGE:
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;

                case MSG_DIALOG_DISMISS:
                    mDialogManager.dismissDialog();
                    break;


            }
        }
    };

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                //测试
//                isRecording = true;
                changeState(STATE_RECORDING);
                break;

            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    //根据x，y的坐标判断是否想要取消
                    if (wanttoCancel(x,y)){
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);

                } else if (!isRecording || mTime < 0.6){
                    //开始了prepare但是没有成功

                    /*Dialog显示时间短*/
                    mDialogManager.tooShort();

                    /*Audio结束，释放资源*/
                    mAudioManager.cancel();

                    /*延迟1.3s关闭tooShort的Dialog*/
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS,1300);

                }else if (mCurSate == STATE_RECORDING){   //正常录制结束
                    mDialogManager.dismissDialog();

                    //release会保存录音文件，而cancel会删除录音文件
                    mAudioManager.release();

                    //callbackToActivity
                    if (mListener!=null){
                        mListener.onFinish(mTime,mAudioManager.getCurrentPath());
                    }

                } else if (mCurSate == STATE_WANT_TO_CANCEL){
                    //cancel
                    mDialogManager.dismissDialog();
                    mAudioManager.cancel();
                }
                reset();
                break;


        }

        return super.onTouchEvent(event);

    }

    private void reset() {
        //恢复标志位
        isRecording = false;
        mReady = false;
        mTime = 0;
        changeState(STATE_NORMAL);

    }

    private boolean wanttoCancel(int x, int y) {
        if (x<0||x>getWidth()) {
            return true;
        }
        if (y<-DISTANCE_Y_CANCEL||y>getHeight()+DISTANCE_Y_CANCEL){
            return true;
        }
        return false;
    }

    private void changeState(int state) {
        //当前状态与目标状态不同时，changeState
        if (mCurSate != state) {
            mCurSate = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.str_recorder_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recorder);
                    setText(R.string.str_recorder_recording);
                    mDialogManager.recording();
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.btn_recorder);
                    setText(R.string.str_recorder_wantCancel);
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }


}
