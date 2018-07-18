package com.android.wisdomrecording.util;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by Administrator on 2018/6/19.
 */

public class MediaPlayerUtil {
    private static MediaPlayer mediaPlayer;
    static int time = 0;
    static int maxTime = 0;
    static Timer timer = null;
    static TimerTask timerTask = null;
    static Handler handler = new Handler();
    static int deleyTime = 0;
    static int currentTime = 0;

    //延迟播放, 先加载，后播放 ( volume 音量)
    public static void playDelayed(final String url, float volume, int delayedTime) {
        deleyTime = delayedTime;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer1) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.setVolume(volume, volume);
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.postDelayed(runnable, 0);
    }

    final static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (currentTime >= deleyTime) {
                mediaPlayer.start();
                currentTime = 0;
                deleyTime = 0;
            } else {
                handler.postDelayed(runnable, 1000);
            }
            currentTime = currentTime + 1000;
        }
    };

    public static void play(String url) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                maxTime = (int) (mediaPlayer.getDuration() / 100);
                time = 0;
                startTimer();
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer1) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        stopTimer();
    }

    public static void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        startTimer();
    }

    public static boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        } else {
            return false;
        }
    }


    public static void stop() {
        stopTimer();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = null;
    }


    private static void startTimer() {
        stopTimer();
        if (timer == null) {
            timer = new Timer();
        }

        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    time = time + 1;
                    if (time > maxTime) {
                        //播放完
                        stopTimer();
                    }
                }
            };
        }
        timer.schedule(timerTask, 0, 100);
    }

    private static void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    public static void playResouce(Context context, int resouceId)
    {
        final MediaPlayer mp = new MediaPlayer();
        AssetFileDescriptor file = context.getResources().openRawResourceFd(resouceId);
        try {
            mp.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                    file.getLength());
            mp.prepare();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.setVolume(1f, 1f);
        mp.setLooping(false);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer1) {
                mp.release();
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mp.stop();
                mp.release();
            }
        },3000);

    }
}
