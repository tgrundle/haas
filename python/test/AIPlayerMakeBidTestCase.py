'''
Created on May 26, 2012

@author: timothy
'''
import unittest
from haas_card import Card
from haas_players import AIPlayerMode
from haas_round import Player
class Test(unittest.TestCase):

    def testNonDealerHaas1(self):
        hand = []
        hand.append(Card('J' 'c'))
        hand.append(Card('J' 'c'))
        hand.append(Card('J' 's'))
        hand.append(Card('J' 's'))
        hand.append(Card('A' 'c'))
        hand.append(Card('A' 'c'))
        hand.append(Card('K' 'c'))
        hand.append(Card('K' 'c'))
        hand.append(Card('Q' 'c'))
        
        playerMode = AIPlayerMode(Player())
        playerMode.player.name="TestCase"
        playerMode.player.hand=hand
        bid = playerMode.makeBid(None, False)
        assert bid.isHaas()
        assert playerMode.bidTrump.suit == 'c'

    def testNonDealerPass1(self):
        hand = []
        hand.append(Card('Q' 'c'))
        hand.append(Card('K' 's'))
        hand.append(Card('Q' 's'))
        hand.append(Card('J' 's'))
        hand.append(Card('10' 's'))
        hand.append(Card('A' 'h'))
        hand.append(Card('J' 'h'))
        hand.append(Card('10' 'h'))
        hand.append(Card('10' 'h'))
        
        playerMode = AIPlayerMode(Player())
        playerMode.player.name="TestCase"
        playerMode.player.hand=hand
        bid = playerMode.makeBid(None, False)
        assert bid.isPass()

    def testNonDealerPass2(self):
        hand = []
        hand.append(Card('Q' 'c'))
        hand.append(Card('K' 's'))
        hand.append(Card('Q' 's'))
        hand.append(Card('10' 's'))
        hand.append(Card('10' 's'))
        hand.append(Card('A' 'h'))
        hand.append(Card('K' 'h'))
        hand.append(Card('10' 'h'))
        hand.append(Card('10' 'h'))
        
        playerMode = AIPlayerMode(Player())
        playerMode.player.name="TestCase"
        playerMode.player.hand=hand
        bid = playerMode.makeBid(None, False)
        assert bid.isPass()

    def testNonDealerPass3(self):
        hand = []
        hand.append(Card('10' 'c'))
        hand.append(Card('A' 'd'))
        hand.append(Card('A' 'd'))
        hand.append(Card('K' 'd'))
        hand.append(Card('Q' 'd'))
        hand.append(Card('9' 'd'))
        hand.append(Card('A' 's'))
        hand.append(Card('A' 'h'))
        hand.append(Card('10' 'h'))
        
        playerMode = AIPlayerMode(Player())
        playerMode.player.name="TestCase"
        playerMode.player.hand=hand
        bid = playerMode.makeBid(None, False)
        assert bid.isPass()
        
    def testNonDealerPass4(self):
        hand = []
        hand.append(Card('A' 'c'))
        hand.append(Card('Q' 'c'))
        hand.append(Card('10' 'c'))
        hand.append(Card('9' 'c'))
        hand.append(Card('A' 'd'))
        hand.append(Card('K' 'd'))
        hand.append(Card('J' 'd'))
        hand.append(Card('10' 'd'))
        hand.append(Card('Q' 'h'))
        
        playerMode = AIPlayerMode(Player())
        playerMode.player.name="TestCase"
        playerMode.player.hand=hand
        bid = playerMode.makeBid(None, False)
        assert bid.isPass()
        
    def testNonDealer5Bid1(self):
        hand = []
        hand.append(Card('Q' 'c'))
        hand.append(Card('10' 'c'))
        hand.append(Card('A' 'd'))
        hand.append(Card('J' 'd'))
        hand.append(Card('9' 'd'))
        hand.append(Card('A' 's'))
        hand.append(Card('Q' 's'))
        hand.append(Card('J' 's'))
        hand.append(Card('J' 'h'))
        
        playerMode = AIPlayerMode(Player())
        playerMode.player.name="TestCase"
        playerMode.player.hand=hand
        bid = playerMode.makeBid(None, False)
        assert bid.count == 5
        assert playerMode.bidTrump.suit == 'd'            
if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testHaas1']
    unittest.main()