package com.rundle.haas.events;

import java.util.Collections;
import java.util.List;

import com.rundle.haas.Table.Bid;

public class AnnounceBidder extends Announcement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5251728339813566308L;
	private final String playerName;
	private final List<Bid> validBids;
	
	public AnnounceBidder(String playerName, List<Bid> validBids) {
		this.playerName = playerName;
		this.validBids = validBids;
	}

	public String getPlayerName() {
		return playerName;
	}
	
	public List<Bid> getValidBids() {
		return Collections.unmodifiableList(validBids);
	}
}