package com.rundle.haas.android.animation;

import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.rundle.haas.android.HaasActivity;
import com.rundle.haas.android.UIActionAnimationListener;
import com.rundle.haas.android.UIAnimationAction;
import com.rundle.haas.android.holders.PlayerHolder;

public class SeatedPlayerAnimation extends UIAnimationAction {

	private final PlayerHolder player;
	private final float destX;
	private final float destY;

	public SeatedPlayerAnimation(HaasActivity context, PlayerHolder player,
			float destX, float destY) {
		super(context);
		this.player = player;
		this.destX = destX;
		this.destY = destY;
	}

	@Override
	protected Animation createAnimation() {
		// TODO Move view stuff to Draw Master
		if (player.getView() == null) {
			TextView playerView = new TextView(context);
			playerView.setTag(player);
			playerView.setText(player.name);
			playerView.setX(player.getX());
			playerView.setY(player.getY());

			if (player.position == 0) {
				playerView.setGravity(Gravity.CENTER);
			} else if (player.position < 3) {
				playerView.setGravity(Gravity.RIGHT);
			} else {
				playerView.setGravity(Gravity.LEFT);
			}
			context.mainView.addView(playerView);
			playerView.getLayoutParams().height = (int) 20;
			playerView.getLayoutParams().width = (int) 75;

			player.setView(playerView);
		}
		float deltaX = destX - player.getX();
		float deltaY = destY - player.getY();
		TranslateAnimation animation = new TranslateAnimation(0, deltaX, 0,
				deltaY);
		animation.setDuration(HaasActivity.ANIMATION_CARDDEALT_SPEED);
		animation.setFillAfter(true);

		return animation;
	}

	@Override
	protected View getViewToAnimate() {
		return player.getView();
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
				player.setPosition(destX, destY);
				player.getView().setX(destX);
				player.getView().setY(destY);
				player.getView().setAnimation(null);
			}
		};
	}

}
