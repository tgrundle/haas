package com.rundle.haas.events;

public interface GameEventHandler {

	void onAnnounceBid(AnnounceBid event);

	void onAnnounceBidder(AnnounceBidder event);

	void onAnnounceDealtCard(AnnounceDealtCard event);

	void onAnnounceKiddieDiscard(AnnounceKiddieDiscard event);

	void onAnnouncePartner(AnnouncePartner event);

	void onAnnouncePartnerCard(AnnouncePartnerCard event);

	void onAnnouncePlay(AnnouncePlay event);

	void onAnnouncePlayerSeated(AnnouncePlayerSeated event);

	void onAnnounceTrickWinner(AnnounceTrickWinner event);

	void onAnnounceTrump(AnnounceTrump event);

	void onAnnounceTurn(AnnounceTurn event);

	void onAnnounceWinningBid(AnnounceWinningBid event);

	void onShowKiddie(ShowKiddie event);

    void onAnnounceRoundResult(AnnounceRoundResult result);
	//void onGetKiddieDiscard(GetKiddieDiscardEvent event);
}
