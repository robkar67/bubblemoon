package com.kar.rob.bubblemoon;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class BubbleMoonActivity extends Activity {

    private final static String TAG = "Bubble-Moon";

    private final static int GAME_TIME = 31;
    private final static int BONUS_TIME = 21;
    private final static int START_BUBBLES = 8;
    private final static float MAX_SPEED_START = 9.5f;
    private final static float MIN_SPEED_START = 0.7f;
    private final static float SPEED_DELTA = 0.3f;
    private final static int MIN_TIME_FOOD = 2;
    private final static int LIFE_FOOD = 5;
    private final static int ORIGINAL_HEIGHT = 1280;
    private final static int ORIGINAL_WIDTH = 720;
    private final int HUNTER_SIZE = 192;
    private final int BUBBLE_SIZE = 96;
    private final int NUMBER_SIZE = 62;
    private int hunterSize;
    private int bubbleSize;
    private int numberSize;


    private CountDownTimer mGameTimer;
    private CountDownTimer[] mFoodTimer = new CountDownTimer[2];

    private static Random rand;

    /**
     * The different types of pop up windows
     */
    private enum POPUP_TYPE {
        START, NEXT, ABOUT, FAILED, BONUS
    }

    private enum FOOD_TYPE {
        INTERMITTENT, FIXED
    }

    // image bitmaps
    private static Bitmap mBubbleImage;
    private static Bitmap mHunterImage;
    private final static Bitmap[] mFoodImage = new Bitmap[9];
    //    private final static Bitmap[] mBlackNumber = new Bitmap[10];
    private final static Bitmap[] mGoldNumber = new Bitmap[10];

    private static NumberItem mLeftTime;
    private static NumberItem mRightTime;
    private static ItemView mHunter = null;
    private static ItemView[] mFood = new ItemView[2];

    private static int mLevel = 1;
    private static int nrofBubbles;
    private static int score = 0;
    private static int time;
    private static float maxSpeed;
    private static float minSpeed;

    // Screen dimensions
    private int mScreenWidth, mScreenHeight;
    private int mDisplayWidth, mDisplayHeight;
    private float alpha;

    // Sound variables
    private static SoundPool mSoundPool;
    private static int mPopID;     // ID for the bubble pop sound
    private static int mSplashID;  // ID for the splash sound
    private static int mSlapID;     // ID for the banana slap sound
    private static float mAudioVolume, mSplashVolume;

    private GestureDetector mGestureDetector;

    private RelativeLayout mMainView;

    private SharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        // Set up user interface
        mMainView = (RelativeLayout) findViewById(R.id.frame);
        maxSpeed = MAX_SPEED_START;
        minSpeed = MIN_SPEED_START;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDisplayHeight = metrics.heightPixels;
        mDisplayWidth = metrics.widthPixels;
        alpha = mDisplayWidth / (float) ORIGINAL_WIDTH;
        hunterSize = (HUNTER_SIZE * mDisplayWidth) / ORIGINAL_WIDTH;
        bubbleSize = (BUBBLE_SIZE * mDisplayWidth) / ORIGINAL_WIDTH;
        numberSize = (NUMBER_SIZE * mDisplayWidth) / ORIGINAL_WIDTH;

        rand = new Random();

        // Load basic Bitmaps, Attribution? License?
        final int buSi = 96; //(96 * mDisplayWidth) / ORIGINAL_WIDTH;
        mBubbleImage = decodeSampledBitmapFromResource(getResources(), R.drawable.b64, buSi, buSi);
        // This image, made by Unnamed (Viktor.Hahn@web.de), is licensed under the Creative Commons
        // Attribution-ShareAlike 3.0 Unported License. http://creativecommons.org/licenses/by-sa/3.0/
        final int huSi = 192; //(192 * mDisplayWidth) / ORIGINAL_WIDTH;
        mHunterImage = decodeSampledBitmapFromResource(getResources(), R.drawable.planet_21, huSi, huSi);
        // Fruit pngs. From pngimg.com public domain
        final int SI = 96; //(96 * mDisplayWidth) / ORIGINAL_WIDTH;
        mFoodImage[0] = decodeSampledBitmapFromResource(getResources(), R.drawable.peach, SI, SI);
        mFoodImage[1] = decodeSampledBitmapFromResource(getResources(), R.drawable.strawberry, SI, SI);
        mFoodImage[2] = decodeSampledBitmapFromResource(getResources(), R.drawable.banana, SI, SI);
        mFoodImage[3] = decodeSampledBitmapFromResource(getResources(), R.drawable.apple, SI, SI);
        mFoodImage[4] = decodeSampledBitmapFromResource(getResources(), R.drawable.watermelon, SI, SI);
        mFoodImage[5] = decodeSampledBitmapFromResource(getResources(), R.drawable.raspberry, SI, SI);
        mFoodImage[6] = decodeSampledBitmapFromResource(getResources(), R.drawable.ice_cream, SI, SI);
        mFoodImage[7] = decodeSampledBitmapFromResource(getResources(), R.drawable.orange, SI, SI);
        mFoodImage[8] = decodeSampledBitmapFromResource(getResources(), R.drawable.cherry, SI, SI);

        final int cySize = 56;
//        final int tiSize = 48;
//        // these black number pictures are from Icons8 https://icons8.com/,
//        // license see https://icons8.com/license/ (in google play and about)
//        mBlackNumber[0] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr0, tiSize, tiSize);
//        mBlackNumber[1] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr1, tiSize, tiSize);
//        mBlackNumber[2] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr2, tiSize, tiSize);
//        mBlackNumber[3] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr3, tiSize, tiSize);
//        mBlackNumber[4] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr4, tiSize, tiSize);
//        mBlackNumber[5] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr5, tiSize, tiSize);
//        mBlackNumber[6] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr6, tiSize, tiSize);
//        mBlackNumber[7] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr7, tiSize, tiSize);
//        mBlackNumber[8] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr8, tiSize, tiSize);
//        mBlackNumber[9] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr9, tiSize, tiSize);
        // gold numbers from https://pixabay.com, CC0 no attribution needed
        mGoldNumber[0] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr0gold, cySize, cySize);
        mGoldNumber[1] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr1gold, cySize, cySize);
        mGoldNumber[2] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr2gold, cySize, cySize);
        mGoldNumber[3] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr3gold, cySize, cySize);
        mGoldNumber[4] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr4gold, cySize, cySize);
        mGoldNumber[5] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr5gold, cySize, cySize);
        mGoldNumber[6] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr6gold, cySize, cySize);
        mGoldNumber[7] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr7gold, cySize, cySize);
        mGoldNumber[8] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr8gold, cySize, cySize);
        mGoldNumber[9] = decodeSampledBitmapFromResource(getResources(), R.drawable.nr9gold, cySize, cySize);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Manage sound
        AudioManager mAudioManager;
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAudioVolume = (float) mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC)
                / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mSplashVolume = Math.min(1.0f, mAudioVolume*1.1f);
        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);   // allow up to 10 sound streams
        //mSoundPool = new SoundPool.Builder
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                setupGestureDetector();
            }
        });
        // load splash.wav from res/raw/splash.wav from http://soundbible.com/2100-Splash-Rock-In-Lake.html, public domain
        mSplashID = mSoundPool.load(this, R.raw.splash, 1);
        // load banana_slap.wav from http://soundbible.com/2047-Banana-Slap.html, public domain
        mSlapID = mSoundPool.load(this, R.raw.banana_slap, 1);
        // load bubble pop from res/raw/bubble_pop.wav
        mPopID = mSoundPool.load(this, R.raw.bubble_pop, 1);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Get the size of the display to know where borders are
            mScreenWidth = mMainView.getWidth();
            mScreenHeight = mMainView.getHeight();
            if (mHunter==null) {
                mHunter = new ItemView(getApplicationContext(), ItemView.ITEM_TYPE.HUNTER, mScreenWidth, mScreenHeight, mHunterImage, maxSpeed, minSpeed, hunterSize, alpha);
                mHunter.start(0);
            }
            if (mMainView.getChildCount() < 1) {
                showPopup(POPUP_TYPE.START);
                prepareNewLevel();
            } else {
                for (int i = 0; i<mMainView.getChildCount(); i++) {
                    ScreenItem item = (ScreenItem) mMainView.getChildAt(i);
                    item.setScreenSize(mScreenWidth, mScreenHeight);
                    item.cancel();
                    item.start(6*i);
                }
            }
            mMainView.postInvalidate();
            if (mGameTimer!=null) {
                mGameTimer.cancel();
            }
            mGameTimer = createGameTimer();
            mGameTimer.start();
            freezeFood();
            mFoodTimer[0] = createFoodBirthTimer(0);
            mFoodTimer[0].start();
            mFoodTimer[1] = createFoodBirthTimer(1);
            mFoodTimer[1].start();
        } else {
            // out of focus
            mGameTimer.cancel();
            freezeFood();
            mMainView.postInvalidate();
            freezeBubbles(false);
        }
    }

    /**
     * Set up the GestureDetector
     */
    private void setupGestureDetector() {
        if (mGestureDetector != null) {
            return; // we only want one gesture detector...
        }
        mGestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent event1, MotionEvent event2,
                                           float velocityX, float velocityY) {
                        // If a fling gesture starts on a bubble or hunter then change the
                        // direction and speed
                        float x = event1.getX();
                        float y = event1.getY();
                        for (int i = 0; i < mMainView.getChildCount(); i++) {
                            View item = mMainView.getChildAt(i);
                            if (item instanceof ItemView) {
                                ItemView itemView = (ItemView) item;
                                if (!itemView.getIsDone() && itemView.getType()!=ItemView.ITEM_TYPE.FOOD && itemView.insideItem(x, y) ) {
                                    itemView.changeSpeedDirection(velocityX, velocityY);
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent event) {
                        // If a single tap inside the hunter, then stop its movement
                        float x = event.getX();
                        float y = event.getY();
                        for (int i = 0; i < mMainView.getChildCount(); i++) {
                            View item = mMainView.getChildAt(i);
                            if (item instanceof ItemView) {
                                ItemView itemView = (ItemView) item;
                                if (itemView.getType() == ItemView.ITEM_TYPE.HUNTER && itemView.insideItem(x, y)) {
                                    // single tap inside the hunter, then remove its movement
                                    itemView.stopMovement();
                                    return true;
                                } else if (itemView.getType() == ItemView.ITEM_TYPE.HUNTER &&
                                        itemView.intersects(x, y, itemView.getRadius() * 1.5f)) {
                                    // single tap too close to the hunter, consume the tap...
                                    return true;
                                }
                            }
                        }
                        // Otherwise, create a new bubble at the tap's location and add
                        // it to mMainView
//                        if (nrofBubbles <9) {
//                            nrofBubbles++;
//                            ItemView bubble = new ItemView(getApplicationContext(), ItemView.ITEM_TYPE.BUBBLE, mScreenWidth, mScreenHeight, mBubbleImage, maxSpeed, minSpeed, bubbleSize, alpha, x, y, false);
//                            bubble.setCheckBubblesForCollision(new ItemView.CheckItemForCollision() {
//                                @Override
//                                public boolean hasCollided(final ItemView itemView) {
//                                    return hasBubbleCollided(itemView);
//                                }
//                            });
//                            bubble.start(0);
//                            mMainView.addView(bubble);
//                        }
                        return true;
                    }
                }
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);    // Pass the touch to the gesture detector
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSoundPool.release();   // Release all SoundPool resources
        mSoundPool = null;

        if (mGameTimer!=null) {
            mGameTimer.cancel();
        }
        freezeFood();
        freezeBubbles(false);
        mMainView.postInvalidate();

        // TODO save state here and restore in onResume...
//		SharedPreferences.Editor ed = mPrefs.edit();
//		ed.putInt("view_mode", mMainView);
//		ed.commit();
    }

    /**
     * Prepare bubbles and hunter for a new level
     */
    private synchronized void prepareNewLevel() {
        mMainView.removeAllViews();
        for (int i = START_BUBBLES; i > 0; i--) {
            final ItemView bubble = new ItemView(getApplicationContext(), ItemView.ITEM_TYPE.BUBBLE, mScreenWidth, mScreenHeight, mBubbleImage, maxSpeed, minSpeed, bubbleSize, alpha);
            bubble.setCheckBubblesForCollision(new ItemView.CheckItemForCollision() {
                @Override
                public boolean hasCollided(final ItemView itemView) {
                    return hasBubbleCollided(itemView);
                }
            });
            bubble.start(i * 6);
            mMainView.addView(bubble);
        }
        float[] x = {mScreenWidth/6, mScreenWidth*5/6, mScreenWidth/6, mScreenWidth*5/6};
        float[] y = {mScreenHeight/6, mScreenHeight/6, mScreenHeight*5/6, mScreenHeight*5/6};
        int[] ind = new int[2];
        ind[0] = rand.nextInt(x.length);
        while ((ind[1] = rand.nextInt(x.length)) == ind[0]) {}
        for (int i : ind) {
            final ItemView food = new ItemView(getApplicationContext(), ItemView.ITEM_TYPE.FOOD, mScreenWidth, mScreenHeight, mFoodImage[0], maxSpeed, minSpeed, bubbleSize, alpha, x[i], y[i], false);
            food.setCheckBubblesForCollision(new ItemView.CheckItemForCollision() {
                @Override
                public boolean hasCollided(final ItemView itemView) {
                    return hasFoodCollided(itemView, FOOD_TYPE.FIXED);
                }
            });
            mMainView.addView(food);
        }
        nrofBubbles = START_BUBBLES;
        mHunter.setIsDone(false);
        mHunter.moveToMiddleStopMovement();
        mMainView.addView(mHunter);
        int high = GAME_TIME / 10;
        int low = GAME_TIME % 10;
        time = GAME_TIME;
        mLeftTime = new NumberItem(getApplicationContext(), NumberItem.NUMBER_TYPE.TIME, high, 1, mScreenWidth, mScreenHeight, mGoldNumber[high], numberSize, alpha);
        mMainView.addView(mLeftTime);
        mRightTime = new NumberItem(getApplicationContext(), NumberItem.NUMBER_TYPE.TIME, low, 0, mScreenWidth, mScreenHeight, mGoldNumber[low], numberSize, alpha);
        mMainView.addView(mRightTime);
        showLevel();
        showScore();
        mMainView.postInvalidate();
    }

    /**
     * Prepare bubbles and hunter for a bonus level
     */
    private synchronized void prepareBonusLevel() {
        mMainView.removeAllViews();
        float[] x = {mScreenWidth/6, mScreenWidth*5/6, mScreenWidth/6, mScreenWidth*5/6, mScreenWidth/3, mScreenWidth*2/3, mScreenWidth/3, mScreenWidth*2/3};
        float[] y = {mScreenHeight/6, mScreenHeight/6, mScreenHeight*5/6, mScreenHeight*5/6, mScreenHeight/3, mScreenHeight/3, mScreenHeight*2/3, mScreenHeight*2/3};
        for (int i = x.length-1; i >= 0; i--) {
            final ItemView bubble = new ItemView(getApplicationContext(), ItemView.ITEM_TYPE.BUBBLE, mScreenWidth, mScreenHeight, mBubbleImage, 0, 0, bubbleSize, alpha, x[i], y[i], false);
            bubble.setCheckBubblesForCollision(new ItemView.CheckItemForCollision() {
                @Override
                public boolean hasCollided(final ItemView itemView) {
                    return hasBubbleCollided(itemView);
                }
            });
            bubble.start(i * 6);
            mMainView.addView(bubble);
        }
        nrofBubbles = x.length;
        mHunter.setIsDone(false);
        mHunter.moveToMiddleStopMovement();
        mMainView.addView(mHunter);
        int high = BONUS_TIME / 10;
        int low = BONUS_TIME % 10;
        time = BONUS_TIME;
        mLeftTime = new NumberItem(getApplicationContext(), NumberItem.NUMBER_TYPE.TIME, high, 1, mScreenWidth, mScreenHeight, mGoldNumber[high], numberSize, alpha);
        mMainView.addView(mLeftTime);
        mRightTime = new NumberItem(getApplicationContext(), NumberItem.NUMBER_TYPE.TIME, low, 0, mScreenWidth, mScreenHeight, mGoldNumber[low], numberSize, alpha);
        mMainView.addView(mRightTime);
        showLevel();
        showScore();
        mMainView.postInvalidate();
    }

    /**
     * Check if the bubble has collided with the hunter
     * @param bubble a bubble
     * @return true if they have collided
     */
    private synchronized boolean hasBubbleCollided(final ItemView bubble) {
        if (mHunter.intersects(bubble)){
            bubble.setIsDone(true);
            bubble.cancel();
            mHunter.changeSpeedCollision(bubble);
            mHunter.shrink(6);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMainView.removeView(bubble);
                    mMainView.postInvalidate();
                }
            });
            mSoundPool.play(mPopID, mAudioVolume, mAudioVolume, 0, 0, 1);

            if (nrofBubbles==0) {
                return true;
            }
            nrofBubbles--;
            final int highT = time / 10;
            final int lowT = time % 10;
            score += (mLevel + 10)*highT + lowT;
            showScore();
            if (nrofBubbles==0) {
                mGameTimer.cancel();
                freezeFood();
                mLevel++;
                mHunter.stopMovement();
                maxSpeed += SPEED_DELTA;
                minSpeed += SPEED_DELTA/3;
                mHunter.setMaxSpeed(maxSpeed);
                mHunter.setMinSpeed(minSpeed);
                if (mLevel % 3 == 0) {
                    showPopup(POPUP_TYPE.BONUS);
                } else {
                    showPopup(POPUP_TYPE.NEXT);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Check if the food has collided with the hunter
     * @param food a food item
     * @param foodType the life type of food
     * @return true if they have collided
     */
    private synchronized boolean hasFoodCollided(final ItemView food, FOOD_TYPE foodType) {
        if (mHunter.intersects(food)){
            food.setIsDone(true);
            food.cancel();
            mHunter.changeSpeedCollision(food);
            mHunter.grow(7);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMainView.removeView(food);
                    mMainView.invalidate();
                }
            });
            mSoundPool.play(mSlapID, mAudioVolume, mAudioVolume, 0, 0, 1);
            score += (time + mLevel) * 3 + 21;
            showScore();
            if (foodType==FOOD_TYPE.INTERMITTENT) {
                time = time + 4;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGameTimer.cancel();
                        int index;
                        if (food == mFood[0]) {
                            index = 0;
                        } else {
                            index = 1;
                        }
                        mFoodTimer[index].cancel();
                        mFoodTimer[index] = createFoodBirthTimer(index);
                        mFoodTimer[index].start();
                        mGameTimer = createGameTimer();
                        mGameTimer.start();
                    }
                });
            }
            return true;
        }
        return false;
    }

    /**
     * Creates a game timer
     */
    private CountDownTimer createGameTimer() {
        return new CountDownTimer(time*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                // update the numbers on the display
                time--;
                final int high = time / 10;
                final int low = time % 10;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLeftTime.setNumber(high, mGoldNumber[high]);
                        mRightTime.setNumber(low, mGoldNumber[low]);
                        mLeftTime.invalidate();
                        mRightTime.invalidate();
                    }
                });
            }
            public void onFinish() {
                mHunter.cancel();
                mFoodTimer[0].cancel();
                mFoodTimer[1].cancel();
                mSoundPool.play(mSplashID, mSplashVolume, mSplashVolume, 0, 0, 1.5f);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLeftTime.setNumber(0, mGoldNumber[0]);
                        mRightTime.setNumber(0, mGoldNumber[0]);
                        mLeftTime.invalidate();
                        mRightTime.invalidate();
                    }
                });
                for (int i = 0; i<mMainView.getChildCount(); i++) {
                    View item = mMainView.getChildAt(i);
                    if (item instanceof ItemView) {
                        ItemView itemView = (ItemView) item;
                        itemView.stopMovement();
                        itemView.setIsDone(true);
                    }
                }
                showPopup(POPUP_TYPE.FAILED);
            }
        };
    }

    /**
     * Creates a food timer that removes the food item on timeout
     * @param index the index of the food item
     */
    private CountDownTimer createFoodDeathTimer(final int index) {
        final int next = rand.nextInt(3) + 4;
        return new CountDownTimer(next*1000, 1000) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                mFood[index].setIsDone(true);
                mFood[index].cancel();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMainView.removeView(mFood[index]);
                        mMainView.postInvalidate();
                    }
                });
                mFoodTimer[index] = createFoodBirthTimer(index);
                mFoodTimer[index].start();
            }
        };
    }

    /**
     * Creates a food timer that give birth to a food item on time out
     * @param index the index of the food item
     */
    private CountDownTimer createFoodBirthTimer(final int index) {
        final int next = rand.nextInt(4) + 2;
        return new CountDownTimer(next*1000, 1000) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                float x, y;
                do {
                    x = rand.nextFloat() * mScreenWidth;
                    y = rand.nextFloat() * mScreenHeight;
                } while (mHunter.intersects(x, y, bubbleSize * 2f));
                int i = rand.nextInt(mFoodImage.length - 1) + 1;
                mFood[index] = new ItemView(getApplicationContext(), ItemView.ITEM_TYPE.FOOD, mScreenWidth, mScreenHeight, mFoodImage[i], maxSpeed, minSpeed, bubbleSize, alpha, x, y, false);
                mFood[index].setCheckBubblesForCollision(new ItemView.CheckItemForCollision() {
                    @Override
                    public boolean hasCollided(final ItemView itemView) {
                        return hasFoodCollided(itemView, FOOD_TYPE.INTERMITTENT);
                    }
                });
                mFood[index].start(0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMainView.addView(mFood[index]);
                        mMainView.postInvalidate();
                    }
                });
                mFoodTimer[index] = createFoodDeathTimer(index);
                mFoodTimer[index].start();
            }
        };
    }

    /**
     * Show the level on the display
     */
    private void showLevel() {
        int right = mLevel;
        if (mLevel>9) {
            int left = mLevel / 10;
            while (left>=10) {
                left = left % 10; // above level 100, let counter wrap around
            }
            right = mLevel % 10;
            NumberItem mLeftLevel = new NumberItem(getApplicationContext(), NumberItem.NUMBER_TYPE.LEVEL, left, 1, mScreenWidth, mScreenHeight, mGoldNumber[left], numberSize, alpha);
            mMainView.addView(mLeftLevel);
            mLeftLevel.postInvalidate();
        }
        NumberItem mRightLevel = new NumberItem(getApplicationContext(), NumberItem.NUMBER_TYPE.LEVEL, right, 0, mScreenWidth, mScreenHeight, mGoldNumber[right], numberSize, alpha);
        mMainView.addView(mRightLevel);
        mRightLevel.postInvalidate();
    }

    /**
     * Show the score on the display
     */
    private void showScore() {
        for (int i = mMainView.getChildCount()-1; i>=0; i--) {
            View item = mMainView.getChildAt(i);
            if (item instanceof NumberItem) {
                NumberItem numberItem = (NumberItem) mMainView.getChildAt(i);
                if (numberItem.getType() == NumberItem.NUMBER_TYPE.SCORE) {
                    mMainView.removeView(numberItem);
                }
            }
        }
        int digits = 1;
        if (score>1) {
            digits = (int) Math.log10(score) + 1;
        }
        int number = score;
        for (int i = 0; i<digits; i++) {
            int digit = number % 10;
            number /= 10;
            NumberItem item = new NumberItem(getApplicationContext(), NumberItem.NUMBER_TYPE.SCORE, digit, i, mScreenWidth, mScreenHeight, mGoldNumber[digit], numberSize, alpha);
            mMainView.addView(item);
            item.postInvalidate();
        }
    }

    /**
     * Show a pop up
     * @param popupType the type of pop up to show
     */
    private void showPopup(final POPUP_TYPE popupType) {
        int popupWidth = (300 * mDisplayWidth) / ORIGINAL_WIDTH;
        int popupHeight = (200 * mDisplayHeight) / ORIGINAL_HEIGHT;

        LinearLayout viewGroup;
        final View layout;
        switch (popupType) {
            case BONUS:
                viewGroup = (LinearLayout) findViewById(R.id.bonus);
                layout = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.bonus_layout, viewGroup);
                break;
            case NEXT:
                viewGroup = (LinearLayout) findViewById(R.id.next);
                layout = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.next_layout, viewGroup);
                break;
            case START:
                viewGroup = (LinearLayout) findViewById(R.id.start);
                layout = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.start_layout, viewGroup);
                break;
            case ABOUT:
                viewGroup = (LinearLayout) findViewById(R.id.about);
                layout = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.about_layout, viewGroup);
                popupWidth = mScreenWidth - 5;
                popupHeight = mScreenHeight - 5;
                break;
            case FAILED:
            default:
                viewGroup = (LinearLayout) findViewById(R.id.failed);
                layout = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.failed_layout, viewGroup);
                ((TextView) layout.findViewById(R.id.bummer)).setText(getString(R.string.game_over) +
                        getString(R.string.level) + " " + mLevel + "\n" +
                        getString(R.string.score) + " " + score);
                popupHeight = (popupHeight * 140) / 100;
                break;
        }

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(this);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);
        popup.setOutsideTouchable(true);
        popup.setBackgroundDrawable(null);

        // Displaying the popup at the middle of the screen
        final int width = mScreenWidth / 2 - popupWidth / 2;
        final int height = mScreenHeight / 3 - popupHeight / 2;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                popup.showAtLocation(layout, Gravity.NO_GRAVITY, width, height);
            }
        });

        // Get a reference to the button, and close the popup when clicked.
        Button button = (Button) layout.findViewById(R.id.cont);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                if (popupType == POPUP_TYPE.NEXT) {
                    prepareNewLevel();
                } else if (popupType == POPUP_TYPE.BONUS) {
                    prepareBonusLevel();
                } else if (popupType == POPUP_TYPE.FAILED) {
                    restartGame();
                }
                mMainView.postInvalidate();
            }
        });
    }

    /**
     * Set up a new game
     */
    private void restartGame() {
        mLevel = 1;
        score = 0;
        maxSpeed = MAX_SPEED_START;
        minSpeed = MIN_SPEED_START;
        mHunter.resetHunter();
        mHunter.setMaxSpeed(maxSpeed);
        mHunter.setMinSpeed(minSpeed);
        freezeFood();
        prepareNewLevel();
    }

    /**
     * Stop and remove the intermittent food items
     */
    private void freezeFood() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mFoodTimer[0]!=null) {
                    mFoodTimer[0].cancel();
                    mFoodTimer[0] = null;
                }
                if (mFoodTimer[1]!=null) {
                    mFoodTimer[1].cancel();
                    mFoodTimer[1] = null;
                }
                if (mFood[0]!=null) {
                    mFood[0].cancel();
                    mMainView.removeView(mFood[0]);
                    mFood[0] = null;
                }
                if (mFood[1]!=null) {
                    mFood[1].cancel();
                    mMainView.removeView(mFood[1]);
                    mFood[1] = null;
                }
            }
        });
    }

    /**
     * Stop all movements
     * @param remove true if bubbles should be removed
     */
    private void freezeBubbles(final boolean remove) {
        for (int i = 0; i<mMainView.getChildCount(); i++) {
            View item = mMainView.getChildAt(i);
            if (item instanceof ItemView) {
                ItemView itemView = (ItemView) item;
                itemView.cancel();
                if (remove && itemView.getType() == ItemView.ITEM_TYPE.BUBBLE) {
                    itemView.setIsDone(true);
                    mMainView.removeView(itemView);
                }
            }
        }
        mMainView.postInvalidate();
    }

    @Override
    public void onBackPressed() {
        openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        mGameTimer.cancel();
        mFoodTimer[0].cancel();
        mFoodTimer[1].cancel();
        freezeBubbles(false);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_still_mode:
                showPopup(POPUP_TYPE.ABOUT);
                mHunter.setSpeedType(ItemView.SPEED_TYPE.STILL);
                return true;
            case R.id.menu_restart:
                restartGame();
                return true;
            case R.id.menu_random_mode:
                mHunter.setSpeedType(ItemView.SPEED_TYPE.RANDOM);
                return true;
            case R.id.quit:
                exitRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Exit the app
     */
    private void exitRequested() {
        mMainView.removeAllViews();
        mHunter = null;
        mLevel = 1;
        score = 0;
        super.onBackPressed();
    }

    /**
     * Calculate the largest inSampleSize value that is a power of 2 and keeps both
     * height and width larger than the requested height and width.
     *
     * @param options input options
     * @param reqWidth required width
     * @param reqHeight required height
     * @return inSampleSize as a power of 2 where height and width are larger than requested
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * Efficiently load scaled down version of bitmaps.
     * @param res Applications resources
     * @param resId Resource id to load
     * @param reqWidth Requested width
     * @param reqHeight Requested height
     * @return A scaled down bitmap
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}