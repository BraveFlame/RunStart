package com.runstart.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;


/**
 * Created by zhonghao.song on 17-9-28.
 * 用来保存绘制的进度仪表盘
 */

public class DrawingCanvas extends Canvas {

    private Bitmap outputBitmap;

    private DrawingCanvas(Bitmap outputBitmap_, float width_, float height_) {
        super(outputBitmap_);
        outputBitmap = outputBitmap_;
    }

    public static DrawingCanvas instance(float width_, float height_) {
        Bitmap bitmap = Bitmap.createBitmap((int) width_, (int) height_, Bitmap.Config.ARGB_8888);

        return new DrawingCanvas(bitmap, width_, height_);
    }

    public Bitmap getOutput() {
        return outputBitmap;
    }


}
