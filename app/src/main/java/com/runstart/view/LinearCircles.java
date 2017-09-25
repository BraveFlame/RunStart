package com.runstart.view;

/**
 * Created by user on 17-9-5.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.runstart.R;

/**
 * 需要费脑的地方：
 * 1.绘制多条线段组成弧形
 * 2.指针跟随着手指方向且长度确定
 * 3.指针的指向不能越过仪表盘
 */
public class LinearCircles extends View {

    private int width;
    private int height;
    /**
     * 画步数的数值的字体大小
     */
    private float numberTextSize = 0;


    private Paint linePaint;//线段画笔
    private Paint nowPaint;
    private Paint vTextPaint;


    private Path outerCirclePath;//外层圆的Path
    private Path innerCirclePath;//内层圆的Path
    private Path iouterCirclePath;//外层圆的Path
    private Path iinnerCirclePath;//内层圆的Path

    private String type = "";
    public float[] trace = new float[205];


    private RectF outRectF;//用于绘制外层圆
    private RectF innerRectF;//用于绘制内层圆
    private RectF iOutRectF;
    private RectF iInnerRectF;

    private int count = 50;//画count根线
    private float systemX = 1;//getContext().getResources().getDisplayMetrics().xdpi/480;
    private int outerR = (int) (300 * systemX);//外部圆环的半径
    private int innerR = (int) (outerR * 0.77f);//内部圆环的半径

    private int outerRR = (int) (innerR * 0.95f);//内外部圆环的半径
    private int innerRR = (int) (outerRR * 0.90f);//内内部圆环的半径

    private int shortageAngle = 125;//缺失的部分的角度
    private int startAngle;//开始的角度
    private int sweepAngle;//扫过的角度


    public float pace = 0f;

    private StringBuilder unitTx=new StringBuilder("");

    public LinearCircles(Context context) {
        super(context);
        initPaint();
        initAngle();
        drawOuterCircle();
        drawInnerCircle();
        drawiInnerCircle();
        drawiOuterCircle();

    }

    public LinearCircles(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        initAngle();
        drawOuterCircle();
        drawInnerCircle();
        drawiInnerCircle();
        drawiOuterCircle();
    }

    public LinearCircles(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        initAngle();
        drawOuterCircle();
        drawInnerCircle();
        drawiInnerCircle();
        drawiOuterCircle();
    }

    public void setXY(float x) {

        float systemX = getContext().getResources().getDisplayMetrics().xdpi;
        outerR = (int) (x * systemX / 480);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        //让指针一开始指向正上方
        drawInnerCircle();
        float init[] = new float[2];
        PathMeasure inMeasure = new PathMeasure(innerCirclePath, false);
        inMeasure.getPosTan(0, init, null);

    }


    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        canvas.translate(width / 2, height / 2);
        drawLine(canvas);
        showPaces(canvas, pace);
        drawTextNumber(canvas);
    }

    /**
     * 外层圆圈
     */
    private void drawOuterCircle() {
        //一般绘制圆圈的方法，不做介绍了
        outerCirclePath = new Path();
        if (outRectF == null) {
            outRectF = new RectF(-outerR, -outerR, outerR, outerR);
        }
        outerCirclePath.addArc(outRectF, startAngle, sweepAngle);
    }

    /**
     * 内层圆圈
     */
    private void drawInnerCircle() {
        //一般绘制圆圈的方法，不做介绍了
        innerCirclePath = new Path();
        if (innerRectF == null) {
            innerRectF = new RectF(-innerR, -innerR, innerR, innerR);
        }
        innerCirclePath.addArc(innerRectF, startAngle, sweepAngle);
    }


    /**
     * nei外层圆圈
     */
    private void drawiOuterCircle() {
        //一般绘制圆圈的方法，不做介绍了
        iouterCirclePath = new Path();
        if (iOutRectF == null) {
            iOutRectF = new RectF(-outerRR, -outerRR, outerRR, outerRR);
        }
        iouterCirclePath.addArc(iOutRectF, startAngle, sweepAngle);
    }

    /**
     * nei内层圆圈
     */
    private void drawiInnerCircle() {
        //一般绘制圆圈的方法，不做介绍了
        iinnerCirclePath = new Path();
        if (iInnerRectF == null) {
            iInnerRectF = new RectF(-innerRR, -innerRR, innerRR, innerRR);
        }
        iinnerCirclePath.addArc(iInnerRectF, startAngle, sweepAngle);
    }


    /**
     * 画直线，组成一个类似于弧形的形状
     *
     * @param canvas
     */
    public void drawLine(Canvas canvas) {
        Path nowLine, iNowPath;

        nowLine = new Path();

        iNowPath = new Path();
        //用于外层圆的测量
        PathMeasure outMeasure = new PathMeasure(outerCirclePath, false);
        float outlength = outMeasure.getLength();
        float[] outPos = new float[2];

        //用于内层圆的测量
        PathMeasure inMeasure = new PathMeasure(innerCirclePath, false);
        float inlength = inMeasure.getLength();
        float[] inPos = new float[2];


        //用于内外层圆的测量
        PathMeasure ioutMeasure = new PathMeasure(iouterCirclePath, false);
        float ioutlength = ioutMeasure.getLength();
        float[] ioutPos = new float[2];

        //用于内内层圆的测量
        PathMeasure iinMeasure = new PathMeasure(iinnerCirclePath, false);
        float iinlength = iinMeasure.getLength();
        float[] iinPos = new float[2];


        int j = 0;
        //用来画多条线段，组成弧形
        for (int i = 0; i <= count; i++) {
            //外层圆当前的弧长
            float outNowLength = outlength * i / (count * 1.0f);
            //当前弧长下对应的坐标outPos
            outMeasure.getPosTan(outNowLength, outPos, null);

            //内层圆当前的弧长
            float inNowLength = inlength * i / (count * 1.0f);
            //当前弧长下对应的坐标inPos
            inMeasure.getPosTan(inNowLength, inPos, null);


            //nei外层圆当前的弧长
            float ioutNowLength = ioutlength * i / (count * 1.0f);
            //当前弧长下对应的坐标outPos
            ioutMeasure.getPosTan(ioutNowLength, ioutPos, null);

            //nei内层圆当前的弧长
            float iinNowLength = iinlength * i / (count * 1.0f);
            //当前弧长下对应的坐标inPos
            iinMeasure.getPosTan(iinNowLength, iinPos, null);
            /**
             * 位置左上为负值
             */
            if (i % 5 == 0) {
                if (i < 10)
                    canvas.drawText("0" + i, iinPos[0] + 6.0f, iinPos[1] + 12.21f, nowPaint);
                else if (i == 10)
                    canvas.drawText(i + "", iinPos[0] + 11.06f, iinPos[1] + 21.02f, nowPaint);
                else if (i == 15)
                    canvas.drawText(i + "", iinPos[0], iinPos[1] + 31.04f, nowPaint);
                else if (i == 20)
                    canvas.drawText(i + "", iinPos[0] - 11.0f, iinPos[1] + 33.04f, nowPaint);
                else if (i == 25)
                    canvas.drawText(i + "", iinPos[0] - 16.0f, iinPos[1] + 33.04f, nowPaint);
                else if (i == 30)
                    canvas.drawText(i + "", iinPos[0] - 24.04f, iinPos[1] + 33.0f, nowPaint);
                else if (i == 35)
                    canvas.drawText(i + "", iinPos[0] - 36.04f, iinPos[1] + 27.0f, nowPaint);
                else if (i == 40)
                    canvas.drawText(i + "", iinPos[0] - 43.04f, iinPos[1] + 20.04f, nowPaint);
                else if (i == 45)
                    canvas.drawText(i + "", iinPos[0] - 43.04f, iinPos[1] + 16.04f, nowPaint);
                else
                    canvas.drawText(i + "", iinPos[0] - 43.04f, iinPos[1] + 15.0f, nowPaint);

            }
//
            //moveTo到内层圆弧上的点
            nowLine.moveTo(outPos[0], outPos[1]);
            //lineTo到外层圆弧上的点
            nowLine.lineTo(inPos[0], inPos[1]);
            canvas.drawPath(nowLine, linePaint);

            j = i * 4;
            trace[j] = outPos[0];
            trace[j + 1] = outPos[1];
            trace[j + 2] = inPos[0];
            trace[j + 3] = inPos[1];

            iNowPath.moveTo(ioutPos[0], ioutPos[1]);
            iNowPath.lineTo(iinPos[0], iinPos[1]);
            canvas.drawPath(iNowPath, linePaint);


        }
    }


    /**
     * 3.圆环中心的步数
     */
    private void drawTextNumber(Canvas canvas) {
        setTextSize(pace);
        vTextPaint.setTextSize(numberTextSize);
        vTextPaint.setColor(getContext().getColor(R.color.calor_now_circle));
        if (type.equals("pace")) {
            canvas.drawText("" + (int) pace, 0, 40.0f * systemX, vTextPaint);
            vTextPaint.setTextSize(dipToPx(16));
            vTextPaint.setColor(getContext().getColor(R.color.calor_total_circle));
            canvas.drawText(unitTx.toString(), 0, -75 * systemX, vTextPaint);
        } else {
            canvas.drawText("" + pace, 0, 40.0f * systemX, vTextPaint);
            vTextPaint.setTextSize(dipToPx(16));
            vTextPaint.setColor(getContext().getColor(R.color.calor_total_circle));
            canvas.drawText(unitTx.toString(), 0, -75 * systemX, vTextPaint);

        }
    }


    /**
     * 设置文本大小,防止步数特别大之后放不下，将字体大小动态设置
     *
     * @param num
     */
    public void setTextSize(float num) {
        String s = String.valueOf(num);
        int length = s.length();
        if (length <= 4) {
            numberTextSize = dipToPx(40);
        } else if (length > 4 && length <= 6) {
            numberTextSize = dipToPx(30);
        } else if (length > 6 && length <= 8) {
            numberTextSize = dipToPx(25);
        } else if (length > 8) {
            numberTextSize = dipToPx(20);
        }
    }


    /**
     * dip 转换成px
     *
     * @param dip
     * @return
     */

    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    public void showPaces(Canvas canvas, float pace) {
        Path nowLine;
        nowLine = new Path();
        if (pace > 5000) {
            pace = pace / 1000;
            unitTx.replace(0,unitTx.length(),"1k*");
        } else if (pace > 500) {
            pace = pace / 100;
            unitTx.replace(0,unitTx.length(),"0.1k*");
        } else if (pace > 50) {
            pace = pace / 10;
            unitTx.replace(0,unitTx.length(),"10*");
        } else {
            unitTx.replace(0,unitTx.length(),"1*");
        }
        if(type.equals("pace")){
            unitTx.append("s");
        }else unitTx.append("m");
        for (int i = 0; i <= pace; i++) {

                //moveTo到内层圆弧上的点
                nowLine.moveTo(trace[4 * i], trace[4 * i + 1]);
                //lineTo到外层圆弧上的点
                nowLine.lineTo(trace[4 * i + 2], trace[4 * i + 3]);
                canvas.drawPath(nowLine, nowPaint);


        }

    }


    public void show(float i, String type) {
        this.type = type;
          pace=i;
        invalidate();
    }


    /**
     * 初始化画笔
     */
    private void initPaint() {

        if (linePaint == null) {
            linePaint = new Paint();
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setStrokeWidth(3 * systemX);
            // linePaint.setColor(0xff1d8ffe);
            linePaint.setColor(getContext().getColor(R.color.calor_total_circle));
        }
        if (nowPaint == null) {
            nowPaint = new Paint();
            nowPaint.setStyle(Paint.Style.STROKE);
            nowPaint.setStrokeWidth(3);
            nowPaint.setTextSize(28);
            nowPaint.setColor(getContext().getColor(R.color.calor_now_circle));
        }
        if (vTextPaint == null) {
            vTextPaint = new Paint();
            vTextPaint.setTextAlign(Paint.Align.CENTER);
            vTextPaint.setAntiAlias(true);//抗锯齿功能
            vTextPaint.setTextSize(numberTextSize);
            Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
            vTextPaint.setTypeface(font);//字体风格
            vTextPaint.setColor(getContext().getColor(R.color.pace_show));
        }


    }

    /**
     * 根据shortageAngle来调整圆弧的角度
     */
    private void initAngle() {
        sweepAngle = 360 - shortageAngle;
        startAngle = 90 + shortageAngle / 2;
    }
}