package com.example.zhangchong.myapplication.cardbag;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.example.zhangchong.myapplication.CardViewTag;
import com.example.zhangchong.myapplication.cardbag.cardanimation.CardBagAnimationController;
import com.example.zhangchong.myapplication.cardbag.cardanimation.DefaultBagLayoutAnimation;
import com.example.zhangchong.myapplication.cardbag.cardanimation.DefaultBagLayoutListener;
import com.example.zhangchong.myapplication.cardbag.cardanimation.DefaultBagViewAnimation;
import com.example.zhangchong.myapplication.cardbag.cardanimation.ICardBagViewAnimationListener;
import com.example.zhangchong.myapplication.cardbag.cardanimation.ICardBagViewAnimation;

/**
 * Created by zhangchong on 2018/4/19.
 */

public class CardBagViewImpl extends FrameLayout implements ICardBagView{
    public static final int PADDING_STEP = 16;

    private CardBagAnimationController mAnimationController;
    private ICardBagViewAnimation mBagViewAnimation;
    private ICardBagViewAnimationListener mBagViewAnimationListener;
    private boolean mUseAnimation = true;
    private DefaultBagLayoutListener.IDefaultControllerLayoutListener mControllerLayoutListener;

    public CardBagViewImpl(@NonNull Context context) {
        super(context);
        init();
    }

    public CardBagViewImpl(@NonNull Context context, DefaultBagLayoutListener.IDefaultControllerLayoutListener controllerLayoutListener) {
        super(context);
        mControllerLayoutListener = controllerLayoutListener;
        init();
    }

    public CardBagViewImpl(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        initLayoutAnimation();
    }

    private void initLayoutAnimation() {
        if(!mUseAnimation
            || getLayoutAnimation() != null){
            return;
        }
        if (mAnimationController == null) {
            mAnimationController = new CardBagAnimationController();
        }
        LayoutTransition layoutTransition = mAnimationController.getLayoutTransition();
        if (layoutTransition == null) {
            LayoutTransition.TransitionListener listener =
                mAnimationController.getLayoutTransitionListener();
            if (listener == null) {
                initCardBagAnimation();
                listener = new DefaultBagLayoutListener(mAnimationController.getBagViewAnimation(), mControllerLayoutListener);
                mAnimationController.setLayoutTransitionListener(listener);
            }
            layoutTransition = new DefaultBagLayoutAnimation(listener);
            mAnimationController.setLayoutTransition(layoutTransition);
        }
        this.setLayoutTransition(layoutTransition);
    }

    private void initCardBagAnimation(){
        if (mAnimationController == null) {
            mAnimationController = new CardBagAnimationController();
        }
        ICardBagViewAnimation animation = mAnimationController.getBagViewAnimation();
        if (animation == null) {
            mBagViewAnimationListener = new CardBagListener();
            animation = new DefaultBagViewAnimation(mBagViewAnimationListener);
            mBagViewAnimation = animation;
            mAnimationController.setBagViewAnimation(animation);
        }
    }

    private void clearLayoutAnimation() {
        LayoutTransition layoutTransition = mAnimationController.getLayoutTransition();
        if(layoutTransition instanceof  DefaultBagLayoutAnimation){
            ((DefaultBagLayoutAnimation)layoutTransition).isRunning();

        }

        this.setLayoutTransition(null);
    }

    @Override
    public ViewGroup getBagView() {
        return this;
    }

    public static void updateViewTag(View view) {
        CardViewTag tag = (CardViewTag) view.getTag();
        tag.setOriginalPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(),
            view.getPaddingBottom());
    }

    @Override
    public void addView(int index, View view, boolean animation) {
        if (animation) {
            initLayoutAnimation();
        } else {
            clearLayoutAnimation();
        }
        addView(view, index);
    }

    @Override
    public void removeView(int index, boolean animation) {
        if (animation) {
            initLayoutAnimation();
        } else {
            clearLayoutAnimation();
        }

        removeViewAt(index);
    }

    @Override
    public void updateView(int index, View view, boolean animation) {
        if (index == getChildCount()) {
            addNewCard(view, animation);
            return;
        }

        int height = (int) (Math.random() * 200) + 400;
        FrameLayout.LayoutParams params =
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);

        view.setPadding(
            DefaultBagViewAnimation.calculatePaddingLeft(index, getChildCount(), PADDING_STEP),
            DefaultBagViewAnimation.calculatePaddingTop(index, getChildCount(), PADDING_STEP),
            DefaultBagViewAnimation.calculatePaddingLeft(index, getChildCount(), PADDING_STEP),
            DefaultBagViewAnimation.calculatePaddingBottom(index, getChildCount(), PADDING_STEP));

        updateViewTag(view);

        if (animation) {
            initLayoutAnimation();
        } else {
            clearLayoutAnimation();
        }
        addView(view, params);

        initCardBagAnimation();
        mBagViewAnimation.displayAnimation(this,  view);
    }

    @Override
    public void updateCardBag(boolean animation) {
        mBagViewAnimation.syncAllView(this, animation);
    }

    @Override
    public View getViewCard(int index) {
        if (index > getViewCount() - 1) {
            return null;
        }
        return getChildAt(index);
    }

    @Override
    public void addNewCard(final View view, boolean animation) {
        int index = getChildCount();
        //最大的top就是(卡片数量-1)*step
        int height = (int) (Math.random() * 200) + 400;
        FrameLayout.LayoutParams params =
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);

        view.setPadding(
            DefaultBagViewAnimation.calculatePaddingLeft(index, getChildCount(), PADDING_STEP),
            DefaultBagViewAnimation.calculatePaddingTop(index, getChildCount(), PADDING_STEP),
            DefaultBagViewAnimation.calculatePaddingRight(index, getChildCount(), PADDING_STEP),
            DefaultBagViewAnimation.calculatePaddingBottom(index, getChildCount(), PADDING_STEP));
        updateViewTag(view);

        if (animation) {
            initLayoutAnimation();
        } else {
            clearLayoutAnimation();
        }
        addView(view, params);
    }

    @Override
    public void removeNewCard(boolean animation) {
        if (getChildCount() == 0) {
            return;
        }
        View view = getNewCard();

        if (animation) {
            initLayoutAnimation();
        } else {
            clearLayoutAnimation();
        }
        removeView(view);
    }

    public void displayAnimation(final View view){
        if(view == null)
            return;
        post(new Runnable() {
            @Override
            public void run() {
                initCardBagAnimation();
                mBagViewAnimation.displayAnimation(CardBagViewImpl.this,  view);
            }
        });
    }

    public void dismissAnimation(final View view){
        if(view == null)
            return;
        post(new Runnable() {
            @Override
            public void run() {
                initCardBagAnimation();
                mBagViewAnimation.dismissAnimation(CardBagViewImpl.this,  view);
            }
        });
    }

    @Override
    public void updateNewCard(View view, boolean animation) {
        removeNewCard(animation);
        if (animation) {
            initLayoutAnimation();
        } else {
            clearLayoutAnimation();
        }
        addNewCard(view, animation);
    }

    @Override
    public View getNewCard() {
        if (getChildCount() == 0) {
            return null;
        }
        return getChildAt(getChildCount() - 1);
    }

    @Override
    public void clearViews() {
        removeAllViews();
    }

    @Override
    public int getViewCount() {
        return getChildCount();
    }

    class CardBagListener implements ICardBagViewAnimationListener {
        @Override
        public void finishDisplayAnimation() {

        }

        @Override
        public void finishDismissAnimation() {

        }
    }
}
