package com.zxb.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;

public class CartoonUtil {
	private static boolean mNeedShake = true;
    private boolean mStartShake = true;

    private static final int ICON_WIDTH = 80;
    private static final int ICON_HEIGHT = 94;
    private static final float DEGREE_0 = 1.8f;
    private static final float DEGREE_1 = -2.0f;
    private static final float DEGREE_2 = 2.0f;
    private static final float DEGREE_3 = -1.5f;
    private static final float DEGREE_4 = 1.5f;
    private static final int ANIMATION_DURATION = 80;

    private static int mCount = 0;

    static float mDensity;
    public static void shakeAnimation(final View v) {
	        float rotate = 0;
	        int c = mCount++ % 5;
	        if (c == 0) {
	            rotate = DEGREE_0;
	        } else if (c == 1) {
	            rotate = DEGREE_1;
	        } else if (c == 2) {
	            rotate = DEGREE_2;
	        } else if (c == 3) {
	            rotate = DEGREE_3;
	        } else {
	            rotate = DEGREE_4;
	        }
	        final RotateAnimation mra = new RotateAnimation(rotate, -rotate, ICON_WIDTH * mDensity / 2, ICON_HEIGHT * mDensity / 2);
	        final RotateAnimation mrb = new RotateAnimation(-rotate, rotate, ICON_WIDTH * mDensity / 2, ICON_HEIGHT * mDensity / 2);

	        mra.setDuration(ANIMATION_DURATION);
	        mrb.setDuration(ANIMATION_DURATION);

	        mra.setAnimationListener(new AnimationListener() {
	            @Override
	            public void onAnimationEnd(Animation animation) {
	                if (mNeedShake) {
	                    mra.reset();
	                    v.startAnimation(mrb);
	                }
	            }

	            @Override
	            public void onAnimationRepeat(Animation animation) {

	            }

	            @Override
	            public void onAnimationStart(Animation animation) {

	            }

	        });

	        mrb.setAnimationListener(new AnimationListener() {
	            @Override
	            public void onAnimationEnd(Animation animation) {
	                if (mNeedShake) {
	                    mrb.reset();
	                    v.startAnimation(mra);
	                }
	            }

	            @Override
	            public void onAnimationRepeat(Animation animation) {

	            }

	            @Override
	            public void onAnimationStart(Animation animation) {

	            }

	        });
	        v.startAnimation(mra);
	    }

}
