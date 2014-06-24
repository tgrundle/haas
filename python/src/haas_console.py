"""
Created on Nov 17, 2010

@author: timothy
"""
import sys
from haas_card import Card
from haas_engine import Engine
from haas_logger import getLogger


class ConsoleView:
    def __init__(self):
        getLogger().log("Running in console view!")
        self.engine = Engine(self)

    def __getInput(self, message, validResponses=[]):
        print(message)

        validResponsesAsString = []

        for entry in validResponses:
            validResponsesAsString.append(str(entry))

        while (True):
            response = sys.stdin.readline()[:-1]

            if len(validResponsesAsString) == 0 or response in validResponsesAsString:
                break
            else:
                print("Invalid response. Valid responses are ", validResponsesAsString, ":")

        return response

    def __printCardList(self, display, cardList):
        printableList = []
        for card in cardList:
            printableList.append(str(card))

        print(display, printableList)

    def addKiddieToHand(self, kiddie):
        pass;

    def announceBid(self, bid):
        print(bid.bidder.name + " bids " + str(bid.count) + ".")

    def announceBidder(self, bid):
        print(bid.bidder.name + " is under for " + str(bid.count) + ".")

    def announceGameWinner(self, gameWinner):
        print(gameWinner.name + " won the game.")

    def announceMessage(self, message):
        print(message)

    def announcePartners(self, bidder, partner):
        print(partner.name + " is partner with " + bidder.name)

    def announcePartnerCard(self, partnerCard):
        print("Looking for the " + str(partnerCard))

    def announceRoundResult(self, message):
        print(message)

    def announceTrickWinner(self, playerName):
        print("===================================================================")
        print("         *** " + playerName + " won the trick. ***                 ")
        print("===================================================================")

    def announceTrump(self, trump):
        print("Trump is " + trump.suit)

    def blockToReadyToBegin(self):
        self.__getInput("Would you like to (b)egin the game", ["b"])

    def namePartnerCard(self, possiblePartnerCard):
        partnerCard = self.__getInput("Enter Card for partner: ", possiblePartnerCard)
        return Card(partnerCard[:-1], partnerCard[-1])

    def nameTrump(self):
        return self.__getInput("Enter Trump (c, d, h, s): ", ["c", "d", "s", "h"])

    def getDiscard(self, hand):
        return self.__getInput("Enter card to discard: ", hand)

    def getBid(self, validBids, minBid):
        return self.__getInput("Enter Bid (" + str(minBid) + " - 9, haas, or pass): ", validBids)

    def getGameType(self):
        return self.__getInput("Would you like to (s)tart or (j)oin a game? ", ["s", "j"])

    def greet(self):
        print("Welcome to the Yost/Rundle flavor of Haas")
        name = self.getInput("Enter your name: ")
        print("Hello ", name, "!")
        return name

    def getCardFromHand(self, hand, message, possiblePlays):
        self.showHand(hand)
        self.__printCardList("Your possible options: ", possiblePlays)

        play = None

        while True:
            inputText = self.__getInput(message)
            try:
                play = Card(inputText[:-1], inputText[-1])
            except:
                self.announceMessage("Invalid entry: " + inputText)

            if play in possiblePlays:
                return play

    def joinTable(self, localPlayer, playerList):
        for name in playerList:
            self.announceMessage(name + " welcomes you to the game!")

    def showDealtCards(self):
        pass

    def showHand(self, hand):
        self.__printCardList("Your cards are: ", hand)

    def showKiddie(self, kiddie):
        self.__printCardList("The Kiddie is: ", kiddie.cardList)

    def showPlay(self, play):
        pass

    def showTrickResult(self, winningPlayer, trickCount):
        self.announceMessage(winningPlayer.name + " won the trick!")
        for playerName in trickCount:
            self.announceMessage(playerName + "'s Trick Count: " + str(trickCount[playerName]))

    def sitPlayer(self, playerName):
        self.announceMessage(playerName + " has joined the game!")

    def run(self):
        self.engine.playGame(self)


if __name__ == '__main__':
    pass
