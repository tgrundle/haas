package com.rundle.haas.android.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.rundle.haas.android.HaasActivity;
import com.rundle.haas.android.UIActionAnimationListener;
import com.rundle.haas.android.UIAnimationAction;
import com.rundle.haas.android.holders.BidHolder;

public class BidAnimation extends UIAnimationAction {

	private final BidHolder bid;
	private final float destX;
	private final float destY;

	public BidAnimation(HaasActivity context, BidHolder bid, float destX,
			float destY) {
		super(context);
		this.bid = bid;
		this.destX = destX;
		this.destY = destY;
	}

	@Override
	protected Animation createAnimation() {
		if (bid.getView() == null) {
			TextView bidView = new TextView(context);
			bidView.setTag(bid);
			bidView.setText(bid.getBid().toString());
			bidView.setX(bid.getX());
			bidView.setY(bid.getY());
			context.mainView.addView(bidView);
			bid.setView(bidView);
		}
		float deltaX = destX - bid.getX();
		float deltaY = destY - bid.getY();
		TranslateAnimation animation = new TranslateAnimation(0, deltaX, 0,
				deltaY);
		animation.setDuration(HaasActivity.ANIMATION_CARDDEALT_SPEED);
		animation.setFillAfter(true);
		return animation;
	}

	@Override
	protected View getViewToAnimate() {
		return bid.getView();
	}

	@Override
	protected UIActionAnimationListener createAnimationListener() {
		return new UIActionAnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void doOnAnimationEnd(Animation animation) {
				bid.setPosition(destX, destY);
				bid.getView().setX(destX);
				bid.getView().setY(destY);
				bid.getView().setAnimation(null);
			}
		};
	}
}
