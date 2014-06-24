package com.rundle.haas.events;

import com.rundle.haas.Card;

public class GetPlayEvent extends CardSelectionEvent {
	public GetPlayEvent(Card card) {
		super(card);
	}
}
