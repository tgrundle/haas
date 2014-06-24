package com.rundle.haas.android.holders;

import android.view.View;
import android.widget.TextView;

import com.rundle.haas.Table.Bid;
import com.rundle.haas.android.ViewHolder;

public class BidHolder implements ViewHolder {

	private final Bid bid;
	private final String playerName;
	public String getPlayerName() {
		return playerName;
	}

	private float x;
	private float y;
	private TextView view;
	
	public BidHolder(Bid bid, String playerName) {
		super();
		this.bid = bid;
		this.playerName = playerName;
	}

	public Bid getBid() {
		return bid;
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

	public View getView() {
		return view;
	}

	public void setView(TextView view) {
		this.view = view;
	}
	
}
