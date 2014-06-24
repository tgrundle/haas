package com.rundle.haas.events;

import com.rundle.haas.Table.Bid;

public class GetBidEvent extends GameEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4873518430021522288L;
	private final Bid bid;

	public GetBidEvent(Bid bid) {
		super();
		this.bid = bid;
	}

	public Bid getBid() {
		return bid;
	}
}