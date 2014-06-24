'''
Created on Nov 17, 2010

@author: timothy
'''
from haas_guitk import GUITkView
from haas_round import Bid
from haas_logger import getLogger
from tkinter import Event

import random

class GUITkViewProxy:
    def __init__(self, aiPlayerMode, gameManager, gamesToPlay):
        self.aiPlayerMode = aiPlayerMode
        self.guitk = GUITkView()
        self.gameManager = gameManager
        self.cardsToPlay = None
        self.gamesToPlay = gamesToPlay
        self.gamesPlayed = 0
    def addKiddieToHand(self, kiddie):
        self.guitk.addKiddieToHand(kiddie)
    def announceBid(self, bid):
        self.guitk.announceBid(bid)
    def announceBidder(self, bid):
        self.guitk.canvas.after(150, self.guitk.continueButton.continueGame)
        self.guitk.announceBidder(bid)
    def announceGameWinner(self, gameWinner):
        self.guitk.canvas.after(150, self.guitk.continueButton.continueGame)
        
        self.gamesPlayed += 1
        if(self.gamesPlayed < self.gamesToPlay):
            self.guitk.root.after(250, self.guitk.startGame)
        else:
            self.guitk.root.after(250, self.guitk.exitGame)
        self.guitk.announceGameWinner(gameWinner)
        getLogger().prefix = "Game #" + str(self.gamesPlayed + 1) + ": "
    def announceMessage(self, message):
        self.guitk.announceMessage(message)
    def announcePartners(self, bidder, partner):
        self.guitk.announcePartners(bidder, partner)
    def announcePartnerCard(self, partnerCard):
        self.guitk.announcePartnerCard(partnerCard)
    def announceRoundResult(self, message):
        self.guitk.canvas.after(150, self.guitk.continueButton.continueGame)
        self.guitk.announceRoundResult(message)
    def announceScore(self, player):
        self.guitk.announceScore(player)
    def announceTrump(self, trump):
        self.guitk.announceTrump(trump)
    def readyToBegin(self):
        return self.guitk.readyToBegin()
    def getBid(self, validBids, minBid):
        isStuck =  "4" in validBids or "haasx2" in validBids
        if minBid == 4:
            currentBid = -1
        elif minBid == 18:
            currentBid = 9
        elif minBid == 36:
            currentBid = 18
        else:
            currentBid = minBid -1
        bid = self.aiPlayerMode.makeBid(Bid(None,currentBid), isStuck)

        if bid.isPass():
            self.guitk.canvas.after(150, self.guitk.bidButtons.passBid)
        else:
            if not(isStuck):
                self.guitk.canvas.after(150, self.guitk.bidButtons.makeBid)
            if bid.count == 18:
                setBid = SetBid(self.guitk.bidButtons, "haas")
            elif bid.count == 36:
                setBid = SetBid(self.guitk.bidButtons, "haasx2")
            else:
                setBid = SetBid(self.guitk.bidButtons, bid.count)
            self.guitk.canvas.after(250, setBid.execute)
        result = self.guitk.getBid(validBids, minBid)
        return result
    def getGameType(self):
        return self.guitk.getGameType()
    def getCardFromHand(self, hand, msg, possiblePlays=None):
        cardToPlay = None
        if msg ==  "Select card to pass back to partner":
            if self.cardsToPlay is None:
                self.aiPlayerMode.round = self.gameManager.game.gameRound
                self.cardsToPlay = self.aiPlayerMode.exchangeCards([])
                self.aiPlayerMode.player.hand.extend(self.cardsToPlay)
            cardToPlay = self.cardsToPlay.pop()
            if len(self.cardsToPlay) == 0:
                self.cardsToPlay = None
        elif msg == "Select card to pass partner":
            if self.cardsToPlay is None:
                self.aiPlayerMode.round = self.gameManager.game.gameRound
                self.cardsToPlay = self.aiPlayerMode.passCards()
                self.aiPlayerMode.player.hand.extend(self.cardsToPlay)
            cardToPlay = self.cardsToPlay.pop()
            if len(self.cardsToPlay) == 0:
                self.cardsToPlay = None
        elif msg == "Select card to discard":
            if self.cardsToPlay is None:
                
                self.aiPlayerMode.round = self.gameManager.game.gameRound
                for card in self.gameManager.game.gameRound.kiddie.cardList:
                    self.aiPlayerMode.player.hand.remove(card)
                self.cardsToPlay = self.aiPlayerMode.pickupKiddie()
                self.aiPlayerMode.player.hand.extend(self.cardsToPlay)

            cardToPlay = self.cardsToPlay.pop()
            if len(self.cardsToPlay) == 0:
                self.cardsToPlay = None
        else:
            if possiblePlays is None:
                listToUse = hand
            else:
                listToUse = possiblePlays
            playsIndex = random.sample(range(len(listToUse)), 1)
            cardToPlay = listToUse[playsIndex[0]]                
    
        for item in self.guitk.table.hand.items():
            imgtag, cardInHand = item
            if cardInHand.card == cardToPlay:
                activeCardEvent = ActiveCardEvent(self.guitk.activeHand, cardInHand)
                self.guitk.canvas.after(150, activeCardEvent.execute)
                break
        return self.guitk.getCardFromHand(hand, msg, [cardToPlay])
    def greet(self):
        self.guitk.greet()
    def joinTable(self, localPlayer):
        self.guitk.joinTable(localPlayer)
    def namePartnerCard(self, validCardList):
        self.guitk.canvas.after(150, self.guitk.confirmNamePartner.confirm)
        return self.guitk.namePartnerCard(validCardList)
    def nameTrump(self):
        if self.aiPlayerMode.bidTrump.suit == "c":
            self.guitk.canvas.after(150, self.guitk.nameTrumpButtons.selectClubs)
        if self.aiPlayerMode.bidTrump.suit == "d":
            self.guitk.canvas.after(150, self.guitk.nameTrumpButtons.selectDiamonds)
        if self.aiPlayerMode.bidTrump.suit == "h":
            self.guitk.canvas.after(150, self.guitk.nameTrumpButtons.selectHearts)
        if self.aiPlayerMode.bidTrump.suit == "s":
            self.guitk.canvas.after(150, self.guitk.nameTrumpButtons.selectSpades)
                    
        return self.guitk.nameTrump()
    def run(self, engine):
        getLogger().prefix = "Game #" + str(self.gamesPlayed + 1) + ": "
        self.guitk.run(engine)
    def showHand(self, hand):
        self.aiPlayerMode.player.hand = hand
        self.guitk.showHand(hand)
    def showDealtCards(self):
        self.guitk.showDealtCards()      
    def showPlay(self, play):
        self.guitk.showPlay(play)
    def showKiddie(self, kiddie):
        self.guitk.showKiddie(kiddie)
    def showTrickResult(self, winningPlayer, trickCount):
        self.guitk.showTrickResult(winningPlayer, trickCount)
    def sitPlayer(self, playerName):
        self.guitk.sitPlayer(playerName)

class SetBid():
    def __init__(self, bidButtons, bidCount):
        self.bidButtons = bidButtons
        self.bidCount = bidCount
    def execute(self):
        self.bidButtons.bidDialog.bid.set(self.bidCount)
        self.bidButtons.bidDialog.ok()
class  ActiveCardEvent():
    def __init__(self, activeHand, cardInHand):
        self.activeHand = activeHand
        self.cardInHand = cardInHand
    def execute(self):
        event = Event()
        event.x = self.cardInHand.x
        event.y = self.cardInHand.y
        self.activeHand.throwCard(event)
        
if __name__ == '__main__':
    pass


