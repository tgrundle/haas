package com.rundle.haas.android.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.rundle.haas.android.HaasActivity;
import com.rundle.haas.android.UIActionAnimationListener;
import com.rundle.haas.android.UIAnimationAction;
import com.rundle.haas.android.ViewHolder;

public class MoveViewHolderAnimation extends UIAnimationAction {
	final ViewHolder viewHolder;
	final float destX;
	final float destY;
	final long speed;
	
	public MoveViewHolderAnimation(HaasActivity context, ViewHolder viewHolder,
                                   float destX, float destY, long speed) {
		super(context);
		this.viewHolder = viewHolder;
		this.destX = destX;
		this.destY = destY;
		this.speed = speed;
	}

	@Override
	protected Animation createAnimation() {
		float deltaX = destX - viewHolder.getX();
		float deltaY = destY - viewHolder.getY();
		TranslateAnimation animation = new TranslateAnimation(0, deltaX, 0,
				deltaY);
		animation.setDuration(speed);
		animation.setFillAfter(true);

		return animation;
	}

	@Override
	protected View getViewToAnimate() {
		return viewHolder.getView();
	}

	@Override
	protected UIActionAnimationListener createAnimationListener() {
		return new UIActionAnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
                viewHolder.setPosition(destX, destY);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void doOnAnimationEnd(Animation animation) {

                viewHolder.getView().bringToFront();
                viewHolder.getView().setX(destX);
                viewHolder.getView().setY(destY);
                viewHolder.getView().setAnimation(null);
			}
		};
	}
}