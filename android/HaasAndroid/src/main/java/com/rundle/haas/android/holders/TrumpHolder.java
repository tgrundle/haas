package com.rundle.haas.android.holders;

import android.view.View;
import android.widget.ImageView;

import com.rundle.haas.Trump;
import com.rundle.haas.android.ViewHolder;


public class TrumpHolder implements ViewHolder {
    public final Trump trump;
    private ImageView view;
    private float x;
    private float y;

    public TrumpHolder (Trump trump, ImageView view) {
        this.trump = trump;
        this.view = view;
    }

    public View getView() {
        return view;
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
