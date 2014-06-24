package com.rundle.haas;

public class Card implements Comparable<Card> {

	public enum CardValue {
		ACE, KING, QUEEN, JACK, TEN, NINE
	}

	public enum CardSuit {
		HEART, DIAMOND, CLUB, SPADE
	}

    public enum CardIndex {
        FIRST, SECOND
    }

    static final CardValue[] CARDVALUE_BYWEAKEST = new CardValue[]{CardValue.NINE, CardValue.TEN, CardValue.JACK, CardValue.QUEEN, CardValue.KING, CardValue.ACE};
    public final CardValue value;
	public final CardSuit suit;
    public final CardIndex index;
	private int weight;

	public Card(CardValue value, CardSuit suit, CardIndex index) {

		this.value = value;
		this.suit = suit;
        this.index = index;

		calculateWeight();
	}


	public void calculateWeight() {

		switch (suit) {
		case CLUB:
			weight = 0;
			break;
		case DIAMOND:
			weight = 8;
			break;
		case SPADE:
			weight = 16;
			break;
		case HEART:
			weight = 24;
			break;
		}
		switch (value) {
		case NINE:
			weight += 7;
			break;
		case TEN:
			weight += 6;
			break;
		case JACK:
			weight += 5;
			break;
		case QUEEN:
			weight += 4;
			break;
		case KING:
			weight += 3;
			break;
		case ACE:
			weight += 2;
			break;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Card)) {
			return Boolean.FALSE;
		}
		return this.weight == ((Card) o).weight && this.index == ((Card) o).index;
    }

    public boolean equalsCard(Card o) {
        return this.weight == ((Card) o).weight;
    }

    public void makeRightBar() {
		switch (suit) {
		case CLUB:
			weight = 0;
			break;
		case DIAMOND:
			weight = 8;
			break;
		case SPADE:
			weight = 16;
			break;
		case HEART:
			weight = 24;
			break;
		}
	}

	public void makeLeftBar() {
		switch (suit) {
		case CLUB:
			weight = 17;
			break;
		case SPADE:
			weight = 1;
			break;
		case DIAMOND:
			weight = 25;
			break;
		case HEART:
			weight = 9;
			break;
		}
	}

	public void resetWeight() {
		calculateWeight();
	}
	
	protected int weight() {
		return weight;
	}

	@Override
	public String toString() {
		return value.toString() + " of "+ suit.toString() + " (" + weight + ") ";
	}

	@Override
	public int compareTo(Card card) {
		
		if(card == null) {
			throw new NullPointerException();
		} else if (weight == card.weight) {
			return 0;
		} else if (weight > card.weight) {
			return 1;
		} else {
			return -1;
		}
	}
	
	
}
