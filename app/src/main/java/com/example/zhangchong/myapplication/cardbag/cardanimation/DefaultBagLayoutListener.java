package com.example.zhangchong.myapplication.cardbag.cardanimation;

import android.animation.LayoutTransition;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhangchong on 2018/4/20.
 */

public class DefaultBagLayoutListener implements LayoutTransition.TransitionListener {
    private ICardBagViewAnimation mBagViewAnimation;
    private IDefaultControllerLayoutListener mControllerLayoutListener;

    public interface IDefaultControllerLayoutListener {
        void controllerDisplay();
        void controllerDismiss();
    }

    public DefaultBagLayoutListener(ICardBagViewAnimation bagViewAnimation, IDefaultControllerLayoutListener listener) {
        this.mBagViewAnimation = bagViewAnimation;
        this.mControllerLayoutListener = listener;
    }

    @Override
    public void startTransition(LayoutTransition transition, ViewGroup container, View view,
        int transitionType) {
        if (mBagViewAnimation != null) {
            mBagViewAnimation.endAnimation();
        }
    }

    @Override
    public void endTransition(LayoutTransition transition, ViewGroup container, View view,
        int transitionType) {
        if (mBagViewAnimation != null) {
            mBagViewAnimation.endAnimation();
        }

        switch (transitionType) {
            case LayoutTransition.APPEARING:
                if (mBagViewAnimation != null) {
                    mBagViewAnimation.displayAnimation(container, view);
                }
                if(mControllerLayoutListener != null){
                    mControllerLayoutListener.controllerDisplay();
                }
                break;
            case LayoutTransition.DISAPPEARING:
                if (mBagViewAnimation != null) {
                    mBagViewAnimation.dismissAnimation(container, view);
                }
                if(mControllerLayoutListener != null){
                    mControllerLayoutListener.controllerDismiss();
                }
                break;
            default:
                break;
        }
    }
}