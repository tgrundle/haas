package com.rundle.haas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rundle.haas.Card.CardSuit;

public class Table {

	private static Table instance;

	public static Table getInstance() {
		if (instance == null) {
			instance = new Table();
		}
		return instance;
	}

	public enum GameState {
		PARTNER_NAME, GAME_START, ROUND_START, SHUFFLING, DEALING, BIDDING, KIDDIE_VIEW, KIDDIE_DISCARD, TRICK_VIEW, TRUMP_NAME, TRUMP_VIEW, HAAS_PASS_TO_PARTNER, TRICK_START, TRICK_PLAY, ROUND_OVER, TRICK_OVER, HAAS_SELECT_CARD_FOR_PARTNER, HAAS_FIND_PARTNER, HAAS_ADD_CARDS_FROM_PARTNER, HAAS_SELECT_CARD_FOR_BIDDER, GAME_OVER, ANNOUNCE_BIDDER, ANNOUNCE_BID, TRICK_ANNOUNCE_TURN
	}

	public enum Bid {
		HAAS, DOUBLE_HAAS, PASS, STUCK, FIVE, SIX, SEVEN
	}

	protected int seatedPlayers = 0;
	protected Player dealer;
	//
	protected final Player[] players = new Player[5];

	protected final List<Card> kiddie = new ArrayList<Card>();
	protected final List<Card> cardsToPassToPartner = new ArrayList<Card>();
	protected final List<Card> cardsPassedFromPartner = new ArrayList<Card>();
	protected final List<Trick> tricks = new ArrayList<Trick>();
	protected final List<Card> kiddieDiscards = new ArrayList<Card>();
	protected Bid bid;
	protected Trump trump;
	protected Card lookingForCard;
	protected Player bidder;
	protected Player partner;
	protected Player waitingOnPlayer;
	protected Player playerInController;

	protected GameState gamestate = GameState.ROUND_START;

	protected Map<CardSuit, Card> currentSuitWinners = new HashMap<CardSuit, Card>();

	private Table() {
	}
	
	public Player[] getPlayers() {
		return players;
	}

	public Bid getBid() {
		return bid;
	}

	public Trump getTrump() {
		return trump;
	}

	public Card getLookingForCard() {
		return lookingForCard;
	}
	
	public List<Card> getKiddieDiscards() {
		return Collections.unmodifiableList(kiddieDiscards);
	}
	
	public Card getLead() {
		Card lead = null;
	
		if(tricks.size() > 0) {
			Trick trick = tricks.get(tricks.size()-1);
			lead = trick.lead;
		}
		
		return lead;
	}

	public Trick getCurrentTrick() {
		Trick trick = null;
	
		if(tricks.size() > 0) {
			trick = tricks.get(tricks.size()-1);
		}
		
		return trick;
	}

	public Player getBidder() {
		return bidder;
	}

	public Player getPartner() {
		return partner;
	}
	public List<Trick> getTricks() {
		return Collections.unmodifiableList(tricks);
	}
//
//	public int getSeatedPlayers() {
//		return seatedPlayers;
//	}
//
//	public GameState getGamestate() {
//		return gamestate;
//	}	
	
}
