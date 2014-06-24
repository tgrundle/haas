package com.rundle.haas.android.animation;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.rundle.haas.android.HaasActivity;
import com.rundle.haas.android.UIActionAnimationListener;
import com.rundle.haas.android.UIAnimationAction;
import com.rundle.haas.android.ViewHolder;

public class RemoveViewAnimation extends UIAnimationAction {

	// private final HaasActivity context;
	private final ViewHolder viewHolder;
	private final long duration;

	public RemoveViewAnimation(HaasActivity context, ViewHolder viewHolder, long duration) {
		super(context);
		this.viewHolder = viewHolder;
		this.duration = duration;
	}

	@Override
	protected Animation createAnimation() {
		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
		fadeOut.setDuration(duration);
		fadeOut.setFillAfter(false);
		return fadeOut;
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
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void doOnAnimationEnd(Animation animation) {
				viewHolder.getView().setAnimation(null);
				viewHolder.getView().setVisibility(View.INVISIBLE);
				context.mainView.removeView(viewHolder.getView());
				// ViewGroup parent = (ViewGroup) view.getParent();
				// if(parent != null) {
				// parent.removeView(view);
				// }
			}
		};
	}
}