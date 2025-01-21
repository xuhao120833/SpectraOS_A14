package com.htc.spectraos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.htc.spectraos.MyApplication;
import com.htc.spectraos.R;

import androidx.annotation.Nullable;

/**
 * Author:
 * Date:
 * Description:
 */
public class BaseActivity extends Activity implements View.OnClickListener, View.OnHoverListener, View.OnFocusChangeListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        setWallPaper();

        super.onResume();
    }

    public void setWallPaper(){
        if (MyApplication.otherDrawable!=null){
            RelativeLayout relativeLayout =findViewById(R.id.rl_main);
            if (relativeLayout!=null)
                relativeLayout.setBackground(MyApplication.otherDrawable);
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void startNewActivity(Class<?> cls){
        Intent intent = new Intent(this,cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        AnimationSet animationSet = new AnimationSet(true);
        v.bringToFront();
        if (hasFocus) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.10f,
                    1.0f, 1.10f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(150);
            animationSet.addAnimation(scaleAnimation);
            animationSet.setFillAfter(true);
            v.startAnimation(animationSet);
        } else {
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.10f, 1.0f,
                    1.10f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            animationSet.addAnimation(scaleAnimation);
            scaleAnimation.setDuration(150);
            animationSet.setFillAfter(true);
            v.startAnimation(animationSet);
        }
    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        int what = event.getAction();
        switch (what) {
            case MotionEvent.ACTION_HOVER_ENTER: // 鼠标进入view
                v.requestFocus();
                break;
            case MotionEvent.ACTION_HOVER_MOVE: // 鼠标在view上
                break;
            case MotionEvent.ACTION_HOVER_EXIT: // 鼠标离开view
                break;
        }
        return false;
    }
}
