package com.runstart.help;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.ScaleAnimation;

/**
 * Created by user on 17-9-26.
 */

public class MyScaleAnimation extends ScaleAnimation {
    float xFrom,yFrom,xTo,yTo;


    public MyScaleAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScaleAnimation(float fromX, float toX, float fromY, float toY) {
        super(fromX, toX, fromY, toY);
        this.xFrom=fromX;
        this.xTo=toX;
        this.yFrom=fromY;
        this.yTo=toY;
    }

    public MyScaleAnimation(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY) {
        super(fromX, toX, fromY, toY, pivotX, pivotY);
    }

    public MyScaleAnimation(float fromX, float toX, float fromY, float toY, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        super(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue);
    }

    public void setScaleAnimation(){

    }
}
