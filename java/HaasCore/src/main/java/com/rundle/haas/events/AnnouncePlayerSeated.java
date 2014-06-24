package com.rundle.haas.events;

public class AnnouncePlayerSeated extends Announcement {
	/**
	 * 
	 */
	private static final long serialVersionUID = -311690987944980626L;
	private final String playerName;
	private final int seatPosition;
	
	public AnnouncePlayerSeated(String playerName, int seatPosition) {
		this.playerName = playerName;
		this.seatPosition = seatPosition;
	}

	public String getPlayerName() {
		return playerName;
	}

	public int getSeatPosition() {
		return seatPosition;
	}

}