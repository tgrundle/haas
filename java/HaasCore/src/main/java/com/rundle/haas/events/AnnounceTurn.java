package com.rundle.haas.events;

import java.util.Collections;
import java.util.List;

import com.rundle.haas.Card;

public class AnnounceTurn extends Announcement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2273231857672775046L;
	private final String playerName;
	private final List<Card> validPlays;
	
	public AnnounceTurn(String playerName, List<Card> validPlays) {
		this.playerName = playerName;
		this.validPlays = validPlays;
	}

	public String getPlayerName() {
		return playerName;
	}

	public List<Card> getValidPlays() {
		return Collections.unmodifiableList(validPlays);
	}
}