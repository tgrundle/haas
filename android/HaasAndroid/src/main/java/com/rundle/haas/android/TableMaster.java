package com.rundle.haas.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rundle.haas.android.holders.BidHolder;
import com.rundle.haas.android.holders.CardHolder;
import com.rundle.haas.android.holders.PlayerHolder;
import com.rundle.haas.android.holders.TrumpHolder;

public class TableMaster {

	static final TableMaster INSTANCE = new TableMaster();
	
	private TableMaster() {
		
	}
	
	//protected CardMaster animatedCard;
	protected final Map<String, PlayerHolder> seatedPlayer = Collections.synchronizedMap(new HashMap<String, PlayerHolder>());
	protected final List<CardHolder> kiddie = Collections.synchronizedList(new ArrayList<CardHolder>(3));
	protected final List<BidHolder> bids = Collections.synchronizedList(new ArrayList<BidHolder>());

	protected TrumpHolder trump;
	protected BidHolder winningBid;
    protected CardHolder partnerCard;
	
	//drawable entities
	
	protected final List<CardHolder> currentTrick = Collections.synchronizedList(new ArrayList<CardHolder>(5));
	protected final List<CardHolder> previousTrick = Collections.synchronizedList(new ArrayList<CardHolder>(5));
}
