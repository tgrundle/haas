"""
Created on Nov 15, 2010

@author: timothy
"""
import random


# from haas_logger import getLogger

class Card:
    def __init__(self, value, suit=None):

        if suit is None:
            self.value = value[0:-1]
            self.suit = value[-1]
        else:
            self.value = value
            self.suit = suit

        if not (self.value in ["A", "K", "Q", "J", "10", "9"]):
            raise "Invalid value for card" + self.value

        if not (self.suit in ['c', 'd', 'h', 's']):
            raise "Invalid suit for card: " + self.suit

        self.__calculateWeight()

    def __calculateWeight(self):
        if self.suit == 'c':
            self.weight = 0
        elif self.suit == 'd':
            self.weight = 8
        elif self.suit == 'h':
            self.weight = 24
        elif self.suit == 's':
            self.weight = 16

        if self.value == '9':
            self.weight += 7
        elif self.value == '10':
            self.weight += 6
        elif self.value == 'J':
            self.weight += 5
        elif self.value == 'Q':
            self.weight += 4
        elif self.value == 'K':
            self.weight += 3
        elif self.value == 'A':
            self.weight += 2

    def __eq__(self, x):
        if x is None or not (type(x) == type(self)):
            areEqual = False
        else:
            areEqual = self.weight == x.weight

        return areEqual

    def __lt__(self, x):

        return self.weight < x.weight

    def __str__(self):
        return self.value + self.suit + "(" + str(self.weight) + ")"

    def isBetter(self, card, trump):
        if self == trump.rightBar:
            betterCard = False
        elif card == trump.rightBar:
            betterCard = True
        elif self == trump.leftBar:
            betterCard = False
        elif card == trump.leftBar:
            betterCard = True
        elif self.suit == card.suit:
            betterCard = card.weight < self.weight
        elif card.suit == trump.suit:
            betterCard = True
        else:
            betterCard = False

        return betterCard

    def makeLeftBar(self):
        if self.suit == 'c':
            self.weight = 17
        elif self.suit == 'd':
            self.weight = 25
        elif self.suit == 'h':
            self.weight = 9
        elif self.suit == 's':
            self.weight = 1

    def makeRightBar(self):
        if self.suit == 'c':
            self.weight = 0
        elif self.suit == 'd':
            self.weight = 8
        elif self.suit == 'h':
            self.weight = 24
        elif self.suit == 's':
            self.weight = 16

    def resetWeight(self):
        self.__calculateWeight()


class Deck:
    def __init__(self):
        self.cardList = []
        self.cardList.extend(
            [Card("9", "c"), Card("10", "c"), Card("J", "c"), Card("Q", "c"), Card("K", "c"), Card("A", "c")])
        self.cardList.extend(
            [Card("9", "s"), Card("10", "s"), Card("J", "s"), Card("Q", "s"), Card("K", "s"), Card("A", "s")])
        self.cardList.extend(
            [Card("9", "d"), Card("10", "d"), Card("J", "d"), Card("Q", "d"), Card("K", "d"), Card("A", "d")])
        self.cardList.extend(
            [Card("9", "h"), Card("10", "h"), Card("J", "h"), Card("Q", "h"), Card("K", "h"), Card("A", "h")])
        self.cardList.extend(
            [Card("9", "c"), Card("10", "c"), Card("J", "c"), Card("Q", "c"), Card("K", "c"), Card("A", "c")])
        self.cardList.extend(
            [Card("9", "s"), Card("10", "s"), Card("J", "s"), Card("Q", "s"), Card("K", "s"), Card("A", "s")])
        self.cardList.extend(
            [Card("9", "d"), Card("10", "d"), Card("J", "d"), Card("Q", "d"), Card("K", "d"), Card("A", "d")])
        self.cardList.extend(
            [Card("9", "h"), Card("10", "h"), Card("J", "h"), Card("Q", "h"), Card("K", "h"), Card("A", "h")])

    def shuffle(self):
        sortOrder = random.sample(range(48), 48)
        for cardIndex in sortOrder:
            card = self.cardList.pop(cardIndex)
            self.cardList.append(card)
