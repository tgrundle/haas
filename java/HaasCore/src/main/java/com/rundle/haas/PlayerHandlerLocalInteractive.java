package com.rundle.haas;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.rundle.haas.Table.Bid;
import com.rundle.haas.events.*;

public class PlayerHandlerLocalInteractive implements PlayerHandler {

	public final Object SYNC_MSG_TO_CLIENT = new Object();
	public final Object SYNC_MSG_FROM_CLIENT = new Object();
	public final Queue<GameEvent> messagesToClientQueue = new ConcurrentLinkedQueue<>();
	public final Queue<GameEvent> messagesFromClientQueue = new ConcurrentLinkedQueue<>();
	private ReceivedMessagesThread thread;

	final Object SYNC_NODE = new Object();

	GetBidEvent bidEvent = null;
	GetHaasPartnerCardPassEvent haasPartnerCardPassEvent = null;
	GetKiddieDiscardEvent kiddieDiscardEvent = null;
	GetPlayEvent playEvent = null;
	GetTrumpEvent trumpEvent = null;

	PlayerHandlerLocalInteractive() {
		resume();
	}

	@Override
	public Bid getBid() {
		synchronized (SYNC_NODE) {
			try {
				SYNC_NODE.wait(5000);
			} catch (InterruptedException ignored) {
			}
		}

		Bid bid = null;
		if (bidEvent != null) {
			bid = bidEvent.getBid();
			bidEvent = null;
		}
		return bid;
	}

	@Override
	public Trump getTrump() {
		synchronized (SYNC_NODE) {
			try {
				SYNC_NODE.wait(5000);
			} catch (InterruptedException ignored) {
			}
		}
		Trump trump = null;
		if (trumpEvent != null) {
			trump = trumpEvent.getTrump();
			trumpEvent = null;
		}
		return trump;
	}

	@Override
	public Card getKiddieDiscard() {
		synchronized (SYNC_NODE) {
			try {
				SYNC_NODE.wait(5000);
			} catch (InterruptedException ignored) {
			}
		}
		Card card = null;
		if (kiddieDiscardEvent != null) {
			card = kiddieDiscardEvent.getCard();
			kiddieDiscardEvent = null;
		}
		return card;
	}

	@Override
	public Card getCardsToPassToPartner() {
		synchronized (SYNC_NODE) {
			try {
				SYNC_NODE.wait(5000);
			} catch (InterruptedException ignored) {
			}
		}
		Card card = null;
		if (haasPartnerCardPassEvent != null) {
			card = haasPartnerCardPassEvent.getCard();
			haasPartnerCardPassEvent = null;
		}
		return card;

	}

	@Override
	public Card getCardsToPassToBidder() {
		return null;
	}

	@Override
	public Card getPlay(List<Card> possiblePlays) {
		synchronized (SYNC_NODE) {
			try {
				SYNC_NODE.wait(5000);
			} catch (InterruptedException ignored) {
			}
		}
		Card play = null;
		if (playEvent != null) {
			play = playEvent.getCard();
			playEvent = null;
		}
		return play;

	}

	public void pause() {
		if (thread != null) {
			thread.isActive = false;
			thread = null;
		}
	}

	public void resume() {
		if (thread != null && thread.isAlive()) {
			return;
		}
		thread = new ReceivedMessagesThread(this);
		// Hack for Android
		thread.setPriority(10);
		thread.start();
	}

	static class ReceivedMessagesThread extends Thread {

		protected boolean isActive = true;
		private final PlayerHandlerLocalInteractive phi;

		ReceivedMessagesThread(PlayerHandlerLocalInteractive phi) {
			super();
			this.phi = phi;
		}

		public void run() {

			while (isActive) {
				try {
					synchronized (phi.SYNC_MSG_FROM_CLIENT) {
						phi.SYNC_MSG_FROM_CLIENT.wait(5000);
					}

					GameEvent event = phi.messagesFromClientQueue.poll();
					if (event != null) {
						if (event instanceof GetBidEvent) {
							phi.bidEvent = (GetBidEvent) event;
						} else if (event instanceof GetHaasPartnerCardPassEvent) {
							phi.haasPartnerCardPassEvent = (GetHaasPartnerCardPassEvent) event;
						} else if (event instanceof GetKiddieDiscardEvent) {
							phi.kiddieDiscardEvent = (GetKiddieDiscardEvent) event;
						} else if (event instanceof GetPlayEvent) {
							phi.playEvent = (GetPlayEvent) event;
						} else if (event instanceof GetTrumpEvent) {
							phi.trumpEvent = (GetTrumpEvent) event;
						}
						synchronized (phi.SYNC_NODE) {
							phi.SYNC_NODE.notify();
						}
					}
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}