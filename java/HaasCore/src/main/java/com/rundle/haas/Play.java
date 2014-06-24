package com.rundle.haas;

public class Play {

	private final Player player;
	private final Card card;
	
	public Play(Player player, Card card) {
		super();
		this.player = player;
		this.card = card;
	}

	public Player getPlayer() {
		return player;
	}

	public Card getCard() {
		return card;
	}
	
}
