package com.kuo.myapp.Fragment;

import android.support.v4.app.Fragment;
import android.view.animation.Animation;

import com.kuo.myapp.Util.TransitionAnimator;

public class BaseFragment extends Fragment implements TransitionAnimator {
    private boolean mTransitionAnimation;
    @Override
    public void disableTransitionAnimation() {
        mTransitionAnimation = false;
    }

    @Override
    public void enableTransitionAnimation() {
        mTransitionAnimation = true;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation result;
        if(!mTransitionAnimation){
            Animation dummyAnimation = new Animation() {
            };
            dummyAnimation.setDuration(0);
            result = dummyAnimation;
        } else {
            result = super.onCreateAnimation(transit, enter, nextAnim);
        }
        return result;
    }
}
