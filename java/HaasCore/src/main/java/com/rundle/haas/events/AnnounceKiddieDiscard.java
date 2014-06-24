package com.rundle.haas.events;

import com.rundle.haas.Card;

public class AnnounceKiddieDiscard extends Announcement {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2566874338259805625L;
	private final String playerName;
	private final Card card;

	public AnnounceKiddieDiscard(String playerName, Card card) {
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