package com.monke.mprogressbar.sample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.monke.mprogressbar.MHorProgressBar;
import com.monke.mprogressbar.OnProgressListener;

public class MHorProgressBar2Activity extends Activity{

    private MHorProgressBar mhp_2;
    private TextView tv_2;

    private MHorProgressBar mhp_3;
    private TextView tv_3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_demo);
        bindView();
        bindEvent();
    }

    private void bindEvent() {
        mhp_2.setProgressListener(new OnProgressListener() {
            @Override
            public void moveStartProgress(float dur) {

            }

            @Override
            public void durProgressChange(float dur) {
                float tvMarginLeft = mhp_2.getRealProgressWidth()*1.0f*(dur/mhp_2.getMaxProgress()-0.02f) - tv_2.getWidth();     // -0.02f目的是字体不要紧贴进度条最右端
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) tv_2.getLayoutParams();
                if(tvMarginLeft>0){
                    layoutParams.leftMargin = (int) tvMarginLeft;
                }else{
                    layoutParams.leftMargin = 0;
                }
                tv_2.setLayoutParams(layoutParams);
                tv_2.setText((int)(dur*100/mhp_2.getMaxProgress())+"%");
            }

            @Override
            public void moveStopProgress(float dur) {

            }

            @Override
            public void setDurProgress(float dur) {

            }
        });

        mhp_3.setProgressListener(new OnProgressListener() {
            @Override
            public void moveStartProgress(float dur) {

            }

            @Override
            public void durProgressChange(float dur) {
                tv_3.setText((int)(dur*100/mhp_3.getMaxProgress())+"%");
            }

            @Override
            public void moveStopProgress(float dur) {

            }

            @Override
            public void setDurProgress(float dur) {

            }
        });
    }

    private void bindView() {
        mhp_2 = (MHorProgressBar) findViewById(R.id.mhp_2);
        tv_2 = (TextView) findViewById(R.id.tv_2);

        mhp_3 = (MHorProgressBar) findViewById(R.id.mhp_3);
        tv_3 = (TextView) findViewById(R.id.tv_3);
    }
}
