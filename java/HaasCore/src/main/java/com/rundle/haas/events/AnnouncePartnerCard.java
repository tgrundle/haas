package com.rundle.haas.events;

import com.rundle.haas.Card;

public class AnnouncePartnerCard extends Announcement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5541106419078349631L;
	private final String playerName;
	private final Card card;

	public AnnouncePartnerCard(String playerName, Card card) {
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