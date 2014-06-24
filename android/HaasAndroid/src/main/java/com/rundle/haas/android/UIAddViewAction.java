package com.rundle.haas.android;

import android.view.View;
import android.view.ViewGroup;

public class UIAddViewAction extends UIAction {

    // private final HaasActivity context;
    private final View view;
    private final ViewGroup mainView;
    private final int h;
    private final int w;
    public UIAddViewAction(HaasActivity context, View view, float w, float h) {
        this(context, view, (int) w, (int) h);
    }

    public UIAddViewAction(HaasActivity context, View view, int w, int h) {
        super(context);
        this.view = view;
        this.mainView = context.mainView;
        this.h = h;
        this.w = w;
    }

    @Override
    public void doRun() {
        mainView.addView(view);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(params != null) {
            params.height = h;
            params.width = w;
        }
        view.bringToFront();
        doNext();
    }
}
