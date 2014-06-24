package com.rundle.haas.android;

import android.app.Fragment;
import android.app.FragmentTransaction;


public class UIDialogAction extends UIAction {

	private final Fragment fragment;
	private final int viewId;
	
	public UIDialogAction(HaasActivity context, Fragment fragment, int viewId) {
		super(context);
		this.fragment = fragment;
		this.viewId = viewId;
	}

	@Override
	public void doRun() {
		
		FragmentTransaction transaction = context.getFragmentManager()
				.beginTransaction();
		transaction.add(viewId, fragment);
		transaction.commit();
		
	}

}
