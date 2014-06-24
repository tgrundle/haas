package com.rundle.haas.events;

import com.rundle.haas.Card;

public abstract class CardSelectionEvent extends GameEvent {

    private final Card card;

    public CardSelectionEvent(Card card) {
        super();
        this.card = card;
    }

    public Card getCard() {
        return card;
    }
}
