package com.example.zhangchong.myapplication.cardbag.cardanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.example.zhangchong.myapplication.CardViewTag;
import com.example.zhangchong.myapplication.cardbag.ICardBagController;

/**
 * Created by zhangchong on 2018/4/20.
 */

public class DefaultBagViewAnimation implements ICardBagViewAnimation {
    public static final int DURATION_LAYOUT_DISMISS = 300;
    public static final int PADDING_STEP = 16;
    private ICardBagViewAnimationListener mListener;
    private Animator mAnimator;

    public DefaultBagViewAnimation(ICardBagViewAnimationListener listener) {
        mListener = listener;
    }

    @Override
    public void endAnimation() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.end();
            mAnimator = null;
        }
    }

    @Override
    public void dismissAnimation(final ViewGroup container, final View view) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1.0f);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                updateViewTags(container);
                if (mListener != null) {
                    mListener.finishDismissAnimation();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                updateViewTags(container);
                if (mListener != null) {
                    mListener.finishDismissAnimation();
                }
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                magnifyCards(animation, container, view);
            }
        });

        mAnimator = animator;
        animator.setDuration(DURATION_LAYOUT_DISMISS);
        animator.start();
    }

    @Override
    public void displayAnimation(final ViewGroup container, final View view) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1.0f);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                updateViewTags(container);
                if (mListener != null) {
                    mListener.finishDisplayAnimation();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                updateViewTags(container);
                if (mListener != null) {
                    mListener.finishDisplayAnimation();
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                updateViewHeight(view);
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                shrinkCards(animation, container, view);
            }
        });

        mAnimator = animator;
        animator.setDuration(DURATION_LAYOUT_DISMISS);
        animator.start();
    }

    private void shrinkCards(ValueAnimator animation, ViewGroup container, View view) {
        if (view == null) return;
        float value = (float) animation.getAnimatedValue();
        int children = container.getChildCount();
        int maxHeight = view.getHeight();
        //要移动的总数
        int i = 0;
        while (i < children) {
            //倒叙拿当前view的下标
            int index = children - 1 - i;
            View child = container.getChildAt(index);
            if (child == null) return;

            CardViewTag tag = (CardViewTag) child.getTag();
            if (tag == null) return;

            int oLeft = tag.getOriginalPaddingLeft();
            int tLeft = calculatePaddingLeft(index, children - 1, PADDING_STEP);
            int left = (int) (oLeft + (tLeft - oLeft) * value);
            int right = left;

            int oBottom = tag.getOriginalPaddingBottom();
            int tBottom = calculatePaddingBottom(index, children - 1, PADDING_STEP);
            int bottom = (int) (oBottom + (tBottom - oBottom) * value);

            int oTop = tag.getOriginalPaddingTop();
            int tTop = calculatePaddingTop(index, children - 1, PADDING_STEP);
            int top = (int) (oTop + (tTop - oTop) * value);
            child.setPadding(left, top, right, bottom);

            if (child.getMeasuredHeight() - child.getPaddingBottom() > maxHeight) {
                //减少高度
                child.getLayoutParams().height =
                    child.getMeasuredHeight() - (int) ((child.getMeasuredHeight() - maxHeight)
                        * value);
                child.requestLayout();
            }
            ++i;
        }
    }

    private void magnifyCards(ValueAnimator animation, ViewGroup container, View view) {
        if (view == null) return;
        float value = (float) animation.getAnimatedValue();
        int children = container.getChildCount();
        int maxHeight = view.getHeight();
        //要移动的总数
        int i = 0;
        while (i < children) {
            //倒叙拿当前view的下标
            int index = children - 1 - i;
            View child = container.getChildAt(index);
            if (child == null) return;

            CardViewTag tag = (CardViewTag) child.getTag();
            if (tag == null) return;

            //两边padding最终是diff-1 * paddingstep*2
            //公式 目标数值 = 当前值 + (目标值 - 当前值) * percent
            int oLeft = tag.getOriginalPaddingLeft();
            int tLeft = calculatePaddingLeft(index, children - 1, PADDING_STEP);
            int left = (int) (oLeft + (tLeft - oLeft) * value);
            int right = left;

            int oBottom = tag.getOriginalPaddingBottom();
            int tBottom = calculatePaddingBottom(index, children - 1, PADDING_STEP);
            int bottom = (int) (oBottom + (tBottom - oBottom) * value);

            int oTop = tag.getOriginalPaddingTop();
            int tTop = calculatePaddingTop(index, children - 1, PADDING_STEP);
            int top = (int) (oTop + (tTop - oTop) * value);
            child.setPadding(left, top, right, bottom);
            Log.e("msg",
                "index:" + index + ", l,t,r,b:" + left + ", " + top + ", " + right + ", " + bottom);

            if (index == children - 1 && //只回弹最上面的view
                tag != null && tag.getOriginalHeight() > maxHeight) {
                child.getLayoutParams().height =
                    child.getMeasuredHeight() + (int) ((tag.getOriginalHeight()
                        - child.getMeasuredHeight()) * value);
                child.requestLayout();
            }
            ++i;
        }
    }

    @Override
    public void syncAllView(ViewGroup container, boolean isAnimation) {

    }

    public static void updateViewTags(ViewGroup container) {
        if (container == null || container.getChildCount() == 0) {
            return;
        }
        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            CardViewTag tag = (CardViewTag) view.getTag();
            tag.setOriginalPadding(view.getPaddingLeft(), view.getPaddingTop(),
                view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    public static void updateViewHeight(View view) {
        if (view == null) return;
        CardViewTag tag = (CardViewTag) view.getTag();
        tag.setOriginalHeight(view.getHeight());
    }

    /**
     * @param index 在view树中的最大的下标
     * @param max view树中最大的下标.
     */
    public static int calculatePaddingLeft(int index, int max, int paddingStep) {
        int diff = index < max ? max - index : 0;
        diff = diff < ICardBagController.MAX_CARD ? diff : ICardBagController.MAX_CARD;
        return diff * paddingStep * 2;
    }

    public static int calculatePaddingRight(int index, int max, int paddingStep) {
        int diff = index < max ? max - index : 0;
        diff = diff < ICardBagController.MAX_CARD ? diff : ICardBagController.MAX_CARD;
        return diff * paddingStep * 2;
    }

    public static int calculatePaddingTop(int index, int max, int paddingStep) {
        int diff = index < max ? index : max;
        diff = diff < ICardBagController.MAX_CARD - 1 ? diff : ICardBagController.MAX_CARD - 1;
        return diff * paddingStep;
    }

    public static int calculatePaddingBottom(int index, int max, int paddingStep) {
        int diff = index < max ? max - index : 0;
        return diff * paddingStep;
    }
}
