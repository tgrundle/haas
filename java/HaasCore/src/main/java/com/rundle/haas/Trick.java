package com.rundle.haas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Trick {

	protected final List<Play> plays = new ArrayList<Play>();
	protected Card lead;
	protected Play winner;
	protected Boolean complete = Boolean.FALSE; 

	public List<Play> getPlays() {
		return Collections.unmodifiableList(plays);
	}

}
