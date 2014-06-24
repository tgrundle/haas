'''
Created on Nov 14, 2010

@author: timothy
'''
import unittest
from haas_card import Card
from haas_round import Trump

class CardIsBetterTrumpTestCase(unittest.TestCase):

    def test_isBetter_RightBarOverRightBar(self):
        card1 = Card('J', 's')
        card2 = Card('J', 's')
        trump = Trump('s')
        trump.adjustCardForTrump(card1)
        trump.adjustCardForTrump(card2)
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_RightBarOverLeftBar(self):
        card1 = Card('J', 's')
        card2 = Card('J', 'c')
        trump = Trump('s')
        trump.adjustCardForTrump(card1)
        trump.adjustCardForTrump(card2)
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_RightBarOverA(self):
        card1 = Card('J', 's')
        card2 = Card('A', 's')
        trump = Trump('s')
        trump.adjustCardForTrump(card1)
        trump.adjustCardForTrump(card2)
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_RightBarOverK(self):
        card1 = Card('J', 's')
        card2 = Card('K', 's')
        trump = Trump('s')
        trump.adjustCardForTrump(card1)
        trump.adjustCardForTrump(card2)
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_RightBarOverQ(self):
        card1 = Card('J', 's')
        card2 = Card('Q', 's')
        trump = Trump('s')
        trump.adjustCardForTrump(card1)
        trump.adjustCardForTrump(card2)
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_RightBarOver10(self):
        card1 = Card('J', 's')
        card2 = Card('10', 's')
        trump = Trump('s')
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_RightBarOver9(self):
        card1 = Card('J', 's')
        card2 = Card('9', 's')
        trump = Trump('s')
        trump.adjustCardForTrump(card1)
        trump.adjustCardForTrump(card2)
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_LeftBarOverLeftBar(self):
        card1 = Card('J', 'c')
        card2 = Card('J', 'c')
        trump = Trump('s')
        trump.adjustCardForTrump(card1)
        trump.adjustCardForTrump(card2)
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_LeftBarOverA(self):
        card1 = Card('J', 'c')
        card2 = Card('A', 's')
        trump = Trump('s')
        trump.adjustCardForTrump(card1)
        trump.adjustCardForTrump(card2)
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_LeftBarOverK(self):
        card1 = Card('J', 'c')
        card2 = Card('K', 's')
        trump = Trump('s')
        trump.adjustCardForTrump(card1)
        trump.adjustCardForTrump(card2)
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_LeftBarOverQ(self):
        card1 = Card('J', 'c')
        card2 = Card('Q', 's')
        trump = Trump('s')
        trump.adjustCardForTrump(card1)
        trump.adjustCardForTrump(card2)
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_LeftBarOver10(self):
        card1 = Card('J', 'c')
        card2 = Card('10', 's')
        trump = Trump('s')
        trump.adjustCardForTrump(card1)
        trump.adjustCardForTrump(card2)
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_LeftBarOver9(self):
        card1 = Card('J', 'c')
        card2 = Card('9', 's')
        trump = Trump('s')
        trump.adjustCardForTrump(card1)
        trump.adjustCardForTrump(card2)
        assert not(card1.isBetter(card2, trump))

if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()