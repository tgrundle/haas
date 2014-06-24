package com.rundle.haas.events;

public class AnnouncePartner extends Announcement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8705002606165171815L;
	private final String playerName;

	public AnnouncePartner(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerName() {
		return playerName;
	}

}