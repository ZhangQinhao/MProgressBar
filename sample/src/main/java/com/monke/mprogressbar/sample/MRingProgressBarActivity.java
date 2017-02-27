package com.monke.mprogressbar.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import com.monke.mprogressbar.MRingProgressBar;
import com.monke.mprogressbar.OnRingProgressListener;

public class MRingProgressBarActivity extends Activity{
    private MRingProgressBar mrp_1;

    private MRingProgressBar mrp_2;
    private TextView tv_2;

    private MRingProgressBar mrp_3;
    private TextView tv_3;

    private MRingProgressBar mrp_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mringprogressbar);
        bindView();
        bindEvent();
        sample();
    }

    private void bindEvent() {
        mrp_2.setRingProgressListener(new OnRingProgressListener() {
            @Override
            public void durProgressChange(float dur) {
                tv_2.setText(((int)dur*100/mrp_2.getMaxProgress())+"%");
            }
        });

        mrp_3.setRingProgressListener(new OnRingProgressListener() {
            @Override
            public void durProgressChange(float dur) {
                tv_3.setText(((int)dur*100/mrp_3.getMaxProgress())+"%");
            }
        });
    }

    private void sample() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mrp_1.setDurProgressWithAnim(80);
                mrp_2.setDurProgressWithAnim(50);

                mrp_3.setDurProgressWithAnim(90);

                mrp_4.setDurProgressWithAnim(70);
            }
        },1000);
    }

    private void bindView() {
        mrp_1 = (MRingProgressBar) findViewById(R.id.mrp_1);

        mrp_2 = (MRingProgressBar) findViewById(R.id.mrp_2);
        tv_2 = (TextView) findViewById(R.id.tv_2);

        mrp_3 = (MRingProgressBar) findViewById(R.id.mrp_3);
        tv_3 = (TextView) findViewById(R.id.tv_3);

        mrp_4 = (MRingProgressBar) findViewById(R.id.mrp_4);
    }
}
