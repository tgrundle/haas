package com.rundle.haas.android.holders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.view.View;
import android.widget.TextView;

import com.rundle.haas.R;
import com.rundle.haas.android.ViewHolder;

public class PlayerHolder implements ViewHolder {

	public final String name;
	public final int position;
	public final boolean showHand;
	public final List<CardHolder> hand = Collections
			.synchronizedList(new ArrayList<CardHolder>(12));
	private TextView view;
	private float x;
	private float y;

	public PlayerHolder(String name, int position, boolean showHand) {
		super();
		this.name = name;
		this.position = position;
		this.showHand = showHand;
	}

	public void setView(TextView view) {
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

    public void markAsBidder() {
        view.setTextColor(view.getContext().getResources().getColor(R.color.bidder_name));
    }

    public void markAsPartner() {
        view.setTextColor(view.getContext().getResources().getColor(R.color.partner_name));
    }

    public void reset() {
        view.setTextColor(view.getContext().getResources().getColor(R.color.standard_name));
    }
}
