package com.rundle.haas.android;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rundle.haas.GameEngine;
import com.rundle.haas.GameEventQueueManager;
import com.rundle.haas.PlayerHandlerLocalInteractive;
import com.rundle.haas.PlayerRegistry;
import com.rundle.haas.R;
import com.rundle.haas.Table;
import com.rundle.haas.Table.Bid;
import com.rundle.haas.Trump;
import com.rundle.haas.android.animation.BidAnimation;
import com.rundle.haas.android.animation.FlipCardAnimation;
import com.rundle.haas.android.animation.MoveViewHolderAnimation;
import com.rundle.haas.android.animation.RemoveViewAnimation;
import com.rundle.haas.android.dialogs.MakeBidDialog;
import com.rundle.haas.android.dialogs.NameTrumpDialog;
import com.rundle.haas.android.holders.BidHolder;
import com.rundle.haas.android.holders.CardHolder;
import com.rundle.haas.android.holders.PlayerHolder;
import com.rundle.haas.android.holders.TrumpHolder;
import com.rundle.haas.events.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

public class HaasActivity extends Activity implements GameEventHandler {
	// public static final int ANNOUNCEMENTTIME = 175;
	public static final int ANIMATION_CARDDEALT_SPEED = 375;
	public static final int ANIMATION_CARDPLAY_SPEED = 775;
	public static final int ANIMATION_CARDINHAND_SPEED = 10;
	public static final int ANIMATION_FLIP_KIDDIE = 15;
	public static final int VIEW_KIDDIE_TIME = 3750;

	private GameEngine gameEngine;
	private PlayerHandlerLocalInteractive phi;
	private GameEventQueueManager eventListener;
	// private BroadcastReceiver eventProcessor;
	public DrawMaster drawMaster;
	public ViewGroup mainView;

	static String playerName = "Timothy";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		drawMaster = new DrawMaster(this);
		setContentView(R.layout.activity_main);
		// final Context context = this;
		mainView = ((ViewGroup) findViewById(R.id.main_view));
		mainView.addOnLayoutChangeListener(new OnLayoutChangeListener() {

			@Override
			public void onLayoutChange(View v, int left, int top, int right,
					int bottom, int oldLeft, int oldTop, int oldRight,
					int oldBottom) {
				// its possible that the layout is not complete in which case
				// we will get all zero values for the positions, so ignore the
				// event
				if (left == 0 && top == 0 && right == 0 && bottom == 0) {
					return;
				}

				boolean sizeChanged = left != oldLeft && top != oldTop
						&& right != oldRight && bottom != oldBottom;
				boolean notInitialized = drawMaster.getKiddieX() == -1;

				if (sizeChanged || notInitialized) {
					drawMaster.setScreenSize(mainView);
					// Move cards on table
				}
			}
		});

		// eventProcessor = new BroadcastReceiver() {
		//
		// @Override
		// public void onReceive(Context context, Intent intent) {
		//
		// String methodName = (String) intent.getExtras().getString(
		// "Method");
		// //Log.d("HaasActivity", "Received event: " + methodName);
		//
		// Object gameEvent = intent.getExtras().getSerializable("Args");
		//
		// try {
		// Method method = HaasActivity.class.getMethod(methodName,
		// gameEvent.getClass());
		// method.invoke(HaasActivity.this, new Object[] { gameEvent });
		//
		// } catch (InvocationTargetException ite) {
		// Log.e("HaasActivity", "BroadcastReceiver", ite.getTargetException());
		// } catch (Throwable t) {
		// Log.e("HaasActivity", "BroadcastReceiver", t);
		// }
		// }
		//
		// };

		// IntentFilter filter = new IntentFilter("HAAS_EVENT");
		// LocalBroadcastManager.getInstance(this).registerReceiver(
		// eventProcessor, filter);
	}

	@Override
	protected void onResume() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				gameEngine = new GameEngine();

				if (phi == null) {
					phi = PlayerRegistry.getInstance().registerLocalPlayer(
							Table.getInstance(), "Timothy");

				} else {
					phi.resume();
				}
				if (eventListener == null) {
					eventListener = new GameEventQueueManager(
							GameEventHandlerAdapter
									.newInstance(HaasActivity.this),
							phi);
				} else {
					eventListener.resume();
				}
				gameEngine.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
				gameEngine.start();

			}
		}, 2000);

		super.onResume();
	}

	@Override
	protected void onPause() {
		gameEngine.pause();
		eventListener.pause();
		phi.pause();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onAnnouncePlayerSeated(AnnouncePlayerSeated seatedPlayer) {

		boolean localPlayer = seatedPlayer.getPlayerName().equals(playerName);
		PlayerHolder player = new PlayerHolder(seatedPlayer.getPlayerName(),
				seatedPlayer.getSeatPosition(), localPlayer);
		TableMaster.INSTANCE.seatedPlayer.put(seatedPlayer.getPlayerName(),
				player);

		TextView playerView = new TextView(this);
		playerView.setTag(player);
		playerView.setText(player.name);
		playerView.setX(player.getX());
		playerView.setY(player.getY());
		// playerView.setTextSize();

		if (player.position == 0) {
			playerView.setGravity(Gravity.CENTER);
		} else if (player.position < 3) {
			playerView.setGravity(Gravity.RIGHT);
		} else {
			playerView.setGravity(Gravity.LEFT);
		}

		player.setView(playerView);

		UIAction actions = new UIAddViewAction(this, playerView, drawMaster.getPLAYER_NAME_WIDTH(),
				drawMaster.getPLAYER_NAME_HEIGHT());
		actions.setNextAction(new MoveViewHolderAnimation(
				this, player,
				drawMaster.getPlayerNameX(player.position),
				drawMaster.getPlayerNameY(player.position),
				ANIMATION_CARDDEALT_SPEED));
		actions.run();
	}

	public void onBidSelected(MakeBidDialog bidFragment) {
		Bid bid = bidFragment.getBid();
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.remove(bidFragment);
		transaction.commit();
		UIAction.ACTIVE_ACTION_CHAIN = null;
		phi.messagesFromClientQueue.offer(new GetBidEvent(bid));
		synchronized (phi.SYNC_MSG_FROM_CLIENT) {
			phi.SYNC_MSG_FROM_CLIENT.notify();
		}

	}

	public void onAnnounceBid(AnnounceBid event) {

		PlayerHolder player = TableMaster.INSTANCE.seatedPlayer.get(event
				.getPlayerName());

		if (player == null) {
			return;
		}

		BidHolder bid = new BidHolder(event.getBid(), event.getPlayerName());
		bid.setPosition(drawMaster.getScreenWidth() / 2,
				drawMaster.getScreenHeight() / 2);

		TableMaster.INSTANCE.bids.add(bid);

		(new BidAnimation(this, bid, drawMaster.getPlayerBidX(player.position),
				drawMaster.getPlayerBidY(player.position))).run();
	}

	public void onAnnounceBidder(AnnounceBidder event) {
		if (HaasActivity.playerName.equals(event.getPlayerName())) {
			MakeBidDialog bidFragment = new MakeBidDialog();
			bidFragment.getValidBids().addAll(event.getValidBids());

			(new UIDialogAction(this, bidFragment, R.id.main_view)).run();
		}
	}

	@Override
	public void onAnnounceDealtCard(AnnounceDealtCard event) {
		ImageView cardView = new ImageView(this);
		cardView.setImageBitmap(drawMaster.mCardHidden);
		cardView.setX(0);
		cardView.setY(0);
		CardHolder card = new CardHolder(event.getCard(), cardView);
		float destX;
		float destY;

		UIAction animation = new UIAddViewAction(this, cardView, (int) drawMaster.getCARD_WIDTH(),
				(int) drawMaster.getCARD_HEIGHT());

		if (!AnnounceDealtCard.KIDDIE.equals(event.getPlayerName())) {
			card.setInHand(true);
			PlayerHolder player = TableMaster.INSTANCE.seatedPlayer.get(event
					.getPlayerName());
			if (playerName.equals(event.getPlayerName())) {

				LinkedList<UIAction> actionQueue = drawMaster
						.createInsertCardActionQueue(this, card, player.hand,
								true);
				// actionQueue.add(new MoveAndFlipCardAnimation(this, card));

				for (UIAction action : actionQueue) {
					animation.setNextAction(action);
				}
			} else {
				destX = drawMaster.getPlayerHandX(player.position,
						player.hand.size());
				destY = drawMaster.getPlayerHandY(player.position);
				animation.setNextAction(
						new MoveViewHolderAnimation(this, card, destX, destY, HaasActivity.ANIMATION_CARDDEALT_SPEED));
			}

			player.hand.add(card);
			Collections.sort(player.hand);
		} else {
			destX = drawMaster.getKiddieX();
			destY = drawMaster.getKiddieY();
			animation.setNextAction(
					new MoveViewHolderAnimation(this, card, destX, destY, HaasActivity.ANIMATION_CARDDEALT_SPEED));
			TableMaster.INSTANCE.kiddie.add(card);
		}

		animation.run();
	}

	@Override
	public void onAnnounceKiddieDiscard(AnnounceKiddieDiscard event) {
		PlayerHolder player = TableMaster.INSTANCE.seatedPlayer.get(event
				.getPlayerName());

		CardHolder cardHolder = null;
		for (CardHolder card : player.hand) {
			if (card.card.equalsCard(event.getCard())
					&& (!player.name.equals(playerName) || !card.isInHand())) {
				cardHolder = card;
				player.hand.remove(card);
				// mainView.removeView(card.view);
				break;
			}
		}

		float destX = drawMaster.getKiddieX();
		float destY = drawMaster.getKiddieY();
		UIAction action = new MoveViewHolderAnimation(this, cardHolder, destX, destY,
				HaasActivity.ANIMATION_CARDDEALT_SPEED);

		TableMaster.INSTANCE.kiddie.add(cardHolder);

		if (TableMaster.INSTANCE.kiddie.size() == 3) {
			if (player.name.equals(playerName)) {
				for (final CardHolder playerCardHolder : player.hand) {
					playerCardHolder.getView().setOnTouchListener(null);
				}

				action.setNextAction(new UIDelayedAction(this, 500));
				for (CardHolder card : TableMaster.INSTANCE.kiddie) {
					action
							.setNextAction(new FlipCardAnimation(this, card, ANIMATION_FLIP_KIDDIE));
				}

			}
		}

		action.run();
	}

	@Override
	public void onAnnouncePartner(AnnouncePartner event) {
		(new RemoveViewAnimation(this, TableMaster.INSTANCE.partnerCard, 250)).run();
		PlayerHolder player = TableMaster.INSTANCE.seatedPlayer.get(event
				.getPlayerName());
		player.markAsPartner();
		TableMaster.INSTANCE.partnerCard = null;
	}

	@Override
	public void onAnnouncePartnerCard(AnnouncePartnerCard event) {
		ImageView iView = new ImageView(this);
		iView.setImageBitmap(this.drawMaster.bitmapForCard(event.getCard()));

		iView.setX((drawMaster.getScreenWidth() - drawMaster.getCARD_WIDTH()) / 2);
		iView.setY(0);
		TableMaster.INSTANCE.partnerCard = new CardHolder(event.getCard(), iView);
		// TableMaster.INSTANCE.partnerCard.setInHand(false);
		(new UIAddViewAction(this, iView, (int) drawMaster.getCARD_WIDTH(), (int) drawMaster.getCARD_HEIGHT())).run();

		if (TableMaster.INSTANCE.winningBid.getBid() != Bid.HAAS
				&& TableMaster.INSTANCE.winningBid.getBid() != Bid.DOUBLE_HAAS) {
			return;
		}

		Map<String, PlayerHolder> seatedPlayers = TableMaster.INSTANCE.seatedPlayer;
		BidHolder winningBid = TableMaster.INSTANCE.winningBid;
		String winningBidPlayerName = winningBid.getPlayerName();
		final PlayerHolder player = seatedPlayers.get(winningBidPlayerName);

		if (!player.name.equals(playerName)) {
			return;
		}

		(new UIEnableTouchForCardSelectionAction(this, player, phi, GetHaasPartnerCardPassEvent.class)).run();
	}

	@Override
	public void onAnnouncePlay(AnnouncePlay event) {

		PlayerHolder player = TableMaster.INSTANCE.seatedPlayer.get(event
				.getPlay().getPlayer().getName());

		CardHolder cardOfPlay = null;
		for (CardHolder cardInHand : player.hand)
			if (cardInHand.card.equals(event.getPlay().getCard())
					&& (!player.name.equals(playerName) || !cardInHand.isInHand())) {
				cardOfPlay = cardInHand;
				break;
			}

		if (cardOfPlay != null) {
			player.hand.remove(cardOfPlay);
			TableMaster.INSTANCE.currentTrick.add(cardOfPlay);
			if (!cardOfPlay.isShown()) {
				float destX = drawMaster.getPlayerPlayX(player.position);
				float destY = drawMaster.getPlayerPlayY(player.position);

				UIAction mca = new MoveViewHolderAnimation(this, cardOfPlay, destX, destY,
						HaasActivity.ANIMATION_CARDPLAY_SPEED);
				mca.setNextAction(new FlipCardAnimation(this, cardOfPlay, ANIMATION_FLIP_KIDDIE));
				mca.run();
			}
		}
	}

	@Override
	public void onAnnounceTrickWinner(AnnounceTrickWinner event) {
		PlayerHolder player = TableMaster.INSTANCE.seatedPlayer.get(event
				.getPlayerName());

		final UIAction delayedAction = new UIDelayedAction(this, 3000);
		float destX = drawMaster.getPlayerPlayX(player.position);
		float destY = drawMaster.getPlayerPlayY(player.position);
		for (CardHolder card : TableMaster.INSTANCE.currentTrick) {
			delayedAction.setNextAction(new MoveViewHolderAnimation(this, card,
					destX, destY, HaasActivity.ANIMATION_CARDDEALT_SPEED));
		}

		for (CardHolder card : TableMaster.INSTANCE.currentTrick) {
			delayedAction.setNextAction(new RemoveViewAnimation(
					HaasActivity.this, card, 5));
		}

		TableMaster.INSTANCE.previousTrick.clear();
		TableMaster.INSTANCE.previousTrick
				.addAll(TableMaster.INSTANCE.currentTrick);
		TableMaster.INSTANCE.currentTrick.clear();

		delayedAction.run();

	}

	@Override
	public void onAnnounceTrump(AnnounceTrump event) {
		ImageView iView = new ImageView(this);
		int index;

		switch (event.getTrump().getSuit()) {
			case SPADE: {
				index = 2;
				break;
			}
			case CLUB: {
				index = 0;
				break;
			}
			case HEART: {
				index = 3;
				break;
			}
			case DIAMOND: {
				index = 1;
				break;
			}
			default: {
				throw new RuntimeException("Invalid trump");
			}
		}
		iView.setImageBitmap(drawMaster.mSuitLargeBitmap[index]);
		iView.setX((drawMaster.getScreenWidth() - drawMaster.getTRUMP_WIDTH()) / 2);
		iView.setY(drawMaster.getPlayerHandY(2) + drawMaster.getCARD_HEIGHT() / 2);

		(new UIAddViewAction(this, iView, (int) drawMaster.getTRUMP_WIDTH(), (int) drawMaster.getTRUMP_HEIGHT())).run();

		TableMaster.INSTANCE.trump = new TrumpHolder(event.getTrump(), iView);

		drawMaster.sortHand(TableMaster.INSTANCE.seatedPlayer.get(HaasActivity.playerName).hand);
	}

	@Override
	public void onAnnounceTurn(AnnounceTurn event) {
		final AnnounceTurn announceTurnEvent = event;
		if (HaasActivity.playerName.equals(event.getPlayerName())) {

			final PlayerHolder player = TableMaster.INSTANCE.seatedPlayer.get(event
					.getPlayerName());

			final HaasActivity context = this;
			for (final CardHolder cardHolder : player.hand) {

				cardHolder.getView().setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						int[] locations = new int[2];
						context.mainView.getLocationOnScreen(locations);

						float x_cord = event.getRawX() - locations[0];
						float y_cord = event.getRawY() - locations[1];

						if (x_cord < 0) {
							x_cord = 0;
						} else if (x_cord + drawMaster.getCARD_WIDTH() > drawMaster.getScreenWidth()) {
							x_cord = drawMaster.getScreenWidth() - drawMaster.getCARD_WIDTH();
						}
						if (y_cord < 0) {
							y_cord = 0;
						} else if (y_cord + drawMaster.getCARD_HEIGHT() > drawMaster.getScreenHeight()) {
							y_cord = drawMaster.getScreenHeight() - drawMaster.getCARD_HEIGHT();
						}

						switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:
								cardHolder.getView().bringToFront();
								break;
							case MotionEvent.ACTION_MOVE:

								cardHolder.getView().setX(x_cord - drawMaster.getCARD_WIDTH() / 2);
								cardHolder.getView().setY(y_cord - drawMaster.getCARD_HEIGHT() / 2);

								break;
							case MotionEvent.ACTION_UP:

								if (drawMaster.isCoordInPlayArea(x_cord, y_cord)
										&& announceTurnEvent.getValidPlays().contains(cardHolder.card)) {

									cardHolder.setInHand(false);
									phi.messagesFromClientQueue.offer(new GetPlayEvent(cardHolder.card));
									synchronized (phi.SYNC_MSG_FROM_CLIENT) {
										phi.SYNC_MSG_FROM_CLIENT.notify();
									}

									for (CardHolder card : player.hand) {
										card.getView().setOnTouchListener(null);
									}

									UIAction animation = null;
									LinkedList<UIAction> actionQueue = drawMaster.removeCardFromHand(context,
											player.hand);
									for (UIAction action : actionQueue) {
										if (animation == null) {
											animation = action;
										} else {
											animation.setNextAction(action);
										}
									}

									// float playX = DrawMaster.convertDpToPixel(drawMaster.getPlayerPlayX(0),
									// context);
									// float playY = DrawMaster.convertDpToPixel(drawMaster.getPlayerPlayY(0),
									// context);
									float playX = drawMaster.getPlayerPlayX(0);
									float playY = drawMaster.getPlayerPlayY(0);
									cardHolder.getView().setX(playX);
									cardHolder.getView().setY(playY);
									cardHolder.setPosition(playX, playY);
									if (animation != null) {
										animation.run();
									}

								} else {
									cardHolder.getView().setX(cardHolder.getX());
									cardHolder.getView().setY(cardHolder.getY());
									drawMaster.cardReturnedToHand(context, player.hand, cardHolder);
								}

								break;
							default:
								break;
						}
						return true;
					}
				});
			}
		}

	}

	@Override
	public void onAnnounceWinningBid(AnnounceWinningBid event) {
		PlayerHolder player = TableMaster.INSTANCE.seatedPlayer.get(event.getPlayerName());

		if (player == null) {
			return;
		}

		for (BidHolder bid : TableMaster.INSTANCE.bids) {
			if (bid.getPlayerName().equals(event.getPlayerName())) {
				TableMaster.INSTANCE.winningBid = bid;
			} else {
				mainView.removeView(bid.getView());
			}
		}

		TableMaster.INSTANCE.bids.clear();

		player.markAsBidder();

		if (event.getPlayerName().equals(playerName)) {
			FragmentTransaction transaction = getFragmentManager()
					.beginTransaction();
			transaction.add(R.id.main_view, new NameTrumpDialog());
			transaction.commit();
		}
	}

	@Override
	public void onShowKiddie(ShowKiddie event) {
		UIAction animation = null;
		for (CardHolder cardView : TableMaster.INSTANCE.kiddie) {
			if (animation == null) {
				animation = new FlipCardAnimation(this, cardView, ANIMATION_FLIP_KIDDIE);
			} else {
				animation.setNextAction(new FlipCardAnimation(this, cardView, ANIMATION_FLIP_KIDDIE));
			}
		}
		// animation.run();

		if (animation != null) {
			animation.setNextAction(new UIDelayedAction(this, VIEW_KIDDIE_TIME));
		} else {
			animation = new UIDelayedAction(this, VIEW_KIDDIE_TIME);
		}

		Map<String, PlayerHolder> seatedPlayers = TableMaster.INSTANCE.seatedPlayer;
		BidHolder winningBid = TableMaster.INSTANCE.winningBid;
		String winningBidPlayerName = winningBid.getPlayerName();
		final PlayerHolder player = seatedPlayers.get(winningBidPlayerName);

		if (player.name.equals(playerName)) {
			for (CardHolder cardView : TableMaster.INSTANCE.kiddie) {
				cardView.setInHand(true);
				LinkedList<UIAction> actionQueue = drawMaster
						.createInsertCardActionQueue(HaasActivity.this,
								cardView, player.hand, false);
				for (UIAction action : actionQueue) {
					animation.setNextAction(action);
				}
				player.hand.add(cardView);
				Collections.sort(player.hand);
			}

			animation.setNextAction(
					new UIEnableTouchForCardSelectionAction(this, player, phi, GetKiddieDiscardEvent.class));

		} else {
			player.hand.addAll(TableMaster.INSTANCE.kiddie);

			float destX = drawMaster.getPlayerHandX(player.position,
					player.hand.size());
			float destY = drawMaster.getPlayerHandY(player.position);

			for (CardHolder cardView : TableMaster.INSTANCE.kiddie) {
				cardView.setInHand(true);
				animation.setNextAction(new MoveViewHolderAnimation(
						HaasActivity.this, cardView, destX, destY, HaasActivity.ANIMATION_CARDDEALT_SPEED));
				animation.setNextAction(new FlipCardAnimation(
						HaasActivity.this, cardView, ANIMATION_FLIP_KIDDIE));
			}
		}

		TableMaster.INSTANCE.kiddie.clear();
		animation.run();
	}

	@Override
	public void onAnnounceRoundResult(AnnounceRoundResult event) {

		final UIAction animation = new RemoveViewAnimation(this, TableMaster.INSTANCE.winningBid, 50);
		animation.setNextAction(new RemoveViewAnimation(this, TableMaster.INSTANCE.trump, 50));
		for (CardHolder cardHolder : TableMaster.INSTANCE.kiddie) {
			animation.setNextAction(new RemoveViewAnimation(this, cardHolder, 50));
		}

		for (PlayerHolder playerHolder : TableMaster.INSTANCE.seatedPlayer.values()) {
			playerHolder.reset();
		}
		animation.run();
		TableMaster.INSTANCE.trump = null;
		TableMaster.INSTANCE.winningBid = null;
		TableMaster.INSTANCE.kiddie.clear();
	}

	public void onTrumpSelected(NameTrumpDialog nameTrumpDialog) {
		Trump trump = nameTrumpDialog.getTrump();
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.remove(nameTrumpDialog);
		transaction.commit();
		UIAction.ACTIVE_ACTION_CHAIN = null;
		phi.messagesFromClientQueue.offer(new GetTrumpEvent(trump));
		synchronized (phi.SYNC_MSG_FROM_CLIENT) {
			phi.SYNC_MSG_FROM_CLIENT.notify();
		}

	}

	public DrawMaster getDrawMaster() {
		return drawMaster;
	}
}
