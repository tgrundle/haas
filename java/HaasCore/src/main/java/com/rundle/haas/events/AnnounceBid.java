package com.rundle.haas.events;

import com.rundle.haas.Table.Bid;

public class AnnounceBid extends Announcement {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1799075650775940427L;
	private final String playerName;
	private final Bid bid;

	public AnnounceBid(String playerName, Bid bid) {
		this.playerName = playerName;
		this.bid = bid;
	}

	public String getPlayerName() {
		return playerName;
	}

	public Bid getBid() {
		return bid;
	}
	
}