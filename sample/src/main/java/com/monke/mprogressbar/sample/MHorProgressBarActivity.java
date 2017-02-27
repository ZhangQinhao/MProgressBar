package com.monke.mprogressbar.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.monke.mprogressbar.MHorProgressBar;
import com.monke.mprogressbar.MVerProgressBar;

public class MHorProgressBarActivity extends Activity{
    //MHorProgress
    private MHorProgressBar mhp_1;
    private MHorProgressBar mhp_2;
    private MHorProgressBar mhp_3;
    private MHorProgressBar mhp_4;
    private MHorProgressBar mhp_5;
    //MVerProgress
    private MVerProgressBar mvp_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mhorprogressbar);
        bindView();

        sample();
    }

    private void sample() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mhp_1.setDurProgressWithAnim(50);
                mhp_2.setDurProgressWithAnim(50);
                mhp_3.setDurProgressWithAnim(50);
                mhp_4.setDurProgressWithAnim(50);
                mhp_5.setDurProgressWithAnim(mhp_5.getMaxProgress());

                mvp_1.setDurProgress(mvp_1.getMaxProgress());
            }
        },1000);
    }

    private void bindView() {
        mhp_1 = (MHorProgressBar) findViewById(R.id.mhp_1);
        mhp_1.setSpeed(0.5f);   //设置进度动画移动速度

        mhp_2 = (MHorProgressBar) findViewById(R.id.mhp_2);

        mhp_3 = (MHorProgressBar) findViewById(R.id.mhp_3);

        mhp_4 = (MHorProgressBar) findViewById(R.id.mhp_4);

        mhp_5 = (MHorProgressBar) findViewById(R.id.mhp_5);

        mvp_1 = (MVerProgressBar) findViewById(R.id.mvp_1);
    }
}
