package com.rundle.haas.events;

import java.util.List;

import com.rundle.haas.Card;

public class ShowKiddie extends Announcement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2680311334276860601L;
	private final List<Card> kiddie;

	public ShowKiddie(List<Card> kiddie) {
		super();
		this.kiddie = kiddie;
	}
	
	public List<Card> getKiddie(){
		return kiddie;
	}
}
