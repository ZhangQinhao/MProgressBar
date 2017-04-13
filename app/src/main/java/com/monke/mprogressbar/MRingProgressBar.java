package com.monke.mprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

public class MRingProgressBar extends View {
    private int speed = 1;   //如果设置当前进度使用动画  动画速度

    private float maxProgress = 100;
    private float durProgress = 0;
    private float durProgressFinal = 0;   //如果是动画 这个则为动画目标值

    private Drawable fontDrawable;
    private Bitmap fontBitmap;

    private Drawable bgDrawable;
    private Bitmap bgBitmap;

    private int bgBorderColor = 0xFF00CCFF;
    private int bgBorderWidth = 0;   //底部边框默认宽度

    private int startLeft = 1;    //0逆时针    1顺时针

    private int progressWidth = dip2px(15);  //进度条宽度

    private StateListDrawable cursorDrawable;   //因为没有触摸移动 所以其中只有一张drawable有效
    private Bitmap cursorBitmap;
    private int cursorDrawableWidth = dip2px(15);  //游标图标默认宽度
    private int cursorDrawableHeight = dip2px(15); //游标图标默认高度

    private int startAngle = 0; //起始角度

    private Handler handler;

    private Paint paint;

    public MRingProgressBar(Context context) {
        this(context, null);
    }

    public MRingProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MRingProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setAntiAlias(true);
        handler = new Handler(Looper.getMainLooper());
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MProgressBar);
        bgBorderColor = a.getColor(R.styleable.MProgressBar_bgbordercolor, bgBorderColor);
        bgBorderWidth = a.getDimensionPixelSize(R.styleable.MProgressBar_bgborderwidth, bgBorderWidth);

        bgDrawable = a.getDrawable(R.styleable.MProgressBar_bgdrawable);
        if (bgDrawable == null) {
            bgDrawable = new ColorDrawable(0xFFC1C1C1);
        }

        fontDrawable = a.getDrawable(R.styleable.MProgressBar_fontdrawable);
        if (fontDrawable == null) {
            fontDrawable = new ColorDrawable(0xFF00CCFF);
        }

        maxProgress = a.getFloat(R.styleable.MProgressBar_maxprogress, maxProgress);
        durProgress = a.getFloat(R.styleable.MProgressBar_durprogress, durProgress);
        durProgressFinal = durProgress;
        startAngle = a.getInt(R.styleable.MProgressBar_startangle, startAngle);
        startAngle = startAngle % 360;
        startLeft = a.getInt(R.styleable.MProgressBar_startLeftOrRight, startLeft);
        progressWidth = a.getDimensionPixelSize(R.styleable.MProgressBar_progresswidth, progressWidth);
        try {
            if (a.getDrawable(R.styleable.MProgressBar_cursordrawable) != null) {
                if (a.getDrawable(R.styleable.MProgressBar_cursordrawable) instanceof StateListDrawable) {
                    cursorDrawable = (android.graphics.drawable.StateListDrawable) a.getDrawable(R.styleable.MProgressBar_cursordrawable);
                } else {
                    cursorDrawable = new StateListDrawable();
                    Drawable temp = a.getDrawable(R.styleable.MProgressBar_cursordrawable);
                    cursorDrawable.addState(new int[]{}, temp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cursorDrawableWidth = a.getDimensionPixelSize(R.styleable.MProgressBar_cursordrawable_width, cursorDrawableWidth);
        cursorDrawableHeight = a.getDimensionPixelSize(R.styleable.MProgressBar_cursordrawable_height, cursorDrawableHeight);
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(progressWidth-bgBorderWidth*2<=0){
            throw new RuntimeException("bgBorderWidth超过绘制限度");
        }

        int abc = 0;   //怎么描述？就是如果cursorDrawable图片对角线的大小比progress大的时候 为了让cursorDrawable显示完全 需要将图片半径统一缩小abc
        if (cursorDrawable != null) {
            int temp = (int) Math.sqrt(cursorDrawableWidth * cursorDrawableWidth + cursorDrawableHeight * cursorDrawableHeight);
            if (temp > progressWidth) {
                abc = (temp - progressWidth) / 2;
            }
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(bgBorderColor);
        if(bgBorderWidth > 0){
            this.paint.setStrokeWidth(bgBorderWidth);
            float borderRadio_long = getMeasuredHeight() < getMeasuredWidth() ? getMeasuredHeight() / 2 : getMeasuredWidth() / 2 - bgBorderWidth / 2 - abc;
            canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, borderRadio_long, this.paint);
        }

        Bitmap bBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas bCanvas = new Canvas(bBitmap);
        if(null == bgBitmap){
            bgBitmap = getBgBitmap();
        }
        paint.setColor(Color.parseColor("#000000"));
        this.paint.setStrokeWidth(progressWidth - bgBorderWidth * 2);
        float bgRadio = getMeasuredHeight() < getMeasuredWidth() ? getMeasuredHeight() / 2 : getMeasuredWidth() / 2 - progressWidth / 2 - abc;
        bCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, bgRadio, this.paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        bCanvas.drawBitmap(bgBitmap,null,new Rect(abc+bgBorderWidth,abc+bgBorderWidth,getMeasuredWidth()-abc-bgBorderWidth,getMeasuredHeight()-abc-bgBorderWidth),paint);
        paint.setXfermode(null);
        canvas.drawBitmap(bBitmap,0,0,null);

        if(bgBorderWidth > 0){
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(bgBorderColor);
            this.paint.setStrokeWidth(bgBorderWidth);
            float borderRadio_shot = getMeasuredHeight() < getMeasuredWidth() ? getMeasuredHeight() / 2 : getMeasuredWidth() / 2 - (progressWidth - bgBorderWidth / 2) - abc;
            canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, borderRadio_shot, this.paint);
        }

        paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(progressWidth-bgBorderWidth*2);
        int r = (getMeasuredHeight() < getMeasuredWidth() ? getMeasuredHeight() : getMeasuredWidth()) / 2 - progressWidth / 2 - abc;
        float sweepAngle = (startLeft == 1 ? 1 : -1) * (durProgress / maxProgress * 360);
        Bitmap durBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas durCanvas = new Canvas(durBitmap);
        if(null == fontBitmap){
            fontBitmap = getFontBitmap();
        }

        durCanvas.drawArc(new RectF(getMeasuredWidth()/2-r,getMeasuredHeight()/2-r,getMeasuredWidth()/2+r,getMeasuredHeight()/2+r),startAngle,sweepAngle,false,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        durCanvas.drawBitmap(fontBitmap,null,new Rect(abc+bgBorderWidth,abc+bgBorderWidth,getMeasuredWidth()-abc-bgBorderWidth,getMeasuredHeight()-abc-bgBorderWidth),paint);
        paint.setXfermode(null);
        canvas.drawBitmap(durBitmap,0,0,null);

        if (null != cursorDrawable) {
            float cursorDrawableX = (float) (getMeasuredWidth() / 2 + r * Math.cos((startAngle + sweepAngle) / 180 * Math.PI));
            float cursorDrawableY = (float) (getMeasuredHeight() / 2 + r * Math.sin((startAngle + sweepAngle) / 180 * Math.PI));
            Rect cursorDrawableRect = new Rect((int) (cursorDrawableX - cursorDrawableWidth / 2), (int) (cursorDrawableY - cursorDrawableHeight / 2), (int) (cursorDrawableX + cursorDrawableWidth / 2), (int) (cursorDrawableY + cursorDrawableHeight / 2));
            canvas.drawBitmap(getCursortoBitmap(), null, cursorDrawableRect, paint);
        }

        if (durProgress != durProgressFinal) {
            if (durProgress > durProgressFinal) {
                durProgress -= speed;
                if (durProgress < durProgressFinal)
                    durProgress = durProgressFinal;
            } else {
                durProgress += speed;
                if (durProgress > durProgressFinal)
                    durProgress = durProgressFinal;
            }
            invalidate();
        }

        if(ringProgressListener != null){
            ringProgressListener.durProgressChange(durProgress);
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void refreshDurProgress(float durProgress) {
        this.durProgress = durProgress;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    private Bitmap getBitmap(Drawable cursorD, Rect rect) {
        Bitmap bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        cursorD.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        cursorD.draw(canvas);
        return bitmap;
    }

    private Bitmap getCursortoBitmap() {
        if (cursorBitmap == null) {
            cursorBitmap = getBitmap(cursorDrawable.getCurrent(), new Rect(0, 0, cursorDrawableWidth, cursorDrawableHeight));
        }
        Matrix m = new Matrix();
        float orientationDegree = 360 * durProgress / maxProgress;
        m.setRotate(orientationDegree, cursorDrawableWidth / 2, cursorDrawableHeight / 2);

        Bitmap bm1 = Bitmap.createBitmap(cursorBitmap.getHeight(), cursorBitmap.getWidth(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(cursorBitmap, m, paint);

        return bm1;
    }

    private Bitmap getFontBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),Bitmap.Config.ARGB_8888);
        Canvas fontCanvas = new Canvas(bitmap);
        fontDrawable.setBounds(0,0,bitmap.getWidth(),bitmap.getHeight());
        fontDrawable.draw(fontCanvas);
        return bitmap;
    }

    private Bitmap getBgBitmap(){
        Bitmap bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),Bitmap.Config.ARGB_8888);
        Canvas bgCanvas = new Canvas(bitmap);
        bgDrawable.setBounds(0,0,bitmap.getWidth(),bitmap.getHeight());
        bgDrawable.draw(bgCanvas);
        return bitmap;
    }

    ///////////////////////////////////////////////属性修改/////////////////////////////////////
    private OnRingProgressListener ringProgressListener;

    public void setRingProgressListener(OnRingProgressListener ringProgressListener) {
        this.ringProgressListener = ringProgressListener;
    }

    public float getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    public float getDurProgress() {
        return durProgress;
    }

    public void setDurProgress(float dur) {
        if (dur < 0) {
            dur = 0;
        } else if (dur > maxProgress) {
            dur = maxProgress;
        }
        durProgressFinal = dur;
        durProgress = durProgressFinal;
        refreshDurProgress(durProgress);
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        if (speed > 0)
            this.speed = speed;
        else throw new RuntimeException("speed must > 0");
    }

    public void setDurProgressWithAnim(float dur) {
        if (dur < 0) {
            dur = 0;
        } else if (dur > maxProgress) {
            dur = maxProgress;
        }
        durProgressFinal = dur;
        refreshDurProgress(durProgress);
    }

    public void setFontDrawable(Drawable fontDrawable) {
        this.fontDrawable = fontDrawable;
        fontBitmap = null;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    public void setBgDrawable(Drawable bgDrawable) {
        this.bgDrawable = bgDrawable;
        bgBitmap = null;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    public void setBgBorderColor(int bgBorderColor) {
        this.bgBorderColor = bgBorderColor;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    public void setBgBorderWidth(int bgBorderWidth) {
        this.bgBorderWidth = bgBorderWidth;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    public void setStartLeft(int startLeft) {
        this.startLeft = startLeft;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    public void setProgressWidth(int progressWidth) {
        this.progressWidth = progressWidth;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    public void setCursorDrawable(StateListDrawable cursorDrawable) {
        this.cursorDrawable = cursorDrawable;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    public void setCursorDrawableWidth(int cursorDrawableWidth) {
        this.cursorDrawableWidth = cursorDrawableWidth;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    public void setCursorDrawableHeight(int cursorDrawableHeight) {
        this.cursorDrawableHeight = cursorDrawableHeight;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    public float getDurProgressFinal() {
        return durProgressFinal;
    }

    public Drawable getFontDrawable() {
        return fontDrawable;
    }

    public Drawable getBgDrawable() {
        return bgDrawable;
    }

    public int getBgBorderColor() {
        return bgBorderColor;
    }

    public int getBgBorderWidth() {
        return bgBorderWidth;
    }

    public int getStartLeft() {
        return startLeft;
    }

    public int getProgressWidth() {
        return progressWidth;
    }

    public StateListDrawable getCursorDrawable() {
        return cursorDrawable;
    }

    public Bitmap getCursorBitmap() {
        return cursorBitmap;
    }

    public int getCursorDrawableWidth() {
        return cursorDrawableWidth;
    }

    public int getCursorDrawableHeight() {
        return cursorDrawableHeight;
    }

    public int getStartAngle() {
        return startAngle;
    }
}