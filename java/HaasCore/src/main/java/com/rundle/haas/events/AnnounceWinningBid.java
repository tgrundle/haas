package com.rundle.haas.events;

import com.rundle.haas.Table.Bid;

public class AnnounceWinningBid extends Announcement {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7413062522785523251L;
	private final String playerName;
	private final Bid bid;

	public  AnnounceWinningBid(String playerName, Bid bid) {
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