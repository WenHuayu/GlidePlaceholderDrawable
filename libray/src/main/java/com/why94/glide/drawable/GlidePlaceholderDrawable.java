package com.why94.glide.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

/**
 * Glide进行placeholder和image进行过渡时,使用普通的资源placeholder会变形
 * 而这个类对图片资源进行了处理,使过渡前后placeholder一致
 * Created by WenHuayu(why94@qq.com) on 2017/4/7.
 */
public class GlidePlaceholderDrawable extends Drawable {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float[] mMatrixValues = new float[9];
    private final int mWidth, mHeight;
    private final Bitmap mResource;

    public GlidePlaceholderDrawable(Resources res, @DrawableRes int resource) {
        this(BitmapFactory.decodeResource(res, resource));
    }

    public GlidePlaceholderDrawable(Bitmap resource) {
        this.mHeight = resource.getHeight();
        this.mWidth = resource.getWidth();
        this.mResource = resource;
    }

    @Override
    public int getMinimumHeight() {
        return mHeight;
    }

    @Override
    public int getMinimumWidth() {
        return mWidth;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        //canvas.getMatrix()这个方法已经@Deprecated了,但是这里要实现功能不得不用,缩放,位移啊,数据都在matrix里了
        Matrix matrix = canvas.getMatrix();
        matrix.getValues(mMatrixValues);
        //由于缩放的中心是在左上角,而不是图片中心,故需要再平衡一下因为缩放造成的位移
        mMatrixValues[Matrix.MTRANS_X] = ((canvas.getWidth() - mWidth) / 2 - mMatrixValues[Matrix.MTRANS_X]) / mMatrixValues[Matrix.MSCALE_X];
        mMatrixValues[Matrix.MTRANS_Y] = ((canvas.getHeight() - mHeight) / 2 - mMatrixValues[Matrix.MTRANS_Y]) / mMatrixValues[Matrix.MSCALE_Y];
        //尺寸反向缩放
        mMatrixValues[Matrix.MSCALE_X] = 1 / mMatrixValues[Matrix.MSCALE_X];
        mMatrixValues[Matrix.MSCALE_Y] = 1 / mMatrixValues[Matrix.MSCALE_Y];
        matrix.setValues(mMatrixValues);
        canvas.drawBitmap(mResource, matrix, mPaint);
    }

    @Override
    public void setAlpha(int i) {
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}