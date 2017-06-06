package io.github.anotherme17.a_digit;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import io.github.anotherme17.a_digit.view.ACountDownView;

/**
 * Created by Administrator on 2017/6/5.
 */

public class CountDownActivity extends Activity {

    private Button restBtn, startBtn;
    private ACountDownView mCountDownView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        restBtn = (Button) findViewById(R.id.rest);
        startBtn = (Button) findViewById(R.id.start);
        mCountDownView = (ACountDownView) findViewById(R.id.acountdown);

        restBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownView.pause();
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownView.start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

       // mCountDownView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mCountDownView.pause();
    }
}
