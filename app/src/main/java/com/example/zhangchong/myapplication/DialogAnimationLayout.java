package com.example.zhangchong.myapplication;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangchong on 2018/4/10.
 */

public class DialogAnimationLayout extends FrameLayout {
    public static final int MAX_CARD_VIEW = 3;
    private int mAppearHeight = 600;
    private LayoutTransition mTransition;
    private CardPackageUtils mUtils;
    private List<View> mPackageViews = new ArrayList<>();

    public DialogAnimationLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public DialogAnimationLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DialogAnimationLayout(@NonNull Context context, @Nullable AttributeSet attrs,
        int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mUtils = new CardPackageUtils();
        setLayoutTransition();
    }

    private void resetLayoutTransition() {
        clearLayoutTransition();
        mTransition = null;
        setLayoutTransition();
    }

    private void clearLayoutTransition() {
        this.setLayoutTransition(null);
    }

    private void setLayoutTransition() {
        if (mTransition != null) {
            this.setLayoutTransition(mTransition);
            return;
        }
        mTransition = new LayoutTransition();
        // 子View添加到mContainer时的动画
        mTransition.setAnimator(LayoutTransition.APPEARING, getAppearAnimation());

        // 子Veiw从mContainer中移除时的动画
        mTransition.setAnimator(LayoutTransition.DISAPPEARING, getDisAppearAnimation());

        mTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
            @Override
            public void startTransition(LayoutTransition transition, ViewGroup container, View view,
                int transitionType) {
                Log.e("msg", "starts animation");
            }

            @Override
            public void endTransition(LayoutTransition transition, ViewGroup container,final View view,
                final int transitionType) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        continueAnimation(view, transitionType);
                    }
                });
            }
        });

        this.setLayoutTransition(mTransition);
    }

    private void continueAnimation(View view, int transitionType){
        switch (transitionType) {
            case LayoutTransition.APPEARING:
                int viewHeight = view.getHeight();
                CardViewTag tag = new CardViewTag();
                tag.setOriginalHeight(viewHeight);
                view.setTag(tag);
                mUtils.shrinkCards(DialogAnimationLayout.this, viewHeight,
                    new CardControllerListener(0));
                break;
            case LayoutTransition.DISAPPEARING:
                addToFirstChild();
                mUtils.magnifyCards(DialogAnimationLayout.this,
                    new CardControllerListener(1));
                break;
            default:
                break;
        }
    }

    private Animator getAppearAnimation() {
        return ObjectAnimator.ofFloat(null, "translationY", -mAppearHeight, 0)
            .setDuration(CardPackageUtils.DURATION_LAYOUT_DISPLAY);
    }

    private Animator getDisAppearAnimation() {
        return ObjectAnimator.ofFloat(null, "translationY", 0, -mAppearHeight)
            .setDuration(CardPackageUtils.DURATION_LAYOUT_DISMISS);
    }

    public List<View> getPackageViews() {
        return mPackageViews;
    }

    public void setPackageViews(List<View> mPackageViews) {
        this.mPackageViews = mPackageViews;
    }

    public int getAppearHeight() {
        return mAppearHeight;
    }

    public void setAppearHeight(int mAppearHeight) {
        this.mAppearHeight = mAppearHeight;
    }

    public void addNewView(View view) {
        setLayoutTransition();
        int index = getChildCount();
        int paddingTop = CardPackageUtils.padding_step * index;
        int height = (int) (Math.random() * 200) + 400;
        FrameLayout.LayoutParams params =
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        view.setPadding(0, paddingTop, 0, 0);
        addView(view, params);
        mPackageViews.add(view);
    }

    public void removeNewChild() {
        setLayoutTransition();
        if (getChildCount() > 0) {
            removeViewAt(getChildCount() - 1);
        }
        if(mPackageViews.size() > 0){
            mPackageViews.remove(mPackageViews.size() - 1);
        }
    }

    public void addToFirstChild() {
        if (mPackageViews.size() < MAX_CARD_VIEW) {
            return;
        }
        //卡片夹有多余的卡片

        clearLayoutTransition();
        View view = mPackageViews.get(mPackageViews.size() - 1 - getChildCount());
        int left = MAX_CARD_VIEW * CardPackageUtils.padding_step;
        int right = MAX_CARD_VIEW * CardPackageUtils.padding_step;
        int bottom = 0;
        CardViewTag tag = (CardViewTag)view.getTag();
        if(tag != null){
            bottom = tag.getOriginalPadding();
        }
        view.setPadding(left, 0, right, bottom);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(view.getWidth(), view.getHeight());
        addView(view, 0, params);
    }

    public void removeFirstChild() {
        clearLayoutTransition();
        if (getChildCount() > 0) {
            removeViewAt(0);
        }
    }

    private class CardControllerListener implements CardPackageUtils.CardDisplayAnimationListener {
        //0控制添加图片.1控制去掉图片
        private int mType = 0;

        public CardControllerListener(int type) {
            mType = type;
        }

        @Override
        public void AnimationEnd() {
            switch (mType) {
                case 1:
                    showFirstChild();
                    break;
                default:
                    removeFirstChild();
                    break;
            }
        }

        private void showFirstChild() {

        }

        private void removeFirstChild() {
            if (getChildCount() > MAX_CARD_VIEW) {
                DialogAnimationLayout.this.removeFirstChild();
            }
        }
    }
}
