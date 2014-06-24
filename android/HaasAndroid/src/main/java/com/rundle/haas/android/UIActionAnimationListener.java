package com.rundle.haas.android;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class UIActionAnimationListener implements AnimationListener {

	private HaasActivity context;
	private UIAction action;

	public UIActionAnimationListener() {
		super();
	}

	@Override
	public final void onAnimationEnd(Animation animation) {
		
		doOnAnimationEnd(animation);
        action.doNext();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	protected void doOnAnimationEnd(Animation animation) {
	}

	public HaasActivity getContext() {
		return context;
	}

	public void setContext(HaasActivity context) {
		this.context = context;
	}

	public void setAction(UIAction action) {
		this.action = action;
	}

}