package com.example.hwloadingview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HwLoadingView extends View {
    private int hwSize=200;
    private int hwColor= Color.GRAY;
    private int hwDuration=1500;//默认动画时长
    /**小圆数量*/
    private int circleCount=12;
    /**每个小圆间隔的角度*/
    private float circleDegree=360f/circleCount;
    /**所有小圆的颜色集合*/
    private int[] allCircleColors=new int[circleCount];//用透明度来改变
    /**所有小圆的半径集合*/
    private  float[] allCircleRadius=new float[circleCount];
    /**小圆最大半径*/
    private float maxCircleRadius;

    private ValueAnimator mAnimator;
    private int mAnimateValue = 0;

    private Paint circlePaint;


    public int getHwSize() {
        return hwSize;
    }

    public void setHwSize(int hwSize) {
        this.hwSize = hwSize;
    }

    public int getHwColor() {
        return hwColor;
    }

    public void setHwColor(int hwColor) {
        this.hwColor = hwColor;
    }

    public int getHwDuration() {
        return hwDuration;
    }

    public void setHwDuration(int hwDuration) {
        this.hwDuration = hwDuration;
    }

    public HwLoadingView(Context context) {
        this(context,null);
    }

    public HwLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HwLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.HwLoadingView);
        hwSize=typedArray.getDimensionPixelSize(R.styleable.HwLoadingView_hw_size,hwSize);
        hwColor=typedArray.getColor(R.styleable.HwLoadingView_hw_color,hwColor);
        hwDuration=typedArray.getDimensionPixelSize(R.styleable.HwLoadingView_hw_duration,hwDuration);
        typedArray.recycle();

        initPaint();
        initData();
    }

    private void initPaint() {
        circlePaint=new Paint();
        circlePaint.setColor(hwColor);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);
    }

    private void initData() {
        float minCircleRadius=hwSize/24;
        for (int i = 0; i < circleCount; i++) {
            switch (i){
                case 7:
                    allCircleColors[i]=(int) (255 * 0.7f);
                    allCircleRadius[i]=minCircleRadius * 1.25f;
                    break;
                case 8:
                    allCircleColors[i]=(int) (255 * 0.8f);
                    allCircleRadius[i]=minCircleRadius * 1.5f;
                    break;
                case 9:
                case 11:
                    allCircleColors[i]=(int) (255 * 0.9f);
                    allCircleRadius[i]=minCircleRadius * 1.75f;
                    break;
                case 10:
                    allCircleColors[i]=255;
                    allCircleRadius[i]=minCircleRadius * 2f;
                    break;
                default:
                    allCircleColors[i]=(int) (255 * 0.5f);
                    allCircleRadius[i]=minCircleRadius;
                    break;

            }
        }

        maxCircleRadius=minCircleRadius*2f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(hwSize,hwSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(hwSize>0){
            //以（hwSize/2，hwSize/2)为轴点旋转
            canvas.rotate(circleDegree*mAnimateValue,hwSize/2,hwSize/2);
        for (int i = 0; i < circleCount; i++) {
            canvas.drawCircle(hwSize/2,maxCircleRadius,allCircleRadius[i],circlePaint);
            canvas.rotate(circleDegree,hwSize/2,hwSize/2);
        }


        }
    }

    //开始动画
    private void startAnim(){
        if(mAnimator==null){
            mAnimator=ValueAnimator.ofInt(0,circleCount-1);
            mAnimator.setDuration(hwDuration);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.setRepeatMode(ValueAnimator.RESTART);
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.addUpdateListener(mUpdateListenr);
            mAnimator.start();
        }else if(!mAnimator.isStarted()){
            mAnimator.start();
        }
    }



    ValueAnimator.AnimatorUpdateListener mUpdateListenr=new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
              mAnimateValue= (int) animation.getAnimatedValue();
              invalidate();
        }
    };


    //停止动画
    private void stopAnim(){
        if(mAnimator!=null){
            mAnimator.removeAllUpdateListeners();
            mAnimator.cancel();
            mAnimator=null;
        }
    }



    /**
     * 根据View可见性变化开始/停止动画
     */
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            startAnim();
        } else {
            stopAnim();
        }
    }


}
