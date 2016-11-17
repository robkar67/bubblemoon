package com.kar.rob.bubblemoon;

import android.content.Context;
import android.graphics.Bitmap;
import java.util.Random;
import android.os.Handler;

/**
 * A new ItemView is created for the hunter, food and each bubble on the display.
 * This class animates, draws, and more.
 */
public class ItemView extends ScreenItem {

    /**
     * the type of different items that an ItemView can be
     */
    public enum ITEM_TYPE {
        BUBBLE, HUNTER, FOOD
    }
    /**
     * the different speed modes, for testing
     */
    public enum SPEED_TYPE {
        RANDOM, SINGLE, STILL
    }
    private final ITEM_TYPE type;
    //private final int startSize;
    private final int MIN_SIZE = 32;    // min size when shrinking
    private final int MIN_TAP = 64;     // min size for inside detection
    private final long REFRESH_RATE = 40L;  // ms between movements
    private volatile boolean isDone;
    private Handler mHandler = new Handler();

    private static SPEED_TYPE speedType = SPEED_TYPE.RANDOM;
    private float maxSpeed;
    private float minSpeed;

    private final Random rand;

    private CheckItemForCollision checkItemForCollision;

    // location, speed and direction, radius of the item,
    // NOTE mX and mY are at upper left corner of the bitmap
    private float mDeltaX, mDeltaY, mSquaredRadius;
    private long mDeltaRotation;

    /**
     * Construct an ItemView for random position bubble or the hunter
     *
     * @param context       the context
     * @param aType         the type of item
     * @param aWidth        the width of the screen
     * @param aHeight       the height of the screen
     * @param aBitmap       the source bitmap for for this ItemView
     * @param aMaxSpeed     a maximum speed
     * @param aMinSpeed     a minimum speed
     * @param aStartSize    a size to start with
     * @param anAlpha       the screen size factor
     */
    public ItemView(Context context, ITEM_TYPE aType, int aWidth, int aHeight, Bitmap aBitmap, float aMaxSpeed, float aMinSpeed, int aStartSize, float anAlpha) {
        this(context, aType, aWidth, aHeight, aBitmap, aMaxSpeed, aMinSpeed, aStartSize, anAlpha, 0f, 0f, true);
    }

    /**
     * Construct an ItemView for items at fixed position or random position
     *
     * @param context       the context
     * @param aType         the type of item
     * @param aWidth        the screen width
     * @param aHeight       the screen height
     * @param aBitmap       the source bitmap for this item
     * @param aMaxSpeed     a maximum speed
     * @param aMinSpeed     a minimum speed
     * @param aStartSize    a size to start with
     * @param anAlpha       the screen size factor
     * @param x             a position in width direction
     * @param y             a position in height direction
     * @param randPosition  set to true for a random position
     */
    public ItemView(Context context, ITEM_TYPE aType, int aWidth, int aHeight, Bitmap aBitmap, float aMaxSpeed, float aMinSpeed, int aStartSize, float anAlpha, float x, float y, boolean randPosition) {
        super(context, aWidth, aHeight, aStartSize, anAlpha, aBitmap);

        type = aType;
        maxSpeed = aMaxSpeed;
        minSpeed = aMinSpeed;
        //startSize = aStartSize;

        rand = new Random(); // random number generator for position, rotation, speed and direction
        isDone = false;
        createBitmap();
        setPosition(x, y, randPosition);
        setMovement();  // Set speed and direction
        setRotation();
    }

    /**
     * Set isDone
     * @param anIsDone a boolean value
     */
    public void setIsDone(boolean anIsDone) {
        isDone = anIsDone;
    }

    /**
     * Get if the item is done
     * @return true if done
     */
    public boolean getIsDone() {
        return isDone;
    }

    /**
     * Get the radius
     * @return radius
     */
    public float getRadius() {
        return mRadius;
    }

    /**
     * Shrink the size
     * @param step step size to shrink by
     */
    public void shrink(int step) {
        mScaledWidth -= step;
        if (mScaledWidth < MIN_SIZE) {
            mScaledWidth = MIN_SIZE;
        }
        mX -= mScaledWidth/2 - mRadius;
        mY -= mScaledWidth/2 - mRadius;
        mRadius = mScaledWidth/2;
        mSquaredRadius = mRadius * mRadius;
        mBitmapScaled = Bitmap.createScaledBitmap(mBitmap, mScaledWidth, mScaledWidth, false);
    }

    /**
     * Grow the size
     * @param step step size to grow by
     */
    public void grow(int step) {
        mScaledWidth += step;
        if (mScaledWidth > startSize) {
            mScaledWidth = startSize;
        }
        mX += mScaledWidth/2 - mRadius;
        mY += mScaledWidth/2 - mRadius;
        mRadius = mScaledWidth/2;
        mSquaredRadius = mRadius * mRadius;
        mBitmapScaled = Bitmap.createScaledBitmap(mBitmap, mScaledWidth, mScaledWidth, false);
    }

    /**
     * Set the type of speed for new bubbles
     * @param st the new speed type
     */
    public void setSpeedType(SPEED_TYPE st) {
        speedType = st;
    }

    /**
     * Move the item to the middle of screen and set zero speed
     */
    public void moveToMiddleStopMovement() {
        mX = mScreenWidth/2 - mRadius;
        mY = mScreenHeight/2 - mRadius;
        mDeltaX = 0;
        mDeltaY = 0;
    }

    /**
     * If this is the hunter, set the size to start size
     */
    public void resetHunter() {
        if (type == ITEM_TYPE.HUNTER) {
            moveToMiddleStopMovement();
            createBitmap();
        }
    }

    /**
     * Set the maximum speed
     * @param aMaxSpeed a maximum speed
     */
    public void setMaxSpeed(float aMaxSpeed) {
        maxSpeed = aMaxSpeed;
    }

    /**
     * Set the minimum speed
     * @param aMinSpeed a minimum speed
     */
    public void setMinSpeed(float aMinSpeed) {
        minSpeed = aMinSpeed;
    }

    /**
     * For bubbles set the position to (x, y) if randPosition is false, if tru randomize positions
     * For hunter set position in the middle of the screen
     *
     * @param x             a x position
     * @param y             a y position
     * @param randPosition  if true, use random position
     */
    private void setPosition(float x, float y, boolean randPosition) {
        switch (type) {
            case HUNTER:
                mX = mScreenWidth/2 - mRadius;
                mY = mScreenHeight/2 - mRadius;
                break;
            case BUBBLE:
            case FOOD:
            default:
                if (randPosition) {
                    do {
                        x = (mScreenWidth - 10) * rand.nextFloat() + 5;
                        y = (mScreenHeight - 10) * rand.nextFloat() + 5;
                    }
                    while ((x - mScreenWidth / 2) * (x - mScreenWidth / 2) + (y - mScreenHeight / 2) * (y - mScreenHeight / 2) <
                            (mRadius + startSize * 2) * (mRadius + startSize * 2) * 0.98);
                }
                mX = x - mRadius;
                mY = y - mRadius;
                break;
        }
    }

    /**
     * Set the rotation speed
     */
    private void setRotation() {
        switch (type) {
            case BUBBLE:
                if (speedType == SPEED_TYPE.RANDOM) {
                    mDeltaRotation = rand.nextInt(7) - 3;   // set rotation in range [-3..3]
                } else {
                    mDeltaRotation = 1;
                }
                break;
            case HUNTER:
                mDeltaRotation = 2;
                break;
            case FOOD:
                mDeltaRotation = rand.nextInt(5) - 2;   // set rotation in range [-2..2]
                break;
            default:
                mDeltaRotation = 0;
        }
    }

    /**
     * Set the movement direction and speed
     */
    private void setMovement() {
        switch (type) {
            case BUBBLE:
                switch (speedType) {
                    case SINGLE:
                        // fixed speed
                        mDeltaX = 8 * alpha;
                        mDeltaY = 8 * alpha;
                        break;
                    case STILL:
                        // No speed
                        mDeltaX = 0;
                        mDeltaY = 0;
                        break;
                    default:
                        // random speed
                        mDeltaX = (rand.nextFloat()*maxSpeed - maxSpeed/2) * alpha;
                        mDeltaY = (rand.nextFloat()*maxSpeed - maxSpeed/2) * alpha;
                        checkMaxSpeed();
                        // check min speed
                        if ((mDeltaX * mDeltaX + mDeltaY * mDeltaY) < minSpeed * minSpeed * alpha * alpha) {
                            double angle = 2*Math.PI*rand.nextDouble();
                            mDeltaX = minSpeed * (float)Math.cos(angle) * alpha;
                            mDeltaY = minSpeed * (float)Math.sin(angle) * alpha;
                        }
                }
                break;
            default:
                mDeltaX = 0;
                mDeltaY = 0;
        }
    }

    /**
     * Create the scaled bitmap
     */
    private void createBitmap() {
        mScaledWidth = startSize;
        mRadius = mScaledWidth/2;
        mSquaredRadius = mRadius * mRadius;
        mBitmapScaled = Bitmap.createScaledBitmap(mBitmap, mScaledWidth, mScaledWidth, false);
    }

    /**
     * Start the movements of this item
     * @param initDelay an initial delay in ms before first movement
     */
    protected void start(long initDelay) {
        mHandler.postDelayed(periodicActions, initDelay);
    }

    /**
     * Cancel the ItemView's future movement
     */
    protected void cancel() {
        mHandler.removeCallbacks(periodicActions);
    }

    /**
     * Function to move item and invalidate the view so that it will be redrawn,
     * then post the next movement
     */
    private Runnable periodicActions = new Runnable() {
        @Override
        public void run() {
            moveAndCheckIfNotCaught();
            postInvalidate();
            mHandler.postDelayed(this, REFRESH_RATE);
        }
    };

    /**
     * Check if (x, y) is inside this item, using some slack in the detection
     * @param x a x position
     * @param y a y position
     * @return true if (x, y) is inside this item
     */
    public synchronized boolean insideItem(float x, float y) {
        return (x - mRadius - mX) * (x - mRadius - mX) + (y - mRadius - mY) * (y - mRadius - mY) < Math.max(MIN_TAP * MIN_TAP, mSquaredRadius * 1.1f);
    }

    /**
     * Check if circle with center position (x, y) and radius r intersects this item
     * @param x a x position
     * @param y a y position
     * @param r a radius
     * @return true if they intersect
     */
    public synchronized boolean intersects(float x, float y, float r) {
        return (x - mRadius - mX) * (x - mRadius - mX) + (y - mRadius - mY) * (y - mRadius - mY)
                < (r + mRadius) * (r + mRadius) * 0.96; // a small overlap is required...
    }

    /**
     * Check if this item intersects with itemView
     * @param itemView an ItemView
     * @return true if this item intersects with itemView
     */
    public synchronized boolean intersects(ItemView itemView) {
        return  intersects(itemView.mX + itemView.mRadius, itemView.mY + itemView.mRadius, itemView.mRadius);
    }

    /**
     * Set movement to zero
     */
    public synchronized void stopMovement() {
        mDeltaX = 0;
        mDeltaY = 0;
    }

    /**
     * Set the speed and direction according to (speedX, speedY)
     * @param speedX    a x speed
     * @param speedY    a y speed
     */
    public synchronized void changeSpeedDirection(float speedX, float speedY) {
        mDeltaX = speedX / REFRESH_RATE / 4.5f;
        mDeltaY = speedY / REFRESH_RATE / 4.5f;
        checkMaxSpeed();
    }

    /**
     * Reset the item to an initial state with a random position
     */
    public synchronized void reset() {
        isDone = false;
        createBitmap();
        setPosition(0f, 0f, true);
        setMovement();
        setRotation();
    }

    /**
     * Method to be called on the hunter after a collision with a bubble itemView
     * to set a new speed (hunter bounces off the bubble)
     * @param itemView a bubble ItemView
     */
    public synchronized void changeSpeedCollision(ItemView itemView) {
        float ex = mX + mRadius - itemView.mX - itemView.mRadius;
        float ey = mY + mRadius - itemView.mY - itemView.mRadius;
        float en = (float) Math.sqrt(ex * ex + ey * ey);
        en = (en>0) ? en: 1.0f;
        ex = ex / en;
        ey = ey / en;
        float vx = ex * mDeltaX - ey * mDeltaY;
        float vy = ex * mDeltaY + ey * mDeltaX;
        mDeltaX -= 1.5 * vx;
        mDeltaY -= 1.5 * vy;
        checkMaxSpeed();
    }

    /**
     * If speed is above the maxSpeed, then set it to maxSpeed
     */
    private synchronized void checkMaxSpeed() {
        float speed;
        if ((speed = mDeltaX * mDeltaX + mDeltaY * mDeltaY) > maxSpeed * maxSpeed * alpha * alpha) {
            speed = (float) Math.sqrt(speed);
            mDeltaX = maxSpeed * (mDeltaX / speed) * alpha;
            mDeltaY = maxSpeed * (mDeltaY / speed) * alpha;
        }
    }

    /**
     * Each time this method is run the ItemView should move one step.
     * If bubble/food exits the display, move to opposite side.
     * If a hunter exit the display it bounces back.
     *
     * @return true if ItemView should remain on screen
     */
    private synchronized boolean moveAndCheckIfNotCaught() {
        mX += mDeltaX;  // update position
        mY += mDeltaY;  // update position
        mRotation += mDeltaRotation;
        if (type==ITEM_TYPE.HUNTER) {
            if ((mX + mRadius) >= mScreenWidth) {
                mDeltaX = -mDeltaX;
            } else if ((mX + mRadius) <= 0) {
                mDeltaX = -mDeltaX;
            }
            if ((mY + mRadius) >= mScreenHeight) {
                mDeltaY = -mDeltaY;
            } else if (mY + mRadius <= 0) {
                mDeltaY = -mDeltaY;
            }
        } else {
            if (mX >= mScreenWidth) {
                mX = 1 - 2*mRadius;
            } else if (mX <= -2*mRadius) {
                mX = mScreenWidth - 1;
            }
            if (mY >= mScreenHeight) {
                mY = 1 - 2*mRadius;
            } else if (mY <= -2*mRadius) {
                mY = mScreenHeight - 1;
            }
        }
        // if this is bubble or food, check if the hunter can consume it
        if ((type==ITEM_TYPE.BUBBLE || type==ITEM_TYPE.FOOD) && !isDone) {
            if (checkItemForCollision.hasCollided(this)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the item type
     * @return item type
     */
    public ITEM_TYPE getType() {
        return type;
    }

    /**
     * Interface for checking if a bubble/food has collided with the hunter
     */
    public interface CheckItemForCollision {
        boolean hasCollided(ItemView itemView);
    }

    /**
     * Set the CheckItemForCollision
     * @param cifc a CheckItemForCollision
     */
    public void setCheckBubblesForCollision(CheckItemForCollision cifc){
        checkItemForCollision = cifc;
    }

}
