package com.rundle.haas.android;

import android.os.Handler;

public class UIDelayedAction extends UIAction {

	private final long delay;

	public UIDelayedAction(HaasActivity context, long delay) {
		super(context);
		this.delay = delay;
	}

	@Override
	void doRun() {
		final UIAction uiAction = this;
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				uiAction.doNext();
			}
		}, delay);

	}

}
