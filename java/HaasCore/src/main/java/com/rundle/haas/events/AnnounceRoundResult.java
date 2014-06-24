package com.rundle.haas.events;

public class AnnounceRoundResult extends Announcement {

    //private Map<String, Integer> scoring;
    private final Boolean madeBid;

    public AnnounceRoundResult(boolean madeBid) {
        this.madeBid = madeBid;
    }
}

