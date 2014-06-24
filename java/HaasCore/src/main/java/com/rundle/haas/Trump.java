package com.rundle.haas;

import com.rundle.haas.Card.CardSuit;

public class Trump {

	protected final Card.CardSuit suit;
    protected final Card.CardSuit suitLeftBar;

    public Trump(Card.CardSuit suit) {
        this.suit = suit;
   		switch(suit) {
    			case CLUB:
    	            this.suitLeftBar = Card.CardSuit.SPADE;
    	            break;
    			case SPADE:
    	            this.suitLeftBar = Card.CardSuit.CLUB;
    	            break;
    			case DIAMOND:
    	            this.suitLeftBar = Card.CardSuit.HEART;
    	            break;
    			case HEART:
    	            this.suitLeftBar = Card.CardSuit.DIAMOND;
    	            break;
    	        default:
    	        	throw new RuntimeException("Invalid suit provided to Trump constructor");
    		}
    }

	@Override
	public String toString() {
		return suit.toString();
	}
    
	public CardSuit getSuit() {
		return suit;
	}

    public Boolean isRightBar(Card card) {
        return card.suit == this.suit && card.value == Card.CardValue.JACK;
    }

    public Boolean isLeftBar(Card card) {
        return card.suit == this.suitLeftBar && card.value == Card.CardValue.JACK;
    }

}
