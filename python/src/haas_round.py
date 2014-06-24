"""
Created on Nov 3, 2010

@author: timothy
"""

from haas_card import Card, Deck
from haas_logger import getLogger


class Bid:
    def __init__(self, bidder, count):
        self.bidder = bidder
        self.count = count
        self.trump = None
        self.lookingForCard = None
        self.partner = None

    def isHaas(self):
        return self.count == 18 or self.count == 36

    def isPass(self):
        return self.count == -1


class Dealer:

    def __init__(self, player):
        self.player = player

    def deal(self, kiddie):
        deck = Deck();
        deck.shuffle()
        playerToRecieveCard = self.player.toTheLeft
        ###TODO Add more options
        while len(deck.cardList) > 0:
            numDealt = 0
            while numDealt < 3:
                playerToRecieveCard.addCardToHand(deck.cardList.pop())
                numDealt = numDealt + 1

            if playerToRecieveCard == self.player:
                kiddie.addCard(deck.cardList.pop())

            playerToRecieveCard = playerToRecieveCard.toTheLeft

    def nextDealer(self):
        return Dealer(self.player.toTheLeft)


class Kiddie:
    def __init__(self):
        self.cardList = []

    def addCard(self, card):
        self.cardList.append(card)

    def isFull(self):
        return len(self.cardList) == 3

    def adjustForTrump(self, trump):
        for card in self.cardList:
            trump.adjustCardForTrump(card)


class Round:
    def __init__(self, dealer, playerTable):
        self.dealer = dealer
        self.playerTable = playerTable
        self.tricks = []
        self.kiddie = Kiddie()
        self.bid = None

    def foundPartner(self):
        return not (self.bid is None) and not (self.bid.partner is None)

    def playRound(self):
        scoringTable = {}

        for playerHandle in self.playerTable.values():
            player = playerHandle.player
            scoringTable[player.name] = 0
            player.hand = []
            playerHandle.round = self

        self.dealer.deal(self.kiddie)

        for playerHandle in self.playerTable.values():
            playerHandle.lookAtHand()

        currentBidder = self.dealer.player.toTheLeft

        while currentBidder != self.dealer.player:
            playerBid = self.playerTable[currentBidder.name].makeBid(self.bid, False)
            getLogger().log(currentBidder.name + " bids " + str(playerBid.count))

            if playerBid.isPass():
                currentBidder = currentBidder.toTheLeft
            elif playerBid.isHaas():
                self.bid = playerBid
                currentBidder = self.dealer.player
            else:
                self.bid = playerBid
                currentBidder = currentBidder.toTheLeft

            for playerHandle in self.playerTable.values():
                playerHandle.announceBid(playerBid)

        dealerBid = self.playerTable[self.dealer.player.name].makeBid(self.bid, True)
        getLogger().log(self.dealer.player.name + " bids " + str(dealerBid.count))

        for playerHandle in self.playerTable.values():
            playerHandle.announceBid(dealerBid)

        if not (dealerBid.isPass()):
            self.bid = dealerBid

        getLogger().log("Wining Bidder : " + self.bid.bidder.name)
        getLogger().log("Wining Bid : " + str(self.bid.count))
        for playerHandle in self.playerTable.values():
            playerHandle.announceBidder(self.bid)

        self.trump = self.playerTable[self.bid.bidder.name].nameTrump()
        getLogger().log("Trump is " + self.trump.suit)
        self.kiddie.adjustForTrump(self.trump)
        getLogger().logCardList("Kiddie:", self.kiddie.cardList)
        for playerHandle in self.playerTable.values():
            playerHandle.announceTrumpAndShowKiddie()

        discards = self.playerTable[self.bid.bidder.name].pickupKiddie()
        getLogger().logCardList("Bidder's Kiddie discards:", discards)
        currentPlayer = self.bid.bidder

        invalidList = []
        knownCards = []
        knownCards.extend(discards)
        knownCards.extend(self.bid.bidder.hand)

        for card in knownCards:
            if knownCards.count(card) == 2 and not (card in invalidList):
                invalidList.append(card)

        getLogger().logCardList("Invalid Partner Cards:", invalidList)

        self.lookingForCard = self.playerTable[self.bid.bidder.name].namePartnerCard(invalidList)
        getLogger().log("Partner Card " + str(self.lookingForCard))
        self.trump.adjustCardForTrump(self.lookingForCard)

        for playerHandle in self.playerTable.values():
            playerHandle.announcePartnerCard()

        if self.bid.isHaas():
            self.bid.partner = self.bid.bidder.toTheLeft
            while self.bid.partner != self.bid.bidder:
                getLogger().logCardList("Check for HAAS partner: " + self.bid.partner.name, self.bid.partner.hand)
                if self.lookingForCard in self.bid.partner.hand:
                    break
                self.bid.partner = self.bid.partner.toTheLeft

            if self.bid.partner == self.bid.bidder:
                raise Exception("um....")

            for playerHandle in self.playerTable.values():
                playerHandle.announcePartners()

            cardsToPass = self.playerTable[self.bid.bidder.name].passCards()
            cardsToPass = self.playerTable[self.bid.partner.name].exchangeCards(cardsToPass)
            self.playerTable[self.bid.bidder.name].addExchangedCards(cardsToPass)
        playerInController = self.bid.bidder
        while (len(currentPlayer.hand) > 0):
            trick = Trick(self)

            while True:
                possiblePlays = trick.determinePossiblePlays(currentPlayer.hand)
                getLogger().logCardList(currentPlayer.name + " possible plays are ", possiblePlays, currentPlayer.hand)
                self.playerTable[currentPlayer.name].play(trick, possiblePlays)
                currentPlayer = currentPlayer.toTheLeft

                if self.bid.isHaas() and currentPlayer == self.bid.partner:
                    currentPlayer = currentPlayer.toTheLeft

                if currentPlayer == playerInController:
                    break

            currentPlayer = trick.winner.player
            playerInController = trick.winner.player
            self.tricks.append(trick)
            if trick.winner.player.name == self.bid.bidder.name or trick.winner.player.name == self.bid.partner.name:
                scoringTable[self.bid.bidder.name] = scoringTable[self.bid.bidder.name] + 1
                scoringTable[self.bid.partner.name] = scoringTable[self.bid.partner.name] + 1
            else:
                scoringTable[trick.winner.player.name] = scoringTable[trick.winner.player.name] + 1

            for playerHandle in self.playerTable.values():
                playerHandle.announceTrickWinner(currentPlayer, scoringTable)

        trickCount = scoringTable[self.bid.bidder.name]

        if self.bid.isHaas():
            trickCount = trickCount * 2

        if trickCount >= self.bid.count:
            msg = self.bid.bidder.name + " made the bid!"
        else:
            msg = self.bid.bidder.name + " went under!"
            trickCount = self.bid.count * -1

        for playerHandle in self.playerTable.values():
            playerHandle.announceRoundResult(msg)

        scoringTable[self.bid.bidder.name] = trickCount

        if not (self.bid.partner is None):
            scoringTable[self.bid.partner.name] = trickCount

        for name in scoringTable.keys():
            self.playerTable[name].player.score = self.playerTable[name].player.score + scoringTable[name]
    # endDef


class Play:
    def __init__(self, player, card):
        self.player = player
        self.card = card


class Player:
    def adjustHandForTrump(self, trump):
        for card in self.hand:
            trump.adjustCardForTrump(card)
        self.hand.sort()

    def __init__(self):
        self.name = None
        self.hand = []
        self.score = 0
        self.toTheLeft = None
        self.toTheRight = None

    def addCardToHand(self, card):
        self.hand.append(card)
        self.hand.sort()


class Trick:

    def __init__(self, gameround):
        self.gameround = gameround
        self.trump = gameround.trump
        self.plays = []
        self.lead = None
        self.winner = None
        self.opening = len(self.gameround.tricks) == 0

    def addPlay(self, play):
        getLogger().log(play.player.name + "'s playing " + str(play.card))
        if self.winner is None:
            self.winner = play
            self.lead = play.card
            for playerHandle in self.gameround.playerTable.values():
                playerHandle.announceMessage(play.player.name + " leads with " + str(play.card))
        elif not (self.trump.isCardTrump(self.winner.card)) and self.trump.isCardTrump(play.card):
            self.winner = play
            for playerHandle in self.gameround.playerTable.values():
                playerHandle.announceMessage(play.player.name + " trumps with " + str(play.card))
        else:
            if self.winner.card.isBetter(play.card, self.trump):
                self.winner = play

            for playerHandle in self.gameround.playerTable.values():
                playerHandle.announceMessage(play.player.name + " plays " + str(play.card))
            if not (self.gameround.foundPartner()) and play.card == self.gameround.lookingForCard:
                self.gameround.bid.partner = play.player
                for playerHandle in self.gameround.playerTable.values():
                    playerHandle.announcePartners()

        for playerHandle in self.gameround.playerTable.values():
            playerHandle.showPlay(play)

        self.plays.append(play)

    def determinePossiblePlays(self, hand):
        possiblePlays = []

        if len(self.gameround.tricks) == 0 and self.lead is None:
            if self.gameround.bid.isHaas():
                possiblePlays.extend(hand)
            else:
                strongTrump = []
                for card in hand:
                    if self.trump.isCardTrump(card):
                        if card.isBetter(self.gameround.lookingForCard, self.trump):
                            possiblePlays.append(card)
                        else:
                            strongTrump.append(card)

                if len(possiblePlays) == 0:
                    if len(strongTrump) > 0:
                        strongTrump.sort()
                        possiblePlays.extend(strongTrump)
                    else:
                        possiblePlays.extend(hand)

        elif not (self.gameround.foundPartner()) and self.gameround.lookingForCard in hand:
            possiblePlays.append(self.gameround.lookingForCard)
        elif self.lead is None:
            possiblePlays.extend(hand)
        elif self.trump.isCardTrump(self.lead):
            # Must beat trump
            allTrump = []
            for card in hand:
                if self.trump.isCardTrump(card):
                    allTrump.append(card)
                    if self.winner.card.isBetter(card, self.trump):
                        possiblePlays.append(card)

            if len(possiblePlays) == 0 and len(allTrump) == 0:
                possiblePlays.extend(hand)
            elif len(possiblePlays) == 0:
                possiblePlays.extend(allTrump)

        else:
            suit = self.lead.suit
            for card in hand:
                if card.suit == suit and not (card == self.trump.leftBar):
                    possiblePlays.append(card)
            if len(possiblePlays) == 0:
                possiblePlays.extend(hand)

        return possiblePlays


class Trump:
    def __init__(self, suit):
        self.suit = suit

        if self.suit == 'c':
            self.leftBar = Card('J', 's')
            self.rightBar = Card('J', 'c')
        elif self.suit == 'd':
            self.leftBar = Card('J', 'h')
            self.rightBar = Card('J', 'd')
        elif self.suit == 'h':
            self.leftBar = Card('J', 'd')
            self.rightBar = Card('J', 'h')
        elif self.suit == 's':
            self.leftBar = Card('J', 'c')
            self.rightBar = Card('J', 's')
        else:
            raise Exception("Invalid Suit")

        self.leftBar.makeLeftBar()
        self.rightBar.makeRightBar()

    def isCardTrump(self, card):
        return card.suit == self.suit or card == self.leftBar

    def adjustCardForTrump(self, card):
        leftBarAsNoneTrump = Card(self.leftBar.value, self.leftBar.suit)
        rightBarAsNoneTrump = Card(self.rightBar.value, self.rightBar.suit)

        if card == leftBarAsNoneTrump:
            card.makeLeftBar()
        elif card == rightBarAsNoneTrump:
            card.makeRightBar()
        elif card == self.leftBar or card == self.rightBar:
            pass
        else:
            card.resetWeight()
