package com.rundle.haas.events;

import com.rundle.haas.Card;

public class GetHaasPartnerCardPassEvent extends CardSelectionEvent {

	public GetHaasPartnerCardPassEvent(Card card) {
		super(card);
	}
}
