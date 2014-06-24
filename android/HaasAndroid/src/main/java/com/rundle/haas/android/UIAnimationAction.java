package com.rundle.haas.android;

import android.view.View;
import android.view.animation.Animation;


public abstract class UIAnimationAction extends UIAction {

	protected UIAnimationAction(HaasActivity context) {
		super(context);
	}

	void doRun() {
		Animation animation;
		UIActionAnimationListener listener;

		animation = createAnimation();
		listener = createAnimationListener();
		if (listener == null) {
			listener = new UIActionAnimationListener();
		}
		listener.setAction(this);
		listener.setContext(context);
		animation.setAnimationListener(listener);
		View viewToAnimate = getViewToAnimate();
		viewToAnimate.bringToFront();
		viewToAnimate.startAnimation(animation);
	}

	protected abstract Animation createAnimation();

	protected abstract View getViewToAnimate();

	protected UIActionAnimationListener createAnimationListener() {
		return null;
	}
}