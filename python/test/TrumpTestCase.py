'''
Created on Nov 24, 2010

@author: timothy
'''
import unittest
from haas_card import Card
from haas_round import Trump

class Test(unittest.TestCase):


    def test_LeftBar(self):
        trump = Trump('c')
        card = Card('J', 's')
        trump.adjustCardForTrump(card)
        assert trump.leftBar == card

    def test_RightBar(self):
        trump = Trump('c')
        card = Card('J', 'c')
        trump.adjustCardForTrump(card)
        assert trump.rightBar == card

    def test_isCardTrump_A(self):
        trump = Trump('c')
        card = Card('A', 'c')
        trump.adjustCardForTrump(card)
        assert trump.isCardTrump(card)

    def test_isCardTrump_K(self):
        trump = Trump('c')
        card = Card('K', 'c')
        trump.adjustCardForTrump(card)        
        assert trump.isCardTrump(card)

    def test_isCardTrump_Q(self):
        trump = Trump('c')
        card = Card('A', 'c')
        trump.adjustCardForTrump(card)        
        assert trump.isCardTrump(card)
        
    def test_isCardTrump_RightBar(self):
        trump = Trump('c')
        card = Card('J', 'c')
        trump.adjustCardForTrump(card)        
        assert trump.isCardTrump(card)

    def test_isCardTrump_LeftBar(self):
        trump = Trump('c')
        card = Card('J', 's')
        trump.adjustCardForTrump(card)        
        assert trump.isCardTrump(card)

    def test_isCardTrump_10(self):
        trump = Trump('c')
        card = Card('10', 'c')
        trump.adjustCardForTrump(card)        
        assert trump.isCardTrump(card)
        
    def test_isCardTrump_9(self):
        trump = Trump('c')
        card = Card('9', 'c')
        trump.adjustCardForTrump(card)        
        assert trump.isCardTrump(card)
        
    def test_BadSuit(self):
        try:
            Trump('q')
            passed = False
        except:
            passed = True
            
        assert passed == True
            
if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()