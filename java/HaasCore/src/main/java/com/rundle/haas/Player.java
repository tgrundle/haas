package com.rundle.haas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rundle.haas.Table.Bid;

public class Player {

	protected final List<Card> hand = new ArrayList<Card>();
	protected String name;
	protected int seat;
	protected int score;
	protected int trickCount;
	protected Player playerToTheLeft;
	//protected Player playerToTheRight;
	protected Bid bid;

	public String getName() {
		return name;
	}

	public List<Card> getHand() {
		return Collections.unmodifiableList(hand);
	}

	public int getScore() {
		return score;
	}

	public int getTrickCount() {
		return trickCount;
	}
	
	public Bid getBid() {
		return bid;
	}
}
