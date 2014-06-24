'''
Created on May 27, 2012

@author: timothy
'''

from haas_engine import GameManager, Engine
from haas_players import AIPlayerMode
from haas_round import Player
from haas_guitk import GUITkView
from haas_logger import getLogger

if __name__ == '__main__':
    
    gameManager = GameManager()
    player1 = AIPlayerMode(Player())
    player1.player.name="A1"
    player2 = AIPlayerMode(Player())
    player2.player.name="A2"
    player3 = AIPlayerMode(Player())
    player3.player.name="A3"
    player4 = AIPlayerMode(Player())
    player4.player.name="A4"
    player5 = AIPlayerMode(Player())
    player5.player.name="A5"
    gameManager.addPlayer(player1)
    gameManager.addPlayer(player2)
    gameManager.addPlayer(player3)
    gameManager.addPlayer(player4)
    gameManager.addPlayer(player5)
#    view = GUITkView()
#    engine = Engine(view)
#    engine.gameManager = gameManager
#    view.run(engine)

    i = 0
    while i < 100000:
        getLogger().prefix = "Game #" + str(i + 1) + ": "
        gameManager.launchGame()
        gameManager.reset()
        i += 1
    pass