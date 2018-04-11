package com.example.zhangchong.myapplication;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class AnimationActivity extends AppCompatActivity {
    public static final String TYPE_VIEW = "view";
    private DialogAnimationLayout mLayout;
    private int mRandom = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        mLayout = findViewById(R.id.animation_layout);
    }

    public void startAnimation(View view) {
        newView();
    }

    public void endAnimation(View view) {
        removeView();
    }



    private View newView() {
        FrameLayout frameLayout  = new FrameLayout(this);
        View view = new View(this);
        int i = (int) (Math.random() * 4);
        while (i == mRandom){
            i = (int) (Math.random() * 4);
        }
        switch (i) {
            case 0:
                view.setBackgroundColor(Color.RED);
                break;
            case 1:
                view.setBackgroundColor(Color.BLACK);
                break;
            case 2:
                view.setBackgroundColor(Color.GREEN);
                break;
            case 3:
                view.setBackgroundColor(Color.BLUE);
                break;
        }
        frameLayout.addView(view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mLayout.addNewView(frameLayout);
        mRandom = i;
        return view;
    }

    private void removeView(){
        mLayout.removeNewChild();
    }
}
