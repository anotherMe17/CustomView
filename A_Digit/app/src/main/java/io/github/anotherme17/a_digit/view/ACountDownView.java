package io.github.anotherme17.a_digit.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Random;

import io.github.anotherme17.a_digit.R;
import io.github.anotherme17.digit.ADigit;

/**
 * Created by Administrator on 2017/6/5.
 */

public class ACountDownView extends LinearLayout implements Runnable {

    private static final char[] SEXAGISIMAL = new char[]{'0', '1', '2', '3', '4', '5'};

    private static final char[] DECIMAL = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private ADigit mCharHighSecond;
    private ADigit mCharLowSecond;
    private ADigit mCharHighMinute;
    private ADigit mCharLowMinute;
    private ADigit mCharHighHour;
    private ADigit mCharLowHour;
    private View mClock = this;

    private boolean mStart = false;

    private long startedTime = System.currentTimeMillis();

    private long totalTime = 10 * 60 * 60; // 10 hours count down

    private long elapsedTime = 0;

    public ACountDownView(Context context) {
        this(context, null);
    }

    public ACountDownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ACountDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ACountDownView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        setOrientation(HORIZONTAL);
        inflate(getContext(), R.layout.clock, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mCharHighSecond = (ADigit) findViewById(R.id.charHighSecond);
        mCharLowSecond = (ADigit) findViewById(R.id.charLowSecond);
        mCharHighMinute = (ADigit) findViewById(R.id.charHighMinute);
        mCharLowMinute = (ADigit) findViewById(R.id.charLowMinute);
        mCharHighHour = (ADigit) findViewById(R.id.charHighHour);
        mCharLowHour = (ADigit) findViewById(R.id.charLowHour);

        //mCharHighSecond.setTextSize(100);
        mCharHighSecond.setChars(SEXAGISIMAL);
        //mCharLowSecond.setTextSize(100);
        mCharLowSecond.setChars(DECIMAL);

       // mCharHighMinute.setTextSize(100);
        mCharHighMinute.setChars(SEXAGISIMAL);
        //mCharLowMinute.setTextSize(100);
        mCharLowMinute.setChars(DECIMAL);

        //mCharHighHour.setTextSize(100);
        mCharHighHour.setChars(DECIMAL);
        //mCharLowHour.setTextSize(100);
        mCharLowHour.setChars(DECIMAL);
    }

    public void pause() {
        mStart = false;
        mCharHighSecond.sync();
        mCharLowSecond.sync();
        mCharHighMinute.sync();
        mCharLowMinute.sync();
        mCharHighHour.sync();
        mCharLowHour.sync();
    }

    public void start() {
        if (mStart)
            return;
        mStart = true;

        /*开始计时方法1*/

        elapsedTime = 0;
        int hour = (int) (totalTime / (60 * 60));
        mCharHighHour.setChar(hour > 0 ? hour / 10 : 0);
        mCharLowHour.setChar(hour > 0 ? hour % 10 : 0);

        int minute = (int) ((totalTime % (60)) / (60));
        mCharHighMinute.setChar(minute > 0 ? minute / 10 : 0);
        mCharLowMinute.setChar(minute > 0 ? minute % 10 : 0);

        int second = (int) (totalTime % (60 * 60));
        mCharHighSecond.setChar(second > 0 ? second / 10 : 0);
        mCharLowSecond.setChar(second > 0 ? second % 10 : 0);

        ViewCompat.postOnAnimationDelayed(mClock, this, 1000);

       /*开始计时方法2*/
       /* int time = (int) (totalTime - elapsedTime);*/

    }

    @Override
    public void run() {
        if (!mStart) {
            return;
        }
        mCharLowSecond.pre();
        if (elapsedTime % 10 == 0) {
            mCharHighSecond.pre();
        }
        if (elapsedTime % 60 == 0) {
            mCharLowMinute.pre();
        }
        if (elapsedTime % 600 == 0) {
            mCharHighMinute.pre();
        }
        if (elapsedTime % 3600 == 0) {
            mCharLowHour.pre();
        }
        if (elapsedTime % 36000 == 0) {
            mCharHighHour.pre();
        }
        elapsedTime++;
        ViewCompat.postOnAnimationDelayed(mClock, this, 1000);
    }

    Runnable digit = new Runnable() {
        @Override
        public void run() {
            Random random = new Random();
            for (int i = 0; i < 30; i++) {
                mCharHighHour.setChar(random.nextInt());
                mCharLowHour.setChar(random.nextInt());
                mCharHighMinute.setChar(random.nextInt());
                mCharLowMinute.setChar(random.nextInt());
                mCharHighSecond.setChar(random.nextInt());
                mCharLowSecond.setChar(random.nextInt());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
