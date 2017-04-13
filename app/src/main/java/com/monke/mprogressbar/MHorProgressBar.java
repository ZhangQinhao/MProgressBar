package com.monke.mprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Monke on 2016/10/7.
 */
public class MHorProgressBar extends View {

    private Boolean canTouch = true;
    private float speed = 1;   //如果设置当前进度使用动画  动画速度

    private float maxProgress = 100;
    private float durProgress = 0;
    private float durProgressFinal = 0;   //如果是动画 这个则为动画目标值

    private Drawable fontDrawable;
    private int fontDrawableType = 0;
    private BitmapShader fontShader;

    private Drawable bgDrawable;
    private int bgDrawableType = 0;
    private BitmapShader bgShader;

    private int bgBorderColor = 0x00FFFFFF;
    private int bgBorderWidth = 0;

    private int radius = 0;

    private int startLeft = 0;    //0从左开始到右    1进度从右到左

    private int progressWidth = -1;  //进度条宽度  -1默认填充全部

    private StateListDrawable cursorDrawable;
    private int cursorDrawableWidth = dip2px(15);  //游标图标默认宽度
    private int cursorDrawableHeight = dip2px(15); //游标图标默认高度

    private RectF rectFFont;
    private RectF rectFBg;

    private Paint bgBorderPaint;
    private Paint bgPaint;
    private Paint fontPaint;

    private Handler handler;

    public MHorProgressBar(Context context) {
        this(context, null);
    }

    public MHorProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MHorProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
        handler = new Handler(Looper.getMainLooper());

        bgBorderPaint = new Paint();
        bgBorderPaint.setFilterBitmap(true);
        bgBorderPaint.setAntiAlias(true);
        bgBorderPaint.setStyle(Paint.Style.STROKE);

        bgPaint = new Paint();
        bgPaint.setFilterBitmap(true);
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setStrokeWidth(1);

        fontPaint = new Paint();
        fontPaint.setFilterBitmap(true);
        fontPaint.setAntiAlias(true);
        fontPaint.setStyle(Paint.Style.FILL);
        fontPaint.setStrokeWidth(1);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MProgressBar);
        canTouch = a.getBoolean(R.styleable.MProgressBar_cantouch, canTouch);
        bgBorderColor = a.getColor(R.styleable.MProgressBar_bgbordercolor, bgBorderColor);
        bgBorderWidth = a.getDimensionPixelSize(R.styleable.MProgressBar_bgborderwidth, bgBorderWidth);

        bgDrawable = a.getDrawable(R.styleable.MProgressBar_bgdrawable);
        if (bgDrawable == null) {
            bgDrawable = new ColorDrawable(0xFFC1C1C1);
        }
        bgDrawableType = a.getInt(R.styleable.MProgressBar_bgdrawable_type, bgDrawableType);

        fontDrawable = a.getDrawable(R.styleable.MProgressBar_fontdrawable);
        if (fontDrawable == null) {
            fontDrawable = new ColorDrawable(0xFF00CCFF);
        }
        fontDrawableType = a.getInt(R.styleable.MProgressBar_fontdrawable_type, fontDrawableType);

        maxProgress = a.getFloat(R.styleable.MProgressBar_maxprogress, maxProgress);
        durProgress = a.getFloat(R.styleable.MProgressBar_durprogress, durProgress);
        durProgressFinal = durProgress;
        radius = a.getDimensionPixelSize(R.styleable.MProgressBar_radius, radius);
        startLeft = a.getInt(R.styleable.MProgressBar_startLeftOrRight, startLeft);
        progressWidth = a.getDimensionPixelSize(R.styleable.MProgressBar_progresswidth, progressWidth);
        try {
            if (a.getDrawable(R.styleable.MProgressBar_cursordrawable) != null) {
                if (a.getDrawable(R.styleable.MProgressBar_cursordrawable) instanceof StateListDrawable) {
                    cursorDrawable = (StateListDrawable) a.getDrawable(R.styleable.MProgressBar_cursordrawable);
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (0 >= progressWidth) {
            if (getMeasuredHeight() - bgBorderWidth * 2 <= 0) {
                throw new RuntimeException("bgBorderWidth超过绘制限度");
            }
            progressWidth = getMeasuredHeight();
        } else {
            if (progressWidth - bgBorderWidth * 2 <= 0) {
                throw new RuntimeException("bgBorderWidth超过绘制限度");
            }
        }

        float bx = bgBorderWidth;
        float bxf = getMeasuredWidth() - bgBorderWidth;

        float fx;
        float fxf;
        if (null != cursorDrawable && cursorDrawableWidth / 2 > bgBorderWidth) {
            bx = cursorDrawableWidth / 2;
            bxf = getMeasuredWidth() - cursorDrawableWidth / 2;
            if (0 == startLeft) {
                fx = cursorDrawableWidth / 2;
                fxf = (getMeasuredWidth() - cursorDrawableWidth) * (durProgress / maxProgress) + cursorDrawableWidth / 2;
            } else {
                fx = (getMeasuredWidth() - cursorDrawableWidth) * ((maxProgress - durProgress) / maxProgress) + cursorDrawableWidth / 2;
                fxf = getMeasuredWidth() - cursorDrawableWidth / 2;
            }
        } else {
            if (0 == startLeft) {
                fx = bgBorderWidth;
                fxf = (getMeasuredWidth() - bgBorderWidth * 2) * (durProgress / maxProgress) + bgBorderWidth;
            } else {
                fx = (getMeasuredWidth() - bgBorderWidth * 2) * ((maxProgress - durProgress) / maxProgress) + bgBorderWidth;
                fxf = getMeasuredWidth() - bgBorderWidth;
            }
        }

        //////////////////////////////////////边框绘制///////////////////////////////////
        if (bgBorderWidth > 0) {
            bgBorderPaint.setColor(bgBorderColor);
            bgBorderPaint.setStrokeWidth(bgBorderWidth);
            canvas.drawRoundRect(new RectF(bx - bgBorderWidth / 2, getMeasuredHeight() / 2 - progressWidth / 2 + bgBorderWidth / 2, bxf + bgBorderWidth / 2, getMeasuredHeight() / 2 + progressWidth / 2 - bgBorderWidth / 2), radius - bgBorderWidth / 2, radius - bgBorderWidth / 2, bgBorderPaint);
        }
        //////////////////////////////////////BG绘制///////////////////////////////////////
        rectFBg = new RectF(bx, getMeasuredHeight() / 2 - progressWidth / 2 + bgBorderWidth, bxf, getMeasuredHeight() / 2 + progressWidth / 2 - bgBorderWidth);
        if (bgDrawable instanceof ColorDrawable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            bgPaint.setColor(((ColorDrawable) bgDrawable).getColor());
            canvas.drawRoundRect(rectFBg, radius - bgBorderWidth, radius - bgBorderWidth, bgPaint);
        } else {
            if (bgDrawableType == 0) {
                Bitmap durBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas durCanvas = new Canvas(durBitmap);
                bgPaint.setColor(Color.parseColor("#ff000000"));
                durCanvas.drawRoundRect(rectFBg, radius - bgBorderWidth, radius - bgBorderWidth, bgPaint);
                bgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                durCanvas.drawBitmap(toBgBitmapNormal(rectFBg), 0, 0, bgPaint);
                bgPaint.setXfermode(null);
                canvas.drawBitmap(durBitmap, 0, 0, null);
            } else {
                if (null == bgShader) {
                    updateBgShader(rectFBg);
                }
                bgPaint.setShader(bgShader);
                canvas.drawRoundRect(rectFBg, radius - bgBorderWidth, radius - bgBorderWidth, bgPaint);
                bgPaint.setShader(null);
            }
        }
        ////////////////////////////////////////////Font绘制///////////////////////////////
        Bitmap durBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas durCanvas = new Canvas(durBitmap);
        fontPaint.setColor(Color.parseColor("#ff000000"));
        durCanvas.drawRoundRect(rectFBg, radius - bgBorderWidth, radius - bgBorderWidth, fontPaint);

        rectFFont = new RectF(fx, getMeasuredHeight() / 2 - progressWidth / 2 + bgBorderWidth, fxf, getMeasuredHeight() / 2 + progressWidth / 2 - bgBorderWidth);
        fontPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        if (fontDrawable instanceof ColorDrawable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Paint bP = new Paint();
            Bitmap bB = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas bC = new Canvas(bB);
            bP.setColor(((ColorDrawable) fontDrawable).getColor());
            bC.drawRect(rectFFont, bP);

            durCanvas.drawBitmap(bB, 0, 0, fontPaint);
        } else {
            if (fontDrawableType == 0) {
                durCanvas.drawBitmap(toFontBitmapNormal(rectFFont), 0, 0, fontPaint);
            } else if (fontDrawableType == 2) {
                durCanvas.drawBitmap(toFontBitmapCover(rectFFont), 0, 0, fontPaint);
            } else {
                if (null == fontShader) {
                    updateFontShader(rectFFont);
                }
                Bitmap bitmap = Bitmap.createBitmap(getMeasuredWidth(),getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas1 = new Canvas(bitmap);
                Paint paint1 = new Paint();
                paint1.setShader(fontShader);
                canvas1.drawRect(rectFFont, paint1);
                paint1.setShader(null);

                durCanvas.drawBitmap(bitmap,0,0,fontPaint);
            }
        }
        fontPaint.setXfermode(null);

        canvas.drawBitmap(durBitmap, 0, 0, new Paint());
        ////////////////////////////////////绘制游标图标///////////////////////
        if (null != cursorDrawable) {
            Rect cursorDrawableRect = null;
            Drawable cursorD = cursorDrawable.getCurrent();
            if (0 == startLeft) {
                cursorDrawableRect = new Rect((int) (fxf - cursorDrawableWidth / 2), (int) (getMeasuredHeight() / 2 - cursorDrawableHeight / 2), (int) (fxf + cursorDrawableWidth / 2), (int) (getMeasuredHeight() / 2 + cursorDrawableHeight / 2));
            } else {
                cursorDrawableRect = new Rect((int) (fx - cursorDrawableWidth / 2), (int) (getMeasuredHeight() / 2 - cursorDrawableHeight / 2), (int) (fx + cursorDrawableWidth / 2), (int) (getMeasuredHeight() / 2 + cursorDrawableHeight / 2));
            }
            cursorD.setBounds(cursorDrawableRect);
            cursorD.draw(canvas);
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

        if (progressListener != null)
            progressListener.durProgressChange(durProgress);
    }

    private void updateBgShader(RectF rectF) {
        bgShader = new BitmapShader(toBgBitmap(rectF), Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
    }

    private Bitmap toBgBitmap(RectF rectF) {
        Bitmap bitmap = null;
        if (bgDrawable.getIntrinsicWidth() > 0 && bgDrawable.getIntrinsicHeight() > 0) {
            bitmap = Bitmap.createBitmap((int) (rectF.height() * 1.0f * bgDrawable.getIntrinsicWidth() / bgDrawable.getIntrinsicHeight()), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap((int) rectF.height(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        if (-1 == progressWidth) {
            bgDrawable.setBounds(0, bgBorderWidth, bitmap.getWidth(), bitmap.getHeight() - bgBorderWidth);
        } else {
            bgDrawable.setBounds(0, bitmap.getHeight() / 2 - progressWidth / 2 + bgBorderWidth, bitmap.getWidth(), getHeight() / 2 + progressWidth / 2 - bgBorderWidth);
        }
        bgDrawable.draw(canvas);
        return bitmap;
    }

    private Bitmap toBgBitmapNormal(RectF rectF) {
        Bitmap bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        bgDrawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
        bgDrawable.draw(canvas);
        return bitmap;
    }


    /////////////////////////////////////////////////////////////////
    private void updateFontShader(RectF rectF) {
        fontShader = new BitmapShader(toFontBitmap(rectF), Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
    }

    private Bitmap toFontBitmap(RectF rectF) {
        Bitmap bitmap = null;
        if (fontDrawable.getIntrinsicWidth() > 0 && fontDrawable.getIntrinsicHeight() > 0) {
            bitmap = Bitmap.createBitmap((int) (rectF.height() * 1.0f * fontDrawable.getIntrinsicWidth() / fontDrawable.getIntrinsicHeight()), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap((int) rectF.height(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        fontDrawable.setBounds(0, bitmap.getHeight() / 2 - progressWidth / 2 + bgBorderWidth, bitmap.getWidth(), getHeight() / 2 + progressWidth / 2 - bgBorderWidth);
        fontDrawable.draw(canvas);
        return bitmap;
    }

    private Bitmap toFontBitmapNormal(RectF rectF) {
        Bitmap bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        fontDrawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
        fontDrawable.draw(canvas);
        return bitmap;
    }

    private Bitmap toFontBitmapCover(RectF rectF) {
        Bitmap bitmap = null;
        if (fontDrawable.getIntrinsicWidth() > 0 && fontDrawable.getIntrinsicHeight() > 0) {
            bitmap = Bitmap.createBitmap(fontDrawable.getIntrinsicWidth(), fontDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap((int) rectF.height(), (int) rectF.height(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        fontDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        fontDrawable.draw(canvas);

        Bitmap result = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas resultCanvas = new Canvas(result);
        Rect a = null;
        if (0 == startLeft) {
            a = new Rect(0, 0, (int) (bitmap.getWidth() * durProgress / maxProgress), bitmap.getHeight());
        } else {
            a = new Rect((int) (bitmap.getWidth() * (maxProgress - durProgress) / maxProgress), 0, bitmap.getWidth(), bitmap.getHeight());
        }
        resultCanvas.drawBitmap(bitmap, a, rectF, new Paint());

        return result;
    }

    private void refreshDurProgress(float durProgress) {
        this.durProgress = durProgress;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (canTouch) {
            int action = event.getAction();
            float x = event.getX();
            x = x - (null == cursorDrawable ? 0 : cursorDrawableWidth / 2);
            int progressWidth = null == cursorDrawable ? getMeasuredWidth() : getMeasuredWidth() - cursorDrawableWidth;

            if (x < 0) {
                x = 0;
            } else if (x > progressWidth) {
                x = progressWidth;
            }

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (null != cursorDrawable) {
                        cursorDrawable.setState(new int[]{android.R.attr.state_pressed});
                    }
                    if (startLeft == 0) {
                        durProgressFinal = x / progressWidth * maxProgress;
                        refreshDurProgress(durProgressFinal);
                    } else {
                        durProgressFinal = (progressWidth - x) / progressWidth * maxProgress;
                        refreshDurProgress(durProgressFinal);
                    }
                    if (progressListener != null)
                        progressListener.moveStartProgress(durProgress);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (startLeft == 0) {
                        durProgressFinal = x / progressWidth * maxProgress;
                        refreshDurProgress(durProgressFinal);
                    } else {
                        durProgressFinal = (progressWidth - x) / progressWidth * maxProgress;
                        refreshDurProgress(durProgressFinal);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (null != cursorDrawable) {
                        cursorDrawable.setState(new int[]{});
                    }
                    if (startLeft == 0) {
                        durProgressFinal = x / progressWidth * maxProgress;
                        refreshDurProgress(durProgressFinal);
                    } else {
                        durProgressFinal = (progressWidth - x) / progressWidth * maxProgress;
                        refreshDurProgress(durProgressFinal);
                    }
                    if (progressListener != null)
                        progressListener.moveStopProgress(durProgress);
                    break;
            }
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //////////////////////////////属性修改///////////////////////////
    private OnProgressListener progressListener;

    public void setProgressListener(OnProgressListener progressListener) {
        this.progressListener = progressListener;
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
        if (progressListener != null) {
            final float finalDurProgress = dur;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressListener.setDurProgress(finalDurProgress);
                }
            });
        }
    }

    public void setDurProgressWithAnim(float dur) {
        if (dur < 0) {
            dur = 0;
        } else if (dur > maxProgress) {
            dur = maxProgress;
        }
        durProgressFinal = dur;
        refreshDurProgress(durProgress);
        if (progressListener != null) {
            final float finalDurProgress = dur;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressListener.setDurProgress(finalDurProgress);
                }
            });
        }
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        if (speed > 0)
            this.speed = speed;
        else throw new RuntimeException("speed must > 0");
    }

    public Boolean getCanTouch() {
        return canTouch;
    }

    public void setCanTouch(Boolean canTouch) {
        this.canTouch = canTouch;
    }

    public void setFontDrawable(@NonNull Drawable fontDrawable) {
        this.fontDrawable = fontDrawable;
        fontShader = null;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    public void setFontDrawableType(int fontDrawableType) {
        this.fontDrawableType = fontDrawableType;
        fontShader = null;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    public void setBgDrawable(@NonNull Drawable bgDrawable) {
        this.bgDrawable = bgDrawable;
        bgShader = null;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    public void setBgDrawableType(int bgDrawableType) {
        this.bgDrawableType = bgDrawableType;
        bgShader = null;
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

    public void setRadius(int radius) {
        this.radius = radius;
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

    public void setProgressWidth(int progressWidth) {
        this.progressWidth = progressWidth;
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

    public int getRadius() {
        return radius;
    }

    public int getStartLeft() {
        return startLeft;
    }

    public int getProgressWidth() {
        return progressWidth;
    }

    public void setCursorDrawableHeight(int cursorDrawableHeight) {
        this.cursorDrawableHeight = cursorDrawableHeight;
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

    public void setCursorDrawable(StateListDrawable cursorDrawable) {
        this.cursorDrawable = cursorDrawable;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
    }

    public StateListDrawable getCursorDrawable() {
        return cursorDrawable;
    }

    public int getCursorDrawableWidth() {
        return cursorDrawableWidth;
    }

    public int getCursorDrawableHeight() {
        return cursorDrawableHeight;
    }

    public int getRealProgressWidth(){
        return (int) rectFBg.width();
    }


}