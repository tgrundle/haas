package com.rundle.haas.events;

import com.rundle.haas.Trump;

public class GetTrumpEvent extends GameEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7825011841913504845L;
	private final Trump trump;

	public GetTrumpEvent(Trump trump) {
		super();
		this.trump = trump;
	}

	public Trump getTrump() {
		return trump;
	}
	
	
}
