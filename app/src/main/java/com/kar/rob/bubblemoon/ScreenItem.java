package com.kar.rob.bubblemoon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Abstract class for items to display on the screen
 */
public abstract class ScreenItem extends View {

    private final static Paint mPainter = new Paint();
    protected int mScaledWidth;
    protected Bitmap mBitmapScaled;
    protected Bitmap mBitmap;

    protected int mScreenWidth, mScreenHeight;
    protected final int startSize;
    protected final float alpha;


    // location of the item,
    // NOTE mX and mY are at upper left corner of the bitmap
    protected float mX, mY, mRadius;
    protected long mRotation;

    /**
     * Instantiate a NumberView for a number
     *
     * @param context      the context the view is running in
     * @param aWidth       screen width
     * @param aHeight      screen height
     * @param aStartSize   the size to start with
     * @param anAlpha      the screen size factor
     * @param aBitmap      source bitmap for the number
     */
    public ScreenItem(Context context, int aWidth, int aHeight, int aStartSize, float anAlpha, Bitmap aBitmap) {
        super(context);

        mScreenWidth = aWidth;
        mScreenHeight = aHeight;
        startSize = aStartSize;
        alpha = anAlpha;
        mBitmap = aBitmap;
        mRotation = 0L;
        mPainter.setAntiAlias(true);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // Draw the ItemView at its current location

        // save the canvas
        canvas.save();

        // Rotate to mRotation degrees around the ItemView's center (not its position)
        canvas.rotate(mRotation, mX + mRadius, mY + mRadius);

        // draw the bitmap at its new location
        canvas.drawBitmap(mBitmapScaled, mX, mY, mPainter);

        // restore the canvas
        canvas.restore();
    }

    /**
     * Cancel the future movements
     */
    abstract void cancel();

    /**
     * Start the future movements
     */
    abstract void start(long initDelay);

    /**
     * Set the screen size
     * @param width     a screen width
     * @param height    a screen height
     */
    protected void setScreenSize(int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;
    }

}

