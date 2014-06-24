package com.rundle.haas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rundle.haas.Card.CardSuit;
import com.rundle.haas.Card.CardValue;
import com.rundle.haas.Table.Bid;
import com.rundle.haas.Table.GameState;
import com.rundle.haas.events.*;

public class GameEngine extends Thread {

	private static final CardValue[] partnerCardOrder = new CardValue[] { CardValue.ACE, CardValue.KING, CardValue.QUEEN, CardValue.TEN, CardValue.NINE };

	private final Table table;
	private final PlayerRegistry registry;
	private boolean isActive = true;

	public GameEngine() {
		super("GameEngine");
		this.table = Table.getInstance();
		this.registry = PlayerRegistry.getInstance();
	}

	public GameEngine(SavedTable savedTable, PlayerRegistry registry) {
		super();
		this.table = Table.getInstance();
		this.registry = registry;
	}

	public void pause() {
		isActive = false;
	}

	public void run() {
		for (String aiPlayer : new String[] { "Anna", "TJ", "Kathryn", "Granville", "Timothy" }) {
			if (table.seatedPlayers == 5) {
				break;
			}
			registry.registerPlayerHandler(table, aiPlayer, new PlayerHandlerAI());
		}

		while (isActive) {
			switch (table.gamestate) {
			case ROUND_START:
				roundStart();
				break;
			case DEALING:
				// System.out.println("Dealing");
				deal();
				break;
			case ANNOUNCE_BIDDER:
				announceBidder();
				break;
			case ANNOUNCE_BID:
				announceBid();
				break;
			case BIDDING:
				bidding();
				break;
			case TRUMP_NAME:
				trumpName();
				break;
			case KIDDIE_VIEW:
				kiddieView();
				break;
			case KIDDIE_DISCARD:
				kiddieDiscard();
				break;
			case PARTNER_NAME:
				// System.out.println("Naming Partner");
				namePartner();
				registry.makePublicAnnouncement(new AnnouncePartnerCard(table.bidder.name, table.lookingForCard));
				break;
			case HAAS_FIND_PARTNER:
				// System.out.println("Finding HAAS partner");
				if (table.partner == null) {
					table.partner = table.bidder.playerToTheLeft;
				} else if (table.partner == table.bidder) {
					throw new RuntimeException("um");
				}

				if ( GameUtils.isCardInCollection(table.partner.hand, table.lookingForCard)) {
					/*
					 * for playerHandle in table.playerTable.values():
					 * playerHandle.announcePartners()
					 */

					table.gamestate = GameState.HAAS_SELECT_CARD_FOR_PARTNER;
				} else {
					// Announce Player declines
					table.partner = table.partner.playerToTheLeft;
				}
			case HAAS_SELECT_CARD_FOR_PARTNER:
				// System.out.println("Selecting cards to pass to partner");
				Card cardToPass = registry.getPlayerHandler(table.bidder.name).getCardsToPassToPartner();

				if (cardToPass == null) {
					break;
				}

				table.cardsToPassToPartner.add(cardToPass);
				table.bidder.hand.remove(cardToPass);

				registry.makePublicAnnouncement(new AnnounceCardPassedToPartner(table.bidder.name, cardToPass));

				if (table.cardsToPassToPartner.size() == 2) {
					table.gamestate = GameState.HAAS_PASS_TO_PARTNER;
				}
				break;
			case HAAS_PASS_TO_PARTNER:
				table.partner.hand.addAll(table.cardsToPassToPartner);
                //Announce Cards passed
				table.gamestate = GameState.HAAS_SELECT_CARD_FOR_BIDDER;
				break;
			case HAAS_SELECT_CARD_FOR_BIDDER:
				Card cardFromPartner = registry.getPlayerHandler(table.partner.name).getCardsToPassToBidder();

				table.cardsPassedFromPartner.add(cardFromPartner);

				if (table.cardsPassedFromPartner.size() == 2) {
					table.gamestate = GameState.HAAS_ADD_CARDS_FROM_PARTNER;
				}
				break;
			case HAAS_ADD_CARDS_FROM_PARTNER:
                //Announce card return
				table.bidder.hand.addAll(table.cardsPassedFromPartner);
				table.gamestate = GameState.TRICK_START;
				break;
			case TRICK_START:
				// System.out.println("Starting Trick");

				if (table.tricks.size() == 0) {
					table.waitingOnPlayer = table.bidder;
					table.playerInController = table.bidder;

					table.tricks.add(new Trick());
				} else if (table.tricks.get(table.tricks.size() - 1).complete) {
					table.tricks.add(new Trick());
				}
				table.gamestate = GameState.TRICK_ANNOUNCE_TURN;

				break;
			case TRICK_ANNOUNCE_TURN:
				List<Card> possiblePlays = GameUtils.determinePossiblePlays(table.waitingOnPlayer.hand);
				registry.makePublicAnnouncement(new AnnounceTurn(table.waitingOnPlayer.name, possiblePlays));
				table.gamestate = GameState.TRICK_PLAY;
			case TRICK_PLAY:
				trickPlay();
				break;
			case TRICK_OVER:
				// System.out.println("Trick Over");
				if (table.partner == null) {
					throw new RuntimeException("Some one false carded");
				}

				Trick completedTrick = table.tricks.get(table.tricks.size() - 1);
				registry.makePublicAnnouncement(new AnnounceTrickWinner(completedTrick.winner.getPlayer().name));
				table.waitingOnPlayer = completedTrick.winner.getPlayer();
				table.playerInController = completedTrick.winner.getPlayer();

				if (completedTrick.winner.getPlayer() == table.bidder || completedTrick.winner.getPlayer() == table.partner) {
					table.bidder.trickCount++;
					table.partner.trickCount++;
				} else {
					completedTrick.winner.getPlayer().trickCount++;
				}

				if (table.tricks.size() == 9) {
					table.gamestate = GameState.ROUND_OVER;
				} else {
					table.gamestate = GameState.TRICK_START;
				}
				break;
			case ROUND_OVER:
				roundOver();
				break;

			case GAME_OVER:
				isActive = false;
				break;
			} // switch
		} // while
	}

	private void trickPlay() {
		// System.out.println("Playing Trick");
		Trick trick = table.tricks.get(table.tricks.size() - 1);
		List<Card> possiblePlays = GameUtils.determinePossiblePlays(table.waitingOnPlayer.hand);
		Card cardPlayed = registry.getPlayerHandler(table.waitingOnPlayer.name).getPlay(possiblePlays);
		if (cardPlayed != null && possiblePlays.contains(cardPlayed)) {
			table.waitingOnPlayer.hand.remove(cardPlayed);
			Play play = new Play(table.waitingOnPlayer, cardPlayed);

			if (trick.winner == null) {
				trick.winner = play;
			} else if (!(GameUtils.isCardTrump(trick.winner.getCard(), table.trump)) && GameUtils.isCardTrump(play.getCard(), table.trump)) {
				trick.winner = play;
			} else {
				if (GameUtils.isBetter(trick.winner.getCard(), play.getCard(), table.trump)) {
					trick.winner = play;
				}

				if (table.partner == null && table.lookingForCard.equalsCard(play.getCard())) {
					table.partner = play.getPlayer();
                    registry.makePublicAnnouncement(new AnnouncePartner(table.partner.getName()));
				}
			}

			if (trick.plays.size() == 0) {
				trick.lead = play.getCard();
			}
			trick.plays.add(play);

			registry.makePublicAnnouncement(new AnnouncePlay(play));

			boolean isHaas = table.bid == Bid.HAAS || table.bid == Bid.DOUBLE_HAAS;

			trick.complete = trick.plays.size() == 5 || (isHaas && trick.plays.size() == 4);

			if (trick.complete) {
                table.gamestate = GameState.TRICK_OVER;
//				if (table.tricks.size() == 9) {
//					table.gamestate = GameState.ROUND_OVER;
//				} else {
//
//				}
			} else {
				table.waitingOnPlayer = table.waitingOnPlayer.playerToTheLeft;
				if (isHaas && table.waitingOnPlayer == table.partner) {
					table.waitingOnPlayer = table.waitingOnPlayer.playerToTheLeft;
				}

				table.gamestate = GameState.TRICK_ANNOUNCE_TURN;
			}
		}

	}

	private void kiddieDiscard() {
		// System.out.println("Getting Kiddie Discard");
		Card discard = registry.getPlayerHandler(table.bidder.name).getKiddieDiscard();
		if (discard == null) {
			return;
		}

		if (!table.bidder.hand.remove(discard)) {
			return;
		}

		table.kiddieDiscards.add(discard);
		registry.makePublicAnnouncement(new AnnounceKiddieDiscard(table.bidder.name, discard));

		if (table.bidder.hand.size() != 9) {
			return;
		}

		table.gamestate = GameState.PARTNER_NAME;
	}

	private void kiddieView() {
		// System.out.println("Viewing Kiddie");
		table.bidder.hand.addAll(table.kiddie);
		GameUtils.adjustCardListForTrump(table.bidder.hand, table.trump);
		registry.makePublicAnnouncement(new ShowKiddie(table.kiddie));
		table.gamestate = GameState.KIDDIE_DISCARD;
	}

	private void trumpName() {
		// System.out.println("Naming Trump");
		table.trump = registry.getPlayerHandler(table.bidder.name).getTrump();

		if (table.trump != null) {
			GameUtils.adjustCardListForTrump(table.kiddie, table.trump);
			for (Player player : table.players) {
				GameUtils.adjustCardListForTrump(player.hand, table.trump);
			}
			Collections.sort(table.bidder.hand);
			table.currentSuitWinners.put(table.trump.suit, new Card(CardValue.JACK, table.trump.suit, null));
			registry.makePublicAnnouncement(new AnnounceTrump(table.trump));
			table.gamestate = GameState.KIDDIE_VIEW;
		}
	}

	private void bidding() {
		table.waitingOnPlayer.bid = registry.getPlayerHandler(table.waitingOnPlayer.name).getBid();
		if (table.waitingOnPlayer.bid != null) {
			table.gamestate = GameState.ANNOUNCE_BID;
		}
	}

	private void roundOver() {
		// System.out.println("Round Over");
		int score;
		int tricksNeeded;
		switch (table.bid) {
		case DOUBLE_HAAS:
			score = 36;
			tricksNeeded = 9;
			break;
		case HAAS:
			score = 18;
			tricksNeeded = 9;
			break;
		case SEVEN:
			score = 7;
			tricksNeeded = 7;
			break;
		case SIX:
			score = 6;
			tricksNeeded = 6;
			break;
		case FIVE:
			score = 5;
			tricksNeeded = 5;
			break;
		case STUCK:
			score = 4;
			tricksNeeded = 4;
			break;
		default:
			throw new RuntimeException();
		}

        boolean madeBid = true;
		if (table.bidder.trickCount < tricksNeeded) {
			score = score * -1;
            madeBid = false;
		} else if (table.bidder.trickCount > score) {
			score = table.bidder.trickCount;
		}

        boolean haveWinner = false;
		for (Player player : table.players) {
			if (player == table.bidder || player == table.partner) {
				player.score += score;
			} else {
				player.score += player.trickCount;
			}

            if(player.score >= 64) {
                haveWinner = true;

            }
		}

        registry.makePublicAnnouncement(new AnnounceRoundResult(madeBid));

		// TODO Check Scores
		table.dealer = table.dealer.playerToTheLeft;
		table.bid = null;
		table.bidder = null;
		table.cardsPassedFromPartner.clear();
		table.waitingOnPlayer = null;
		table.trump = null;
		table.tricks.clear();
		table.playerInController = null;
		table.partner = null;
		table.lookingForCard = null;
		table.kiddieDiscards.clear();
		table.kiddie.clear();
		table.cardsToPassToPartner.clear();
		table.currentSuitWinners.clear();


        if(haveWinner) {
            table.gamestate = GameState.GAME_OVER;
        } else {
            table.gamestate = GameState.ROUND_START;
        }

	}

	private void announceBid() {
		registry.makePublicAnnouncement(new AnnounceBid(table.waitingOnPlayer.name, table.waitingOnPlayer.bid));
		if (table.waitingOnPlayer != table.dealer) {
			switch (table.waitingOnPlayer.bid) {
			case PASS:
				table.waitingOnPlayer = table.waitingOnPlayer.playerToTheLeft;
				break;
			case HAAS:
				table.bid = table.waitingOnPlayer.bid;
				table.bidder = table.waitingOnPlayer;
				table.waitingOnPlayer = table.dealer;
				break;
			default:
				table.bid = table.waitingOnPlayer.bid;
				table.bidder = table.waitingOnPlayer;
				table.waitingOnPlayer = table.waitingOnPlayer.playerToTheLeft;
			}

			table.gamestate = GameState.ANNOUNCE_BIDDER;
		} else {
			if (table.waitingOnPlayer.bid != Bid.PASS) {
				table.bid = table.waitingOnPlayer.bid;
				table.bidder = table.waitingOnPlayer;
			}

			// System.out.println(table.bidder.name + " bids  " +
			// table.bid);
			table.waitingOnPlayer = null;
			registry.makePublicAnnouncement(new AnnounceWinningBid(table.bidder.name, table.bid));
			table.gamestate = GameState.TRUMP_NAME;
		}

	}

	private void announceBidder() {
		if (table.waitingOnPlayer == null) {
			table.waitingOnPlayer = table.dealer.playerToTheLeft;
			table.bidder = table.dealer;
			table.bid = Bid.STUCK;
		}

		List<Bid> validBids = new ArrayList<>();

		switch (table.bid) {
		case STUCK:
			validBids.add(Bid.FIVE);
		case FIVE:
			validBids.add(Bid.SIX);
		case SIX:
			validBids.add(Bid.SEVEN);
		case SEVEN:
			validBids.add(Bid.HAAS);
		case HAAS:
			if (table.waitingOnPlayer == table.dealer && table.bid == Bid.HAAS) {
				validBids.add(Bid.DOUBLE_HAAS);
			}
		default:
			validBids.add(Bid.PASS);
		}
		registry.makePublicAnnouncement(new AnnounceBidder(table.waitingOnPlayer.name, validBids));
		table.gamestate = GameState.BIDDING;
	}

	private void roundStart() {
		for (Player player : table.players) {
			player.hand.clear();
			player.trickCount = 0;
		}
		table.currentSuitWinners.put(CardSuit.DIAMOND, new Card(CardValue.ACE, CardSuit.DIAMOND, null));
		table.currentSuitWinners.put(CardSuit.CLUB, new Card(CardValue.ACE, CardSuit.CLUB, null));
		table.currentSuitWinners.put(CardSuit.HEART, new Card(CardValue.ACE, CardSuit.HEART, null));
		table.currentSuitWinners.put(CardSuit.SPADE, new Card(CardValue.ACE, CardSuit.SPADE, null));
		table.gamestate = GameState.DEALING;
	}

	private void deal() {
		Deck deck = new Deck();
		deck.shuffle();
		Player playerToRecieveCard = table.dealer.playerToTheLeft;

		if (deck.cardList.size() != 48) {
			throw new RuntimeException("Invalid Deck");
		}
		for (Card card : deck.cardList) {
			if (table.kiddie.size() < 3) {
				table.kiddie.add(card);
				registry.makePublicAnnouncement(new AnnounceDealtCard(AnnounceDealtCard.KIDDIE, card));
			} else {
				playerToRecieveCard.hand.add(card);
				registry.makePublicAnnouncement(new AnnounceDealtCard(playerToRecieveCard.name, card));
				playerToRecieveCard = playerToRecieveCard.playerToTheLeft;
			}
		}
		if (table.kiddie.size() != 3) {
			throw new RuntimeException("Kiddie is invalid");
		}
		for (Player player : table.players) {
			if (player.hand.size() != 9) {
				throw new RuntimeException(player.name + " hand is invalid!");
			}
			Collections.sort(player.hand);
		}
		table.gamestate = GameState.ANNOUNCE_BIDDER;

	}

	private void namePartner() {
		List<Card> knownCardsToDealer = new ArrayList<Card>(12);

		knownCardsToDealer.addAll(table.bidder.hand);
		knownCardsToDealer.addAll(table.kiddieDiscards);

		if (knownCardsToDealer.size() != 12) {
			throw new RuntimeException("Known card list invalid");
		}
        Set<Card> invalidPartnerCardList = new HashSet<Card>();
        Set<Card> cardList = new HashSet<Card>();
		for (Card card : knownCardsToDealer) {
            System.out.println("Known Card: " + card.toString());
			if (GameUtils.isCardInCollection(cardList, card)) {
                System.out.println("Invalid Card: " + card.toString());
				invalidPartnerCardList.add(card);
			} else {
                cardList.add(card);
            }
		}
		Card card = null;
        Card leftBar = new Card(Card.CardValue.JACK, table.trump.suitLeftBar, null);
        leftBar.makeLeftBar();
        Card rightBar = new Card(Card.CardValue.JACK, table.trump.suit, null);
        rightBar.makeRightBar();

		if (!GameUtils.isCardInCollection(invalidPartnerCardList, rightBar)) {
			// //getLogger().log("Asking for Right Bar")
			card = rightBar;
		} else if (!GameUtils.isCardInCollection(invalidPartnerCardList, leftBar)) {
			// //getLogger().log("Asking for Left Bar")
			card = leftBar;
		} else {
			// //getLogger().log("Asking for Lower Trump")
			for (CardValue cardValue : partnerCardOrder) {
				card = new Card(cardValue, table.trump.suit, null);
				if (!GameUtils.isCardInCollection(invalidPartnerCardList, card)) {
					break;
				}
			}
		}

		table.lookingForCard = card;

		GameUtils.adjustCardForTrump(table.lookingForCard, table.trump);

		// System.out.println("Looking for the " + table.lookingForCard);

		if (table.bid == Bid.HAAS || table.bid == Bid.DOUBLE_HAAS) {
			table.gamestate = GameState.HAAS_FIND_PARTNER;
		} else {
			table.gamestate = GameState.TRICK_START;
		}
	}
}
