'''
Created on Nov 14, 2010

@author: timothy
'''
import unittest
from haas_card import Card


class CardTestCase(unittest.TestCase):

    def test_equal(self):
        card1 = Card('A', 'c')
        card2 =Card('A', 'c')
        
        assert card1 == card2
        
    def test_equalDifferntValue(self):
        card1 = Card('A', 'c')
        card2 =Card('K', 'c')
        
        assert not(card1 == card2)

    def test_equalDifferntSuit(self):
        card1 = Card('A', 'c')
        card2 =Card('A', 'd')
        
        assert not(card1 == card2)
        
    def test_equalNone1(self):
        card1 = Card('A', 'c')
        
        assert not(card1 == None)

    def test_equalNone2(self):
        card1 = Card('A', 'c')
        
        assert not(None == card1)
        
    def test_invalidSuite(self):
        try: 
            Card("9", "q")
            passed = False
        except:
            passed = True
        
        assert passed == True
        
    def test_invalidValue1(self):
        try: 
            Card("8", "c")
            passed = False
        except:
            passed = True

        assert passed == True

    def test_invalidValue2(self):
        try: 
            Card("A", "c")
            assert False
        except:
            pass

        
    def test_validInit(self):
        Card("9", "c")
        Card("10", "c")
        Card("J", "c")
        Card("Q", "c")
        Card("K", "c")
        Card("A", "c")
        Card("9", "s")
        Card("10", "s")
        Card("J", "s")
        Card("Q", "s")
        Card("K", "s")
        Card("A", "s")
        Card("9", "d")
        Card("10", "d")
        Card("J", "d")
        Card("Q", "d")
        Card("K", "d")
        Card("A", "d")
        Card("9", "h")
        Card("10", "h")
        Card("J", "h")
        Card("Q", "h")
        Card("K", "h")
        Card("A", "h")
        Card("9", "c")
        Card("10", "c")
        Card("J", "c")
        Card("Q", "c")
        Card("K", "c")
        Card("A", "c")
        Card("9", "s")
        Card("10", "s")
        Card("J", "s")
        Card("Q", "s")
        Card("K", "s")
        Card("A", "s")
        Card("9", "d")
        Card("10", "d")
        Card("J", "d")
        Card("Q", "d")
        Card("K", "d")
        Card("A", "d")
        Card("9", "h")
        Card("10", "h")
        Card("J", "h")
        Card("Q", "h")
        Card("K", "h")
        Card("A", "h")


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()