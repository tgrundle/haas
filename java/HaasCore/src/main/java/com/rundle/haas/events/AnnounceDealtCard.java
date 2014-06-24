package com.rundle.haas.events;

import com.rundle.haas.Card;

public class AnnounceDealtCard extends Announcement {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5262953815827387777L;

	public static final String KIDDIE = "KIDDIE";
	
	private final String playerName;
	private final Card card;

	public AnnounceDealtCard(String playerName, Card card) {
		this.playerName = playerName;
		this.card = card;
	}

	public String getPlayerName() {
		return playerName;
	}

	public Card getCard() {
		return card;
	}
}