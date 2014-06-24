package com.rundle.haas.events;

import com.rundle.haas.Card;

public class GetKiddieDiscardEvent extends CardSelectionEvent {

	public GetKiddieDiscardEvent(Card card) {
		super(card);
	}
}
