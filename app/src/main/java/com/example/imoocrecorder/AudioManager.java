package com.example.imoocrecorder;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.FileNameMap;
import java.util.UUID;
import android.media.MediaRecorder.AudioSource;

/**
 * Created by LeiYang on 2016/9/9 0009.
 */

public class AudioManager {
    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    private static AudioManager mInstance;

    private boolean isPrepared;

    private AudioManager(String dir){
        mDir = dir;
    }

    public String getCurrentPath() {
        return mCurrentFilePath;
    }


    /**
     * 回掉准备完毕
     */
    public interface AudioStateListener{
        void wellPrepared();
    }

    public AudioStateListener mlistener;

    public void setOnAudioStateListener(AudioStateListener listener){
        mlistener = listener;
    }

    public static AudioManager getInstance(String dir){
        if (mInstance == null) {
            synchronized(AudioManager.class){
                if (mInstance == null) {
                    mInstance = new AudioManager(dir);
                }
            }
        }
        return mInstance;
    }

    public void prepareAudio(){

        /*在开始之前isPrepared设置为false，准备完成后设置为true*/
        isPrepared = false;
        try {
            File dir = new File(mDir);
            if (!dir.exists()){
                dir.mkdirs();
            }
            String fileName = generateFileName();
            File file = new File(dir,fileName);
            mCurrentFilePath = file.getAbsolutePath();
            mMediaRecorder = new MediaRecorder();
            //设置输出文件
            mMediaRecorder.setOutputFile(mCurrentFilePath);
            //设置MediaRecorder的音频源是麦克风
            mMediaRecorder.setAudioSource(AudioSource.MIC);
            //设置音频的格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            //设置音频的编码为AMR
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mMediaRecorder.prepare();
            mMediaRecorder.start();
            //准备结束
            isPrepared = true;
            if (mlistener !=null) {
                mlistener.wellPrepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String generateFileName() {
        return UUID.randomUUID().toString()+".amr";

    }

    public int getVoiceLevel(int maxLevel){
        if (isPrepared) {

            /*maxLevel:取值：1~7*/
            try{
                /*
                 *mMediaRecorder.getMaxAmplitude():振幅 值范围：1~32767
                 * mMediaRecorder.getMaxAmplitude()/32768  在0~1之间
                 * 7*mMediaRecorder.getMaxAmplitude()/32768 在0~7之间，但最大只能取到6，所以加1
                 */
                return maxLevel*mMediaRecorder.getMaxAmplitude()/32768+1;

            } catch (Exception e) {

            }

     }
        return 1;
    }

    public void release(){
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    public void cancel(){
        /**
         * cancel()包含两部分  ：release()和删除文件
         */
        release();

        /*删除文件*/
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            /*删除文件后把路径对象设置为null*/
            mCurrentFilePath = null;
        }

    }
}
