package com.rundle.haas;

import java.util.Queue;

import com.rundle.haas.events.GameEvent;
import com.rundle.haas.events.GameEventHandler;

public class GameEventQueueManager {
	public final Queue<GameEvent> eventQueue;
	public final Object SYNC_NODE;
	public final GameEventHandler handler;
	private GameEventQueueListener thread;

	public GameEventQueueManager(GameEventHandler handler, PlayerHandlerLocalInteractive ph) {
		super();
		this.eventQueue = ph.messagesToClientQueue;
		this.SYNC_NODE = ph.SYNC_MSG_TO_CLIENT;
		this.handler = handler;
		resume();
	}

	public void pause() {
		if(thread != null) {
			thread.isActive = false;
			thread = null;
		}
	}

	public void resume() {
		if (thread != null && thread.isAlive()) {
			return;
		}
		thread = new GameEventQueueListener(this);
		thread.setPriority(10);
		thread.start();
	}

//	public void handleEvent(GameEvent event) throws InterruptedException {
//		if (event instanceof AnnouncePlayerSeated) {
//			AnnouncePlayerSeated seatedPlayer = (AnnouncePlayerSeated) event;
//			if (HaasActivity.playerName.equals(seatedPlayer.getPlayerName())) {
//				activity.sitDown(seatedPlayer);
//			} else {
//				activity.showWelcomeDialog(seatedPlayer);
//			}
//		} else if (event instanceof AnnounceBidder) {
//			activity.announceBidder((AnnounceBidder) event);
//		} else if (event instanceof AnnounceBid) {
//			activity.announceBid((AnnounceBid) event);
//		} else if (event instanceof AnnounceWinningBid) {
//			AnnounceWinningBid bidAnnouncement = (AnnounceWinningBid) event;
//			if (HaasActivity.playerName.equals(bidAnnouncement.getPlayerName())) {
//				System.out.println("You won the bidding with: " + bidAnnouncement.getBid());
//				System.out.println("Enter trump:");
//			} else {
//				System.out.println(bidAnnouncement.getPlayerName() + " wins the bidding with: " + bidAnnouncement.getBid());
//			}
//		} else if (event instanceof AnnounceDealtCard) {
//			AnnounceDealtCard dealtCardAnnouncement = (AnnounceDealtCard) event;
//			if (dealtCardAnnouncement.getPlayerName().equals(HaasActivity.playerName)) {
//				System.out.println("You were dealt the " + dealtCardAnnouncement.getCard());
//			} else {
//				System.out.println(dealtCardAnnouncement.getPlayerName() + " was dealt a card");
//			}
//		} else if (event instanceof ShowKiddie) {
//		} else if (event instanceof AnnounceKiddieDiscard) {
//			AnnounceKiddieDiscard announcement = (AnnounceKiddieDiscard) event;
//			if (announcement.getPlayerName().equalsIgnoreCase(HaasActivity.playerName)) {
//				System.out.println("You discarded the " + announcement.getCard());
//			} else {
//				System.out.println(announcement.getPlayerName() + " discard a card.");
//			}
//		} else if (event instanceof AnnounceTrump) {
//			System.out.println("Trump is ");
//		} else if (event instanceof AnnouncePartnerCard) {
//			AnnouncePartnerCard partnerCardAnnouncement = (AnnouncePartnerCard) event;
//			if (partnerCardAnnouncement.getPlayerName().equals(HaasActivity.playerName)) {
//				System.out.println("You are looking for the ");
//			} else {
//				System.out.println(partnerCardAnnouncement.getPlayerName() + " is looking for the ");
//			}
//		} else if (event instanceof AnnounceTurn) {
//			AnnounceTurn announceTurn = (AnnounceTurn) event;
//			// HaasActivity.allowMakePlay = true;
//			if (announceTurn.getPlayerName().equals(HaasActivity.playerName)) {
//				System.out.println("Your turn to play");
//			} else {
//				System.out.println("Waiting on " + announceTurn.getPlayerName() + " to play.");
//			}
//		} else if (event instanceof AnnouncePlay) {
//			AnnouncePlay play = (AnnouncePlay) event;
//			if (play.getPlay().getPlayer().getName().equals(HaasActivity.playerName)) {
//				// HaasActivity.allowMakePlay = false;
//				// HaasActivity.localHand.remove(play.getPlay().getCard());
//				System.out.println("You played the " + play.getPlay().getCard());
//			} else {
//				System.out.println(play.getPlay().getPlayer().getName() + " played the " + play.getPlay().getCard());
//			}
//		} else if (event instanceof AnnounceTrickWinner) {
//			AnnounceTrickWinner trickWinner = (AnnounceTrickWinner) event;
//			if (HaasActivity.playerName.equals(trickWinner.getPlayerName())) {
//				System.out.println("You won the trick!");
//			} else {
//				System.out.println(trickWinner.getPlayerName() + " won the trick!");
//			}
//		} else {
//			System.out.println("Recieved event: " + event);
//		}
//	}
}

class GameEventQueueListener extends Thread {

	private final GameEventQueueManager manager;
	boolean isActive = true;

	public GameEventQueueListener(GameEventQueueManager manager) {
		this.manager = manager;
	}

	@Override
	public void run() {
		try {
			while (isActive) {
				synchronized (manager.SYNC_NODE) {
					manager.SYNC_NODE.wait(5000);
				}
				// TODO - May lead to strange state
				while (!manager.eventQueue.isEmpty() && isActive) {
					GameEvent event = manager.eventQueue.poll();
					event.handleEvent(manager.handler);
				}
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
