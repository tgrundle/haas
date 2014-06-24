package com.rundle.haas.events;

import com.rundle.haas.Play;

public class AnnouncePlay extends Announcement {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8900642609178416265L;
	private final Play play;

	public AnnouncePlay(Play play) {
		this.play = play;
	}

	public Play getPlay() {
		return play;
	}

}