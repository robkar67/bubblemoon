package com.kar.rob.bubblemoon;

import android.content.Context;
import android.graphics.Bitmap;

/**
 / NumberItem displays a number bitmap as part of the score, the time or the level.
 */
public class NumberItem extends ScreenItem {

    /**
     * the type of different items that a NumberItem can be
     */
    public enum NUMBER_TYPE {
        TIME, LEVEL, SCORE
    }
    private final NUMBER_TYPE type;
    private int number;
    private final int position;   // 0 = least significant digit, 1 = second least significant digit, ...
    private final int TIME_SIZE = 48;
    private final int LEVEL_SIZE = TIME_SIZE + 14;
    private final int SCORE_SIZE = LEVEL_SIZE;

    /**
     * Instantiate a NumberItem with a number
     *
     * @param context      the context the view is running in
     * @param aType        NUMBER_TYPE SCORE, TIME or LEVEL
     * @param aNumber      the number to present
     * @param aPosition    0 = least significant digit, 1 = second least sign. digit
     * @param aWidth       screen width
     * @param aHeight      screen height
     * @param aBitmap      source bitmap for the number
     * @param aStartSize   the size to start with
     */
    public NumberItem(Context context, NUMBER_TYPE aType, int aNumber, int aPosition, int aWidth, int aHeight, Bitmap aBitmap, int aStartSize, float anAlpha) {
        super(context, aWidth, aHeight, aStartSize, anAlpha, aBitmap);

        type = aType;
        number = aNumber;
        position = aPosition;

        // Creates the bitmap for this item
        createBitmap();

        setPosition(position);
    }

    /**
     * Set a new number and bitmap for this item
     * @param aNumber   a number
     * @param aBitmap   a bitmap
     */
    public void setNumber(int aNumber, Bitmap aBitmap) {
        number = aNumber;
        mBitmap = aBitmap;
        createBitmap();
        postInvalidate();
    }

    /**
     * Get the number on this view
     * @return the number on this NumberItem
     */
    public int getNumber() {
        return number;
    }

    /**
     * Get the NumberItem type
     * @return the NumberItem type
     */
    public NUMBER_TYPE getType() {
        return type;
    }

    /**
     * Set the position depending on the type and position input
     * @param position the relative position in x direction
     */
    private void setPosition(int position) {
        switch (type) {
            case TIME:
                mX = (position>0) ? 3 : -10 + TIME_SIZE;
                mY = 3; //mScreenHeight-3 - TIME_SIZE;
                break;
            case LEVEL:
                mX = mScreenWidth - LEVEL_SIZE + ((position>0) ?  - 36: +3);
                mY = -4; //mScreenHeight - (-4) - LEVEL_SIZE;
                break;
            case SCORE:
                mX = mScreenWidth/2 + 3 - position * 39;
                mY = -4; //mScreenHeight - (-4) - SCORE_SIZE;
                break;
            default:
                //throw RuntimeException("Error in input");
        }
    }

    /**
     * ´Scale the bitmap to correct size
     */
    private void createBitmap() {
        switch (type) {
            case LEVEL:
                mScaledWidth = LEVEL_SIZE;
                break;
            case TIME:
                mScaledWidth = TIME_SIZE;
                break;
            case SCORE:
                mScaledWidth = SCORE_SIZE;
                break;
            default:
                //;
        }
        mBitmapScaled = Bitmap.createScaledBitmap(mBitmap, mScaledWidth, mScaledWidth, false);
    }

    void cancel() {
    }

    void start(long initDelay) {
    }
}

