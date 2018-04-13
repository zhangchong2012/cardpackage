package com.example.zhangchong.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by zhangchong on 2018/4/10.
 */

public class CardPackageHelper {
    public static final int DURATION_LAYOUT_DISPLAY = 500;
    public static final int DURATION_LAYOUT_DISMISS = 300;
    public static final int PADDING_STEP = 16;

    public static ValueAnimator shrinkCards(final FrameLayout parentView, final int maxHeight,
        final CardDisplayAnimationListener listener) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1.0f);
        final int max = (parentView).getChildCount();

        //对其最后一个view的高度.
        final Rect rectLast = new Rect();
        if (max > 1) {
            ViewGroup lastView = (ViewGroup) (parentView).getChildAt(max - 1);
            lastView.getChildAt(0).getLocalVisibleRect(rectLast);
        }
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                if (listener != null) listener.onAnimationEnd();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (listener != null) listener.onAnimationEnd();
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                calculateZoomSmall(animation, parentView, max, PADDING_STEP, maxHeight);
            }
        });
        animator.setDuration(DURATION_LAYOUT_DISMISS);
        animator.start();

        return animator;
    }

    private static void calculateZoomSmall(ValueAnimator animation, FrameLayout parentView, int max,
        int paddingStep, int maxHeight) {
        float value = (float) animation.getAnimatedValue();

        int index = 0;
        //要移动的总数
        int moveMax = max - 1;
        while (index < moveMax) {
            int diff = moveMax - index;
            int left = (int) ((diff - 1) * paddingStep * 2 + paddingStep * 2 * value);
            int right = (int) ((diff - 1) * paddingStep * 2 + paddingStep * 2 * value);
            int bottom = (int) ((diff - 1) * paddingStep + paddingStep * value);

            View child = parentView.getChildAt(index);
            int top = index * paddingStep;
            if (max > DialogAnimationLayout.MAX_CARD_VIEW) {
                //超过3个卡片.才移动paddingtop
                top = (int) (index * paddingStep - paddingStep * value);
            }
            Log.e("msg", "l,t,r,b:"
                + left
                + ", "
                + top
                + ", "
                + right
                + ", "
                + bottom
                + ", index:"
                + index
                + ", diff:"
                + diff);
            child.setPadding(left, top, right, bottom);
            if(child == null)
                return;

            if (child.getMeasuredHeight() - child.getPaddingBottom() > maxHeight) {
                //减少高度
                child.getLayoutParams().height =
                    child.getMeasuredHeight() - (int) ((child.getMeasuredHeight() - maxHeight)
                        * value);
                child.requestLayout();


                Log.e("msg", "child.getLayoutParams().height:" + child.getLayoutParams().height);
                //缩放高度
                //float scale = 1 - (0.5f * value);
                //(((ViewGroup)child).getChildAt(0)).setScaleY(scale);
            }
            ++index;
        }
    }

    private static void calculateZoomBig(ValueAnimator animation, FrameLayout parentView, int max,
        int paddingStep) {
        float value = (float) animation.getAnimatedValue();

        int index = 0;
        //要移动的总数
        while (index < max) {
            int diff = max - index;
            int left = (int) (diff * paddingStep * 2 - paddingStep * 2 * value);
            int right = (int) (diff * paddingStep * 2 - paddingStep * 2 * value);
            int bottom = (int) (diff * paddingStep - paddingStep * value);

            View child = parentView.getChildAt(index);

            int top = (int) ((index - 1) * paddingStep + paddingStep * value);
            if (max < DialogAnimationLayout.MAX_CARD_VIEW) {
                //小于3个卡片.不移动paddingtop
                top = index * paddingStep;
            }
            Log.e("msg", "l,t,r,b:"
                + left
                + ", "
                + top
                + ", "
                + right
                + ", "
                + bottom);
            if(child == null)
                return;
            child.setPadding(left, top, right, bottom);

            CardViewTag tag = (CardViewTag) child.getTag();
            if (index == max - 1 && //只回弹最上面的view
                tag != null && tag.getOriginalHeight() > child.getHeight()) {
                child.getLayoutParams().height =
                    child.getHeight() + (int) ((tag.getOriginalHeight()
                        - child.getMeasuredHeight()) * value);
                child.requestLayout();

                //还原高度
                //float scale = child.getScaleY() + (1 - child.getScaleY()) * value;
                //(((ViewGroup)child).getChildAt(0)).setScaleY(scale);
            }
            ++index;
        }
    }

    public static ValueAnimator magnifyCards(final FrameLayout parentView,
        final CardDisplayAnimationListener listener) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                if (listener == null) {
                    return;
                }
                listener.onAnimationEnd();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (listener == null) {
                    return;
                }
                listener.onAnimationEnd();
            }
        });
        final int max = parentView.getChildCount();
        //剩余元素是否大于最大值(3).决定上边距是否移动
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                calculateZoomBig(animation, parentView, max, PADDING_STEP);
            }
        });
        animator.setDuration(DURATION_LAYOUT_DISMISS);
        animator.start();
        return animator;
    }

    @FunctionalInterface
    public interface CardDisplayAnimationListener {
        void onAnimationEnd();
    }
}
