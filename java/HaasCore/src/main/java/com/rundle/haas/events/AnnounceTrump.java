package com.rundle.haas.events;

import com.rundle.haas.Trump;

public class AnnounceTrump extends Announcement{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7479446076642668960L;
	private final Trump trump;

	public AnnounceTrump(Trump trump) {
		super();
		this.trump = trump;
	}

	public Trump getTrump() {
		return trump;
	}
	
}
