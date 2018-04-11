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

public class CardPackageUtils {
    public static final int DURATION_LAYOUT_DISPLAY = 500;
    public static final int DURATION_LAYOUT_DISMISS = 300;
    public static final int padding_step = 16;

    public void shrinkCards(final FrameLayout parentView, final int maxHeight,
        final CardDisplayAnimationListener listener) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1.0f);
        final int MAX = (parentView).getChildCount();

        //对其最后一个view的高度.
        final Rect rectLast = new Rect();
        int viewCount = (parentView).getChildCount();
        if (viewCount > 1) {
            ViewGroup lastView = (ViewGroup) (parentView).getChildAt(viewCount - 1);
            lastView.getChildAt(0).getLocalVisibleRect(rectLast);
        }
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                if (listener != null) listener.AnimationEnd();
                updatePaddingBottom(parentView);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (listener != null) listener.AnimationEnd();
                updatePaddingBottom(parentView);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                updatePaddingBottom(parentView);
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (parentView == null) {
                    return;
                }

                int index = 0;
                //要移动的总数
                int moveMax = MAX - 1;
                while (index < moveMax) {
                    int diff = moveMax - index;
                    int left = (int) ((diff - 1) * padding_step * 2 + padding_step * 2 * value);
                    int right = (int) ((diff - 1) * padding_step * 2 + padding_step * 2 * value);
                    int bottom = (int) ((diff - 1) * padding_step + padding_step * value);

                    View child = parentView.getChildAt(index);
                    int top = index * padding_step;
                    if (MAX > DialogAnimationLayout.MAX_CARD_VIEW) {
                        //超过3个卡片.才移动paddingtop
                        top = (int) (index * padding_step - padding_step * value);
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

                    if(child.getMeasuredHeight() - child.getPaddingBottom() > maxHeight){
                        child.getLayoutParams().height = child.getMeasuredHeight() - (int)((child.getMeasuredHeight() - maxHeight) *  value);
                        child.requestLayout();
                    }
                    ++index;
                }
            }
        });
        animator.setDuration(DURATION_LAYOUT_DISMISS);
        animator.start();
    }

    private void updatePaddingBottom(FrameLayout parentView){
        for (int i = 0; i < parentView.getChildCount(); i++) {
            View view  = parentView.getChildAt(i);
            if(view == null || view.getTag() == null)
                continue;
            CardViewTag tag = (CardViewTag)view.getTag();
            tag.setOriginalPadding(view.getPaddingBottom());
        }
    }

    public void magnifyCards(final FrameLayout parentView,
        final CardDisplayAnimationListener listener) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                if (listener == null) {
                    return;
                }
                listener.AnimationEnd();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (listener == null) {
                    return;
                }
                listener.AnimationEnd();
            }
        });
        final int MAX = parentView.getChildCount();
        //剩余元素是否大于最大值(3).决定上边距是否移动
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (parentView == null){
                    return;
                }

                int index = 0;
                //要移动的总数
                while (index < MAX) {
                    int diff = MAX - index;
                    int left = (int) (diff * padding_step * 2 - padding_step * 2 * value);
                    int right = (int) (diff * padding_step * 2 - padding_step * 2 * value);
                    int bottom = (int) (diff * padding_step - padding_step * value);

                    View child = parentView.getChildAt(index);

                    int top = (int) ((index - 1) * padding_step + padding_step * value);
                    if (MAX < DialogAnimationLayout.MAX_CARD_VIEW) {
                        //小于3个卡片.不移动paddingtop
                        top = index * padding_step;
                    }
                    Log.e("msg", "l,t,r,b:"
                        + left
                        + ", "
                        + top
                        + ", "
                        + right
                        + ", "
                        + bottom);
                    child.setPadding(left, top,  right, bottom);

                    CardViewTag tag = (CardViewTag)child.getTag();
                    if(index == MAX - 1 && //只回弹最上面的view
                        tag != null && tag.getOriginalHeight() > child.getHeight()){
                        child.getLayoutParams().height = child.getHeight() + (int)((tag.getOriginalHeight() - child.getHeight()) *  value);
                        child.requestLayout();
                    }
                    ++index;
                }
            }
        });
        animator.setDuration(DURATION_LAYOUT_DISMISS);
        animator.start();
    }

    @FunctionalInterface
    public interface CardDisplayAnimationListener {
        void AnimationEnd();
    }
}
