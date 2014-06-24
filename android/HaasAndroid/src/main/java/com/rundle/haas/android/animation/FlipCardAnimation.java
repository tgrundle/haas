package com.rundle.haas.android.animation;

//import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.rundle.haas.android.HaasActivity;
import com.rundle.haas.android.UIActionAnimationListener;
import com.rundle.haas.android.UIAnimationAction;
import com.rundle.haas.android.holders.CardHolder;

public class FlipCardAnimation extends UIAnimationAction {

	// private final HaasActivity context;
	private final CardHolder cardHolder;
	private final long duration;

	public FlipCardAnimation(HaasActivity context, CardHolder cardHolder, long duration) {
		super(context);
		this.cardHolder = cardHolder;
		this.duration = duration;
	}

	@Override
	protected Animation createAnimation() {
		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
		fadeOut.setDuration(duration);
		fadeOut.setFillAfter(true);
		return fadeOut;
	}

	@Override
	protected View getViewToAnimate() {
		return cardHolder.getView();
	}

	@Override
	protected UIActionAnimationListener createAnimationListener() {
		return new UIActionAnimationListener() {
			final ImageView cardView = new ImageView(context);

			@Override
			public void onAnimationStart(Animation animation) {
				cardView.setVisibility(View.INVISIBLE);
				if (cardHolder.isShown()) {
					cardView.setImageBitmap(context.drawMaster.mCardHidden);
				} else {
					cardView.setImageBitmap(context.drawMaster.bitmapForCard(cardHolder.card));
				}
				cardView.setX(cardHolder.getX());
				cardView.setY(cardHolder.getY());
				context.mainView.addView(cardView);
				ViewGroup.LayoutParams params = cardView.getLayoutParams();
				if (params != null) {
					params.height = cardHolder.getView().getHeight();
					params.width = cardHolder.getView().getWidth();
				}

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void doOnAnimationEnd(Animation animation) {
				cardHolder.getView().setAnimation(null);
				View oldView = cardHolder.getView();
				oldView.setVisibility(View.INVISIBLE);

				// ViewGroup parent = (ViewGroup) oldView.getParent();
				// if(parent != null) {
				// parent.removeView(oldView);
				// }
				cardHolder.flip();
				cardHolder.setImageView(cardView);
				cardView.setVisibility(View.VISIBLE);
			}
		};
	}

}