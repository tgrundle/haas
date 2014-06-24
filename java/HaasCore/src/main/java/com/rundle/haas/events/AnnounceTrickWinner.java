package com.rundle.haas.events;

public class AnnounceTrickWinner extends Announcement {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8277874152767778960L;
	private final String playerName;

	public AnnounceTrickWinner(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerName() {
		return playerName;
	}

}