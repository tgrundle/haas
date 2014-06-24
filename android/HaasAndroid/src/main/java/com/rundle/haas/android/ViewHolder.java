package com.rundle.haas.android;

import android.view.View;

public interface ViewHolder {

    public float getX();

    public float getY();

    public void setPosition(float x, float y);

    public View getView();

}
