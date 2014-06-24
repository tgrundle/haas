package com.rundle.haas.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

import com.rundle.haas.Card;
import com.rundle.haas.Card.CardSuit;
import com.rundle.haas.Card.CardValue;
import com.rundle.haas.GameEngine;
import com.rundle.haas.GameEventQueueManager;
import com.rundle.haas.GameUtils;
import com.rundle.haas.Play;
import com.rundle.haas.Player;
import com.rundle.haas.PlayerHandlerLocalInteractive;
import com.rundle.haas.PlayerRegistry;
import com.rundle.haas.Table;
import com.rundle.haas.Table.Bid;
import com.rundle.haas.Trick;
import com.rundle.haas.Trump;
import com.rundle.haas.events.AnnounceBid;
import com.rundle.haas.events.AnnounceBidder;
import com.rundle.haas.events.AnnounceDealtCard;
import com.rundle.haas.events.AnnounceKiddieDiscard;
import com.rundle.haas.events.AnnouncePartner;
import com.rundle.haas.events.AnnouncePartnerCard;
import com.rundle.haas.events.AnnouncePlay;
import com.rundle.haas.events.AnnouncePlayerSeated;
import com.rundle.haas.events.AnnounceRoundResult;
import com.rundle.haas.events.AnnounceTrickWinner;
import com.rundle.haas.events.AnnounceTrump;
import com.rundle.haas.events.AnnounceTurn;
import com.rundle.haas.events.AnnounceWinningBid;
import com.rundle.haas.events.GameEvent;
import com.rundle.haas.events.GameEventHandler;
import com.rundle.haas.events.GetBidEvent;
import com.rundle.haas.events.GetKiddieDiscardEvent;
import com.rundle.haas.events.GetPlayEvent;
import com.rundle.haas.events.GetTrumpEvent;
import com.rundle.haas.events.ShowKiddie;

public class HaasConsole implements GameEventHandler {

	static String playerName = "Timothy";

	List<Card> localHand = new ArrayList<>();
	boolean isBidder = false;
	boolean allowBidding = false;
	boolean allowNamingTrump = false;
	boolean allowDiscarding = false;
	boolean allowMakePlay = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HaasConsole console = new HaasConsole(); 
		GameEngine gameEngine = new GameEngine();
		PlayerHandlerLocalInteractive phi = PlayerRegistry.getInstance()
				.registerLocalPlayer(Table.getInstance(), playerName);
		GameEventQueueManager eventListener = new GameEventQueueManager(
				console, phi);
		eventListener.resume();
		gameEngine.start();

		ConsoleInputThread cih = new ConsoleInputThread(console, phi);

		cih.start();

		try {
			cih.join();
		} catch (InterruptedException ie) {

		}

		if (gameEngine.isAlive()) {
			gameEngine.pause();
		}
		phi.pause();
	}

	public HaasConsole() {
	}

	void showCardList(String message, List<Card> cardList) {
		System.out.println(message);
		for (Card card : cardList) {
			System.out.println("\t" + card);
		}
	}

	static CardSuit convertStringToSuit(String suitAsString) {
		CardSuit suit = null;
		if ("C".equalsIgnoreCase(suitAsString)) {
			suit = CardSuit.CLUB;
		} else if ("D".equalsIgnoreCase(suitAsString)) {
			suit = CardSuit.DIAMOND;
		} else if ("H".equalsIgnoreCase(suitAsString)) {
			suit = CardSuit.HEART;
		} else if ("S".equalsIgnoreCase(suitAsString)) {
			suit = CardSuit.SPADE;
		}
		return suit;
	}

	static CardValue convertStringToValue(String valueAsString) {
		CardValue value = null;
		if ("A".equalsIgnoreCase(valueAsString)) {
			value = CardValue.ACE;
		} else if ("K".equalsIgnoreCase(valueAsString)) {
			value = CardValue.KING;
		} else if ("Q".equalsIgnoreCase(valueAsString)) {
			value = CardValue.QUEEN;
		} else if ("J".equalsIgnoreCase(valueAsString)) {
			value = CardValue.JACK;
		} else if ("10".equalsIgnoreCase(valueAsString)) {
			value = CardValue.TEN;
		} else if ("9".equalsIgnoreCase(valueAsString)) {
			value = CardValue.NINE;
		}
		return value;
	}

	@Override
	public void onAnnounceBid(AnnounceBid event) {
		System.out.println(event.getPlayerName() + " bids " + event.getBid());
	}

	@Override
	public void onAnnounceBidder(AnnounceBidder event) {
		if (playerName.equals(event.getPlayerName())) {
			showCardList("Your turn to bid: ", localHand);
			allowBidding = true;
		} else {
			System.out.println("Waiting on " + event.getPlayerName()
					+ " to bid");
		}
	}

	@Override
	public void onAnnounceDealtCard(AnnounceDealtCard event) {
		if (event.getPlayerName().equals(playerName)) {
			System.out.println("You were dealt the " + event.getCard());
			localHand.add(event.getCard());
			Collections.sort(localHand);
		} else {
			System.out.println(event.getPlayerName() + " was dealt a card");
		}
	}

	@Override
	public void onAnnounceKiddieDiscard(AnnounceKiddieDiscard event) {
		if (event.getPlayerName().equalsIgnoreCase(playerName)) {
			localHand.remove(event.getCard());
			System.out.println("You discarded the " + event.getCard());
		} else {
			System.out.println(event.getPlayerName() + " discard a card.");
		}
	}

	@Override
	public void onAnnouncePartner(AnnouncePartner event) {
		System.out.println(event.getPlayerName() + "is Partner");

	}

	@Override
	public void onAnnouncePartnerCard(AnnouncePartnerCard event) {
		if (event.getPlayerName().equals(playerName)) {
			System.out.println("You are looking for the " + event.getCard());
		} else {
			System.out.println(event.getPlayerName() + " is looking for the "
					+ event.getCard());
		}
	}

	@Override
	public void onAnnouncePlay(AnnouncePlay event) {
		if (event.getPlay().getPlayer().getName().equals(playerName)) {
			allowMakePlay = false;
			localHand.remove(event.getPlay().getCard());
			System.out.println("You played the " + event.getPlay().getCard());
		} else {
			System.out.println(event.getPlay().getPlayer().getName()
					+ " played the " + event.getPlay().getCard());
		}
	}

	@Override
	public void onAnnouncePlayerSeated(AnnouncePlayerSeated event) {
		System.out.println("Player Seated: "
				+ ((AnnouncePlayerSeated) event).getPlayerName());
	}

	@Override
	public void onAnnounceTrickWinner(AnnounceTrickWinner event) {
		if (playerName.equals(event.getPlayerName())) {
			System.out.println("You won the trick!");
		} else {
			System.out.println(event.getPlayerName() + " won the trick!");
		}
	}

	@Override
	public void onAnnounceTrump(AnnounceTrump event) {
		if (!isBidder) {
			System.out.println("Trump is " + event.getTrump());
		}
		GameUtils.adjustCardListForTrump(localHand, event.getTrump());
	}

	@Override
	public void onAnnounceTurn(AnnounceTurn event) {
		allowMakePlay = true;
		if (event.getPlayerName().equals(playerName)) {
			System.out.println("Your turn to play");
		} else {
			System.out.println("Waiting on " + event.getPlayerName()
					+ " to play.");
		}
	}

	@Override
	public void onAnnounceWinningBid(AnnounceWinningBid event) {
		if (playerName.equals(event.getPlayerName())) {
			System.out.println("You won the bidding with: " + event.getBid());
			isBidder = true;
			allowNamingTrump = true;
			System.out.println("Enter trump:");
		} else {
			System.out.println(event.getPlayerName()
					+ " wins the bidding with: " + event.getBid());
			isBidder = false;
		}
	}

	@Override
	public void onShowKiddie(ShowKiddie event) {
		List<Card> kiddie = event.getKiddie();
		if (isBidder) {
			localHand.addAll(kiddie);
			GameUtils.adjustCardListForTrump(localHand, Table.getInstance().getTrump());
			Collections.sort(localHand);
			showCardList("The Kiddie:", kiddie);
			showCardList("Your Hand:", localHand);
			System.out.println("Enter Card to discard: ");
			allowDiscarding = true;
		} else {
			showCardList("The Kiddie:", kiddie);
		}
	}

    @Override
    public void  onAnnounceRoundResult(AnnounceRoundResult event) {

    }
}

class ConsoleInputThread extends Thread {
	protected boolean isActive = true;
	private final Queue<GameEvent> eventQueue;
	private final Object SYNC_NODE;
	private final HaasConsole console;
	
	public ConsoleInputThread(HaasConsole console, PlayerHandlerLocalInteractive phi) {
		super();
		this.console = console;
		this.eventQueue = phi.messagesFromClientQueue;
		this.SYNC_NODE = phi.SYNC_MSG_FROM_CLIENT;
	}

	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			while (isActive) {
				// System.out.print("==> ");
				String commandLine = reader.readLine();
				if (commandLine == null || "".equals(commandLine)) {
					continue;
				}
				StringTokenizer st = new StringTokenizer(commandLine, " ");
				String command = st.nextToken();
				if ("quit".equalsIgnoreCase(command)
						|| "exit".equalsIgnoreCase(command)
						|| "end".equalsIgnoreCase(command)) {
					isActive = false;
				} else if ("help".equalsIgnoreCase(command)) {

				} else if (command.equalsIgnoreCase("bid")) {
					if (!console.allowBidding) {
						System.out.println("Command not valid at this time");
					} else if (st.countTokens() != 1) {
						System.out.println("Invalid command format: bid <int>");

					} else {
						String bidAsString = st.nextToken();
						try {
							int bidAsInt = Integer.parseInt(bidAsString);
							Bid bid = GameUtils.convertBidIntToEnum(bidAsInt);
							eventQueue.offer(new GetBidEvent(bid));
							synchronized (SYNC_NODE) {
								SYNC_NODE.notify();
							}
							console.allowBidding = false;
						} catch (NumberFormatException nfe) {
							System.out.println("Invalid command format: bid <int>. Use -1 to pass, 9 for hass and 18 for double hass.");
						}
					}
				} else if (command.equalsIgnoreCase("show")) {
					if (st.countTokens() != 1) {
						System.out
								.println("Invalid command format: show [hand | trump | bid | trick | partner | plays]");
					} else {
						String action = st.nextToken();
						if ("hand".equalsIgnoreCase(action)) {
							console.showCardList("Your Hand:", console.localHand);
						} else if ("trump".equalsIgnoreCase(action)) {
							System.out.println("Trump is "
									+ Table.getInstance().getTrump());
						} else if ("bid".equalsIgnoreCase(action)) {
							System.out.println("Bid is "
									+ Table.getInstance().getBid());
						} else if ("trick".equalsIgnoreCase(action)) {
							Trick trick = Table.getInstance().getCurrentTrick();

							if (trick == null) {
								System.out
										.println("There is no trick at this time");
							} else {
								System.out.println("Current Trick: ");
								for (Play play : trick.getPlays()) {
									System.out.println("\t"
											+ play.getPlayer().getName() + ": "
											+ play.getCard());
								}
							}
						} else if ("partners".equalsIgnoreCase(action)) {
							Player bidder = Table.getInstance().getBidder();
							Player partner = Table.getInstance().getPartner();
							if (bidder != null) {
								System.out.println("Bidder: "
										+ bidder.getName());
								if (partner != null) {
									System.out.println("Partner: "
											+ partner.getName());
								} else {
									System.out.println("Partner: none");
								}
							} else {
								System.out
										.println("There are no partners at this time.");
							}
						} else if ("plays".equalsIgnoreCase(action)) {
							List<Card> possiblePlays = GameUtils
									.determinePossiblePlays(console.localHand);
							System.out.println("Posisble plays are: ");
							for (Card card : possiblePlays) {
								System.out.println("\t" + card);
							}
						} else if ("score".equalsIgnoreCase(action)) {
							System.out.println("Current Scores:");
							for (Player player : Table.getInstance()
									.getPlayers()) {
								System.out.println("\t" + player.getName()
										+ " = " + player.getScore());
							}
						} else if ("count".equalsIgnoreCase(action)) {
							System.out.println("Current Count:");
							for (Player player : Table.getInstance()
									.getPlayers()) {
								System.out.println("\t" + player.getName()
										+ " = " + player.getTrickCount());
							}

						}
					}
				} else if (command.toLowerCase().startsWith("name")) {
					if (!console.allowNamingTrump) {
						System.out.println("Command not valid at this time");
					} else if (st.countTokens() != 2) {
						System.out
								.println("Invalid command format: name [trump | partner] <value>");
					} else {
						String action = st.nextToken();
						if ("trump".equalsIgnoreCase(action)) {
							String suitAsString = st.nextToken().toUpperCase();
							CardSuit suit = HaasConsole.convertStringToSuit(suitAsString);
							if (suit != null) {
								eventQueue.offer(new GetTrumpEvent(new Trump(
										suit)));
								synchronized (SYNC_NODE) {
									SYNC_NODE.notify();
								}
								console.allowNamingTrump = false;
							} else {
								System.out
										.println("Invalid value provided from trump: "
												+ suitAsString);
							}
						} else if ("partner".equalsIgnoreCase(action)) {

						} else {

						}
					}
				} else if (command.equalsIgnoreCase("discard")) {
					if (!console.allowDiscarding) {
						System.out.println("Command not valid at this time");
					} else if (st.countTokens() != 1) {
						System.out
								.println("Invalid command format: discard <card>");
					} else {
						String cardAsString = st.nextToken();
						int cardAsStringLength = cardAsString.length();
						if (cardAsStringLength != 2 && cardAsStringLength != 3) {
							System.out.println("Invalid card format: "
									+ cardAsString);
						} else {
							CardSuit suit = HaasConsole.convertStringToSuit(cardAsString
									.substring(cardAsStringLength - 1,
											cardAsStringLength));
							CardValue value = HaasConsole.convertStringToValue(cardAsString
									.substring(0, cardAsStringLength - 1));

							if (suit == null || value == null) {
								System.out.println("Invalid card format: "
										+ cardAsString);
							} else {
								Card card = new Card(value, suit, null);
								GameUtils.adjustCardForTrump(card, Table.getInstance().getTrump());
								if (console.localHand.contains(card)) {
									eventQueue.offer(new GetKiddieDiscardEvent(
											card));
									synchronized (SYNC_NODE) {
										SYNC_NODE.notify();
									}
									if (console.localHand.size() == 9) {
										console.allowDiscarding = false;
									}
								} else {
									System.out
											.println("Card in not in you hand: "
													+ cardAsString);
								}
							}
						}
					}
				} else if (command.equalsIgnoreCase("play")) {
					if (!console.allowMakePlay) {
						System.out.println("Command not valid at this time");
					} else if (st.countTokens() != 1) {
						System.out
								.println("Invalid command format: play <card>");
					} else {
						String cardAsString = st.nextToken();
						int cardAsStringLength = cardAsString.length();
						if (cardAsStringLength != 2 && cardAsStringLength != 3) {
							System.out.println("Invalid card format: "
									+ cardAsString);
						} else {
							CardSuit suit = HaasConsole.convertStringToSuit(cardAsString
									.substring(cardAsStringLength - 1,
											cardAsStringLength));
							CardValue value = HaasConsole.convertStringToValue(cardAsString
									.substring(0, cardAsStringLength - 1));

							if (suit == null || value == null) {
								System.out.println("Invalid card format: "
										+ cardAsString);
							} else {
								Card card = new Card(value, suit, null);
								GameUtils.adjustCardForTrump(card, Table.getInstance().getTrump());

								if (GameUtils.determinePossiblePlays(console.localHand)
										.contains(card)) {
									// localHand.remove(card);
									eventQueue.offer(new GetPlayEvent(card));
									synchronized (SYNC_NODE) {
										SYNC_NODE.notify();
									}
									if (console.localHand.size() == 9) {
										console.allowMakePlay = false;
									}
								} else {
									System.out.println("Invalid play: "
											+ cardAsString);
								}
							}
						}
					}
				} else if (command.equalsIgnoreCase("debug")) {
					String action = st.nextToken();
					if ("whohas".equalsIgnoreCase(action)) {
						String cardAsString = st.nextToken();
						int cardAsStringLength = cardAsString.length();
						CardSuit suit = HaasConsole.convertStringToSuit(cardAsString
								.substring(cardAsStringLength - 1,
										cardAsStringLength));
						CardValue value = HaasConsole.convertStringToValue(cardAsString
								.substring(0, cardAsStringLength - 1));

						if (suit == null || value == null) {
							System.out.println("Invalid card format: "
									+ cardAsString);
						} else {
							Card card = new Card(value, suit, null);
							GameUtils.adjustCardForTrump(card, Table.getInstance().getTrump());
							for (Player player : Table.getInstance()
									.getPlayers()) {
								if (player.getHand().contains(card)) {
									System.out.println(player.getName()
											+ " has the " + card);
								}
							}
						}
					} else if ("show".equalsIgnoreCase(action)) {
						if (st.hasMoreTokens()) {
							String object = st.nextToken();
							if ("knowncards".equalsIgnoreCase(object)) {
								System.out.println("Kiddie Discards:");
								for (Card card : Table.getInstance().getKiddieDiscards()) {
									System.out.println("\t" + card);
								}
								int trickNo = 1;
								for (Trick trick : Table.getInstance()
										.getTricks()) {
									System.out
											.println("Trick " + trickNo + ":");
									for (Play play : trick.getPlays()) {
										System.out.println("\t"
												+ play.getCard());
									}
									trickNo++;
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
