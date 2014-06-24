package com.rundle.haas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.rundle.haas.Table.Bid;

public class GameUtils extends Thread {

	public static boolean isBetter(Card currentWinner, Card currentPlay, Trump trump) {

		boolean betterCard;
		if (trump.isRightBar(currentWinner)) {
			betterCard = Boolean.FALSE;
		} else if (trump.isRightBar(currentPlay)) {
			betterCard = Boolean.TRUE;
		} else if (trump.isLeftBar(currentWinner)) {
			betterCard = Boolean.FALSE;
		} else if (trump.isLeftBar(currentPlay)) {
			betterCard = Boolean.TRUE;
		} else if (currentWinner.suit == currentPlay.suit) {
			betterCard = currentPlay.weight() < currentWinner.weight();
		} else if (currentPlay.suit == trump.suit) {
			betterCard = Boolean.TRUE;
		} else {
			betterCard = Boolean.FALSE;
		}
		return betterCard;
	}

	public static void adjustCardListForTrump(List<Card> cardList, Trump trump) {
		for (Card card : cardList) {
			adjustCardForTrump(card, trump);
		}
		Collections.sort(cardList);
	}

	public static void adjustCardForTrump(Card card, Trump trump) {

		card.resetWeight();

		if (trump.isLeftBar(card)) {
			card.makeLeftBar();
		} else if (trump.isRightBar(card)) {
			card.makeRightBar();
		}

		// GameEngine.sortHand(cardList);
	}

	public static boolean isCardTrump(Card card, Trump trump) {
		boolean sameSuit = card.suit == trump.suit;
		boolean leftBar = trump.isLeftBar(card);
		return sameSuit || leftBar;
	}

	public static Bid convertBidIntToEnum(int maxPotentialBid) {
		Bid bid;

		if (maxPotentialBid == 5) {
			bid = Bid.FIVE;
		} else if (maxPotentialBid == 6) {
			bid = Bid.SIX;
		} else if (maxPotentialBid == 7) {
			bid = Bid.SEVEN;
		} else if (maxPotentialBid > 6) {
			bid = Bid.HAAS;
		} else {
			bid = Bid.PASS;
		}
		return bid;
	}

	public static int convertBidEnumToInt(Bid bid) {
		int bidAsInt;
		switch (bid) {
		case HAAS:
			bidAsInt = 9;
			break;
		case SEVEN:
			bidAsInt = 7;
			break;
		case SIX:
			bidAsInt = 6;
			break;
		case FIVE:
			bidAsInt = 5;
			break;
		default:
			bidAsInt = 4;
		}
		// Bid currentBid, boolean isDealer
		return bidAsInt;
	}

	public static List<Card> determinePossiblePlays(List<Card> hand) {
		List<Card> possiblePlays = new ArrayList<Card>();

		Table table = Table.getInstance();
		Trick trick = table.tricks.get(table.tricks.size() - 1);
		boolean isLeadPlay = trick.plays.size() == 0;
		boolean isFirstTrick = table.tricks.size() == 1;
		if (isFirstTrick && isLeadPlay) {
			if (table.bid == Bid.HAAS || table.bid == Bid.DOUBLE_HAAS) {
				possiblePlays.addAll(hand);
			} else {
				List<Card> strongTrump = new ArrayList<Card>();
				for (Card card : hand) {
					if (GameUtils.isCardTrump(card, table.trump)) {
						if (GameUtils.isBetter(card, table.lookingForCard, table.trump)) {
							possiblePlays.add(card);
						} else {
							strongTrump.add(card);
						}
					}
				}

				if (possiblePlays.size() == 0) {
					if (strongTrump.size() > 0) {
						Collections.sort(strongTrump);
						possiblePlays.addAll(strongTrump);
					} else {
						possiblePlays.addAll(hand);
					}
				}
			}
		} else if (table.partner == null &&  isCardInCollection(hand, table.lookingForCard)) {
            possiblePlays.add(cardInCollection(hand, table.lookingForCard));
		} else if (isLeadPlay) {
			possiblePlays.addAll(hand);
		} else if (GameUtils.isCardTrump(trick.lead, table.trump)) {
			// Must beat trump
			List<Card> allTrump = new ArrayList<Card>();
			for (Card card : hand) {
				if (GameUtils.isCardTrump(card, table.trump)) {
					allTrump.add(card);
					if (GameUtils.isBetter(trick.winner.getCard(), card, table.trump)) {
						possiblePlays.add(card);
					}
				}
			}

			if (possiblePlays.isEmpty() && allTrump.isEmpty()) {
				possiblePlays.addAll(hand);
			} else if (possiblePlays.isEmpty()) {
				possiblePlays.addAll(allTrump);
			}

		} else {
			Card.CardSuit suit = trick.lead.suit;
			for (Card card : hand) {
				if (card.suit == suit && !(table.trump.isLeftBar(card))) {
					possiblePlays.add(card);
				}
			}
			if (possiblePlays.isEmpty()) {
				possiblePlays.addAll(hand);
			}
		}
		return possiblePlays;
	}

    public static Boolean isCardInCollection(Collection<Card> listOfCards, Card card) {
        Boolean containsCard = Boolean.FALSE;

        for(Card cardInList : listOfCards) {
            containsCard = card.equalsCard(cardInList);

            if(containsCard) break;
        }

        return containsCard;
    }

    public static Card cardInCollection(Collection<Card> listOfCards, Card card) {
        Card theCardInList = null;

        for(Card cardInList : listOfCards) {
            if( card.equalsCard(cardInList)) {
                theCardInList = cardInList;
                break;
            }
        }

        return theCardInList;
    }

}
