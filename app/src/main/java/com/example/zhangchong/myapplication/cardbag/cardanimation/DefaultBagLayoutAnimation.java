package com.example.zhangchong.myapplication.cardbag.cardanimation;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;

/**
 * Created by zhangchong on 2018/4/20.
 */

public class DefaultBagLayoutAnimation extends LayoutTransition {
    private static final int mAppearHeight = 600;
    private static final int DURATION_LAYOUT_DISPLAY = 500;
    private static final int DURATION_LAYOUT_DISMISS = 300;

    public DefaultBagLayoutAnimation(LayoutTransition.TransitionListener listener) {
        setStartDelay(LayoutTransition.APPEARING, 0);
        setStartDelay(LayoutTransition.DISAPPEARING, 0);

        // 子View添加到mContainer时的动画
        setAnimator(LayoutTransition.APPEARING, getAppearAnimation());

        // 子Veiw从mContainer中移除时的动画
        setAnimator(LayoutTransition.DISAPPEARING, getDisAppearAnimation());
        addTransitionListener(listener);
    }

    private Animator getAppearAnimation() {
        return ObjectAnimator.ofFloat(null, "translationY", -mAppearHeight, 0)
            .setDuration(DURATION_LAYOUT_DISPLAY);
    }

    private Animator getDisAppearAnimation() {
        return ObjectAnimator.ofFloat(null, "translationY", 0, -mAppearHeight)
            .setDuration(DURATION_LAYOUT_DISMISS);
    }
}