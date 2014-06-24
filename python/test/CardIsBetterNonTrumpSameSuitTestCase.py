'''
Created on Nov 14, 2010

@author: timothy
'''
import unittest
from haas_card import Card
from haas_round import Trump

class CardIsBetterNonTrumpSameSuitTestCase(unittest.TestCase):


    def test_isBetter_AtoA(self):
        card1 = Card('A', 's')
        card2 = Card('A', 's')
        trump = Trump('h')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_AtoK(self):
        card1 = Card('A', 's')
        card2 = Card('K', 's')
        trump = Trump('h')
        
        assert not(card1.isBetter(card2, trump))


    def test_isBetter_AtoQ(self):
        card1 = Card('A', 's')
        card2 = Card('Q', 's')
        trump = Trump('h')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_AtoJ(self):
        card1 = Card('A', 's')
        card2 = Card('J', 's')
        trump = Trump('h')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_Ato10(self):
        card1 = Card('A', 's')
        card2 = Card('10', 's')
        trump = Trump('c')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_Ato9(self):
        card1 = Card('A', 's')
        card2 = Card('9', 's')
        trump = Trump('c')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_KtoA(self):
        card1 = Card('K', 's')
        card2 = Card('A', 's')
        trump = Trump('h')
        
        assert card1.isBetter(card2, trump)

    def test_isBetter_KtoK(self):
        card1 = Card('K', 's')
        card2 = Card('K', 's')
        trump = Trump('h')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_KtoQ(self):
        card1 = Card('K', 's')
        card2 = Card('Q', 's')
        trump = Trump('h')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_KtoJ(self):
        card1 = Card('K', 's')
        card2 = Card('J', 's')
        trump = Trump('h')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_Kto10(self):
        card1 = Card('K', 's')
        card2 = Card('10', 's')
        trump = Trump('h')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_Kto9(self):
        card1 = Card('K', 's')
        card2 = Card('9', 's')
        trump = Trump('h')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_QtoA(self):
        card1 = Card('Q', 's')
        card2 = Card('A', 's')
        trump = Trump('h')
        
        assert card1.isBetter(card2, trump)

    def test_isBetter_QtoK(self):
        card1 = Card('Q', 's')
        card2 = Card('K', 's')
        trump = Trump('h')
        
        assert card1.isBetter(card2, trump)

    def test_isBetter_QtoQ(self):
        card1 = Card('Q', 's')
        card2 = Card('Q', 's')
        trump = Trump('h')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_QtoJ(self):
        card1 = Card('Q', 's')
        card2 = Card('Q', 's')
        trump = Trump('h')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_Qto10(self):
        card1 = Card('Q', 's')
        card2 = Card('Q', 's')
        trump = Trump('h')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_Qto9(self):
        card1 = Card('Q', 's')
        card2 = Card('9', 's')
        trump = Trump('h')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_JtoA(self):
        card1 = Card('J', 's')
        card2 = Card('A', 's')
        trump = Trump('h')
        
        assert card1.isBetter(card2, trump)

    def test_isBetter_JtoK(self):
        card1 = Card('J', 's')
        card2 = Card('K', 's')
        trump = Trump('h')
        
        assert card1.isBetter(card2, trump)

    def test_isBetter_JtoQ(self):
        card1 = Card('J', 's')
        card2 = Card('Q', 's')
        trump = Trump('h')
        
        assert card1.isBetter(card2, trump)

    def test_isBetter_JtoJ(self):
        card1 = Card('J', 's')
        card2 = Card('J', 's')
        trump = Trump('h')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_Jto10(self):
        card1 = Card('J', 's')
        card2 = Card('10', 's')
        trump = Trump('h')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_Jto9(self):
        card1 = Card('J', 's')
        card2 = Card('9', 's')
        trump = Trump('h')
        
        assert not(card1.isBetter(card2, trump))
        
    def test_isBetter_10toA(self):
        card1 = Card('10', 's')
        card2 = Card('A', 's')
        trump = Trump('c')
        
        assert card1.isBetter(card2, trump)

    def test_isBetter_10toK(self):
        card1 = Card('10', 's')
        card2 = Card('K', 's')
        trump = Trump('c')
        
        assert card1.isBetter(card2, trump)

    def test_isBetter_10toQ(self):
        card1 = Card('10', 's')
        card2 = Card('Q', 's')
        trump = Trump('c')
        
        assert card1.isBetter(card2, trump)

    def test_isBetter_10toJ(self):
        card1 = Card('10', 's')
        card2 = Card('J', 's')
        trump = Trump('c')
        
        assert card1.isBetter(card2, trump)

    def test_isBetter_10to10(self):
        card1 = Card('10', 's')
        card2 = Card('10', 's')
        trump = Trump('c')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_10to9(self):
        card1 = Card('10', 's')
        card2 = Card('9', 's')
        trump = Trump('c')
        
        assert not(card1.isBetter(card2, trump))

    def test_isBetter_9toA(self):
        card1 = Card('9', 's')
        card2 = Card('A', 's')
        trump = Trump('c')
        
        assert card1.isBetter(card2, trump)

    def test_isBetter_9toK(self):
        card1 = Card('9', 's')
        card2 = Card('K', 's')
        trump = Trump('c')
        
        assert card1.isBetter(card2, trump)

    def test_isBetter_9toQ(self):
        card1 = Card('9', 's')
        card2 = Card('Q', 's')
        trump = Trump('c')
        
        assert card1.isBetter(card2, trump)

    def test_isBetter_9toJ(self):
        card1 = Card('9', 's')
        card2 = Card('J', 's')
        trump = Trump('c')
        
        assert card1.isBetter(card2, trump)

    def test_isBetter_9to10(self):
        card1 = Card('9', 's')
        card2 = Card('10', 's')
        trump = Trump('c')
        
        assert card1.isBetter(card2, trump)

    def test_isBetter_9to9(self):
        card1 = Card('9', 's')
        card2 = Card('9', 's')
        trump = Trump('c')
        
        assert not(card1.isBetter(card2, trump))

if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()