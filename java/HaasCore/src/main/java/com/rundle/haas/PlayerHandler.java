package com.rundle.haas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.rundle.haas.Card.CardSuit;
import com.rundle.haas.Card.CardValue;
import com.rundle.haas.Table.Bid;

public interface PlayerHandler {

	Bid getBid();

	Trump getTrump();

	Card getKiddieDiscard();

	Card getCardsToPassToPartner();

	Card getCardsToPassToBidder();

	Card getPlay(List<Card> possiblePlays);
	
	void pause();
	
	void resume();
}

class PlayerHandlerAI implements PlayerHandler {

	private final CardValue[] trickLeadOrder = new CardValue[] { CardValue.QUEEN, CardValue.KING, CardValue.TEN, CardValue.NINE, CardValue.ACE };
	private CardSuit preferredTrump;
	private final Table table = Table.getInstance();
	@Override
	public Bid getBid() {

		int maxPotentialBid = -100;

		// //getLogger().logCardList(table.waitingOnPlayer.name + "'s Hand { ",
		// table.waitingOnPlayer.hand)
		// //getLogger().log("   Bid\tSwing\tWeak\tSideAs\tTrump\tLosers")
		for (CardSuit suit : CardSuit.values()) {
			Trump trump = new Trump(suit);
			GameUtils.adjustCardListForTrump(table.waitingOnPlayer.hand, trump);
			List<Card> trumpCards = new ArrayList<>();
			List<Card> sideAces = new ArrayList<>();
//			List<Card> losers = new ArrayList<>();
//			List<Card> swings = new ArrayList<>();
			List<Card> weakTrump = new ArrayList<>();
			for (Card card : table.waitingOnPlayer.hand) {
				if (GameUtils.isCardTrump(card, trump)) {
					trumpCards.add(card);
				} else if (card.value == CardValue.ACE) {
					sideAces.add(card);
//				} else {
//					losers.add(card);
				}
			}

			if (trumpCards.size() < 3) {
				continue;
			}
			int swingCount = 1;

			for (Card trumpCard : trumpCards) {
				if (trump.isRightBar(trumpCard)) {
					swingCount += 1;
//					swings.add(trumpCard);
				} else if (trump.isLeftBar(trumpCard) && swingCount > 1) {
					swingCount += 1;
				} else if (trumpCard.value == CardValue.ACE && swingCount > 3) {
					swingCount += 1;
				} else if (trumpCard.value == CardValue.KING && swingCount > 5) {
					swingCount += 1;
				} else if (trumpCard.value == CardValue.QUEEN && swingCount > 7) {
					swingCount += 1;
				} else if (trumpCard.value == CardValue.TEN && swingCount > 9) {
					swingCount += 1;
				} else {
					weakTrump.add(trumpCard);
				}
			}

			int weakTrumpWinners = (weakTrump.size() - 1) / 2;
			// lets be optimistic
			int unaccountedTrump = 14 - (swingCount * 5) - weakTrumpWinners;
			if (unaccountedTrump < 0) {
				unaccountedTrump = 0;
			}

			int bidCount = swingCount + sideAces.size() + weakTrumpWinners + 1 - unaccountedTrump;

			// //getLogger().log(suit + " { " + str(bidCount) + "\t" +
			// str(swingCount) + "\t" + str(len(weakTrump)) + "\t" +
			// str(len(sideAces)) + "\t" + str(len(trumpCards)) + "\t" +
			// str(len(losers)))
			if (bidCount > maxPotentialBid) {
				preferredTrump = suit;
				maxPotentialBid = bidCount;
			}
		}
		for (Card card : table.waitingOnPlayer.hand) {
			card.resetWeight();
		}
		Bid bid;
		
		System.out.println(table.waitingOnPlayer.name + ": " + maxPotentialBid);
		boolean isDealer = table.waitingOnPlayer == table.bidder;
		// If we are dealer && there == no bid we are stuck
		if (isDealer && table.bid == Bid.STUCK && maxPotentialBid < 7) {
			bid = Bid.STUCK;
		} else if (isDealer && table.bid == Bid.HAAS && maxPotentialBid > 6) {
			bid = Bid.DOUBLE_HAAS;
		} else {
			// can't make 5 so pass
			if (maxPotentialBid < 5) {
				bid = Bid.PASS;
				// can't beat the current bid, pass
			} else if (maxPotentialBid <= GameUtils.convertBidEnumToInt(table.bid)) {
				bid = Bid.PASS;
			} else {
				bid = GameUtils.convertBidIntToEnum(maxPotentialBid);
			}
		}
		return bid;
	}

	@Override
	public Trump getTrump() {
		return new Trump(preferredTrump);
	}

	@Override
	public Card getKiddieDiscard() {
		List<Card> possibleDiscards = new ArrayList<>();
		List<Card> sideAces = new ArrayList<>();
		// getLogger().logCardList("Hand: ", table.bidder.hand)

		for (Card card : table.bidder.hand) {
			if (GameUtils.isCardTrump(card, table.trump)) {
				continue;
			}

			if (card.value == CardValue.ACE) {
				sideAces.add(card);
			} else {
				possibleDiscards.add(card);
			}
		}
		// getLogger().logCardList("PossibleDiscards: ", possibleDiscards)

		Card discard;
		int discardIndex;
		Random rand = new Random();
		if (possibleDiscards.size() == 1) {
			discard = possibleDiscards.get(0);
		} else if (possibleDiscards.size() > 1) {
			discardIndex = rand.nextInt(possibleDiscards.size() - 1);
			discard = possibleDiscards.get(discardIndex);
		} else if (sideAces.size() == 1) {
			discard = sideAces.get(0);
		} else if (sideAces.size() > 1) {
			discardIndex = rand.nextInt(sideAces.size() - 1);
			discard = sideAces.get(discardIndex);
		} else {
			discardIndex = table.bidder.hand.size() - 1;
			discard = table.bidder.hand.get(discardIndex);
		}
		return discard;
	}

	@Override
	public Card getCardsToPassToPartner() {
		List<Card> nonTrump = new ArrayList<>();
		List<Card> cardsToPass = new ArrayList<>();
		for (Card card : table.bidder.hand) {
			if (!GameUtils.isCardTrump(card, table.trump)) {
				nonTrump.add(card);
			}
		}
		if (nonTrump.isEmpty()) {
			// Take two weakest trump
			cardsToPass.add(table.bidder.hand.get(table.bidder.hand.size() - 1));
			cardsToPass.add(table.bidder.hand.get(table.bidder.hand.size() - 2));

		} else if (nonTrump.size() == 1) {
			// Take non trump && weakest trump
			cardsToPass.add(nonTrump.get(0));
			if (nonTrump.get(0) == table.bidder.hand.get(table.bidder.hand.size() - 1)) {
				cardsToPass.add(table.bidder.hand.get(table.bidder.hand.size() - 2));
			} else {
				cardsToPass.add(table.bidder.hand.get(table.bidder.hand.size() - 1));
			}
		} else if (nonTrump.size() == 2) {
			cardsToPass.addAll(nonTrump);
		} else {
			// take weakest non trump.
			for (CardValue cardValue : Card.CARDVALUE_BYWEAKEST) {
				for (Card card : nonTrump) {
					if (card.value == cardValue && cardsToPass.size() < 2) {
						cardsToPass.add(card);
					}
				}
			}
		}

		Card cardToPass = cardsToPass.get(0);
		table.bidder.hand.remove(cardToPass);
		return cardToPass;
	}

	@Override
	public Card getCardsToPassToBidder() {

        Collections.sort(table.partner.hand);

        for(Card cardInHand: table.partner.hand ) {
            if(GameUtils.isCardTrump(cardInHand, table.trump)) {
				return cardInHand;
            }
        }

        return table.partner.hand.get(table.partner.hand.size() - 1);

	}

	@Override
	public Card getPlay(List<Card> possiblePlays) {
		// time.sleep(random.uniform(1,2))
		Card play = null;
		String playLogic = "";
		boolean isBidder = table.waitingOnPlayer == table.bidder;
		boolean isPartner = table.waitingOnPlayer == table.partner;
		Collections.sort(possiblePlays);

		Trick trick = table.tricks.get(table.tricks.size() - 1);
		if (possiblePlays.size() == 1) {
			playLogic = "One option";
			play = possiblePlays.get(0);
		} else {
			// Determine strategy

			if (trick.winner != null) {
				boolean isBidderOrPartnerWinning = trick.winner.getPlayer() == table.bidder || trick.winner.getPlayer() == table.partner;
				List<Card> potentialTrickWinners = new ArrayList<>();

				for (Card possiblePlay : possiblePlays) {
					if (GameUtils.isBetter(trick.winner.getCard(), possiblePlay, table.trump)) {
						potentialTrickWinners.add(possiblePlay);
					}
				}
				Collections.sort(potentialTrickWinners);

				Card winningCard;
				// isWinner play off otherwise take trick
				if (table.trump.isLeftBar(trick.winner.getCard())) {
					winningCard = table.currentSuitWinners.get(table.trump.suit);
				} else {
					winningCard = table.currentSuitWinners.get(trick.winner.getCard().suit);
				}
				// Is partner/bidder && bidder/partner winning
				if ((isBidder || isPartner) && isBidderOrPartnerWinning) {

					// partner has it won, throw lowest power card
					if (trick.plays.size() == 4 || winningCard.equalsCard(trick.winner.getCard())) {
						playLogic = "Partner has it won";
						play = possiblePlays.get(possiblePlays.size() - 1);
						// partner doesn't have winner so take it
					} else if ((play = GameUtils.cardInCollection(potentialTrickWinners, winningCard)) != null) {
						playLogic = "Taking Control";
						// } else if ((non-trump lead && only trump left trump
						// to win
					}
				}
				// Is partner/bidder && bidder/partner loosing
				if ((isBidder || isPartner) && !isBidderOrPartnerWinning) {
					// If we have the winning card play it
					if ((play = GameUtils.cardInCollection(potentialTrickWinners, winningCard)) != null) {
						playLogic = "We can win it";
						// Otherwise play strongest potential winners
					} else if (potentialTrickWinners.size() > 0) {
						if (!(GameUtils.isCardTrump(trick.lead, table.trump)) && GameUtils.isCardTrump(potentialTrickWinners.get(0), table.trump)) {
							if (trick.plays.size() == 4 || trick.plays.size() == 3
									&& (table.waitingOnPlayer.playerToTheLeft == table.bidder || table.waitingOnPlayer.playerToTheLeft == table.partner)) {
								playLogic = "we should trump with weakest";
								play = potentialTrickWinners.get(potentialTrickWinners.size() - 1);
							} else {
								playLogic = "we should trump with strongest";
								play = potentialTrickWinners.get(0);
							}
						} else {
							playLogic = "we could win it";
							play = potentialTrickWinners.get(0);
						}
					}
				}
				// Is non partner && partner/bidder winning
				if (!isBidder && !isPartner && isBidderOrPartnerWinning) {
					// If we have the winning card play it
					if ((play = GameUtils.cardInCollection(potentialTrickWinners, winningCard)) != null) {
						playLogic = "I can win it from them";
						// Otherwise play strongest potential winners
					} else if (potentialTrickWinners.size() > 0) {
						if (GameUtils.isCardTrump(trick.lead, table.trump)) {
							playLogic = "I must overstick";
							play = potentialTrickWinners.get(potentialTrickWinners.size() - 1);
						} else {
							playLogic = "I could win it from them";
							play = potentialTrickWinners.get(0);
						}
					}
				}
				// Is non partner && partner/bidder loosing
				if (!isBidder && !isPartner && !isBidderOrPartnerWinning) {
					// If we have the winning card play it
					if ((play = GameUtils.cardInCollection(potentialTrickWinners, winningCard)) != null) {
						playLogic = "I want it";
						// Otherwise play strongest potential winners
					} else if (potentialTrickWinners.size() > 0) {
						play = potentialTrickWinners.get(0);
						if (GameUtils.isCardTrump(play, table.trump) && trick.plays.size() == 4) {
							// save trump
							play = null;
						} else {
							playLogic = "I would like it";
						}
					}
				}

				if (play == null) {
					if (possiblePlays.size() == table.waitingOnPlayer.hand.size()) {
						playLogic = "Play weakest : hand";
						for (CardValue valuePower : Card.CARDVALUE_BYWEAKEST) {
							for (Card possiblePlay : possiblePlays) {
								// trying to play weak, filter out trump
								if (!GameUtils.isCardTrump(possiblePlay, table.trump)
										&& possiblePlay.value == valuePower) {
									play = possiblePlay;
									break;
								}
							}
							if (play != null) {
								break;
							}
						}
					} else {
						playLogic = "Play weakest";
						play = possiblePlays.get(possiblePlays.size() - 1);
					}
				}
				// Is starting Round
			} else if (trick.plays.isEmpty() && table.tricks.size() == 1) {
				if (table.bid == Bid.DOUBLE_HAAS || table.bid == Bid.HAAS) {
					playLogic = "StartHaasRound";
                    for (Card possiblePlay : possiblePlays) {
                        if (possiblePlay.value == Card.CardValue.JACK && possiblePlay.suit == table.trump.suit) {
                            play = possiblePlay;
                            break;
                        }
                    }
				} else {
					playLogic = "StartNormalRound";
					for (CardValue valuePower : trickLeadOrder) {
						for (Card possiblePlay : possiblePlays) {
							if (possiblePlay.value == valuePower && GameUtils.isCardTrump(possiblePlay, table.trump)) {
								play = possiblePlay;
								break;
							}
						}
						if (play != null) {
							break;
						}
					}

					// TODO Ask for Left Bar, then who knows
				}
				// Starting Trick
			} else {
				Card nonTrumpWinner = null;
				Card trumpWinner = null;
				List<Card> trumpInHand = new ArrayList<>();
				for (Card possiblePlay : possiblePlays) {
					if (GameUtils.isCardTrump(possiblePlay, table.trump)) {
						trumpInHand.add(possiblePlay);
						if (table.currentSuitWinners.get(table.trump.suit).equalsCard(possiblePlay)) {
							trumpWinner = possiblePlay;
						}
					} else {
						if (table.currentSuitWinners.get(possiblePlay.suit).equalsCard(possiblePlay)) {
							nonTrumpWinner = possiblePlay;
						}
					}
				}
				if (trumpWinner != null && (isBidder || isPartner)) {
					playLogic = "We have a trump winner";
					play = trumpWinner;
				} else if (nonTrumpWinner != null) {
					playLogic = "We have a nontrump winner";
					play = nonTrumpWinner;
				} else if (trumpInHand.size() > 0 && isPartner) {
					playLogic = "Play into Bidder";
					for (CardValue value : trickLeadOrder) {
                        //Won't hit left bar
                        for (Card possiblePlay : possiblePlays) {
                            if (possiblePlay.value == value && possiblePlay.suit == table.trump.suit) {
                                play = possiblePlay;
                                break;
                            }
                        }
					}
				}
			}
		}

		if (play == null) {
			playLogic = "Random";
			int possiblePlaysIndex = (new Random()).nextInt(possiblePlays.size());
			play = possiblePlays.get(possiblePlaysIndex);
		}

		System.out.println("Play Logic: " + playLogic);
		return play;
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
	
	
}
