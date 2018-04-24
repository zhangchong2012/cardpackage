package com.example.zhangchong.myapplication.cardbag.cardanimation;

import android.animation.LayoutTransition;

/**
 * Created by zhangchong on 2018/4/19.
 */

public class CardBagAnimationController {
    //布局动画
    private LayoutTransition mTransition;
    //布局动画监听器.关联卡片包其他动画
    private LayoutTransition.TransitionListener mTransitionListener;
    //卡片包所有的动画
    private ICardBagViewAnimation mBagViewAnimation;

    public CardBagAnimationController() {
    }

    public LayoutTransition getLayoutTransition() {
        return mTransition;
    }

    public void setLayoutTransition(LayoutTransition transition) {
        this.mTransition = transition;
    }

    public LayoutTransition.TransitionListener getLayoutTransitionListener() {
        return mTransitionListener;
    }

    public void setLayoutTransitionListener(LayoutTransition.TransitionListener transitionListener) {
        this.mTransitionListener = transitionListener;
    }

    public ICardBagViewAnimation getBagViewAnimation() {
        return mBagViewAnimation;
    }

    public void setBagViewAnimation(ICardBagViewAnimation bagViewAnimation) {
        this.mBagViewAnimation = bagViewAnimation;
    }
}
