"""
Created on Nov 3, 2010

@author: timothy
"""

import random
from haas_card import Card
from haas_logger import getLogger
from haas_round import Trump, Bid, Play


class PlayerMode:
    def __init__(self, player, view=None):
        self.player = player
        self.view = view
        self.round = None

    def addExchangedCards(self, passedCards):
        self.player.hand.extend(passedCards)
        self.player.hand.sort()
        if self.view:
            self.view.showHand(self.player.hand)

    def announceBid(self, bid):
        if self.view:
            self.view.announceBid(bid)

    def announceBidder(self, bid):
        if self.view:
            self.view.announceBidder(bid)

    def announceGameWinner(self, gameWinner):
        if self.view:
            self.view.announceGameWinner(gameWinner)

    def announceMessage(self, message):
        if self.view:
            self.view.announceMessage(message)

    def announcePartners(self):
        if self.view:
            self.view.announcePartners(self.round.bid.bidder, self.round.bid.partner)

    def announcePartnerCard(self):
        if self.view:
            self.view.announcePartnerCard(self.round.lookingForCard)

    def announceRoundResult(self, message):
        if self.view:
            self.view.announceRoundResult(message)

    def announceScore(self, player):
        if self.view:
            self.view.announceScore(player)

    def announceTrickWinner(self, player, trickCount):
        if self.view:
            self.view.showTrickResult(player, trickCount)

    def announceTrumpAndShowKiddie(self):
        self.player.adjustHandForTrump(self.round.trump)
        if self.view:
            self.view.announceTrump(self.round.trump)
            self.view.showHand(self.player.hand)
            self.view.showKiddie(self.round.kiddie)

    def joinTable(self):
        if self.view:
            self.view.joinTable(self.player)

    def sitPlayer(self, playerName):
        if self.view:
            self.view.sitPlayer(playerName)

    def lookAtHand(self):
        if self.view:
            self.view.showDealtCards()
            self.view.showHand(self.player.hand)

    def leaveTable(self):
        pass

    def readyToBegin(self):
        if self.view:
            result = self.view.readyToBegin()
            return result


class AIPlayerMode(PlayerMode):
    def __init__(self, player):
        PlayerMode.__init__(self, player)
        self.__trumpCount = 0
        self.__trickCount = 0
        self.bidTrump = None

    def announceTrickWinner(self, player, trickCount):
        if player == self.round.bid.bidder or player.name == self.round.bid.partner:
            self.__trickCount += 1

    def announceTrumpAndShowKiddie(self):
        self.player.adjustHandForTrump(self.round.trump)
        self.__suitWinner = {"c": Card("Ac"), "d": Card("Ad"), "h": Card("Ah"), "s": Card("As")}
        self.__playedCards = {"c": [], "d": [], "h": [], "s": []}
        self.__suitWinner[self.round.trump.suit] = self.round.trump.rightBar

    def exchangeCards(self, passedCards):
        self.player.hand.extend(passedCards)
        self.player.hand.sort()
        cardsToReturn = [self.round.lookingForCard]
        self.player.hand.remove(self.round.lookingForCard)
        for card in self.player.hand:
            if self.round.trump.isCardTrump(card):
                cardsToReturn.append(card)
            if len(cardsToReturn) == 2:
                break
        if len(cardsToReturn) < 2:
            for value in ["A", "K", "Q", "J", "10", "9"]:
                for card in self.player.hand:
                    if card.value == value:
                        cardsToReturn.append(card)
                    if len(cardsToReturn) == 2:
                        break
                if len(cardsToReturn) == 2:
                    break
        return cardsToReturn

    def makeBid(self, currentBid, isDealer):
        bid = None
        biddingTable = {"c": 0, "d": 0, "h": 0, "s": 0}
        getLogger().logCardList(self.player.name + "'s Hand: ", self.player.hand)
        getLogger().log("   Bid\tSwing\tWeak\tSideAs\tTrump\tLosers")
        for suit in biddingTable.keys():
            trump = Trump(suit)
            self.player.adjustHandForTrump(trump)
            trumpCards = []
            sideAces = []
            losers = []
            swings = []
            for card in self.player.hand:
                if trump.isCardTrump(card):
                    trumpCards.append(card)
                elif card.value == 'A':
                    sideAces.append(card)
                else:
                    losers.append(card)

            swingCount = 1
            weakTrump = []
            for trumpCard in trumpCards:
                if trumpCard == trump.rightBar:
                    swingCount += 1
                    swings.append(trumpCard)
                elif trumpCard == trump.leftBar and swingCount > 1:
                    swingCount += 1
                elif trumpCard.value == 'A' and swingCount > 3:
                    swingCount += 1
                elif trumpCard.value == 'K' and swingCount > 5:
                    swingCount += 1
                elif trumpCard.value == 'Q' and swingCount > 7:
                    swingCount += 1
                elif trumpCard.value == '10' and swingCount > 9:
                    swingCount += 1
                else:
                    weakTrump.append(trumpCard)

            weakTrumpWinners = round((len(weakTrump) - 1) / 2)
            # lets be optimistic
            unaccountedTrump = 14 - (swingCount * 5) - weakTrumpWinners
            if unaccountedTrump < 0:
                unaccountedTrump = 0

            bidCount = swingCount + len(sideAces) + weakTrumpWinners + 1 - unaccountedTrump
            if bidCount > 0 and len(trumpCards) < 3:
                bidCount = 0

            getLogger().log(
                suit + ": " + str(bidCount) + "\t" + str(swingCount) + "\t" + str(len(weakTrump)) + "\t" + str(
                    len(sideAces)) + "\t" + str(len(trumpCards)) + "\t" + str(len(losers)))
            if bid is None or bidCount > bid.count:
                # Save this in case we win bid
                self.bidTrump = Trump(suit)
                if bidCount > 6:
                    bidCount = 18
                bid = Bid(self.player, bidCount)

        for card in self.player.hand:
            card.resetWeight()

        # If we are dealer and there is no bid we are stuck
        if isDealer and (currentBid is None or currentBid.count == -1):
            # Don't bid more than we have too
            if bid is None or bid.count < 10:
                bid = Bid(self.player, 4)
        else:
            # can't make 5 so pass
            if bid is None or bid.count < 5:
                bid = Bid(self.player, -1)
            # can't beat the current bid, pass
            elif currentBid and bid.count <= currentBid.count:
                bid = Bid(self.player, -1)
            # otherwise lets bid
            else:
                pass
        return bid

    def nameTrump(self):
        return self.bidTrump

    def namePartnerCard(self, invalidList):
        card = None
        getLogger().log("Right Bars: " + str(self.player.hand.count(self.round.trump.rightBar)))
        getLogger().log("Left Bars: " + str(self.player.hand.count(self.round.trump.leftBar)))

        if self.player.hand.count(self.round.trump.rightBar) < 2:
            getLogger().log("Asking for Right Bar")
            card = self.round.trump.rightBar
        elif self.player.hand.count(self.round.trump.leftBar) < 2:
            getLogger().log("Asking for Left Bar")
            card = self.round.trump.leftBar
        else:
            getLogger().log("Asking for Lower Trump")
            cardValueList = ["A", "K", "Q", "10", "9"]
            for cardValue in cardValueList:
                card = Card(cardValue, self.round.trump.suit)
                if not (card in invalidList):
                    break

        return card

    def passCards(self):
        nonTrump = []
        cardsToPass = []
        for card in self.player.hand:
            if self.round.trump.isCardTrump(card):
                continue
            nonTrump.append(card)

        if len(nonTrump) == 0:
            # Take two weakest trump
            cardsToPass.append(self.player.hand[-1])
            cardsToPass.append(self.player.hand[-2])
        elif len(nonTrump) == 1:
            # Take non trump and weakest trump
            cardsToPass.append(nonTrump[0])
            if nonTrump[0] == self.player.hand[-1]:
                cardsToPass.append(self.player.hand[-2])
            else:
                cardsToPass.append(self.player.hand[-1])
        elif len(nonTrump) == 2:
            cardsToPass.extend(nonTrump)
        else:
            # take weakest non trump.
            cardValueList = ["9", "10", "J", "Q", "K", "A"]
            for cardValue in cardValueList:
                for card in nonTrump:
                    if card.value == cardValue and len(cardsToPass) < 2:
                        cardsToPass.append(card)

        self.player.hand.remove(cardsToPass[0])
        self.player.hand.remove(cardsToPass[1])

        return cardsToPass

    def pickupKiddie(self):

        possibleDiscards = []
        sideAces = []
        self.player.hand.extend(self.round.kiddie.cardList)
        self.player.hand.sort()
        getLogger().logCardList("Hand: ", self.player.hand)

        for card in self.player.hand:
            if self.round.trump.isCardTrump(card):
                continue
            elif card.value == 'A':
                sideAces.append(card)
            else:
                possibleDiscards.append(card)

        getLogger().logCardList("PossibleDiscards: ", possibleDiscards)

        discards = []
        while len(discards) < 3:
            if len(possibleDiscards) > 0:
                discardIndex = random.sample(range(len(possibleDiscards)), 1)
                card = possibleDiscards.pop(discardIndex[0])
                discards.append(card)
                self.player.hand.remove(card)
            elif len(sideAces) > 0:
                discardIndex = random.sample(range(len(sideAces)), 1)
                card = sideAces.pop(discardIndex[0])
                discards.append(card)
                self.player.hand.remove(card)
            else:
                discards.append(self.player.hand.pop())
        return discards

    def play(self, trick, possiblePlays):
        # time.sleep(random.uniform(1,2))
        play = None
        playLogic = ""
        isBidder = self.player == self.round.bid.bidder
        isPartner = self.player == self.round.bid.partner
        possiblePlays.sort()

        if len(possiblePlays) == 1:
            playLogic = "One option"
            play = possiblePlays[0]
        else:
            ##Determine strategy

            if trick.winner:
                isBidderOrPartnerWinning = trick.winner.player == self.round.bid.bidder or trick.winner.player == self.round.bid.partner
                potentialTrickWinners = []

                for possiblePlay in possiblePlays:
                    if trick.winner.card.isBetter(possiblePlay, trick.trump):
                        potentialTrickWinners.append(possiblePlay)

                potentialTrickWinners.sort()

                # isWinner playoff otherwise take trick
                if trick.winner.card == trick.trump.leftBar:
                    winningCard = self.__suitWinner[trick.trump.suit]
                else:
                    winningCard = self.__suitWinner[trick.winner.card.suit]

                # Is partner/bidder and bidder/partner winning
                if (isBidder is True or isPartner is True) and isBidderOrPartnerWinning is True:

                    # partner has it won, throw lowest power card
                    if len(trick.plays) == 4 or winningCard == trick.winner.card:
                        playLogic = "Partner has it won"
                        possiblePlays[-1]
                    # partner doesn't have winner so take it
                    elif winningCard in potentialTrickWinners:
                        playLogic = "Taking Control"
                        play = winningCard
                    # elif non-trump lead and only trump left trump to win

                # Is partner/bidder and bidder/partner loosing
                if (isBidder is True or isPartner is True) and isBidderOrPartnerWinning is False:
                    # If we have the winning card play it
                    if winningCard in potentialTrickWinners:
                        playLogic = "We can win it"
                        play = winningCard
                    # Otherwise play strongest potential winners
                    elif len(potentialTrickWinners) > 0:
                        if not (trick.trump.isCardTrump(trick.lead)) and trick.trump.isCardTrump(
                                potentialTrickWinners[0]):
                            if len(trick.plays) == 4 or len(trick.plays) == 3 and (
                                    self.player.toTheLeft == self.round.bid.bidder or self.player.toTheLeft == self.round.bid.partner):
                                playLogic = "we should trump with weakest"
                                play = potentialTrickWinners[-1]
                            else:
                                playLogic = "we should trump with strongest"
                                play = potentialTrickWinners[0]
                        else:
                            playLogic = "we could win it"
                            play = potentialTrickWinners[0]

                # Is non partner and partner/bidder winning
                if isBidder is False and isPartner is False and isBidderOrPartnerWinning is True:
                    # If we have the winning card play it
                    if winningCard in potentialTrickWinners:
                        playLogic = "I can win it from them"
                        play = winningCard
                    # Otherwise play strongest potential winners
                    elif len(potentialTrickWinners) > 0:
                        if trick.trump.isCardTrump(trick.lead):
                            playLogic = "I must overstick"
                            play = potentialTrickWinners[-1]
                        else:
                            playLogic = "I could win it from them"
                            play = potentialTrickWinners[0]

                # Is non partner and partner/bidder loosing
                if isBidder is False and isPartner is False and isBidderOrPartnerWinning is False:
                    # If we have the winning card play it
                    if winningCard in potentialTrickWinners:
                        playLogic = "I want it"
                        play = winningCard
                    # Otherwise play strongest potential winners
                    elif len(potentialTrickWinners) > 0:
                        play = potentialTrickWinners[0]
                        if trick.trump.isCardTrump(play) and len(trick.plays) == 4:
                            # save trump
                            play = None
                        else:
                            playLogic = "I would like it"

                if play is None:
                    if len(possiblePlays) == len(self.player.hand):
                        playLogic = "Play weakest in hand"
                        for valuePower in ["9", "10", "J", "Q", "K", "A"]:
                            for possiblePlay in possiblePlays:
                                # trying to play weak, filter out trump
                                if trick.trump.isCardTrump(possiblePlay):
                                    continue
                                elif possiblePlay.value == valuePower:
                                    play = possiblePlay
                                    break
                            if play:
                                break
                    else:
                        playLogic = "Play weakest"
                        play = possiblePlays[-1]

            # Is starting Round
            elif trick.opening == True:
                if self.round.bid.isHaas():
                    playLogic = "StartHaasRound"
                    play = possiblePlays[possiblePlays.index(trick.trump.rightBar)]
                else:
                    playLogic = "StartNormalRound"

                    for valuePower in ["Q", "K", "10", "9", "A", "J"]:
                        for possiblePlay in possiblePlays:
                            if possiblePlay.value == valuePower and trick.trump.isCardTrump(possiblePlay):
                                play = possiblePlay
                                break
                        if play:
                            break
            # Starting Trick
            else:
                nonTrumpWinner = None
                trumpWinner = None
                trumpInHand = []
                for possiblePlay in possiblePlays:
                    if trick.trump.isCardTrump(possiblePlay):
                        trumpInHand.append(possiblePlay)
                        if self.__suitWinner[trick.trump.suit] == possiblePlay:
                            trumpWinner = possiblePlay
                    else:
                        if self.__suitWinner[possiblePlay.suit] == possiblePlay:
                            nonTrumpWinner = possiblePlay

                if trumpWinner and (isBidder is True or isPartner is True):
                    playLogic = "We have a trump winner"
                    play = trumpWinner
                elif nonTrumpWinner:
                    playLogic = "We have a nontrump winner"
                    play = nonTrumpWinner
                elif len(trumpInHand) > 0 and isPartner is True:
                    playLogic = "Play into Bidder"
                    for value in ["Q", "K", "10", "A", "9"]:
                        potentialPlay = Card(value, self.round.trump.suit)
                        if potentialPlay in possiblePlays:
                            play = potentialPlay
                            break

        if play is None:
            playLogic = "Random"
            possiblePlaysIndex = random.sample(range(len(possiblePlays)), 1)
            play = possiblePlays[possiblePlaysIndex[0]]

        getLogger().log(self.player.name + " using play logic: " + playLogic)
        self.player.hand.remove(play)
        trick.addPlay(Play(self.player, play))

    def showPlay(self, play):
        if self.round.trump.isCardTrump(play.card):
            self.__trumpCount += 1
            suit = self.round.trump.suit
            isTrump = True
        else:
            suit = play.card.suit
            isTrump = False

        self.__playedCards[suit].append(play.card)
        currentSuitWinner = self.__suitWinner[suit]
        if self.__playedCards[suit].count(currentSuitWinner) == 2:
            if currentSuitWinner == self.round.trump.rightBar:
                self.__suitWinner[suit] = self.round.trump.leftBar
            elif currentSuitWinner == self.round.trump.leftBar:
                self.__suitWinner[suit] = Card("A", suit)
            elif currentSuitWinner.value == "A":
                self.__suitWinner[suit] = Card("K", suit)
            elif currentSuitWinner.value == "K":
                self.__suitWinner[suit] = Card("Q", suit)
            elif currentSuitWinner.value == "Q" and isTrump == False:
                self.__suitWinner[suit] = Card("J", suit)
            elif currentSuitWinner.value == "Q" and isTrump == True:
                self.__suitWinner[suit] = Card("10", suit)
            elif currentSuitWinner.value == "J":
                self.__suitWinner[suit] = Card("10", suit)
            elif currentSuitWinner.value == "10":
                self.__suitWinner[suit] = Card("9", suit)

    def sitPlayer(self, playerName):
        pass


class InteractivePlayerMode(PlayerMode):
    def __init__(self, player, view):
        PlayerMode.__init__(self, player, view)

    def exchangeCards(self, passedCards):
        self.player.hand.extend(passedCards)
        self.player.hand.sort()
        self.view.showHand(self.player.hand)

        cardsToReturn = []
        cardsToReturn.append(self.view.getCardFromHand(self.player.hand, "Select card to pass back to partner"))
        cardsToReturn.append(self.view.getCardFromHand(self.player.hand, "Select card to pass back to partner"))
        self.player.hand.remove(cardsToReturn[0])
        self.player.hand.remove(cardsToReturn[1])
        return cardsToReturn

    def makeBid(self, currentBid, isDealer):
        if currentBid is None or currentBid.count == -1:
            if isDealer:
                minBid = 4
            else:
                minBid = 5
        else:
            minBid = currentBid.count + 1

        if minBid > 18:
            validBids = ["pass", "haasx2"]
        if isDealer and minBid == 4:
            validBids = ["4", "haas"]
        else:
            validBids = ["pass"]
            validBid = minBid
            while validBid < 10:
                validBids.append(str(validBid))
                validBid = validBid + 1
            validBids.append("haas")

        enteredBid = self.view.getBid(validBids, minBid)

        if enteredBid == "pass":
            bid = Bid(self.player, -1)
        elif enteredBid == "haas":
            bid = Bid(self.player, 18)
        elif enteredBid == "haasx2":
            bid = Bid(self.player, 36)
        elif enteredBid in validBids:
            bidNum = int(enteredBid)
            bid = Bid(self.player, bidNum)
        else:
            raise Exception("makeBid interrupted")

        return bid

    def namePartnerCard(self, invalidList):
        possiblePartnerCard = []

        if (self.player.hand.count(self.round.trump.rightBar) < 2) and not (self.round.trump.rightBar in invalidList):
            possiblePartnerCard.append(self.round.trump.rightBar)
        elif (self.player.hand.count(self.round.trump.leftBar) < 2) and not (self.round.trump.leftBar in invalidList):
            possiblePartnerCard.append(self.round.trump.leftBar)
        else:
            for cardValue in ["A", "K", "Q", "10", "9"]:
                card = Card(cardValue, self.round.trump.suit)
                self.round.trump.adjustCardForTrump(card)
                if (self.player.hand.count(card) < 2) and not (card in invalidList):
                    possiblePartnerCard.append(card)
                    break
        for suit in ["c", "d", "h", "s"]:
            if suit == self.round.trump.suit:
                continue
            for cardValue in ["A", "K", "Q", "10", "9"]:
                card = Card(cardValue, suit)
                if (self.player.hand.count(card) < 2) and not (card in invalidList):
                    possiblePartnerCard.append(card)
                    break

        return self.view.namePartnerCard(possiblePartnerCard)

    def nameTrump(self):
        # getLogger().logCardList("Your Hand", self.player.hand)
        suit = self.view.nameTrump()
        return Trump(suit)

    def passCards(self):
        cardsToPass = []
        cardsToPass.append(self.view.getCardFromHand(self.player.hand, "Select card to pass partner"))
        cardsToPass.append(self.view.getCardFromHand(self.player.hand, "Select card to pass partner"))
        self.player.hand.remove(cardsToPass[0])
        self.player.hand.remove(cardsToPass[1])
        return cardsToPass

    def pickupKiddie(self):

        self.player.hand.extend(self.round.kiddie.cardList)
        self.view.addKiddieToHand(self.round.kiddie)

        discards = []
        while len(discards) < 3:
            # self.view.showHand(self.player.hand)
            discard = self.view.getCardFromHand(self.player.hand, "Select card to discard", self.player.hand)
            self.player.hand.remove(discard)
            discards.append(discard)

        return discards

    def play(self, trick, possiblePlays):
        play = self.view.getCardFromHand(self.player.hand, "Select card to play", possiblePlays)

        self.player.hand.remove(play)
        trick.addPlay(Play(self.player, play))

    def showPlay(self, play):
        self.view.showPlay(play)
